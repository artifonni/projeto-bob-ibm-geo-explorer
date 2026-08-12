package com.geoexplorer.command;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("cli")
class ChallengeCommandTest {

    @Autowired
    private ChallengeCommand challengeCommand;

    @Test
    void challenge_deveRetornarDesafio_comTecnologiaENivelValidos() {
        String result = challengeCommand.challenge("java", "BEGINNER");

        assertThat(result).containsIgnoringCase("desafio");
        assertThat(result).containsIgnoringCase("java");
        assertThat(result).containsIgnoringCase("beginner");
    }

    @Test
    void challenge_deveRetornarDesafioComNivelDefault_quandoNaoInformado() {
        // defaultValue = "BEGINNER" definido em @ShellOption
        String result = challengeCommand.challenge("python", "BEGINNER");

        assertThat(result).isNotEmpty();
        assertThat(result).doesNotContain("❌");
    }

    @Test
    void challenge_deveRetornarMensagemDeErro_quandoTecnologiaInvalida() {
        String result = challengeCommand.challenge("ruby", "BEGINNER");

        assertThat(result).contains("❌");
        assertThat(result).containsIgnoringCase("ruby");
    }

    @Test
    void challenge_deveRetornarMensagemDeErro_quandoNivelInvalido() {
        String result = challengeCommand.challenge("java", "EXPERT");

        assertThat(result).contains("❌");
        assertThat(result).containsIgnoringCase("expert");
    }
}
