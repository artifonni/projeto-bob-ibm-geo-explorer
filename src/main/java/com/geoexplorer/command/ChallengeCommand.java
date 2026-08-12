package com.geoexplorer.command;

import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.exception.ResourceNotFoundException;
import com.geoexplorer.service.ChallengeService;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

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
            Challenge ch = challengeService.getChallenge(technology, level);

            return "\n🎯 Desafio: " + ch.getTitle() + "\n"
                    + "─".repeat(56) + "\n"
                    + "Tecnologia : " + technology.toUpperCase() + "\n"
                    + "Nível      : " + ch.getLevel() + "\n\n"
                    + ch.getDescription() + "\n";

        } catch (ResourceNotFoundException e) {
            return "❌ " + e.getMessage()
                    + "\n   Níveis válidos: BEGINNER, INTERMEDIATE, ADVANCED";
        }
    }
}
