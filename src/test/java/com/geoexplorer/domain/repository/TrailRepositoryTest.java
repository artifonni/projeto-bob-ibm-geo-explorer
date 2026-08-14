package com.geoexplorer.domain.repository;

import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Module;
import com.geoexplorer.domain.model.Trail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de persistência: validam que a migration Flyway V1 cria o schema e
 * popula o seed, e que as consultas derivadas dos repositórios funcionam
 * contra o banco real (H2 em memória).
 */
@DataJpaTest
class TrailRepositoryTest {

    @Autowired
    private TrailRepository trailRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Test
    void flywayMigration_devePopularSchemaESeed() {
        assertThat(trailRepository.count()).isEqualTo(3);
        assertThat(challengeRepository.count()).isEqualTo(27);
    }

    @Test
    void findByTechnologyIgnoreCase_deveRetornarTrailCaseInsensitive() {
        Optional<Trail> result = trailRepository.findByTechnologyIgnoreCase("JAVA");

        assertThat(result).isPresent();
        assertThat(result.get().getTechnology()).isEqualTo("java");
    }

    @Test
    void findByTechnologyIgnoreCase_deveRetornarVazio_quandoTecnologiaNaoExiste() {
        assertThat(trailRepository.findByTechnologyIgnoreCase("cobol")).isEmpty();
    }

    @Test
    void findByTechnologyIgnoreCaseWithModules_deveCarregarModulos() {
        Trail trail = trailRepository.findByTechnologyIgnoreCaseWithModules("python").orElseThrow();

        assertThat(trail.getModules()).hasSize(3);
        assertThat(trail.getModules())
                .extracting(Module::getModuleOrder)
                .containsExactlyInAnyOrder(1, 2, 3);
        assertThat(trail.getModules())
                .extracting(Module::getTitle)
                .containsExactlyInAnyOrder("Python Básico e Ambiente", "Estruturas de Dados Nativas",
                        "Funções e Módulos");
    }

    @Test
    void findByTrailAndLevel_deveRetornarDesafiosDoNivel() {
        Trail trail = trailRepository.findByTechnologyIgnoreCase("java").orElseThrow();

        assertThat(challengeRepository.findByTrailAndLevel(trail, Level.BEGINNER))
                .hasSize(3)
                .extracting(Challenge::getTitle)
                .contains("FizzBuzz Clássico");
        assertThat(challengeRepository.findByTrailAndLevel(trail, Level.ADVANCED))
                .hasSize(3)
                .extracting(Challenge::getTitle)
                .contains("Pilha Genérica com Generics");
    }

    @Test
    void findByTrailAndLevel_deveRetornarVazio_quandoSemDesafiosNoNivel() {
        Trail newTrail = trailRepository.save(new Trail("kotlin", "Trilha de Kotlin", Level.BEGINNER));

        assertThat(challengeRepository.findByTrailAndLevel(newTrail, Level.ADVANCED)).isEmpty();
    }
}
