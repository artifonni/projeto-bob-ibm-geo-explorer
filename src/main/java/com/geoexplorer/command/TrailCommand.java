package com.geoexplorer.command;

import com.geoexplorer.domain.model.Module;
import com.geoexplorer.exception.ResourceNotFoundException;
import com.geoexplorer.service.TrailService;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
@Profile("cli")
public class TrailCommand {

    private final TrailService trailService;

    public TrailCommand(TrailService trailService) {
        this.trailService = trailService;
    }

    @ShellMethod(key = "trail", value = "Exibe o plano de estudos de uma tecnologia.")
    public String trail(
            @ShellOption(help = "Nome da tecnologia (ex.: java, python, javascript)")
            String technology) {

        try {
            List<Module> modules = trailService.getTrail(technology);

            StringBuilder sb = new StringBuilder();
            sb.append("\n📚 Trilha de ").append(technology.toUpperCase()).append("\n");
            sb.append("─".repeat(56)).append("\n");

            for (Module module : modules) {
                sb.append(String.format("%d. %s%n", module.getModuleOrder(), module.getTitle()));
                sb.append("   ").append(module.getContent()).append("\n\n");
            }

            return sb.toString().stripTrailing();

        } catch (ResourceNotFoundException e) {
            return "❌ " + e.getMessage()
                    + "\n   Tecnologias disponíveis: java, python, javascript";
        }
    }
}
