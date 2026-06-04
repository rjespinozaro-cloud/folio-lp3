package folio_lp3.service;

import folio_lp3.dto.DetalleComandoPilarDTO;
import folio_lp3.entity.DetalleComandoPilar;
import folio_lp3.entity.Herramienta;
import folio_lp3.entity.PilarCiberseguridad;
import folio_lp3.entity.Subtema;
import folio_lp3.repository.DetalleComandoPilarRepository;
import folio_lp3.repository.HerramientaRepository;
import folio_lp3.repository.PilarCiberseguridadRepository;
import folio_lp3.repository.SubtemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para DetalleComandoPilar
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DetalleComandoPilarService {
    
    private final DetalleComandoPilarRepository detalleRepository;
    private final PilarCiberseguridadRepository pilarRepository;
    private final HerramientaRepository herramientaRepository;
    private final SubtemaRepository subtemaRepository;
    
    public DetalleComandoPilarDTO crear(DetalleComandoPilarDTO dto) {
        PilarCiberseguridad pilar = pilarRepository.findById(dto.getPilarId())
                .orElseThrow(() -> new RuntimeException("Pilar no encontrado"));
        
        Herramienta herramienta = herramientaRepository.findById(dto.getHerramientaId())
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));
        
        Subtema subtema = null;
        if (dto.getSubtemaId() != null) {
            subtema = subtemaRepository.findById(dto.getSubtemaId())
                    .orElseThrow(() -> new RuntimeException("Subtema no encontrado"));
        }
        
        DetalleComandoPilar detalle = DetalleComandoPilar.builder()
                .pilar(pilar)
                .herramienta(herramienta)
                .tipoComando(dto.getTipoComando())
                .sintaxis(dto.getSintaxis())
                .capturaScreenUrl(dto.getCapturaScreenUrl())
                .nivelImpacto(dto.getNivelImpacto())
                .vulnerabilidadAsociada(dto.getVulnerabilidadAsociada())
                .mitigacion(dto.getMitigacion())
                .descripcionPersonalizada(dto.getDescripcionPersonalizada())
                .subtema(subtema)
                .activo(true)
                .build();
        
        DetalleComandoPilar guardado = detalleRepository.save(detalle);
        return convertirADTO(guardado);
    }
    
    public DetalleComandoPilarDTO obtenerPorId(Long id) {
        return detalleRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new RuntimeException("Detalle de comando no encontrado"));
    }
    
    public List<DetalleComandoPilarDTO> listarTodos() {
        return detalleRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<DetalleComandoPilarDTO> listarPorPilar(Long pilarId) {
        return detalleRepository.findActiveByPilar(pilarId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public List<DetalleComandoPilarDTO> listarPorHerramienta(Long herramientaId) {
        return detalleRepository.findByHerramientaId(herramientaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    public DetalleComandoPilarDTO actualizar(Long id, DetalleComandoPilarDTO dto) {
        DetalleComandoPilar detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de comando no encontrado"));
        
        detalle.setTipoComando(dto.getTipoComando());
        detalle.setSintaxis(dto.getSintaxis());
        detalle.setCapturaScreenUrl(dto.getCapturaScreenUrl());
        detalle.setNivelImpacto(dto.getNivelImpacto());
        detalle.setVulnerabilidadAsociada(dto.getVulnerabilidadAsociada());
        detalle.setMitigacion(dto.getMitigacion());
        detalle.setDescripcionPersonalizada(dto.getDescripcionPersonalizada());
        
        DetalleComandoPilar actualizado = detalleRepository.save(detalle);
        return convertirADTO(actualizado);
    }
    
    public void eliminar(Long id) {
        detalleRepository.deleteById(id);
    }
    
    private DetalleComandoPilarDTO convertirADTO(DetalleComandoPilar detalle) {
        return DetalleComandoPilarDTO.builder()
                .id(detalle.getId())
                .pilarId(detalle.getPilar().getId())
                .pilarNombre(detalle.getPilar().getNombrePilar())
                .herramientaId(detalle.getHerramienta().getId())
                .herramientaNombre(detalle.getHerramienta().getNombre())
                .tipoComando(detalle.getTipoComando())
                .sintaxis(detalle.getSintaxis())
                .capturaScreenUrl(detalle.getCapturaScreenUrl())
                .nivelImpacto(detalle.getNivelImpacto())
                .vulnerabilidadAsociada(detalle.getVulnerabilidadAsociada())
                .mitigacion(detalle.getMitigacion())
                .descripcionPersonalizada(detalle.getDescripcionPersonalizada())
                .subtemaId(detalle.getSubtema() != null ? detalle.getSubtema().getId() : null)
                .subtemaCodigo(detalle.getSubtema() != null ? detalle.getSubtema().getCodigo() : null)
                .activo(detalle.getActivo())
                .build();
    }
}
