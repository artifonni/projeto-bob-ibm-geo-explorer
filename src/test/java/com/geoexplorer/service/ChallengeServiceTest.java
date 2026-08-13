package com.geoexplorer.service;

import com.geoexplorer.domain.dto.ChallengeDTO;
import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.ChallengeRepository;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.InvalidInputException;
import com.geoexplorer.exception.InvalidLevelException;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private TrailRepository trailRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private Trail pythonTrail;

    @BeforeEach
    void setUp() {
        pythonTrail = new Trail("python", "Trilha de Python", Level.BEGINNER);
    }

    @Test
    void getChallenge_deveRetornarDTO_quandoExistentes() {
        Challenge ch = new Challenge("FizzBuzz", "Implemente FizzBuzz.", Level.BEGINNER, pythonTrail);

        when(trailRepository.findByTechnologyIgnoreCase("python"))
                .thenReturn(Optional.of(pythonTrail));
        when(challengeRepository.findByTrailAndLevel(pythonTrail, Level.BEGINNER))
                .thenReturn(List.of(ch));

        ChallengeDTO result = challengeService.getChallenge("python", "BEGINNER");

        assertThat(result.title()).isEqualTo("FizzBuzz");
        assertThat(result.level()).isEqualTo(Level.BEGINNER);
    }

    @Test
    void getChallenge_deveAceitarLevelEmMinusculo() {
        Challenge ch = new Challenge("Decorador", "Implemente decorador.", Level.INTERMEDIATE, pythonTrail);

        when(trailRepository.findByTechnologyIgnoreCase("python"))
                .thenReturn(Optional.of(pythonTrail));
        when(challengeRepository.findByTrailAndLevel(pythonTrail, Level.INTERMEDIATE))
                .thenReturn(List.of(ch));

        ChallengeDTO result = challengeService.getChallenge("python", "intermediate");

        assertThat(result.level()).isEqualTo(Level.INTERMEDIATE);
    }

    @Test
    void getChallenge_deveAceitarLevelComEspacos() {
        Challenge ch = new Challenge("Decorador", "Implemente decorador.", Level.INTERMEDIATE, pythonTrail);

        when(trailRepository.findByTechnologyIgnoreCase("python"))
                .thenReturn(Optional.of(pythonTrail));
        when(challengeRepository.findByTrailAndLevel(pythonTrail, Level.INTERMEDIATE))
                .thenReturn(List.of(ch));

        ChallengeDTO result = challengeService.getChallenge("python", "  Intermediate  ");

        assertThat(result.level()).isEqualTo(Level.INTERMEDIATE);
    }

    @Test
    void getChallenge_deveRetornarDesafioDeQualquerCandidato_quandoExisteMaisDeUm() {
        Challenge c1 = new Challenge("Desafio 1", "Desc 1", Level.BEGINNER, pythonTrail);
        Challenge c2 = new Challenge("Desafio 2", "Desc 2", Level.BEGINNER, pythonTrail);

        when(trailRepository.findByTechnologyIgnoreCase("python"))
                .thenReturn(Optional.of(pythonTrail));
        when(challengeRepository.findByTrailAndLevel(pythonTrail, Level.BEGINNER))
                .thenReturn(List.of(c1, c2));

        ChallengeDTO result = challengeService.getChallenge("python", "BEGINNER");

        assertThat(result.title()).isIn("Desafio 1", "Desafio 2");
    }

    @Test
    void getChallenge_deveLancarException_quandoTecnologiaNaoExiste() {
        when(trailRepository.findByTechnologyIgnoreCase("ruby"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> challengeService.getChallenge("ruby", "BEGINNER"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ruby");
    }

    @Test
    void getChallenge_deveLancarException_quandoNenhumDesafioNoNivel() {
        when(trailRepository.findByTechnologyIgnoreCase("python"))
                .thenReturn(Optional.of(pythonTrail));
        when(challengeRepository.findByTrailAndLevel(pythonTrail, Level.ADVANCED))
                .thenReturn(List.of());

        assertThatThrownBy(() -> challengeService.getChallenge("python", "ADVANCED"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ADVANCED");
    }

    @Test
    void getChallenge_deveLancarInvalidLevelException_quandoNivelInvalido() {
        assertThatThrownBy(() -> challengeService.getChallenge("python", "EXPERT"))
                .isInstanceOf(InvalidLevelException.class)
                .hasMessageContaining("EXPERT");
    }

    @Test
    void getChallenge_deveLancarInvalidLevelException_quandoNivelNaoInformado() {
        assertThatThrownBy(() -> challengeService.getChallenge("python", null))
                .isInstanceOf(InvalidLevelException.class);
        assertThatThrownBy(() -> challengeService.getChallenge("python", "  "))
                .isInstanceOf(InvalidLevelException.class);
    }

    @Test
    void getChallenge_deveLancarInvalidInputException_quandoTecnologiaNaoInformada() {
        assertThatThrownBy(() -> challengeService.getChallenge(null, "BEGINNER"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Tecnologia");
        assertThatThrownBy(() -> challengeService.getChallenge("   ", "BEGINNER"))
                .isInstanceOf(InvalidInputException.class);
    }
}
