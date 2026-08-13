package com.geoexplorer.exception;

/**
 * Lançada quando uma entrada do usuário é inválida (ex.: tecnologia em branco
 * ou usuário não informado).
 *
 * <p>Diferente de {@link ResourceNotFoundException}, que indica que o recurso
 * procurado não existe, esta exceção representa erro de validação de entrada.</p>
 */
public class InvalidInputException extends GeoExplorerException {

    public InvalidInputException(String message) {
        super(message);
    }
}
