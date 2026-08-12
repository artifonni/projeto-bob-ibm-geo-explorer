package com.geoexplorer.mcp;

import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.domain.model.Module;
import com.geoexplorer.exception.ResourceNotFoundException;
import com.geoexplorer.service.CertificateService;
import com.geoexplorer.service.ChallengeService;
import com.geoexplorer.service.TrailService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Expõe os três comandos do Geo-Explorer como tools MCP.
 *
 * Esta classe contém os métodos anotados com {@code @Tool} que o
 * {@code MethodToolCallbackProvider} escaneia e registra no servidor MCP.
 *
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

    /**
     * Tool MCP: retorna o plano de estudos de uma tecnologia.
     *
     * @param technology nome da tecnologia (ex.: java, python, javascript)
     * @return plano de estudos formatado em texto
     */
    @Tool(name = "geo_trail",
          description = "Retorna o plano de estudos completo de uma tecnologia, "
                  + "listando todos os módulos em ordem com título e conteúdo. "
                  + "Tecnologias disponíveis: java, python, javascript.")
    public String geoTrail(String technology) {
        try {
            List<Module> modules = trailService.getTrail(technology);

            return modules.stream()
                    .map(m -> String.format("Módulo %d — %s\n%s",
                            m.getModuleOrder(), m.getTitle(), m.getContent()))
                    .collect(Collectors.joining("\n\n"));

        } catch (ResourceNotFoundException e) {
            return "Erro: " + e.getMessage();
        }
    }

    /**
     * Tool MCP: gera um desafio de código para uma tecnologia e nível.
     *
     * @param technology nome da tecnologia (ex.: java, python, javascript)
     * @param level      nível do desafio: BEGINNER, INTERMEDIATE ou ADVANCED
     * @return desafio de código formatado em texto
     */
    @Tool(name = "geo_challenge",
          description = "Gera um desafio de código aleatório para uma tecnologia e nível informados. "
                  + "Tecnologias disponíveis: java, python, javascript. "
                  + "Níveis válidos: BEGINNER, INTERMEDIATE, ADVANCED.")
    public String geoChallenge(String technology, String level) {
        try {
            Challenge ch = challengeService.getChallenge(technology, level);

            return String.format("Desafio: %s\nTecnologia: %s\nNível: %s\n\n%s",
                    ch.getTitle(),
                    technology.toUpperCase(),
                    ch.getLevel(),
                    ch.getDescription());

        } catch (ResourceNotFoundException e) {
            return "Erro: " + e.getMessage();
        }
    }

    /**
     * Tool MCP: emite um certificado fictício para uma trilha concluída.
     *
     * @param technology nome da tecnologia (ex.: java, python, javascript)
     * @param userName   nome completo do usuário para constar no certificado
     * @return certificado fictício formatado em texto
     */
    @Tool(name = "geo_certificate",
          description = "Emite um certificado fictício para o usuário informado, "
                  + "referente à trilha de estudos da tecnologia escolhida. "
                  + "Tecnologias disponíveis: java, python, javascript.")
    public String geoCertificate(String technology, String userName) {
        try {
            return certificateService.generateCertificate(technology, userName);
        } catch (ResourceNotFoundException e) {
            return "Erro: " + e.getMessage();
        }
    }
}
