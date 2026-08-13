package com.geoexplorer.common;

/**
 * Constantes compartilhadas da aplicação.
 */
public final class AppConstants {

    /**
     * Tecnologias disponíveis no seed, usadas nas mensagens de erro e nas
     * descrições das tools MCP. Fonte única para evitar drift entre o seed
     * e as mensagens exibidas ao usuário.
     */
    public static final String AVAILABLE_TECHNOLOGIES = "java, python, javascript";

    /** Níveis de dificuldade válidos, usados nas mensagens de erro. */
    public static final String VALID_LEVELS = "BEGINNER, INTERMEDIATE, ADVANCED";

    private AppConstants() {
    }
}
