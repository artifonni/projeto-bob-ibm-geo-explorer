package com.geoexplorer.service;

import com.geoexplorer.domain.dto.ModuleDTO;
import com.geoexplorer.domain.dto.TrailDTO;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrailService {

    private final TrailRepository trailRepository;

    public TrailService(TrailRepository trailRepository) {
        this.trailRepository = trailRepository;
    }

    /**
     * Retorna os módulos de uma trilha em ordem, dado o nome da tecnologia.
     *
     * @param technology nome da tecnologia (ex.: "java", "python")
     * @return {@link TrailDTO} com lista de módulos ordenada por moduleOrder
     * @throws ResourceNotFoundException se a tecnologia não existir no banco
     */
    @Transactional(readOnly = true)
    public TrailDTO getTrail(String technology) {
        Trail trail = trailRepository.findByTechnologyIgnoreCase(technology)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trilha não encontrada para a tecnologia: " + technology));

        List<ModuleDTO> moduleDTOs = trail.getModules().stream()
                .sorted(java.util.Comparator.comparingInt(m -> m.getModuleOrder()))
                .map(m -> new ModuleDTO(m.getModuleOrder(), m.getTitle(), m.getContent()))
                .toList();

        return new TrailDTO(
                trail.getTechnology(),
                trail.getDescription(),
                trail.getLevel(),
                moduleDTOs
        );
    }
}
