package folio_lp3.aop;

import folio_lp3.entity.Auditoria;
import folio_lp3.repository.AuditoriaRepository;
import folio_lp3.repository.UsuarioRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Aspecto AOP para auditoría automática
 * Registra cambios importantes en el sistema evitando NullPointerExceptions
 * en contextos asíncronos o tareas programadas (Schedulers).
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditoriaAspect {
    
    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    
    @After("execution(* folio_lp3.controller.*Controller.crear*(..))")
    public void auditarCreacion(JoinPoint joinPoint) {
        registrarAuditoria("CREAR", joinPoint);
    }
    
    @After("execution(* folio_lp3.controller.*Controller.actualizar*(..)) || execution(* folio_lp3.service.ConsultaService.actualizar*(..))")
    public void auditarActualizacion(JoinPoint joinPoint) {
        registrarAuditoria("ACTUALIZAR", joinPoint);
    }
    
    @After("execution(* folio_lp3.controller.*Controller.eliminar*(..))")
    public void auditarEliminacion(JoinPoint joinPoint) {
        registrarAuditoria("ELIMINAR", joinPoint);
    }
    
    @After("execution(* folio_lp3.controller.*Controller.cancelar*(..)) || execution(* folio_lp3.service.ConsultaService.cancelar*(..))")
    public void auditarCancelacion(JoinPoint joinPoint) {
        registrarAuditoria("CANCELAR", joinPoint);
    }
    
    private void registrarAuditoria(String accion, JoinPoint joinPoint) {
        try {
            // 1. Obtener usuario de forma segura (Soporta peticiones Web y Schedulers)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = "SISTEMA"; // Valor por defecto para procesos en background
            
            if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                email = authentication.getName();
            }
            
            // 2. Obtener la tabla afectada de forma segura
            String tablaAfectada = "DESCONOCIDA";
            Object target = joinPoint.getTarget();
            if (target != null) {
                // Limpiamos los sufijos "Controller" o "Service" para extraer el nombre base
                tablaAfectada = target.getClass().getSimpleName()
                        .replace("Controller", "")
                        .replace("Service", "")
                        .toUpperCase();
            }
            
            // 3. Crear registro de auditoría
            Auditoria auditoria = Auditoria.builder()
                    .accion(accion)
                    .tablaAfectada(tablaAfectada)
                    .descripcion("Acción: " + accion + " en " + tablaAfectada + " ejecutada por: " + email)
                    .fechaHora(LocalDateTime.now())
                    .build();
            
            // 4. Si no es el SISTEMA, intentamos vincular el usuario real
            if (!email.equals("SISTEMA")) {
                usuarioRepository.findByEmail(email).ifPresent(auditoria::setUsuario);
            }
            
            auditoriaRepository.save(auditoria);
            log.info("Auditoría registrada correctamente: {} - {} por {}", accion, tablaAfectada, email);
            
        } catch (Exception e) {
            log.error("Error crítico al intentar registrar la auditoría: {}", e.getMessage());
        }
    }
}