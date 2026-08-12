package com.geoexplorer.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuração do servidor MCP para o profile "mcp".
 *
 * Registra as tools do Geo-Explorer como {@link ToolCallbackProvider}.
 * O {@code McpServerAutoConfiguration} do Spring AI detecta este bean
 * automaticamente e injeta as tools no servidor MCP STDIO.
 *
 * O isolamento por profile garante que nenhum componente MCP seja
 * carregado quando o projeto sobe no profile "cli".
 */
@Configuration
@Profile("mcp")
public class McpToolsConfig {

    /**
     * Registra as tools MCP escaneando os métodos {@code @Tool} de
     * {@link GeoExplorerTools} via {@link MethodToolCallbackProvider}.
     *
     * O Spring AI gera o JSON Schema de cada tool automaticamente
     * a partir da assinatura dos métodos.
     */
    @Bean
    public ToolCallbackProvider geoExplorerToolCallbackProvider(GeoExplorerTools geoExplorerTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(geoExplorerTools)
                .build();
    }
}
