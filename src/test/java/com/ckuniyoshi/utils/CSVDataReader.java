package com.ckuniyoshi.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVDataReader {
    private static final Logger logger = LoggerFactory.getLogger(CSVDataReader.class);
    private static final String ACCIONISTA_PREFIX = "documentoAccionista";
    public static Map<Long, Double> mapPartAccionaria = new HashMap<>();
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public static List<Map<String, String>> readCSV(String filePath) {
        List<Map<String, String>> data = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] headers = reader.readNext(); // Lee la primera línea para obtener los encabezados
            String[] values;
            while ((values = reader.readNext()) != null) {
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    row.put(headers[i], values.length > i ? values[i] : "");
                }
                data.add(row);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error al leer el archivo CSV: " + e.getMessage());
        }

        return data;
    }

    public static int contarAccionistas(Map<String, String> row) {
        return (int) row.keySet().stream()
                .filter(key -> key.startsWith(ACCIONISTA_PREFIX))
                .count();
    }

    public static void procesarAccionistas(Map<String, String> row, int maxAccionistas) {

        mapPartAccionaria.clear();

        for (int i = 1; i <= maxAccionistas; i++) {
            String keyDocumentoAccionista = "documentoAccionista" + i;
            String keyPartAccionaria = "partAccionaria" + i;

            Long documentoAccionista = parseDocumentoNullable(row.get(keyDocumentoAccionista));
            Double partAccionaria = obtenerValorDoubleNullable(row.get(keyPartAccionaria));

            if (documentoAccionista != null && partAccionaria != null) {
                mapPartAccionaria.put(documentoAccionista, partAccionaria);
            }

            // Procesamiento y almacenamiento
            logger.info("Documento accionista " + i + ": " + documentoAccionista);
            logger.info("Participación accionaria " + i + ": " + partAccionaria);
        }
    }

    public static Long parseDocumentoNullable(String documentoStr) {
        try {
            return (documentoStr != null && !documentoStr.trim().isEmpty())
                    ? Long.parseLong(documentoStr.trim())
                    : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double obtenerValorDoubleNullable(String valorStr) {
        try {
            return (valorStr != null && !valorStr.trim().isEmpty())
                    ? Double.parseDouble(valorStr.trim())
                    : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer obtenerValorIntNullable(String valorStr) {
        try {
            return (valorStr != null && !valorStr.trim().isEmpty())
                    ? Integer.parseInt(valorStr.trim())
                    : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Boolean obtenerValorBooleanNullable(String valorStr) {
        try {
            return (valorStr != null && !valorStr.trim().isEmpty())
                    ? Boolean.parseBoolean(valorStr.trim())
                    : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double obtenerValorDouble(String key, Map<String, String> row) {
        return row.containsKey(key) && !row.get(key).isEmpty()
                ? Double.parseDouble(row.get(key))
                : 0.0;
    }

    public static Integer obtenerValorInt(String key, Map<String, String> row) {
        return row.containsKey(key) ? Integer.parseInt(row.get(key)) : 0;
    }

    public static Boolean obtenerValorBoolean(String key, Boolean defaultValue, Map<String, String> row) {
        return row.containsKey(key)
                ? Boolean.parseBoolean(row.get(key))
                : defaultValue;
    }

    public static String obtenerFechaCierreBalance(Map<String, String> row) {
        return row.containsKey("fechaCierreBalance") && !row.get("fechaCierreBalance").isEmpty()
                ? row.get("fechaCierreBalance")
                : null;
    }

    public static void imprimirInformacion(int numeroCaso, String canal, Long documento, String fechaCierreBalance,
            Double facturacionAnual,
            Double patrimonioNeto, Double resultadoOperativo, Double montoAcreditaciones,
            String lineaSeleccionada, Integer codigoFranquicia,
            Boolean presentaGarantiaSGR, Boolean grupoEconomico, Boolean politicaSGR,
            Boolean balanceFranquicia, Boolean retencion, Integer lineaSeleccionadaCodigo, int tipoDocumento,
            boolean vendors) {

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        logger.info("*******************************************CASO N°: " + numeroCaso
                + "********************************************************");
        logger.info("Canal: " + canal);
        logger.info("Documento: " + documento);
        logger.info("Fecha Cierre Balance: " + (fechaCierreBalance != null ? fechaCierreBalance : "N/A"));
        logger.info("Facturación Anual: " + decimalFormat.format(facturacionAnual));
        logger.info("RPC: " + decimalFormat.format(patrimonioNeto));
        logger.info("Resultado Operativo: " + decimalFormat.format(resultadoOperativo));
        logger.info("Monto Acreditaciones: " + decimalFormat.format(montoAcreditaciones));
        logger.info("Código Franquicia: " + (codigoFranquicia != null ? codigoFranquicia : null));
        logger.info("Presenta Garantia SGR: " + (presentaGarantiaSGR != null ? presentaGarantiaSGR : "N/A"));
        logger.info("Grupo Económico: " + grupoEconomico);
        logger.info("Politica SGR: " + politicaSGR);
        logger.info("Balance Franquicia: " + balanceFranquicia);
        logger.info("Retención: " + retencion);
        logger.info("Línea Seleccionada: " + lineaSeleccionadaCodigo);
        logger.info("Tipo Documento: " + tipoDocumento);
        logger.info("Vendors: " + vendors);
    }

}
