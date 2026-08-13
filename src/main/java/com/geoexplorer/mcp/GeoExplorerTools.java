package com.geoexplorer.mcp;

import com.geoexplorer.common.AppConstants;
import com.geoexplorer.domain.dto.ChallengeDTO;
import com.geoexplorer.domain.dto.ModuleDTO;
import com.geoexplorer.domain.dto.TrailDTO;
import com.geoexplorer.exception.GeoExplorerException;
import com.geoexplorer.service.CertificateService;
import com.geoexplorer.service.ChallengeService;
import com.geoexplorer.service.TrailService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Expõe os três comandos do Geo-Explorer como tools MCP.
 * Ativa apenas no profile "mcp" — não carregada no profile "cli".
 */
@Component
@Profile("mcp")
public class GeoExplorerTools {

    private final TrailService trailService;
    private final ChallengeService challengeService;
    private final CertificateService certificateService;

    public GeoExplorerTools(TrailService trailService,
                            ChallengeService challengeService,
                            CertificateService certificateService) {
        this.trailService = trailService;
        this.challengeService = challengeService;
        this.certificateService = certificateService;
    }

    @Tool(name = "geo_trail",
          description = "Retorna o plano de estudos completo de uma tecnologia, "
                  + "listando todos os módulos em ordem com título e conteúdo. "
                  + "Tecnologias disponíveis: " + AppConstants.AVAILABLE_TECHNOLOGIES + ".")
    public String geoTrail(String technology) {
        try {
            TrailDTO trail = trailService.getTrail(technology);

            return trail.modules().stream()
                    .map(m -> String.format("Módulo %d — %s\n%s",
                            m.moduleOrder(), m.title(), m.content()))
                    .collect(Collectors.joining("\n\n"));

        } catch (GeoExplorerException e) {
            return "Erro: " + e.getMessage();
        }
    }

    @Tool(name = "geo_challenge",
          description = "Gera um desafio de código aleatório para uma tecnologia e nível informados. "
                  + "Tecnologias disponíveis: " + AppConstants.AVAILABLE_TECHNOLOGIES + ". "
                  + "Níveis válidos: " + AppConstants.VALID_LEVELS + ".")
    public String geoChallenge(String technology, String level) {
        try {
            ChallengeDTO ch = challengeService.getChallenge(technology, level);

            return String.format("Desafio: %s\nTecnologia: %s\nNível: %s\n\n%s",
                    ch.title(),
                    technology.toUpperCase(Locale.ROOT),
                    ch.level(),
                    ch.description());

        } catch (GeoExplorerException e) {
            return "Erro: " + e.getMessage();
        }
    }

    @Tool(name = "geo_certificate",
          description = "Emite um certificado fictício para o usuário informado, "
                  + "referente à trilha de estudos da tecnologia escolhida. "
                  + "Tecnologias disponíveis: " + AppConstants.AVAILABLE_TECHNOLOGIES + ".")
    public String geoCertificate(String technology, String userName) {
        try {
            return certificateService.generateCertificate(technology, userName);
        } catch (GeoExplorerException e) {
            return "Erro: " + e.getMessage();
        }
    }
}
