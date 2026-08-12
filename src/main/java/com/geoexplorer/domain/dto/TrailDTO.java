package com.geoexplorer.domain.dto;

import com.geoexplorer.domain.model.Level;

import java.util.List;

/**
 * DTO de saída para uma trilha de estudos completa.
 *
 * @param technology nome da tecnologia
 * @param description descrição da trilha
 * @param level nível da trilha
 * @param modules lista de módulos ordenados
 */
public record TrailDTO(
        String technology,
        String description,
        Level level,
        List<ModuleDTO> modules
) {}
