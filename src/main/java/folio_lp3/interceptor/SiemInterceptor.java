package folio_lp3.interceptor;

import folio_lp3.entity.SiemLog;
import folio_lp3.repository.SiemLogRepository;
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

    private final SiemLogRepository siemLogRepository;
    private final JwtUtil jwtUtil;
    private static final String SIEM_LOG_ATTRIBUTE = "siemLogAttribute";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        SiemLog log = new SiemLog();
        log.setIpOrigen(ipAddress);
        log.setEndpoint(request.getRequestURI());
        log.setMetodoHttp(request.getMethod());
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setTimestamp(LocalDateTime.now());

        request.setAttribute(SIEM_LOG_ATTRIBUTE, log);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Object siemLogAttr = request.getAttribute(SIEM_LOG_ATTRIBUTE);
        if (siemLogAttr instanceof SiemLog) {
            SiemLog log = (SiemLog) siemLogAttr;

            // 1. Capturar el estado HTTP
            log.setStatusHttp(response.getStatus());

            // 2. Extraer el email del usuario desde el token JWT
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
            
            // 3. Registrar si hubo una excepción (Estructura única corregida)
            Throwable excepcionSpring = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
            
            if (ex != null) {
                log.setDetallesError(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            } else if (excepcionSpring != null) {
                log.setDetallesError(excepcionSpring.getClass().getSimpleName() + ": " + excepcionSpring.getMessage());
            }

            // 4. Guardar el log completo en la base de datos
            siemLogRepository.save(log);
        }
    }
}