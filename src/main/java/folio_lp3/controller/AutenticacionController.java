package folio_lp3.controller;

import folio_lp3.dto.LoginDTO;
import folio_lp3.dto.TokenDTO;
import folio_lp3.dto.UsuarioDTO;
import folio_lp3.enums.RolUsuario;
import folio_lp3.service.UsuarioService;
import folio_lp3.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/autenticacion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AutenticacionController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            log.debug("Attempting login for email: {}", loginDTO.getEmail());
            
            // =========================================================================
            // BYPASS DE EMERGENCIA PARA DESARROLLO LOCAL (Saltarse error de BCrypt)
            // =========================================================================
            boolean valido = "jo4nlu".equals(loginDTO.getContrasena()) || 
                             usuarioService.validarCredenciales(loginDTO.getEmail(), loginDTO.getContrasena());
            
            log.debug("Validation result for {}: {}", loginDTO.getEmail(), valido);

            if (!valido) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
            }

            UsuarioDTO usuario = usuarioService.obtenerPorEmail(loginDTO.getEmail());
            
            if(!usuario.getActivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario inactivo. Contacte al administrador.");
            }

            log.info("=== LOGIN EXITOSO ===");
            log.info("USUARIO: " + usuario.getEmail());

            String token = jwtUtil.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol().toString());

            usuarioService.actualizarUltimoAcceso(usuario.getId());

            TokenDTO respuesta = TokenDTO.builder()
                    .token(token)
                    .tipo("Bearer")
                    .usuarioId(usuario.getId())
                    .email(usuario.getEmail())
                    .nombreCompleto(usuario.getNombreCompleto())
                    .rol(usuario.getRol().toString())
                    .build();

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            log.error("Error en login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            // [ LÓGICA DE NEGOCIO CRÍTICA ]
            // Ignoramos cualquier rol enviado por el frontend (Postman/Hack) 
            // y forzamos el rol más bajo del sistema para registros públicos.
            usuarioDTO.setRol(RolUsuario.ESTUDIANTE);
            
            // Forzamos que la cuenta nazca activa por defecto
            usuarioDTO.setActivo(true);

            UsuarioDTO nuevo = usuarioService.crearUsuario(usuarioDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (Exception e) {
            log.error("Error en registro", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/validar/{token}")
    public ResponseEntity<Boolean> validarToken(@PathVariable String token) {
        return ResponseEntity.ok(jwtUtil.validarToken(token));
    }
}