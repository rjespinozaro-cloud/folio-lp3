package folio_lp3.interceptor;

import folio_lp3.entity.SiemLog;
import folio_lp3.service.SiemService;
import folio_lp3.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SiemInterceptor implements HandlerInterceptor {

    private final SiemService siemService;
    private final JwtUtil jwtUtil;
    private static final String SIEM_LOG_ATTRIBUTE = "siemLogAttribute";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // 🛡️ BYPASS SIEM: solo /network sigue excluido.
        if (requestURI.contains("/api/v1/admin/network")) {
            return true;
        }

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else {
            // 🛡️ CORRECCIÓN CRÍTICA: Si pasan por el mismo router/proxy, tomamos solo la primera IP
            // Evita que cadenas tipo "192.168.1.10, 10.0.0.1" rompan el ancho de columna en la DB
            ipAddress = ipAddress.split(",")[0].trim();
        }

        SiemLog log = new SiemLog();
        log.setIpOrigen(ipAddress);
        log.setEndpoint(requestURI);
        log.setMetodoHttp(request.getMethod());
        
        // Truncamos preventivamente el UserAgent a 255 caracteres por seguridad de la DB
        String ua = request.getHeader("User-Agent");
        log.setUserAgent(ua != null && ua.length() > 255 ? ua.substring(0, 255) : ua);
        
        log.setTimestamp(LocalDateTime.now());

        request.setAttribute(SIEM_LOG_ATTRIBUTE, log);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();

        if (requestURI.contains("/api/v1/admin/network")) {
            return;
        }

        Object siemLogAttr = request.getAttribute(SIEM_LOG_ATTRIBUTE);
        if (siemLogAttr instanceof SiemLog) {
            SiemLog log = (SiemLog) siemLogAttr;
            log.setStatusHttp(response.getStatus());

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String email = jwtUtil.extraerEmail(token);
                    log.setUsuarioEmail(email);
                } catch (Exception e) {
                    log.setUsuarioEmail("token_invalido");
                }
            }

            Throwable excepcionSpring = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
            if (ex != null) {
                log.setDetallesError(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            } else if (excepcionSpring != null) {
                log.setDetallesError(excepcionSpring.getClass().getSimpleName() + ": " + excepcionSpring.getMessage());
            }

            siemService.saveLog(log); // Envío seguro procesado por tu Service
        }
    }
}