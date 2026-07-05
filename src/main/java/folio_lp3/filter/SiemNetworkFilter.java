package folio_lp3.filter;

import folio_lp3.entity.DispositivoUsuario;
import folio_lp3.entity.IpBlacklist;
import folio_lp3.repository.DispositivoUsuarioRepository;
import  folio_lp3.repository.IpBlacklistRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class SiemNetworkFilter implements Filter {

    private final IpBlacklistRepository blacklistRepository;
    private final DispositivoUsuarioRepository dispositivoRepository;

    // 🔥 Caché de Firewall en Memoria RAM para velocidad extrema (Anti-DoS)
    private final Map<String, LocalDateTime> ipBlacklistCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void cargarListaNegraEnMemoria() {
        log.info("🛡️ [SIEM-FIREWALL] Cargando IPs bloqueadas en memoria RAM...");
        blacklistRepository.findAll().forEach(ipEntity -> {
            // Si no ha expirado el baneo, va a la RAM
            if (ipEntity.getExpiracionBloqueo() == null || ipEntity.getExpiracionBloqueo().isAfter(LocalDateTime.now())) {
                ipBlacklistCache.put(ipEntity.getIpBloqueada(), 
                    ipEntity.getExpiracionBloqueo() != null ? ipEntity.getExpiracionBloqueo() : LocalDateTime.MAX);
            }
        });
        log.info("✅ [SIEM-FIREWALL] {} IPs cargadas en el escudo de aislamiento.", ipBlacklistCache.size());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 🕵️‍♂️ Capturar IP real (Soporta Proxies de Render/Cloudflare mediante X-Forwarded-For)
        String ipOrigen = httpRequest.getHeader("X-Forwarded-For");
        if (ipOrigen == null || ipOrigen.isEmpty() || "unknown".equalsIgnoreCase(ipOrigen)) {
            ipOrigen = httpRequest.getRemoteAddr();
        }
        
        // Limpiar en caso de múltiples saltos de proxy (toma la primera IP)
        if (ipOrigen != null && ipOrigen.contains(",")) {
            ipOrigen = ipOrigen.split(",")[0].trim();
        }

        // 🛑 EVALUACIÓN EN MICROSEGUNDO CERO: ¿Está en la Lista Negra?
        if (ipBlacklistCache.containsKey(ipOrigen)) {
            LocalDateTime expiracion = ipBlacklistCache.get(ipOrigen);
            
            if (LocalDateTime.now().isBefore(expiracion)) {
                log.warn("🚨 [SIEM-MITIGATION] Intento de intrusión bloqueado de la IP: {}", ipOrigen);
                
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                httpResponse.getWriter().write("{\"status\":\"ACCESS_DENIED\",\"reason\":\"IP_SUSPENDED_BY_SIEM_FIREWALL\"}");
                return; // Corta la petición aquí, no pasa a los controladores
            } else {
                // El baneo expiró, la removemos del caché en caliente
                ipBlacklistCache.remove(ipOrigen);
            }
        }

        // Si pasa el muro de fuego, continúa la petición normal
        chain.doFilter(request, response);

        // 👤 CAPTURA FORENSE POST-AUTENTICACIÓN (Historial de Dispositivos)
        // Se ejecuta después de pasar los filtros de JWT si el usuario se autenticó con éxito
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String emailUsuario = auth.getName();
            String userAgent = httpRequest.getHeader("User-Agent");
            
            registrarOActualizarDispositivo(emailUsuario, ipOrigen, userAgent);
        }
    }

   private void registrarOActualizarDispositivo(String email, String ip, String userAgentStr) {
    try {
        String dispositivoNombre = interpretarDispositivo(userAgentStr);
        String tipo = userAgentStr != null && (userAgentStr.toLowerCase().contains("mobile") || userAgentStr.toLowerCase().contains("android")) ? "MOBILE" : "DESKTOP";
        
        dispositivoRepository.findByUsuarioEmailAndIpOrigenAndDispositivo(email, ip, dispositivoNombre)
            .ifPresentOrElse(
                dispositivoExisting -> {
                    LocalDateTime ahora = LocalDateTime.now();
                    // 💡 SOLUCIÓN CRÍTICA: Solo impactar MySQL si pasaron más de 60 segundos desde el último check
                    if (dispositivoExisting.getUltimaConexion() == null || 
                        dispositivoExisting.getUltimaConexion().isBefore(ahora.minusMinutes(1))) {
                        
                        dispositivoExisting.setActiva(true);
                        dispositivoExisting.setUltimaConexion(ahora);
                        dispositivoRepository.save(dispositivoExisting);
                        log.debug("🛡️ [SIEM-FORENSE] Telemetría de dispositivo actualizada para {}", email);
                    }
                },
                () -> {
                    // Si el dispositivo es completamente nuevo, se guarda de inmediato
                    DispositivoUsuario nuevoDispositivo = new DispositivoUsuario();
                    nuevoDispositivo.setUsuarioEmail(email);
                    nuevoDispositivo.setIpOrigen(ip);
                    nuevoDispositivo.setDispositivo(dispositivoNombre);
                    nuevoDispositivo.setTipoDispositivo(tipo);
                    nuevoDispositivo.setLugar("📍 Analizando Telemetría..."); 
                    nuevoDispositivo.setUltimaConexion(LocalDateTime.now());
                    dispositivoRepository.save(nuevoDispositivo);
                    log.info("🛡️ [SIEM-FORENSE] Nuevo dispositivo indexado con éxito para {}", email);
                }
            );
    } catch (Exception e) {
        log.error("❌ Error al procesar el log forense de dispositivo: ", e);
    }
}

    // Parseador manual ultra-ligero para no depender de librerías gordas que ralenticen el Servidor
    private String interpretarDispositivo(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) return "Unknown Hardware";
        String ua = userAgent.toLowerCase();
        
        String os = "Unknown OS";
        if (ua.contains("windows")) os = "Windows";
        else if (ua.contains("macintosh") || ua.contains("mac os")) os = "Mac OS";
        else if (ua.contains("android")) os = "Android";
        else if (ua.contains("iphone")) os = "iOS (iPhone)";
        else if (ua.contains("linux")) os = "Linux";

        String browser = "Generic Browser";
        if (ua.contains("chrome") && !ua.contains("chromium")) browser = "Chrome";
        else if (ua.contains("safari") && !ua.contains("chrome")) browser = "Safari";
        else if (ua.contains("firefox")) browser = "Firefox";
        else if (ua.contains("edg")) browser = "Edge";
        
        return os + " (" + browser + ")";
    }

    // ⚡ Métodos públicos expuestos para que tus Controllers puedan banear/desbanear en caliente desde la web
    public void agregarIpBlacklistEnCaliente(String ip, LocalDateTime expiracion) {
        this.ipBlacklistCache.put(ip, expiracion != null ? expiracion : LocalDateTime.MAX);
    }

    public void removerIpBlacklistEnCaliente(String ip) {
        this.ipBlacklistCache.remove(ip);
    }
}