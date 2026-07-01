package folio_lp3.service;

import folio_lp3.dto.PreguntaIADTO;
import folio_lp3.entity.Consulta;
import folio_lp3.entity.PreguntaIA;
import folio_lp3.entity.DetalleComandoPilar;
import folio_lp3.entity.IaConfig;
import folio_lp3.enums.CalificacionIA;
import folio_lp3.repository.ConsultaRepository;
import folio_lp3.repository.PreguntaIARepository;
import folio_lp3.repository.DetalleComandoPilarRepository;
import folio_lp3.repository.IaConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Servicio inteligente con soporte de orquestación contextual (RAG) real para el Cyber Assistant.
 * Optimizado para conmutación dinámica entre Ollama Local y Groq Cloud API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PreguntaIAService {
    
    private final PreguntaIARepository preguntaRepository;
    private final ConsultaRepository consultaRepository;
    private final DetalleComandoPilarRepository comandoRepository;
    private final IaConfigRepository iaConfigRepository; 
    private final ConsultaService consultaService;
    private final RestTemplate restTemplate = new RestTemplate(); 
    
    /**
     * Procesa la pregunta del reclutador o estudiante de forma segura,
     * inyectando las evidencias del portafolio y el contexto vivo del frontend.
     */
    @Transactional
    public PreguntaIADTO generarRespuestaContextual(PreguntaIADTO dto) {
        
        // =========================================================================
        // 🔥 FIJACIÓN DE SEGURIDAD TOTAL: SOPORTE DE SESIONES ANÓNIMAS (ANTI-BD VACÍA)
        // =========================================================================
        Consulta consulta;
        if (dto.getConsultaId() == null) {
            log.info("📡 Consulta entrante sin ID. Enrutando al canal de visitantes anónimos...");
            
            consulta = consultaRepository.findAll().stream()
                    .filter(c -> "Chat Público Anónimo".equals(c.getTemaPrincipal()))
                    .findFirst()
                    .orElseGet(() -> {
                        log.warn("⚠️ No se encontró sesión pública en la BD. Inicializando registro de contingencia de raíz...");
                        
                        folio_lp3.entity.Usuario usuarioSistema = iaConfigRepository.findAll().stream().findFirst()
                                .map(config -> {
                                    return consultaRepository.findAll().stream().findFirst()
                                            .map(Consulta::getEstudiante)
                                            .orElseGet(() -> {
                                                log.warn("🚨 Base de datos sin consultas previas. Generando usuario de respaldo en memoria...");
                                                folio_lp3.entity.Usuario root = new folio_lp3.entity.Usuario();
                                                root.setId(1L); 
                                                return root;
                                            });
                                })
                                .orElseThrow(() -> new IllegalStateException("Fallo crítico: No se localizó la configuración base de la IA."));

                        folio_lp3.entity.PilarCiberseguridad pilarSistema = consultaRepository.findAll().stream()
                                .findFirst()
                                .map(Consulta::getPilar)
                                .orElseGet(() -> {
                                    log.warn("🚨 Base de datos sin Pilares creados. Asignando pilar por defecto ID 1.");
                                    folio_lp3.entity.PilarCiberseguridad pilarDefault = new folio_lp3.entity.PilarCiberseguridad();
                                    pilarDefault.setId(1L); 
                                    return pilarDefault;
                                });

                        Consulta deEmergencia = Consulta.builder()
                                .estudiante(usuarioSistema)
                                .pilar(pilarSistema)
                                .temaPrincipal("Chat Público Anónimo")
                                .estado(folio_lp3.enums.EstadoConsulta.PENDIENTE)
                                .cantidadTokensUsados(0)
                                .build();
                                
                        try {
                            return consultaRepository.save(deEmergencia);
                        } catch (Exception ex) {
                            log.error("❌ Error de llaves foráneas en JPA por BD vacía. Usando bypass transaccional.");
                            return deEmergencia;
                        }
                    });
        } else {
            consulta = consultaRepository.findById(dto.getConsultaId())
                    .orElseThrow(() -> new EntityNotFoundException("Sesión de consulta no válida."));
        }
        
        // 1. LEER CONFIGURACIÓN DE IA EN CALIENTE DESDE LA BASE DE DATOS
        IaConfig configIA = iaConfigRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("El Motor de IA no ha sido configurado por el Administrador."));
        
        // 2. EXTRACTOR DE CONTEXTO ESTÁTICO (Comandos en BD)
        List<DetalleComandoPilar> comandosRelevantes = comandoRepository.searchComandosForIA(dto.getPreguntaEstudiante());
        
        StringBuilder promptConstructor = new StringBuilder();
        
        // Inyección del System Prompt configurado por el Administrador
        promptConstructor.append("[INSTRUCCIONES DE PERSONALIDAD Y ROL]:\n")
                         .append(configIA.getSystemPrompt()).append("\n\n");
        
        // Inyección Contextual Viva (Lo que el usuario está viendo en su pantalla)
        if (dto.getSeccionActual() != null && dto.getContextoPagina() != null) {
            promptConstructor.append("[CONTEXTO VIVO DEL FRONTEND DE LA WEB]:\n")
                             .append("El usuario está actualmente en la sección: ").append(dto.getSeccionActual()).append("\n")
                             .append("Contenido de la pantalla en este momento:\n\"\"\"\n")
                             .append(dto.getContextoPagina()).append("\n\"\"\"\n\n");
        }
        
        // Inyección de Datos Técnicos Internos (Comandos/Mitigaciones)
        promptConstructor.append("[REGISTROS TÉCNICOS DEL PORTAFOLIO DE RONALDINO]:\n");
        if (comandosRelevantes.isEmpty()) {
            promptConstructor.append("No se encontraron comandos específicos ejecutados en este laboratorio para esta consulta.\n");
        } else {
            for (DetalleComandoPilar cmd : comandosRelevantes) {
                promptConstructor.append(String.format("- Herramienta: %s | Comando: %s | Mitigación: %s\n",
                        cmd.getHerramienta().getNombre(), cmd.getSintaxis(), cmd.getMitigacion()));
            }
        }
        
        String promptFinal = promptConstructor.toString();
        String respuestaGeneradaServidor;

        // 3. ORQUESTACIÓN DE LLAMADA AL LLM CORRESPONDIENTE
        try {
            if ("ollama".equalsIgnoreCase(configIA.getProveedorActivo())) {
                respuestaGeneradaServidor = llamarOllamaLocal(configIA, promptFinal, dto.getPreguntaEstudiante());
            } else {
                respuestaGeneradaServidor = llamarGoogleGemini(configIA, promptFinal, dto.getPreguntaEstudiante());
            }
        } catch (Exception e) {
            log.error("❌ Error crítico en la comunicación con el proveedor de IA: {}", e.getMessage());
            respuestaGeneradaServidor = "Lo siento, mi motor de inteligencia artificial experimentó un retraso en la respuesta. Por favor, reintenta tu consulta en un momento.";
        }
        
        // Estimación matemática de tokens tradicionales
        int tokensCalculados = (promptFinal.length() + respuestaGeneradaServidor.length()) / 4;
        
        // 4. PERSISTENCIA SEGURA UTILIZANDO EL BUILDER DE PREGUNTAIA
        PreguntaIA pregunta = PreguntaIA.builder()
                .consulta(consulta)
                .preguntaEstudiante(dto.getPreguntaEstudiante())
                .respuestaIA(respuestaGeneradaServidor)
                .tokensConsumidos(tokensCalculados)
                .fechaHora(java.time.LocalDateTime.now())
                .build();
        
        PreguntaIA guardada = preguntaRepository.save(pregunta);
        
        // 🔥 CORRECCIÓN ANTI-NPE: Registro de telemetría de tokens consumidos solo si la entidad existe físicamente en BD
        if (consulta.getId() != null) {
            consultaService.agregarTokens(consulta.getId(), tokensCalculados);
        } else {
            log.info("ℹ️ Consulta virtual en memoria detectada. Se omitió la sincronización transaccional de tokens.");
        }
        
        return convertirADTO(guardada);
    }

    /**
     * Comunicación REST nativa con tu Contenedor Local de Ollama en Parrot Linux
     */
    private String llamarOllamaLocal(IaConfig config, String systemContext, String userQuery) {
        String endpoint = config.getOllamaUrl() + "/api/generate";
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getNombreModelo()); 
        requestBody.put("prompt", systemContext + "\nPregunta del visitante: " + userQuery);
        requestBody.put("stream", false); 

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("response");
        }
        throw new RuntimeException("Ollama retornó código de estado " + response.getStatusCode());
    }

    /**
     * 🔥 INTERCEPTOR DE INFRAESTRUCTURA: Desvío estratégico de Gemini Cloud API hacia Groq Cloud API
     * Mantiene compatibilidad total con la persistencia original de la BD utilizando tokens gsk_
     */
    private String llamarGoogleGemini(IaConfig config, String systemContext, String userQuery) {
        String endpoint = "https://api.groq.com/openai/v1/chat/completions";

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemContext);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userQuery);

        Map<String, Object> requestBody = new HashMap<>();
        String modeloGroq = config.getNombreModelo().toLowerCase().contains("gemini") ? "llama3-8b-8192" : config.getNombreModelo();
        requestBody.put("model", modeloGroq);
        requestBody.put("messages", List.of(systemMessage, userMessage));
        requestBody.put("temperature", 0.5);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + config.getGeminiApiKey());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List choices = (List) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map choice = (Map) choices.get(0);
                Map message = (Map) choice.get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
        }
        throw new RuntimeException("Groq API Cloud devolvió un formato vacío o erróneo.");
    }
    
    // =========================================================================
    // 📊 NUEVO ENLACE ADICIONADO PARA RECOLECCIÓN DEL HISTORIAL COMPLETO
    // =========================================================================
    @Transactional(readOnly = true)
    public List<PreguntaIADTO> listarHistorialCompleto() {
        return preguntaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PreguntaIADTO obtenerPorId(Long id) {
        return preguntaRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new EntityNotFoundException("Registro de chat de IA no localizado con el ID " + id));
    }
    
    @Transactional(readOnly = true)
    public List<PreguntaIADTO> listarPorConsulta(Long consultaId) {
        return preguntaRepository.findByConsultaId(consultaId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PreguntaIADTO calificar(Long id, CalificacionIA calificacion) {
        PreguntaIA pregunta = preguntaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede calificar. Registro ID " + id + " no existe."));
        
        pregunta.setCalificacion(calificacion);
        return convertirADTO(preguntaRepository.save(pregunta));
    }
    
    @Transactional(readOnly = true)
    public Long obtenerCalificacionesMalas(Long pilarId, int mes, int year) {
        return preguntaRepository.countBadRatingsByPilarAndMonth(pilarId, mes, year);
    }
    
    @Transactional(readOnly = true)
    public List<PreguntaIADTO> obtenerRespuestasMalas(Long pilarId) {
        return preguntaRepository.findBadRatingsByPilar(pilarId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    
    private PreguntaIADTO convertirADTO(PreguntaIA pregunta) {
        Long idConsultaAsociada = (pregunta.getConsulta() != null) ? pregunta.getConsulta().getId() : 1L;
        
        return PreguntaIADTO.builder()
                .id(pregunta.getId())
                .consultaId(idConsultaAsociada)
                .preguntaEstudiante(pregunta.getPreguntaEstudiante())
                .respuestaIA(pregunta.getRespuestaIA())
                .tokensConsumidos(pregunta.getTokensConsumidos())
                .calificacion(pregunta.getCalificacion())
                .fechaHora(pregunta.getFechaHora())
                .build();
    }

    // 🚀 CORRECCIÓN CRÍTICA: Método de purga transaccional añadido para limpiar la tabla pregunta_ia
    @Transactional
    public void purgarTodoElHistorial() {
        preguntaRepository.deleteAll();
    }
}