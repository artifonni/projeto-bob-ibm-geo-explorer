package com.geoexplorer.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de fumaça do profile "mcp".
 *
 * Verifica que:
 * - O contexto Spring sobe sem erros com o profile "mcp".
 * - O ToolCallbackProvider está registrado no contexto.
 * - As três tools (geo_trail, geo_challenge, geo_certificate) estão presentes.
 * - Nenhum componente do pacote "command" (Spring Shell) é carregado.
 */
@SpringBootTest
@ActiveProfiles("mcp")
class McpToolsConfigTest {

    @Autowired
    private ToolCallbackProvider geoExplorerToolCallbackProvider;

    @Autowired(required = false)
    private com.geoexplorer.command.TrailCommand trailCommand;

    @Test
    void toolCallbackProvider_deveEstarRegistradoNoContexto() {
        assertThat(geoExplorerToolCallbackProvider).isNotNull();
    }

    @Test
    void toolCallbackProvider_deveConterAsTresTool() {
        ToolCallback[] callbacks = geoExplorerToolCallbackProvider.getToolCallbacks();
        List<String> toolNames = Arrays.stream(callbacks)
                .map(tc -> tc.getToolDefinition().name())
                .toList();

        assertThat(toolNames)
                .containsExactlyInAnyOrder("geo_trail", "geo_challenge", "geo_certificate");
    }

    @Test
    void perfilMcp_naoDeveCarregarComponentesDoShell() {
        // TrailCommand tem @Profile("cli") — no profile "mcp" deve ser null
        assertThat(trailCommand).isNull();
    }
}
