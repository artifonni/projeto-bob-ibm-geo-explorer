package com.geoexplorer.domain.dto;

import com.geoexplorer.domain.model.Level;

/**
 * DTO de saída para um desafio de código.
 *
 * @param title título do desafio
 * @param description enunciado do desafio
 * @param level nível de dificuldade
 */
public record ChallengeDTO(
        String title,
        String description,
        Level level
) {}
