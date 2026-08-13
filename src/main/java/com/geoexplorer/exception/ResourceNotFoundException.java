package com.geoexplorer.exception;

/**
 * Lançada quando um recurso procurado não existe (ex.: trilha de uma
 * tecnologia não encontrada).
 */
public class ResourceNotFoundException extends GeoExplorerException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
