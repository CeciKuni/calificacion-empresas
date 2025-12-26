package com.ckuniyoshi.utils.CalcularOferta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.text.DecimalFormat;
import com.ckuniyoshi.constants.Oferta1.PonderadoresStandard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcularMontosOferta {

    private static final Logger logger = LoggerFactory.getLogger(CalcularMontosOferta.class);
 
    public static List<Map<String, Integer>> calcularMontosLineas(
            double facturacionAnual) {

        List<Map<String, Integer>> montosLineas = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        // Cálculo simplificado: facturacionMensual * ponderador
        double facturacionMensual = facturacionAnual / 12;
        logger.info("Facturación Mensual: " + decimalFormat.format(facturacionMensual));
        logger.info("----------------------------------------------------------------------------------------------");

        // Obtener ponderadores (0.5, 0.8, 1.2)
        Map<String, Double> ponderadores = PonderadoresStandard.getponderadoresStandard();
        Map<String, Integer> montosCalculados = new LinkedHashMap<>();

        // Calcular montos: facturacionMensual * ponderador
        for (Map.Entry<String, Double> entry : ponderadores.entrySet()) {
            String linea = entry.getKey();
            Double ponderador = entry.getValue();
            int monto = (int) (facturacionMensual * ponderador);
            montosCalculados.put(linea, monto);
            logger.info(linea + ": " + decimalFormat.format(monto) + " (ponderador: " + ponderador + ")");
        }

        montosLineas.add(montosCalculados);
        return montosLineas;
    }


      // Comparar los montos de las líneas del response de Calificación con los montos
    // calculados
    public static void compararMontosOferta(List<Map<String, Integer>> montosLineasCalculadas,
            List<Map<String, Integer>> detalleOfertaCalificacion, Long documento) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0");

        if (montosLineasCalculadas.size() != detalleOfertaCalificacion.size()) {
            throw new AssertionError("Los tamaños de las listas no coinciden. Esperado: " + 
                    montosLineasCalculadas.size() + ", Actual: " + detalleOfertaCalificacion.size());
        }

        for (int i = 0; i < detalleOfertaCalificacion.size(); i++) {
            Map<String, Integer> detalleMap = detalleOfertaCalificacion.get(i);
            Map<String, Integer> resultadoMap = montosLineasCalculadas.get(i);

            for (Map.Entry<String, Integer> entry : detalleMap.entrySet()) {
                String clave = entry.getKey();
                Integer valorDetalle = entry.getValue();

                if (resultadoMap.containsKey(clave)) {
                    Integer valorResultado = resultadoMap.get(clave);
                    // Calcular la diferencia entre los montos
                    int diferencia = Math.abs(valorDetalle - valorResultado);
                    // Verificar si la diferencia está dentro del rango aceptable
                    if (diferencia > 200) {
                        throw new AssertionError("Los montos no coinciden dentro del rango aceptable para la línea: '" +
                                clave + "'. DetalleOfertaCalificacion: " + decimalFormat.format(valorDetalle) +
                                ", MontosLineasCalculadas: " + decimalFormat.format(valorResultado) + " - Documento: "
                                + documento);
                    } else {
                        logger.info("Coincidencia para la línea: '" + clave + "': Monto = " +
                                decimalFormat.format(valorResultado));
                    }
                } else {
                    // Si no se encuentra la línea, lanzamos una excepción
                    throw new AssertionError("Línea no encontrada: " + clave);
                }
            }
        }
    }

}
