package com.ckuniyoshi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatosEconomicosDTO {

    @JsonProperty("facturacion_anual")
    private double facturacionAnual;

    // Constructor que acepta solo la facturación anual como double
    public DatosEconomicosDTO(double facturacionAnual) {
        this.facturacionAnual = facturacionAnual;

    }

    @Override
    public String toString() {
        return "DatosEnconomicosDTO{" +
                "facturacion_anual=" + facturacionAnual +
                '}';
    }
}
