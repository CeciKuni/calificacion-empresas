package com.ckuniyoshi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@AllArgsConstructor

public class EnriquecimientoRequestDTO {

    @JsonProperty("detalle_documento")
    public DetalleDocumentoDTO detalleDocumento;  
    
}
