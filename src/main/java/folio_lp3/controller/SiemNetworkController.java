package folio_lp3.controller;

import folio_lp3.entity.DispositivoUsuario;
import folio_lp3.entity.IpBlacklist;
import folio_lp3.repository.DispositivoUsuarioRepository;
import folio_lp3.repository.IpBlacklistRepository;
import folio_lp3.filter.SiemNetworkFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/network")
@RequiredArgsConstructor
@Slf4j
public class SiemNetworkController {

    private final DispositivoUsuarioRepository dispositivoRepository;
    private final IpBlacklistRepository blacklistRepository;
    private final SiemNetworkFilter siemNetworkFilter;

    // 1. Obtener Historial de Dispositivos Activos (Para tu Device Grid en Vercel)
    @GetMapping("/active-sessions")
    public ResponseEntity<List<DispositivoUsuario>> getActiveSessions() {
        return ResponseEntity.ok(dispositivoRepository.findByActivaTrue());
    }

    // 🔗 SOLUCIÓN AL 404: Endpoint para expulsar y dar de baja una sesión de hardware (KICK / KILL)
    @DeleteMapping("/active-sessions/{id}")
    public ResponseEntity<?> expulsarDispositivo(@PathVariable Long id) {
        return dispositivoRepository.findById(id)
                .map(dispositivo -> {
                    // Baja lógica: Mantiene el registro histórico forense, pero lo saca de la monitorización en vivo
                    dispositivo.setActiva(false);
                    dispositivo.setUltimaConexion(LocalDateTime.now());
                    dispositivoRepository.save(dispositivo);
                    
                    log.info("🚨 [SIEM-API] Terminal ID {} expulsada de forma fulminante por el administrador.", id);
                    return ResponseEntity.ok(Map.of("message", "Terminal expulsada con éxito", "id", id));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 2. Obtener Lista Negra del Firewall (Para la tabla de baneos)
    @GetMapping("/blacklist")
    public ResponseEntity<List<IpBlacklist>> getBlacklist() {
        return ResponseEntity.ok(blacklistRepository.findAll());
    }

    // 3. Banear IP en Caliente (Escribe en Base de Datos y lo inyecta a la RAM del Filtro)
    @PostMapping("/blacklist/ban")
    public ResponseEntity<?> banIp(@RequestBody Map<String, String> payload) {
        String ip = payload.get("ip");
        String motivo = payload.getOrDefault("motivo", "Bloqueo manual por el Administrador");
        
        if (ip == null || ip.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La IP es requerida"));
        }

        // Evita duplicar registros en la base de datos
        if (blacklistRepository.findByIpBloqueada(ip).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "La IP ya se encuentra bloqueada"));
        }

        IpBlacklist nuevoBan = new IpBlacklist();
        nuevoBan.setIpBloqueada(ip.trim());
        nuevoBan.setMotivoBloqueo(motivo);
        // Baneo temporal automático de 24 horas (Ajustable)
        nuevoBan.setExpiracionBloqueo(LocalDateTime.now().plusDays(1)); 
        
        blacklistRepository.save(nuevoBan);
        
        // 🔥 ACTUALIZACIÓN EN CALIENTE: Sincroniza con el ConcurrentHashMap de tu filtro
        siemNetworkFilter.agregarIpBlacklistEnCaliente(nuevoBan.getIpBloqueada(), nuevoBan.getExpiracionBloqueo());
        
        log.info("🛡️ [SIEM-API] IP {} bloqueada y enviada al escudo perimetral.", ip);
        return ResponseEntity.ok(Map.of("message", "IP bloqueada con éxito", "ip", ip));
    }

    // 4. Desbanear IP (Remueve de MariaDB y limpia la RAM usando tu método exacto)
    @DeleteMapping("/blacklist/unban/{id}")
    public ResponseEntity<?> unbanIp(@PathVariable Long id) {
        return blacklistRepository.findById(id)
                .map(ipEntity -> {
                    blacklistRepository.delete(ipEntity);
                    
                    // 🔓 REMOVER EN CALIENTE: Llama exactamente al método de tu clase SiemNetworkFilter
                    siemNetworkFilter.removerIpBlacklistEnCaliente(ipEntity.getIpBloqueada());
                    
                    log.info("🔓 [SIEM-API] Acceso restablecido en memoria y DB para la IP: {}", ipEntity.getIpBloqueada());
                    return ResponseEntity.ok(Map.of("message", "IP desbloqueada correctamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Telemetría de Red: Escáner de Puertos Activos del Sistema (Soporta Windows local y Linux Render)
    @GetMapping("/system-ports")
    public ResponseEntity<?> getSystemOpenPorts() {
        List<Map<String, String>> puertosList = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            Process process;
            // Detecta dinámicamente si estás en desarrollo (Windows) o en producción (Linux de Render)
            if (os.contains("win")) {
                process = Runtime.getRuntime().exec("netstat -ano");
            } else {
                process = Runtime.getRuntime().exec("ss -tlnp"); 
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String linea;
            int contador = 0;

            while ((linea = reader.readLine()) != null && contador < 35) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.toLowerCase().contains("conexiones") || linea.toLowerCase().contains("proto")) continue;
                
                Map<String, String> puertoData = new HashMap<>();
                puertoData.put("raw_data", linea);
                puertosList.add(puertoData);
                contador++;
            }
            process.waitFor();
        } catch (Exception e) {
            log.error("❌ Fallo crítico al leer la telemetría de red del OS: ", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "No se pudo leer la telemetría de sockets"));
        }

        return ResponseEntity.ok(puertosList);
    }
}