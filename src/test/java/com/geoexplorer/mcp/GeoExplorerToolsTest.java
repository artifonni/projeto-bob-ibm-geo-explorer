package com.geoexplorer.mcp;

import com.geoexplorer.domain.dto.ChallengeDTO;
import com.geoexplorer.domain.dto.ModuleDTO;
import com.geoexplorer.domain.dto.TrailDTO;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.exception.InvalidLevelException;
import com.geoexplorer.exception.ResourceNotFoundException;
import com.geoexplorer.service.CertificateService;
import com.geoexplorer.service.ChallengeService;
import com.geoexplorer.service.TrailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoExplorerToolsTest {

    @Mock
    private TrailService trailService;

    @Mock
    private ChallengeService challengeService;

    @Mock
    private CertificateService certificateService;

    @InjectMocks
    private GeoExplorerTools tools;

    @Test
    void geoTrail_deveListarModulosEmOrdem() {
        TrailDTO trail = new TrailDTO("java", "Trilha de Java", Level.BEGINNER,
                List.of(new ModuleDTO(1, "Intro", "Conteúdo 1"),
                        new ModuleDTO(2, "OO", "Conteúdo 2")));

        when(trailService.getTrail("java")).thenReturn(trail);

        String out = tools.geoTrail("java");

        assertThat(out).contains("Módulo 1 — Intro\nConteúdo 1");
        assertThat(out).contains("Módulo 2 — OO\nConteúdo 2");
    }

    @Test
    void geoTrail_deveRetornarErro_quandoTecnologiaNaoExiste() {
        when(trailService.getTrail("cobol"))
                .thenThrow(new ResourceNotFoundException("Trilha não encontrada para a tecnologia: cobol"));

        String out = tools.geoTrail("cobol");

        assertThat(out).startsWith("Erro:");
        assertThat(out).contains("cobol");
    }

    @Test
    void geoChallenge_deveRetornarDesafioComNivel() {
        when(challengeService.getChallenge("python", "BEGINNER"))
                .thenReturn(new ChallengeDTO("FizzBuzz", "Implemente FizzBuzz.", Level.BEGINNER));

        String out = tools.geoChallenge("python", "BEGINNER");

        assertThat(out).contains("Desafio: FizzBuzz");
        assertThat(out).contains("Tecnologia: PYTHON");
        assertThat(out).contains("Nível: BEGINNER");
        assertThat(out).contains("Implemente FizzBuzz.");
    }

    @Test
    void geoChallenge_deveRetornarErro_quandoNivelInvalido() {
        when(challengeService.getChallenge("python", "EXPERT"))
                .thenThrow(new InvalidLevelException("Nível inválido: 'EXPERT'."));

        String out = tools.geoChallenge("python", "EXPERT");

        assertThat(out).startsWith("Erro:");
        assertThat(out).contains("EXPERT");
    }

    @Test
    void geoCertificate_deveRetornarCertificado() {
        when(certificateService.generateCertificate("java", "Ana Lima"))
                .thenReturn("GEO-EXPLORER — CERTIFICADO\nAna Lima");

        String out = tools.geoCertificate("java", "Ana Lima");

        assertThat(out).contains("CERTIFICADO");
        assertThat(out).contains("Ana Lima");
    }

    @Test
    void geoCertificate_deveRetornarErro_quandoTecnologiaNaoExiste() {
        when(certificateService.generateCertificate("golang", "Maria"))
                .thenThrow(new ResourceNotFoundException("Trilha não encontrada para a tecnologia: golang"));

        String out = tools.geoCertificate("golang", "Maria");

        assertThat(out).startsWith("Erro:");
        assertThat(out).contains("golang");
    }
}
