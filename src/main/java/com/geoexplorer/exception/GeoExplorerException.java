package com.geoexplorer.exception;

/**
 * Exceção base do domínio Geo-Explorer.
 *
 * <p>Centraliza as exceções de negócio da aplicação para que as interfaces
 * (CLI e MCP) possam capturá-las de forma uniforme e exibir mensagens
 * amigáveis sem vazar detalhes internos do framework.</p>
 */
public class GeoExplorerException extends RuntimeException {

    public GeoExplorerException(String message) {
        super(message);
    }
}
