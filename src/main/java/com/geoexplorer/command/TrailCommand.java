package com.geoexplorer.command;

import com.geoexplorer.common.AppConstants;
import com.geoexplorer.domain.dto.ModuleDTO;
import com.geoexplorer.domain.dto.TrailDTO;
import com.geoexplorer.exception.GeoExplorerException;
import com.geoexplorer.exception.ResourceNotFoundException;
import com.geoexplorer.service.TrailService;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Locale;

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
            TrailDTO trail = trailService.getTrail(technology);

            StringBuilder sb = new StringBuilder();
            sb.append("\n📚 Trilha de ").append(technology.toUpperCase(Locale.ROOT)).append("\n");
            sb.append("─".repeat(56)).append("\n");

            for (ModuleDTO module : trail.modules()) {
                sb.append(String.format("%d. %s%n", module.moduleOrder(), module.title()));
                sb.append("   ").append(module.content()).append("\n\n");
            }

            return sb.toString().stripTrailing();

        } catch (ResourceNotFoundException e) {
            return "❌ " + e.getMessage()
                    + "\n   Tecnologias disponíveis: " + AppConstants.AVAILABLE_TECHNOLOGIES;
        } catch (GeoExplorerException e) {
            return "❌ " + e.getMessage();
        }
    }
}
