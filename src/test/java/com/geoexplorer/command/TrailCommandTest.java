package com.geoexplorer.command;

import com.geoexplorer.service.TrailService;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("cli")
class TrailCommandTest {

    @Autowired
    private TrailCommand trailCommand;

    @Autowired
    private TrailService trailService;

    @Test
    void trail_deveRetornarConteudoComTecnologiaValida() {
        String result = trailCommand.trail("java");

        assertThat(result).containsIgnoringCase("java");
        assertThat(result).contains("1.");
    }

    @Test
    void trail_deveRetornarMensagemDeErro_quandoTecnologiaInvalida() {
        String result = trailCommand.trail("cobol");

        assertThat(result).contains("❌");
        assertThat(result).containsIgnoringCase("cobol");
    }

    @Test
    void trail_deveRetornarModulosParaPython() {
        String result = trailCommand.trail("python");

        assertThat(result).containsIgnoringCase("python");
        assertThat(result).isNotEmpty();
    }
}
