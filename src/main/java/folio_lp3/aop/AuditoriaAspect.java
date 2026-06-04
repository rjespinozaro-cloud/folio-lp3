package folio_lp3.aop;

import folio_lp3.entity.Auditoria;
import folio_lp3.repository.AuditoriaRepository;
import folio_lp3.repository.UsuarioRepository;
import folio_lp3.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * Aspecto AOP para auditoría automática
 * Registra cambios importantes en el sistema
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditoriaAspect {
    
    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * Auditar creación de recursos (POST)
     */
    @After("execution(* folio_lp3.controller.*Controller.crear*(..))")
    public void auditarCreacion(JoinPoint joinPoint) {
        registrarAuditoria("CREAR", joinPoint.getTarget().getClass().getSimpleName(), joinPoint);
    }
    
    /**
     * Auditar actualización de recursos (PUT)
     */
    @After("execution(* folio_lp3.controller.*Controller.actualizar*(..))")
    public void auditarActualizacion(JoinPoint joinPoint) {
        registrarAuditoria("ACTUALIZAR", joinPoint.getTarget().getClass().getSimpleName(), joinPoint);
    }
    
    /**
     * Auditar eliminación de recursos (DELETE)
     */
    @After("execution(* folio_lp3.controller.*Controller.eliminar*(..))")
    public void auditarEliminacion(JoinPoint joinPoint) {
        registrarAuditoria("ELIMINAR", joinPoint.getTarget().getClass().getSimpleName(), joinPoint);
    }
    
    /**
     * Auditar cambios críticos (cancelar consultas, etc.)
     */
    @After("execution(* folio_lp3.controller.*Controller.cancelar*(..))")
    public void auditarCancelacion(JoinPoint joinPoint) {
        registrarAuditoria("CANCELAR", joinPoint.getTarget().getClass().getSimpleName(), joinPoint);
    }
    
    /**
     * Registrar entrada en auditoría
     */
    private void registrarAuditoria(String accion, String tabla, JoinPoint joinPoint) {
        try {
            // Obtener usuario del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication != null ? authentication.getName() : "SISTEMA";
            
            // Obtener token del request si existe
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String tablaAfectada = tabla.replace("Controller", "").toUpperCase();
            
            // Crear registro de auditoría
            Auditoria auditoria = Auditoria.builder()
                    .accion(accion)
                    .tablaAfectada(tablaAfectada)
                    .descripcion("Acción: " + accion + " en " + tablaAfectada + " por usuario: " + email)
                    .fechaHora(LocalDateTime.now())
                    .build();
            
            // Intentar obtener usuario
            usuarioRepository.findByEmail(email).ifPresent(auditoria::setUsuario);
            
            auditoriaRepository.save(auditoria);
            log.info("Auditoría registrada: {} - {}", accion, tablaAfectada);
        } catch (Exception e) {
            log.error("Error al registrar auditoría: {}", e.getMessage());
        }
    }
}
