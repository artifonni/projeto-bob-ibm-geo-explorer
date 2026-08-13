package com.geoexplorer.exception;

/**
 * Lançada quando o nível informado não é um {@code Level} válido
 * (BEGINNER, INTERMEDIATE ou ADVANCED) ou está em branco.
 */
public class InvalidLevelException extends GeoExplorerException {

    public InvalidLevelException(String message) {
        super(message);
    }
}
