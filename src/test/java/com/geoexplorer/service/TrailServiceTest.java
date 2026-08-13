package com.geoexplorer.service;

import com.geoexplorer.domain.dto.ModuleDTO;
import com.geoexplorer.domain.dto.TrailDTO;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Module;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.InvalidInputException;
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
class TrailServiceTest {

    @Mock
    private TrailRepository trailRepository;

    @InjectMocks
    private TrailService trailService;

    private Trail javaTrail;

    @BeforeEach
    void setUp() {
        javaTrail = new Trail("java", "Trilha de Java", Level.BEGINNER);
    }

    @Test
    void getTrail_deveRetornarDTOComModulosOrdenados_quandoTecnologiaExiste() {
        Module m1 = new Module("Intro", "Conteúdo 1", 1, javaTrail);
        Module m2 = new Module("OO", "Conteúdo 2", 2, javaTrail);
        javaTrail.getModules().addAll(List.of(m2, m1));

        when(trailRepository.findByTechnologyIgnoreCaseWithModules("java"))
                .thenReturn(Optional.of(javaTrail));

        TrailDTO result = trailService.getTrail("java");

        assertThat(result.technology()).isEqualTo("java");
        assertThat(result.modules()).hasSize(2);
        assertThat(result.modules().get(0).title()).isEqualTo("Intro");
        assertThat(result.modules().get(1).title()).isEqualTo("OO");
    }

    @Test
    void getTrail_deveLancarResourceNotFoundException_quandoTecnologiaNaoExiste() {
        when(trailRepository.findByTechnologyIgnoreCaseWithModules("cobol"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trailService.getTrail("cobol"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("cobol");
    }

    @Test
    void getTrail_deveRetornarListaVazia_quandoSemModulos() {
        Trail emptyTrail = new Trail("JAVA", "desc", Level.BEGINNER);
        when(trailRepository.findByTechnologyIgnoreCaseWithModules("JAVA"))
                .thenReturn(Optional.of(emptyTrail));

        TrailDTO result = trailService.getTrail("JAVA");
        assertThat(result.modules()).isEmpty();
    }

    @Test
    void getTrail_deveLancarInvalidInputException_quandoTecnologiaNaoInformada() {
        assertThatThrownBy(() -> trailService.getTrail(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Tecnologia");
        assertThatThrownBy(() -> trailService.getTrail("   "))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void getTrail_deveRetornarDTOComDadosDaTrilha() {
        when(trailRepository.findByTechnologyIgnoreCaseWithModules("python"))
                .thenReturn(Optional.of(javaTrail));

        TrailDTO result = trailService.getTrail("python");

        assertThat(result.description()).isEqualTo("Trilha de Java");
        assertThat(result.level()).isEqualTo(Level.BEGINNER);
    }
}
