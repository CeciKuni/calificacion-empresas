package com.ckuniyoshi.utils.Controles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ckuniyoshi.constants.*;
import com.ckuniyoshi.utils.Responses.ResponseEnriquecimientos;

public class ControlesEnriquecimiento {
    public static List<Map<String, Integer>> controles = new ArrayList<>();


    public static List<Map<String, Integer>> controlesEnriquecimientosConCostos(
            ResponseEnriquecimientos responseControles
           ) {

        control1(responseControles);
        control2(responseControles);

        return controles;
    }

    private static void control1(ResponseEnriquecimientos responseControles) {
        // Se busca el valor del campo en el response de Enriquecimientos
        String controlConCostoStr = responseControles.getControl1();
        int controlConCosto = Integer.parseInt(controlConCostoStr);

        int resultado = controlConCosto > 0 ? 2 : 1;

        if (resultado > 1) {
            Map<String, Integer> controlResult = new HashMap<>();
            controlResult.put(CalificacionConstants.CONTROL_CON_COSTO, resultado);
            controles.add(controlResult);
        }
    }

        private static void control2(ResponseEnriquecimientos responseControles) {
        // Se busca el valor del campo en el response de Enriquecimientos
        String controlConCostoStr = responseControles.getControl2();
        int controlConCosto = Integer.parseInt(controlConCostoStr);

        int resultado = controlConCosto > 0 ? 2 : 1;

        if (resultado > 1) {
            Map<String, Integer> controlResult = new HashMap<>();
            controlResult.put(CalificacionConstants.CONTROL_CON_COSTO, resultado);
            controles.add(controlResult);
        }
    }

}
