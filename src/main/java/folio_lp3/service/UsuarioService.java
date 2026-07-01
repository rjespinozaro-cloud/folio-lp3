package folio_lp3.service;

import folio_lp3.dto.UsuarioDTO;
import folio_lp3.entity.PilarCiberseguridad;
import folio_lp3.entity.Usuario;
import folio_lp3.enums.RolUsuario;
import folio_lp3.repository.PilarCiberseguridadRepository;
import folio_lp3.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PilarCiberseguridadRepository pilarCiberseguridadRepository;

    public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con el correo: " + usuarioDTO.getEmail());
        }

        Usuario.UsuarioBuilder usuarioBuilder = Usuario.builder()
                .email(usuarioDTO.getEmail())
                .nombreCompleto(usuarioDTO.getNombreCompleto())
                .contrasena(passwordEncoder.encode(usuarioDTO.getContrasena()))
                .rol(usuarioDTO.getRol() != null ? usuarioDTO.getRol() : RolUsuario.ESTUDIANTE)
                .activo(true);

        if (usuarioDTO.getPilarAsignadoId() != null) {
            PilarCiberseguridad pilar = pilarCiberseguridadRepository.findById(usuarioDTO.getPilarAsignadoId())
                    .orElseThrow(() -> new RuntimeException("Pilar de ciberseguridad no encontrado"));
            usuarioBuilder.pilarAsignado(pilar);
        }

        Usuario guardado = usuarioRepository.save(usuarioBuilder.build());
        return convertirADTO(guardado);
    }

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
        return usuarioRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol).stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarActivos() {
        return usuarioRepository.findByActivoTrue().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuario.setRol(usuarioDTO.getRol());
        usuario.setActivo(usuarioDTO.getActivo());

        if (usuarioDTO.getContrasena() != null && !usuarioDTO.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
        }

        if (usuarioDTO.getPilarAsignadoId() != null) {
            PilarCiberseguridad pilar = pilarCiberseguridadRepository.findById(usuarioDTO.getPilarAsignadoId())
                    .orElseThrow(() -> new RuntimeException("Pilar de ciberseguridad no encontrado"));
            usuario.setPilarAsignado(pilar);
        } else {
            usuario.setPilarAsignado(null);
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return convertirADTO(actualizado);
    }

    public void actualizarUltimoAcceso(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public boolean validarCredenciales(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword());
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