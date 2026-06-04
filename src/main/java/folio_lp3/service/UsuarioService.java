package folio_lp3.service;

import folio_lp3.dto.UsuarioDTO;
import folio_lp3.entity.Usuario;
import folio_lp3.enums.RolUsuario;
import folio_lp3.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para Usuario - Lógica de negocio
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    // CREATE
    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getNombreCompleto())); // Provisional
        usuario.setRol(usuarioDTO.getRol() != null ? usuarioDTO.getRol() : RolUsuario.ESTUDIANTE);
        usuario.setActivo(true);
        
        Usuario guardado = usuarioRepository.save(usuario);
        return convertirADTO(guardado);
    }
    
    // READ
    public UsuarioDTO obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    public UsuarioDTO obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }
    
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<UsuarioDTO> listarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<UsuarioDTO> listarActivos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    // UPDATE
    public UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuario.setRol(usuarioDTO.getRol());
        usuario.setActivo(usuarioDTO.getActivo());
        
        Usuario actualizado = usuarioRepository.save(usuario);
        return convertirADTO(actualizado);
    }
    
    public void actualizarUltimoAcceso(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
    
    // DELETE
    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
    
    // UTILIDADES
    public boolean validarCredenciales(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getContrasena());
    }
    
    private UsuarioDTO convertirADTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .pilarAsignadoId(usuario.getPilarAsignado() != null ? usuario.getPilarAsignado().getId() : null)
                .activo(usuario.getActivo())
                .ultimoAcceso(usuario.getUltimoAcceso())
                .createdAt(usuario.getCreatedAt())
                .updatedAt(usuario.getUpdatedAt())
                .build();
    }
}
