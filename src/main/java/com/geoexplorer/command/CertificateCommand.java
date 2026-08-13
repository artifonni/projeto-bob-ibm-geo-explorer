package com.geoexplorer.command;

import com.geoexplorer.common.AppConstants;
import com.geoexplorer.exception.GeoExplorerException;
import com.geoexplorer.exception.ResourceNotFoundException;
import com.geoexplorer.service.CertificateService;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@Profile("cli")
public class CertificateCommand {

    private final CertificateService certificateService;

    public CertificateCommand(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @ShellMethod(key = "certificate", value = "Emite um certificado fictício para uma trilha concluída.")
    public String certificate(
            @ShellOption(help = "Nome da tecnologia (ex.: java, python, javascript)")
            String technology,
            @ShellOption(help = "Nome completo do usuário para constar no certificado")
            String user) {

        try {
            return "\n" + certificateService.generateCertificate(technology, user);
        } catch (ResourceNotFoundException e) {
            return "❌ " + e.getMessage()
                    + "\n   Tecnologias disponíveis: " + AppConstants.AVAILABLE_TECHNOLOGIES;
        } catch (GeoExplorerException e) {
            return "❌ " + e.getMessage();
        }
    }
}
