package com.geoexplorer.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityModelTest {

    @Test
    void trail_deveSuportarConstrucaoViaConstrutorESetters() {
        Trail trail = new Trail();
        trail.setTechnology("python");
        trail.setDescription("Trilha de Python");
        trail.setLevel(Level.INTERMEDIATE);

        assertThat(trail.getTechnology()).isEqualTo("python");
        assertThat(trail.getDescription()).isEqualTo("Trilha de Python");
        assertThat(trail.getLevel()).isEqualTo(Level.INTERMEDIATE);
        assertThat(trail.getModules()).isEmpty();
        assertThat(trail.getChallenges()).isEmpty();
    }

    @Test
    void module_deveSuportarConstrucaoComBackReferenceParaTrail() {
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);
        Module module = new Module("Fundamentos", "Sintaxe básica", 1, trail);

        assertThat(module.getTitle()).isEqualTo("Fundamentos");
        assertThat(module.getContent()).isEqualTo("Sintaxe básica");
        assertThat(module.getModuleOrder()).isEqualTo(1);
        assertThat(module.getTrail()).isSameAs(trail);

        module.setTitle("Novo Título");
        module.setContent("Novo conteúdo");
        module.setModuleOrder(2);
        assertThat(module.getTitle()).isEqualTo("Novo Título");
        assertThat(module.getContent()).isEqualTo("Novo conteúdo");
        assertThat(module.getModuleOrder()).isEqualTo(2);
    }

    @Test
    void challenge_deveSuportarConstrucaoComBackReferenceParaTrail() {
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);
        Challenge challenge = new Challenge("Desafio 1", "Resolva o problema", Level.ADVANCED, trail);

        assertThat(challenge.getTitle()).isEqualTo("Desafio 1");
        assertThat(challenge.getDescription()).isEqualTo("Resolva o problema");
        assertThat(challenge.getLevel()).isEqualTo(Level.ADVANCED);
        assertThat(challenge.getTrail()).isSameAs(trail);

        challenge.setTitle("Novo Desafio");
        challenge.setDescription("Nova descrição");
        challenge.setLevel(Level.INTERMEDIATE);
        assertThat(challenge.getTitle()).isEqualTo("Novo Desafio");
        assertThat(challenge.getDescription()).isEqualTo("Nova descrição");
        assertThat(challenge.getLevel()).isEqualTo(Level.INTERMEDIATE);
    }

    @Test
    void level_deveConterOsTresNiveisEsperados() {
        assertThat(Level.values()).containsExactly(
                Level.BEGINNER, Level.INTERMEDIATE, Level.ADVANCED);
    }
}
