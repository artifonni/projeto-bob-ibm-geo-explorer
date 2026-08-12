package com.geoexplorer.service;

import com.geoexplorer.domain.model.Module;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.ModuleRepository;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrailService {

    private final TrailRepository trailRepository;
    private final ModuleRepository moduleRepository;

    public TrailService(TrailRepository trailRepository, ModuleRepository moduleRepository) {
        this.trailRepository = trailRepository;
        this.moduleRepository = moduleRepository;
    }

    /**
     * Retorna os módulos de uma trilha em ordem, dado o nome da tecnologia.
     *
     * @param technology nome da tecnologia (ex.: "java", "python")
     * @return lista de módulos ordenada por moduleOrder
     * @throws ResourceNotFoundException se a tecnologia não existir no banco
     */
    @Transactional(readOnly = true)
    public List<Module> getTrail(String technology) {
        Trail trail = trailRepository.findByTechnologyIgnoreCase(technology)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trilha não encontrada para a tecnologia: " + technology));

        return moduleRepository.findByTrailOrderByModuleOrderAsc(trail);
    }

    /**
     * Retorna a entidade Trail completa dado o nome da tecnologia.
     */
    @Transactional(readOnly = true)
    public Trail getTrailEntity(String technology) {
        return trailRepository.findByTechnologyIgnoreCase(technology)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trilha não encontrada para a tecnologia: " + technology));
    }
}
