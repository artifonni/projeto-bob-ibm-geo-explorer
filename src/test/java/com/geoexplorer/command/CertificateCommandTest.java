package com.geoexplorer.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@ActiveProfiles("cli")
class CertificateCommandTest {

    @Autowired
    private CertificateCommand certificateCommand;

    @Test
    void certificate_deveConterNomeDoUsuario() {
        String result = certificateCommand.certificate("java", "Ana Lima");

        assertThat(result).contains("Ana Lima");
    }

    @Test
    void certificate_deveConterCabecalhoFormatado() {
        String result = certificateCommand.certificate("python", "Carlos Silva");

        assertThat(result).contains("GEO-EXPLORER");
        assertThat(result).contains("CERTIFICADO");
        assertThat(result).contains("Carlos Silva");
    }

    @Test
    void certificate_deveRetornarMensagemDeErro_quandoTecnologiaInvalida() {
        String result = certificateCommand.certificate("golang", "Maria");

        assertThat(result).contains("❌");
        assertThat(result).containsIgnoringCase("golang");
    }

    @Test
    void certificate_deveRetornarMensagemDeErro_quandoTecnologiaNaoInformada() {
        String result = certificateCommand.certificate("   ", "Maria");

        assertThat(result).contains("❌");
        assertThat(result).contains("Tecnologia");
    }

    @Test
    void certificate_deveConterNomeDaTecnologia() {
        String result = certificateCommand.certificate("javascript", "João");

        assertThat(result).containsIgnoringCase("javascript");
    }
}
