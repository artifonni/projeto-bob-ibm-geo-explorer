package com.geoexplorer.command;

import com.geoexplorer.domain.dto.ChallengeDTO;
import com.geoexplorer.exception.GeoExplorerException;
import com.geoexplorer.service.ChallengeService;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Locale;

@ShellComponent
@Profile("cli")
public class ChallengeCommand {

    private final ChallengeService challengeService;

    public ChallengeCommand(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @ShellMethod(key = "challenge", value = "Gera um desafio de código para uma tecnologia e nível.")
    public String challenge(
            @ShellOption(help = "Nome da tecnologia (ex.: java, python, javascript)")
            String technology,
            @ShellOption(help = "Nível do desafio: BEGINNER, INTERMEDIATE ou ADVANCED",
                         defaultValue = "BEGINNER")
            String level) {

        try {
            ChallengeDTO ch = challengeService.getChallenge(technology, level);

            return "\n🎯 Desafio: " + ch.title() + "\n"
                    + "─".repeat(56) + "\n"
                    + "Tecnologia : " + technology.toUpperCase(Locale.ROOT) + "\n"
                    + "Nível      : " + ch.level() + "\n\n"
                    + ch.description() + "\n";

        } catch (GeoExplorerException e) {
            return "❌ " + e.getMessage();
        }
    }
}
