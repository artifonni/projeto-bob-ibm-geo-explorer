package com.geoexplorer.service;

import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private TrailRepository trailRepository;

    @InjectMocks
    private CertificateService certificateService;

    @Test
    void generateCertificate_deveConterNomeDoUsuario() {
        Trail trail = new Trail("javascript", "Trilha de JavaScript", Level.BEGINNER);

        when(trailRepository.findByTechnologyIgnoreCase("javascript"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("javascript", "Ana Lima");

        assertThat(cert).contains("Ana Lima");
    }

    @Test
    void generateCertificate_deveConterNomeDaTecnologia() {
        Trail trail = new Trail("javascript", "Trilha de JavaScript", Level.BEGINNER);

        when(trailRepository.findByTechnologyIgnoreCase("javascript"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("javascript", "Ana Lima");

        assertThat(cert).containsIgnoringCase("javascript");
    }

    @Test
    void generateCertificate_deveConterCabecalhoCertificado() {
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);

        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", "Carlos Silva");

        assertThat(cert).contains("GEO-EXPLORER");
        assertThat(cert).contains("CERTIFICADO");
    }

    @Test
    void generateCertificate_deveLancarException_quandoTecnologiaNaoExiste() {
        when(trailRepository.findByTechnologyIgnoreCase("golang"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificateService.generateCertificate("golang", "Maria"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("golang");
    }
}
