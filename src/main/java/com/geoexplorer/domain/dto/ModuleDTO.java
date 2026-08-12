package com.geoexplorer.domain.dto;

/**
 * DTO de saída para um módulo de estudo.
 *
 * @param moduleOrder posição do módulo na trilha
 * @param title título do módulo
 * @param content conteúdo descritivo do módulo
 */
public record ModuleDTO(
        Integer moduleOrder,
        String title,
        String content
) {}
