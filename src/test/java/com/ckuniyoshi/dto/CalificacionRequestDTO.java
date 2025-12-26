package com.ckuniyoshi.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class CalificacionRequestDTO {

        @JsonProperty("detalle_documento")
        private DetalleDocumentoDTO detalleDocumento;

        @JsonProperty("datos_economicos")
        private DatosEconomicosDTO datosEconomicos;

        @JsonProperty("nombre")
        private String nombre;

        @JsonProperty("apellido")
        private String apellido;

        public CalificacionRequestDTO(DetalleDocumentoDTO detalleDocumento,
                        DatosEconomicosDTO datosEconomicos,
                        String apellido,
                        String nombre) {
                this.detalleDocumento = detalleDocumento;
                this.datosEconomicos = datosEconomicos;
                this.apellido = apellido;
                this.nombre = nombre;

        }

        @Override
        public String toString() {
                return "CalificacionRequestDTO{" +
                                "detalleDocumento=" + detalleDocumento +
                                ", datosEconomicos=" + datosEconomicos +
                                ", nombre='" + nombre + '\'' +
                                ", apellido='" + apellido + '\'' +
                                '}';
        }
}
