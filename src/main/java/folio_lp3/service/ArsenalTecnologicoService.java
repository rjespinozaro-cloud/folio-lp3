// Archivo: src/main/java/folio_lp3/service/ArsenalTecnologicoService.java
package folio_lp3.service;

import folio_lp3.dto.ArsenalRequestDTO;
import folio_lp3.entity.ArsenalTecnologico;
import folio_lp3.repository.ArsenalTecnologicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArsenalTecnologicoService {

    private final ArsenalTecnologicoRepository arsenalRepository;

    public List<ArsenalTecnologico> listarTodo() {
        return arsenalRepository.findAll();
    }

    @Transactional
    public ArsenalTecnologico guardarOActualizar(ArsenalRequestDTO dto) {
        // Lógica para actualizar si ya existe, o crear uno nuevo
        return arsenalRepository.save(ArsenalTecnologico.builder()
                .tecnologia(dto.getTecnologia())
                .porcentaje(dto.getPorcentaje())
                .build());
    }
}