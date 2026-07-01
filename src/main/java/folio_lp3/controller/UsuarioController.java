package folio_lp3.controller;

import folio_lp3.dto.RespuestaGenericaDTO;
import folio_lp3.dto.UsuarioDTO;
import folio_lp3.enums.RolUsuario;
import folio_lp3.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para Usuario implementando estándares Enterprise.
 * Todas las respuestas están encapsuladas en RespuestaGenericaDTO.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<RespuestaGenericaDTO<UsuarioDTO>> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO resultado = usuarioService.crearUsuario(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaGenericaDTO.exitoso("Usuario creado exitosamente", resultado));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaGenericaDTO<UsuarioDTO>> obtenerPorId(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Usuario encontrado", usuario));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<RespuestaGenericaDTO<UsuarioDTO>> obtenerPorEmail(@PathVariable String email) {
        UsuarioDTO usuario = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Usuario encontrado", usuario));
    }
    
    @GetMapping
    public ResponseEntity<RespuestaGenericaDTO<List<UsuarioDTO>>> listarTodos() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Lista de usuarios", usuarios));
    }
    
    @GetMapping("/rol/{rol}")
    public ResponseEntity<RespuestaGenericaDTO<List<UsuarioDTO>>> listarPorRol(@PathVariable String rol) {
        RolUsuario rolEnum = RolUsuario.valueOf(rol.toUpperCase());
        List<UsuarioDTO> usuarios = usuarioService.listarPorRol(rolEnum);
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Usuarios filtrados por rol", usuarios));
    }
    
    @GetMapping("/activos")
    public ResponseEntity<RespuestaGenericaDTO<List<UsuarioDTO>>> listarActivos() {
        List<UsuarioDTO> usuarios = usuarioService.listarActivos();
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Usuarios activos", usuarios));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaGenericaDTO<UsuarioDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO resultado = usuarioService.actualizar(id, usuarioDTO);
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Usuario actualizado", resultado));
    }
    
    @PutMapping("/{id}/ultimo-acceso")
    public ResponseEntity<RespuestaGenericaDTO<Void>> actualizarUltimoAcceso(@PathVariable Long id) {
        usuarioService.actualizarUltimoAcceso(id);
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Último acceso actualizado", null));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<RespuestaGenericaDTO<Void>> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok(RespuestaGenericaDTO.exitoso("Usuario desactivado correctamente", null));
    }
}
