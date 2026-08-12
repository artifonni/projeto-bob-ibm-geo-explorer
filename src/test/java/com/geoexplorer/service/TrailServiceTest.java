package com.geoexplorer.service;

import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Module;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.ModuleRepository;
import com.geoexplorer.domain.repository.TrailRepository;
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

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private TrailService trailService;

    private Trail javaTrail;

    @BeforeEach
    void setUp() {
        javaTrail = new Trail("java", "Trilha de Java", Level.BEGINNER);
    }

    @Test
    void getTrail_deveRetornarModulosOrdenados_quandoTecnologiaExiste() {
        Module m1 = new Module("Intro", "Conteúdo 1", 1, javaTrail);
        Module m2 = new Module("OO", "Conteúdo 2", 2, javaTrail);

        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(javaTrail));
        when(moduleRepository.findByTrailOrderByModuleOrderAsc(javaTrail))
                .thenReturn(List.of(m1, m2));

        List<Module> result = trailService.getTrail("java");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Intro");
        assertThat(result.get(1).getTitle()).isEqualTo("OO");
    }

    @Test
    void getTrail_deveLancarResourceNotFoundException_quandoTecnologiaNaoExiste() {
        when(trailRepository.findByTechnologyIgnoreCase("cobol"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> trailService.getTrail("cobol"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("cobol");
    }

    @Test
    void getTrail_deveSerCaseInsensitive() {
        when(trailRepository.findByTechnologyIgnoreCase("JAVA"))
                .thenReturn(Optional.of(javaTrail));
        when(moduleRepository.findByTrailOrderByModuleOrderAsc(javaTrail))
                .thenReturn(List.of());

        assertThat(trailService.getTrail("JAVA")).isEmpty();
    }
}
