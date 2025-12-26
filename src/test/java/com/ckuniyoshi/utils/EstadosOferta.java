package com.ckuniyoshi.utils;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Nullable;
import com.ckuniyoshi.constants.CalificacionConstants;

public class EstadosOferta {

    private static final Logger logger = LoggerFactory.getLogger(EstadosOferta.class);

    public static String validarEstadoOferta(@Nullable List<Map<String, String>> controles) {

        boolean tieneRechazo = contieneRechazo(controles);

        if (tieneRechazo) {
            logger.info("Estado: " + CalificacionConstants.SOLICITUD_RECHAZADO);
            return CalificacionConstants.SOLICITUD_RECHAZADO;
        } else {
            logger.info("Estado: " + CalificacionConstants.SOLICITUD_APROBADA);
            return CalificacionConstants.SOLICITUD_APROBADA;
        }
    }

    private static boolean contieneRechazo(@Nullable List<Map<String, String>> controles) {
        if (controles == null || controles.isEmpty()) {
            return false;
        }

        for (Map<String, String> control : controles) {
            String resultado = control.get("resultado");
            if (resultado != null && resultado.equals("2")) {
                return true;
            }
        }
        return false;
    }
}
