package folio_lp3.controller;

import folio_lp3.dto.UsuarioDTO;
import folio_lp3.enums.RolUsuario;
import folio_lp3.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para Usuario
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO resultado = usuarioService.crearUsuario(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> obtenerPorEmail(@PathVariable String email) {
        UsuarioDTO usuario = usuarioService.obtenerPorEmail(email);
        return ResponseEntity.ok(usuario);
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioDTO>> listarPorRol(@PathVariable String rol) {
        RolUsuario rolEnum = RolUsuario.valueOf(rol.toUpperCase());
        List<UsuarioDTO> usuarios = usuarioService.listarPorRol(rolEnum);
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioDTO>> listarActivos() {
        List<UsuarioDTO> usuarios = usuarioService.listarActivos();
        return ResponseEntity.ok(usuarios);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO resultado = usuarioService.actualizar(id, usuarioDTO);
        return ResponseEntity.ok(resultado);
    }
    
    @PutMapping("/{id}/ultimo-acceso")
    public ResponseEntity<Void> actualizarUltimoAcceso(@PathVariable Long id) {
        usuarioService.actualizarUltimoAcceso(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok().build();
    }
}
