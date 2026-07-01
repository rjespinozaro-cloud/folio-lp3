package folio_lp3.service;

import folio_lp3.dto.HerramientaDTO;
import folio_lp3.dto.DetalleComandoPilarDTO;
import folio_lp3.entity.Herramienta;
import folio_lp3.entity.DetalleComandoPilar;
import folio_lp3.entity.PilarCiberseguridad;
import folio_lp3.entity.Subtema;
import folio_lp3.repository.HerramientaRepository;
import folio_lp3.repository.PilarCiberseguridadRepository;
import folio_lp3.repository.SubtemaRepository;
import folio_lp3.repository.DetalleComandoPilarRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HerramientaService {

    private final HerramientaRepository herramientaRepository;
    private final PilarCiberseguridadRepository pilarRepository;
    private final SubtemaRepository subtemaRepository;
    private final DetalleComandoPilarRepository detalleComandoPilarRepository;

    @Transactional(readOnly = true)
    public List<HerramientaDTO> listarTodas() {
        return herramientaRepository.findAll().stream()
                .map(this::convertirToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HerramientaDTO obtenerPorId(Long id) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset tecnológico no encontrado con ID: " + id));
        return convertirToDTO(herramienta);
    }

    @Transactional
    public HerramientaDTO crearHerramienta(HerramientaDTO dto) {
        Herramienta herramienta = Herramienta.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .creador(dto.getCreador())
                .nivelDificultad(dto.getNivelDificultad())
                .urlDocumentacion(dto.getUrlDocumentacion())
                .build();
        
        Herramienta guardada = herramientaRepository.save(herramienta);
        return convertirToDTO(guardada);
    }

    @Transactional
    public HerramientaDTO crearHerramientaConComando(HerramientaDTO herramientaDto, DetalleComandoPilarDTO comandoDto) {
        Herramienta herramienta;
        
        // CORRECCIÓN PARA SOPORTAR EDICIONES POR ENTRADA DE FORMULARIO MULTIPART
        if (herramientaDto.getId() != null) {
            herramienta = herramientaRepository.findById(herramientaDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No se localizó la herramienta para actualizar con ID: " + herramientaDto.getId()));
            herramienta.setNombre(herramientaDto.getNombre());
            herramienta.setNivelDificultad(herramientaDto.getNivelDificultad());
            herramienta.setUrlDocumentacion(herramientaDto.getUrlDocumentacion());
            if (herramientaDto.getDescripcion() != null) {
                herramienta.setDescripcion(herramientaDto.getDescripcion());
            }
        } else {
            herramienta = Herramienta.builder()
                    .nombre(herramientaDto.getNombre())
                    .descripcion(herramientaDto.getDescripcion())
                    .creador(herramientaDto.getCreador())
                    .nivelDificultad(herramientaDto.getNivelDificultad())
                    .urlDocumentacion(herramientaDto.getUrlDocumentacion())
                    .build();
        }

        if (comandoDto != null) {
            PilarCiberseguridad pilar = pilarRepository.findById(comandoDto.getPilarId())
                    .orElseThrow(() -> new EntityNotFoundException("Fallo en cascada: El pilar ID " + comandoDto.getPilarId() + " no existe."));

            Subtema subtema = null;
            if (comandoDto.getSubtemaId() != null) {
                subtema = subtemaRepository.findById(comandoDto.getSubtemaId())
                        .orElseThrow(() -> new EntityNotFoundException("Fallo en cascada: El subtema ID " + comandoDto.getSubtemaId() + " no existe."));
            }

            DetalleComandoPilar detalle = DetalleComandoPilar.builder()
                    .pilar(pilar)
                    .herramienta(herramienta) 
                    .tipoComando(comandoDto.getTipoComando())
                    .sintaxis(comandoDto.getSintaxis())
                    .vulnerabilidadAsociada(comandoDto.getVulnerabilidadAsociada())
                    .mitigacion(comandoDto.getMitigacion())
                    .descripcionPersonalizada(comandoDto.getDescripcionPersonalizada())
                    .subtema(subtema)
                    .activo(true)
                    .build();

            herramienta.getComandos().add(detalle);
        }

        Herramienta guardada = herramientaRepository.save(herramienta);
        return convertirToDTO(guardada);
    }

    /**
     * REGISTRAR ENFOQUE EN CALIENTE: Almacena dinámicamente un string vacío de referencia táctica
     * permitiendo que figure en consultas DISTINCT inmediatas de MariaDB
     */
    @Transactional
    public void guardarNuevoEnfoqueOperativo(String nuevoEnfoque) {
        // Obtenemos una herramienta por defecto o creamos un registro pivote fantasma si la tabla está virgen
        Herramienta dummyTool = herramientaRepository.findAll().stream().findFirst().orElseGet(() -> {
            return herramientaRepository.save(Herramienta.builder()
                    .nombre("CORE_DICTIONARY_PIVOT")
                    .descripcion("System dictionary internal record")
                    .creador("SEC-SYSTEM")
                    .nivelDificultad("0%")
                    .build());
        });

        PilarCiberseguridad dummyPilar = pilarRepository.findAll().stream().findFirst().orElseThrow(() -> 
            new EntityNotFoundException("Debe existir al menos un pilar registrado en el sistema antes de inyectar enfoques operativos."));

        DetalleComandoPilar dictionaryPivot = DetalleComandoPilar.builder()
                .pilar(dummyPilar)
                .herramienta(dummyTool)
                .tipoComando(nuevoEnfoque.toUpperCase())
                .sintaxis("DICTIONARY_RESERVED_VAL")
                .activo(false) // No aparecerá listado como comando real en el portafolio
                .build();

        detalleComandoPilarRepository.save(dictionaryPivot);
    }

    @Transactional
    public HerramientaDTO actualizar(Long id, HerramientaDTO dto) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset no localizado para actualización con ID: " + id));
        
        herramienta.setNombre(dto.getNombre());
        herramienta.setDescripcion(dto.getDescripcion());
        herramienta.setCreador(dto.getCreador());
        herramienta.setNivelDificultad(dto.getNivelDificultad());
        herramienta.setUrlDocumentacion(dto.getUrlDocumentacion());
        
        return convertirToDTO(herramientaRepository.save(herramienta));
    }

    @Transactional
    public void eliminar(Long id) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset no localizado para revocación con ID: " + id));
        herramientaRepository.delete(herramienta);
    }

    @Transactional(readOnly = true)
    public List<String> obtenerEnfoquesOperativosExistentes() {
        List<String> enfoques = detalleComandoPilarRepository.findDistinctTipoComando();
        
        // Filtrar valores reservados del sistema del parseador dinámico
        List<String> filtrados = enfoques.stream()
                .filter(e -> e != null && !e.equals("DICTIONARY_RESERVED_VAL"))
                .collect(Collectors.toList());

        if (filtrados.isEmpty()) {
            return java.util.Arrays.asList(
                "PENTESTING: RECONOCIMIENTO & ENUMERACIÓN",
                "PENTESTING: WEB FUZZING & WORDLISTS",
                "BLUE TEAM: LOG ANALYSIS & THREAT HUNTING"
            );
        }
        return filtrados;
    }

    private HerramientaDTO convertirToDTO(Herramienta h) {
        return HerramientaDTO.builder()
                .id(h.getId())
                .nombre(h.getNombre())
                .descripcion(h.getDescripcion())
                .creador(h.getCreador())
                .nivelDificultad(h.getNivelDificultad())
                .urlDocumentacion(h.getUrlDocumentacion())
                .build();
    }
}