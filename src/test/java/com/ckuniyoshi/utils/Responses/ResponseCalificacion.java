package com.ckuniyoshi.utils.Responses;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseCalificacion extends BaseResponseBody {

    private static final Logger logger = LoggerFactory.getLogger(ResponseCalificacion.class);
    public static ObjectMapper mapper = new ObjectMapper();

    public ResponseCalificacion(Response response) {
        super(response);
    }

    public boolean validarEsquemaJson(String schemaJsonPath) {
        logger.info("Ingresando al método de validación del esquema JSON.");
        File schemaJsonFile = new File(schemaJsonPath);
        if (!schemaJsonFile.exists()) {
            logger.error("El archivo del esquema JSON no se encuentra: {}", schemaJsonPath);
            return false;
        }
        try {
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schemaJsonFile));
            logger.info("La validación del esquema JSON fue exitosa.");
            return true;
        } catch (AssertionError e) {
            logger.error("Error al validar el esquema JSON: {}", e.getMessage());
            return false;
        }
    }

    public String getEstadoDescripcion() {
        return findInResponse().getString("estado.descripcion");
    }

    public String getPoliticaDescripcion() {
        return findInResponse().getString("politica.descripcion");
    }

    public List<Map<String, Integer>> getDetalleOferta() {
        List<Map<String, Integer>> resultado = new ArrayList<>();
        try {
            List<Map<String, Object>> detalleOferta = findInResponse().get("oferta.detalle_oferta");
            
            if (detalleOferta != null) {
                Map<String, Integer> productoMontoMap = new LinkedHashMap<>();
                for (Map<String, Object> item : detalleOferta) {
                    String producto = item.get("producto").toString();
                    Integer monto = ((Number) item.get("monto")).intValue();
                    productoMontoMap.put(producto, monto);
                }
                resultado.add(productoMontoMap);
            }
        } catch (Exception e) {
            logger.error("Error al obtener detalle_oferta: {}", e.getMessage());
        }
        return resultado;
    }

    public List<Map<String, Integer>> getControles() {
        List<Map<String, Integer>> resultado = new ArrayList<>();
        try {
            JsonNode rootNode = mapper.readTree(getResponse());
            
            if (rootNode.has("controles") && rootNode.get("controles").isArray()) {
                JsonNode controlesArray = rootNode.get("controles");
                
                for (JsonNode control : controlesArray) {
                    Map<String, Integer> controlMap = new HashMap<>();
                    String nombre = control.get("nombre").asText();
                    Integer valor = control.get("resultado").asInt();
                    controlMap.put(nombre, valor);
                    resultado.add(controlMap);
                }
            }
        } catch (Exception e) {
            logger.error("Error al obtener controles: {}", e.getMessage());
        }
        return resultado;
    }
}
