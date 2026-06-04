package folio_lp3.controller;

import folio_lp3.dto.LoginDTO;
import folio_lp3.dto.TokenDTO;
import folio_lp3.dto.UsuarioDTO;
import folio_lp3.service.UsuarioService;
import folio_lp3.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para Autenticación
 */
@RestController
@RequestMapping("/autenticacion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AutenticacionController {
    
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    
    /**
     * Login - Generar token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            // Validar credenciales
            if (!usuarioService.validarCredenciales(loginDTO.getEmail(), loginDTO.getContrasena())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(TokenDTO.builder().build());
            }
            
            // Obtener usuario
            UsuarioDTO usuario = usuarioService.obtenerPorEmail(loginDTO.getEmail());
            
            // Generar token
            String token = jwtUtil.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol().toString());
            
            // Actualizar último acceso
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * Registrar nuevo usuario
     */
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Verificar si token es válido
     */
    @GetMapping("/validar/{token}")
    public ResponseEntity<Boolean> validarToken(@PathVariable String token) {
        boolean esValido = jwtUtil.validarToken(token);
        return ResponseEntity.ok(esValido);
    }
}
