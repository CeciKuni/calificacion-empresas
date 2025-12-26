package com.ckuniyoshi.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class DetalleDocumentoDTO {

    @JsonProperty("numero_documento")
    private Long numeroDocumento;

    public DetalleDocumentoDTO(Long numeroDocumento) {
        this.numeroDocumento = numeroDocumento;

    }

    @Override
    public String toString() {
        return "DetalleDocumentoDTO{" +
                "numero_documento=" + numeroDocumento +
                '}';
    }

}
