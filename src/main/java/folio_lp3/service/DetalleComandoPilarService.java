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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio orquestador de comandos y evidencias, blindado contra fallos de integridad relacional.
 */
@Service
@RequiredArgsConstructor
public class DetalleComandoPilarService {
    
    private final DetalleComandoPilarRepository detalleRepository;
    private final PilarCiberseguridadRepository pilarRepository;
    private final HerramientaRepository herramientaRepository;
    private final SubtemaRepository subtemaRepository;
    
    @Transactional
    public DetalleComandoPilarDTO crear(DetalleComandoPilarDTO dto) {
        PilarCiberseguridad pilar = pilarRepository.findById(dto.getPilarId())
                .orElseThrow(() -> new EntityNotFoundException("Asignación fallida: El pilar ID " + dto.getPilarId() + " no existe."));
        
        Herramienta herramienta = herramientaRepository.findById(dto.getHerramientaId())
                .orElseThrow(() -> new EntityNotFoundException("Asignación fallida: La herramienta ID " + dto.getHerramientaId() + " no existe."));
        
        Subtema subtema = null;
        if (dto.getSubtemaId() != null) {
            subtema = subtemaRepository.findById(dto.getSubtemaId())
                    .orElseThrow(() -> new EntityNotFoundException("Asignación fallida: El subtema ID " + dto.getSubtemaId() + " no existe."));
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
        
        return convertirADTO(detalleRepository.save(detalle));
    }
    
    @Transactional(readOnly = true)
    public DetalleComandoPilarDTO obtenerPorId(Long id) {
        return detalleRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new EntityNotFoundException("El detalle de comando con ID " + id + " no existe."));
    }
    
    @Transactional(readOnly = true)
    public List<DetalleComandoPilarDTO> listarTodos() {
        return detalleRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DetalleComandoPilarDTO> listarPorPilar(Long pilarId) {
        return detalleRepository.findActiveByPilar(pilarId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DetalleComandoPilarDTO> listarPorHerramienta(Long herramientaId) {
        return detalleRepository.findByHerramientaId(herramientaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public DetalleComandoPilarDTO actualizar(Long id, DetalleComandoPilarDTO dto) {
        DetalleComandoPilar detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar. Comando ID " + id + " no existe."));
        
        detalle.setTipoComando(dto.getTipoComando());
        detalle.setSintaxis(dto.getSintaxis());
        detalle.setCapturaScreenUrl(dto.getCapturaScreenUrl());
        detalle.setNivelImpacto(dto.getNivelImpacto());
        detalle.setVulnerabilidadAsociada(dto.getVulnerabilidadAsociada());
        detalle.setMitigacion(dto.getMitigacion());
        
        // CORRECCIÓN AQUÍ: Se cambió setdescripcionPersonalizada por setDescripcionPersonalizada (P mayúscula)
        detalle.setDescripcionPersonalizada(dto.getDescripcionPersonalizada());
        
        return convertirADTO(detalleRepository.save(detalle));
    }
    
    @Transactional
    public void eliminar(Long id) {
        if (!detalleRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar. Comando ID " + id + " no existe.");
        }
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