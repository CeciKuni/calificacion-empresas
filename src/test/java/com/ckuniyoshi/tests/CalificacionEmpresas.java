package com.ckuniyoshi.tests;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ckuniyoshi.constants.CalificacionConstants;
import com.ckuniyoshi.constants.Urls;
import com.ckuniyoshi.dto.CalificacionRequestDTO;
import com.ckuniyoshi.dto.DatosEconomicosDTO;
import com.ckuniyoshi.dto.DetalleDocumentoDTO;
import com.ckuniyoshi.utils.CSVDataReader;
import com.ckuniyoshi.utils.CalificacionMockServer;
import com.ckuniyoshi.utils.ConfigReader;
import com.ckuniyoshi.utils.EstadosOferta;
import com.ckuniyoshi.utils.RequestBuilder;
import com.ckuniyoshi.utils.TestListener;
import com.ckuniyoshi.utils.CalcularOferta.CalcularMontosOferta;
import com.ckuniyoshi.utils.Responses.ResponseCalificacion;
import com.ckuniyoshi.utils.Responses.ResponseEnriquecimientos;

import io.restassured.response.Response;

@Listeners(TestListener.class)
public class CalificacionEmpresas {

        private final Logger logger = LoggerFactory.getLogger(CalificacionEmpresas.class);
        private int numeroCaso = 0;
        private Long documento = 0L;
        private String politicaGanadora = "";
        private long responseTime = 0L;
        private ResponseCalificacion responseCalificacion = null;
        private CalificacionRequestDTO requestCalificacion = null;
        private DetalleDocumentoDTO detalleDocumento = null;
        private DatosEconomicosDTO datosEconomicos = null;
        private List<Map<String, Integer>> detalleOfertaCalificacion = new ArrayList<>();
        private String csvFileName = "";
        private Map<String, String> row = new HashMap<>();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        public static String politicaCalificacion = "";
        private Double facturacionAnual;
        private String estadoResponseCalificacion = "";
        private int statusCode = 0;
        private ResponseEnriquecimientos responseEnriquecimientos = null;
        private String descripcion = "";

        @BeforeClass
        private void setupEnvironment() {

                // Iniciar servidor WireMock
                CalificacionMockServer.iniciarServidor();

                csvFileName = ConfigReader.getProperty("csv");

                String testBaseUrlEnriquecimiento = "http://localhost:" + CalificacionMockServer.getPuerto();
                String testBaseUrlCalificacion = "http://localhost:" + CalificacionMockServer.getPuerto();

                Urls.setUrls(testBaseUrlEnriquecimiento, testBaseUrlCalificacion);

                limpiarArchivoCsv();

        }

        private void limpiarArchivoCsv() {
                String csvFile = "logs/pruebas.csv";
                try {
                        File file = new File(csvFile);
                        if (file.exists()) {
                                file.delete();
                                logger.info("Archivo CSV limpiado: " + csvFile);
                        }
                        // Resetear el contador de casos
                        numeroCaso = 0;
                } catch (Exception e) {
                        logger.warn("No se pudo limpiar el archivo CSV: " + e.getMessage());
                }
        }

        @AfterClass
        private void teardownEnvironment() {
                // Detener servidor WireMock
                CalificacionMockServer.detenerServidor();
        }

        @AfterMethod
        private void afterScenario() throws IOException {

                String csvFile = "logs/pruebas.csv";
                File file = new File(csvFile);
                boolean isNewFile = file.createNewFile();
                DecimalFormat df = new DecimalFormat("#,##0");

                try (FileWriter fw = new FileWriter(csvFile, true);
                                PrintWriter pw = new PrintWriter(fw)) {

                        if (isNewFile) {
                                pw.print("Caso;");
                                pw.print("Descripcion;");
                                pw.print("Documento;");
                                pw.print("FacturacionAnual;");
                                pw.print("StatusCode;");
                                pw.print("Politica;");
                                pw.print("Estado;");
                                pw.print("Controles;");
                                pw.print("Oferta;");
                                pw.println();
                        }

                        // Escribir datos del caso
                        pw.print(numeroCaso + ";");
                        pw.print(descripcion + ";");
                        pw.print(documento + ";");
                        pw.print(df.format(facturacionAnual) + ";");
                        pw.print(statusCode + ";");
                        pw.print(politicaGanadora + ";");
                        pw.print(estadoResponseCalificacion + ";");
                        
                        // Agregar controles
                        if (responseEnriquecimientos != null) {
                                String control1 = responseEnriquecimientos.getControl1();
                                String control2 = responseEnriquecimientos.getControl2();
                                pw.print("Control1=" + control1 + " Control2=" + control2 + ";");
                        } else {
                                pw.print(";");
                        }
                        
                        // Agregar oferta
                        if (detalleOfertaCalificacion != null && !detalleOfertaCalificacion.isEmpty()) {
                                StringBuilder oferta = new StringBuilder();
                                for (int i = 0; i < detalleOfertaCalificacion.size(); i++) {
                                        Map<String, Integer> item = detalleOfertaCalificacion.get(i);
                                        for (Map.Entry<String, Integer> entry : item.entrySet()) {
                                                oferta.append(entry.getKey()).append("=$").append(df.format(entry.getValue()));
                                                if (i < detalleOfertaCalificacion.size() - 1) {
                                                        oferta.append(" ");
                                                }
                                        }
                                }
                                pw.print(oferta.toString() + ";");
                        } else {
                                pw.print(";");
                        }
                        
                        pw.println();

                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        @DataProvider(name = "csvDataProvider")
        public Object[][] obtenerDatosCSV() {
                try {
                        List<Map<String, String>> csvData = CSVDataReader
                                        .readCSV("./src/test/resources/data/" + csvFileName + ".csv");
                        Object[][] data = new Object[csvData.size()][1];
                        for (int i = 0; i < csvData.size(); i++) {
                                data[i][0] = csvData.get(i).entrySet()
                                                .stream()
                                                .collect(Collectors.toMap(
                                                                e -> e.getKey().trim(),
                                                                e -> e.getValue() != null ? e.getValue().trim()
                                                                                : null));

                        }
                        return data;
                } catch (Exception e) {
                        e.printStackTrace();
                        return new Object[0][0];
                }
        }

        @Test(dataProvider = "csvDataProvider")
        public void ejecutarEscenario(Map<String, String> row) {
                this.row = row;

                limpiezaDeVariables();
                leerDatosDelArchivo();
                
                int statusCalificacion = llamadoPostCalificacion();
                
                // Si el status es 500, terminar el test
                if (statusCalificacion == 500) {
                        logger.info("Error 500 en calificacion, se termina el test");
                        return;
                }
                
                consultarEnriquecimientos();

                boolean tieneRechazo = compararControles();

                // Si hay un control rechazado, no validar estado ni montos
                if (tieneRechazo) {
                        logger.info("Hay controles rechazados, se omite validación de estado y montos");
                        return;
                }

                validarEstadoDeLaOferta();
                validarMontosDeLaOferta();
        }

        private void limpiezaDeVariables() {
                detalleOfertaCalificacion.clear();
                politicaGanadora = "";
                estadoResponseCalificacion = "";
                statusCode = 0;
                responseEnriquecimientos = null;
                responseCalificacion = null;
                descripcion = "";
        }

        private void leerDatosDelArchivo() {

                try {
                        logger.info("Ambiente: TESTING");

                        numeroCaso++;

                        // Leer los campos del CSV
                        documento = CSVDataReader.parseDocumentoNullable(row.get("documento"));
                        facturacionAnual = CSVDataReader.obtenerValorDouble("facturacionAnual", row);
                        descripcion = row.get("descripcion");

                        logger.info("******************************* Caso #" + numeroCaso + " *********************************************************************");
                        logger.info("Descripción: " + descripcion);
                        logger.info("Documento: " + documento);
                        DecimalFormat df = new DecimalFormat("#,##0");
                        logger.info("Facturación Anual: " + df.format(facturacionAnual));

                } catch (Exception e) {
                        logger.error("Error: ", e);
                }
        }

        private int llamadoPostCalificacion() {
                logger.info("Se realiza el llamado al POST de Calificaciones");

                detalleDocumento = new DetalleDocumentoDTO(documento);
                datosEconomicos = new DatosEconomicosDTO(facturacionAnual);

                requestCalificacion = new CalificacionRequestDTO(
                                detalleDocumento,
                                datosEconomicos,
                                "Cecilia",
                                "Kuniyoshi");

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                String url = Urls.getUrl(Urls.CALIFICACION);
                Response response = RequestBuilder.sendPostRequest(url, headers, requestCalificacion);

                responseCalificacion = new ResponseCalificacion(response);

                statusCode = responseCalificacion.getStatusCode();
                logger.info("Status Code: " + statusCode);

                responseTime = responseCalificacion.response.time();
                logger.info("Tiempo de respuesta: " + responseTime + " ms.");

                // Si es un error 500, validar el esquema de error y retornar
                if (statusCode == 500) {
                        String schemaJsonPath = "./src/test/resources/schemas/ResponseCalificacion500.json";
                        boolean esquemaValido = responseCalificacion.validarEsquemaJson(schemaJsonPath);
                        if (!esquemaValido) {
                                throw new AssertionError("La validación del esquema JSON 500 falló");
                        }
                        logger.info("La validación de la estructura JSON 500 es correcta");
                        return statusCode;
                }

                // Validar estructura JSON 200
                String schemaJsonPath = "./src/test/resources/schemas/ResponseCalificacion200.json";
                boolean esquemaValido = responseCalificacion.validarEsquemaJson(schemaJsonPath);
                if (!esquemaValido) {
                        throw new AssertionError("La validación del esquema JSON falló");
                }
                logger.info("La validación de la estructura JSON es correcta");

                // Guardar datos del response
                politicaGanadora = responseCalificacion.getPoliticaDescripcion();
                logger.info("Política: " + politicaGanadora);

                estadoResponseCalificacion = responseCalificacion.getEstadoDescripcion();
                logger.info("Estado: " + estadoResponseCalificacion);

                detalleOfertaCalificacion = responseCalificacion.getDetalleOferta();
                logger.info("Detalle Oferta: " + detalleOfertaCalificacion);

                List<Map<String, Integer>> controlesCalificacion = responseCalificacion.getControles();
                logger.info("Controles: " + controlesCalificacion);
                
                return statusCode;
        }

        private void consultarEnriquecimientos() {
                logger.info("Llamado a Enriquecimientos para el documento: " + documento);

                detalleDocumento = new DetalleDocumentoDTO(documento);

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");

                String url = Urls.getUrl(Urls.ENRIQUECIMIENTOS);
                logger.info("URL Enriquecimientos: " + url);
                Response response = RequestBuilder.sendPostRequest(url, headers, detalleDocumento);


                responseEnriquecimientos = new ResponseEnriquecimientos(response);
                logger.info("Status Code: " + responseEnriquecimientos.getStatusCode());

                // Obtener datos del response
                Long numeroDocumento = responseEnriquecimientos.getNumeroDocumento();
                logger.info("Número Documento: " + numeroDocumento);

                List<Map<String, Integer>> controlesEnriquecimientos = responseEnriquecimientos.getControles();
                logger.info("Controles: " + controlesEnriquecimientos);

                boolean tieneRechazos = responseEnriquecimientos.tieneControlesRechazados();
                logger.info("Tiene controles rechazados: " + tieneRechazos);
        }

        private boolean compararControles() {
                logger.info("Comparación de controles de Calificación vs Enriquecimientos");

                // Obtener controles de calificación
                List<Map<String, Integer>> controlesCalificacion = responseCalificacion.getControles();
                logger.info("Controles de Calificación: " + controlesCalificacion.size());

                // Obtener controles de enriquecimientos
                List<Map<String, Integer>> controlesEnriquecimientos = responseEnriquecimientos.getControles();
                logger.info("Controles de Enriquecimientos: " + controlesEnriquecimientos.size());

                // Verificar que tengan la misma cantidad de controles
                if (controlesCalificacion.size() != controlesEnriquecimientos.size()) {
                        logger.warn("Diferente cantidad de controles - Calificación: " +
                                        controlesCalificacion.size() + ", Enriquecimientos: " +
                                        controlesEnriquecimientos.size());
                }

                boolean tieneRechazo = false;

                // Comparar control por control
                for (int i = 0; i < Math.min(controlesCalificacion.size(), controlesEnriquecimientos.size()); i++) {
                        Map<String, Integer> controlCalificacion = controlesCalificacion.get(i);
                        Map<String, Integer> controlEnriquecimiento = controlesEnriquecimientos.get(i);

                        String nombreCalificacion = controlCalificacion.keySet().iterator().next();
                        Integer resultadoCalificacion = controlCalificacion.get(nombreCalificacion);

                        String nombreEnriquecimiento = controlEnriquecimiento.keySet().iterator().next();
                        Integer resultadoEnriquecimiento = controlEnriquecimiento.get(nombreEnriquecimiento);

                        logger.info("Comparando - Calificación [" + nombreCalificacion + ": " + resultadoCalificacion +
                                        "] vs Enriquecimientos [" + nombreEnriquecimiento + ": "
                                        + resultadoEnriquecimiento + "]");

                        // Verificar si hay rechazo (resultado = 2)
                        if (resultadoCalificacion == 2 || resultadoEnriquecimiento == 2) {
                                tieneRechazo = true;
                                logger.warn("Control rechazado encontrado (resultado = 2)");
                        }

                        if (!resultadoCalificacion.equals(resultadoEnriquecimiento)) {
                                logger.warn("Diferencia encontrada - Calificación: " + resultadoCalificacion +
                                                ", Enriquecimientos: " + resultadoEnriquecimiento);
                        } else {
                                logger.info("Controles coinciden: " + resultadoCalificacion);
                        }
                }

                return tieneRechazo;
        }

        private void validarEstadoDeLaOferta() {
                logger.info("Se valida el estado de la oferta");

                // Obtener controles del response de calificación
                List<Map<String, Integer>> controlesCalificacion = responseCalificacion.getControles();

                // Convertir a List<Map<String, String>> para EstadosOferta
                List<Map<String, String>> controles = new ArrayList<>();
                for (Map<String, Integer> control : controlesCalificacion) {
                        Map<String, String> controlStr = new HashMap<>();
                        for (Map.Entry<String, Integer> entry : control.entrySet()) {
                                controlStr.put(entry.getKey(), String.valueOf(entry.getValue()));
                        }
                        controles.add(controlStr);
                }

                // Calcular estado esperado usando EstadosOferta
                String estadoEsperado = EstadosOferta.validarEstadoOferta(controles);

                // Validar contra el estado del response de calificación
                if (estadoEsperado.equals(estadoResponseCalificacion)) {
                        logger.info("El estado es correcto: " + estadoEsperado);
                } else {
                        Assert.fail("La prueba falla. El estado esperado es: " + estadoEsperado +
                                        " pero el response dice: " + estadoResponseCalificacion +
                                        " - Documento: " + documento);
                }
        }

        private void validarMontosDeLaOferta() {
                logger.info("Validación de los montos de la oferta");

                // Si está rechazado, no validar montos
                if (estadoResponseCalificacion.equals(CalificacionConstants.SOLICITUD_RECHAZADO)) {
                        logger.info("Estado rechazado, no se validan montos");
                        return;
                }

                // Calcular montos esperados usando CalcularMontosOferta
                List<Map<String, Integer>> montosCalculados = CalcularMontosOferta.calcularMontosLineas(
                                facturacionAnual);

                // Obtener montos del response
                List<Map<String, Integer>> detalleOferta = responseCalificacion.getDetalleOferta();

                if (detalleOferta.isEmpty()) {
                        Assert.fail("No hay detalle de oferta en el response");
                        return;
                }

                if (montosCalculados.isEmpty()) {
                        Assert.fail("No se pudieron calcular los montos esperados");
                        return;
                }

                Map<String, Integer> montosEsperados = montosCalculados.get(0);
                Map<String, Integer> montosResponse = detalleOferta.get(0);

                // Comparar montos
                for (Map.Entry<String, Integer> entry : montosEsperados.entrySet()) {
                        String linea = entry.getKey();
                        Integer montoEsperado = entry.getValue();
                        Integer montoResponse = montosResponse.get(linea);

                        if (montoResponse == null) {
                                Assert.fail("Falta la línea " + linea + " en el response");
                        } else {
                                int diferencia = Math.abs(montoEsperado - montoResponse);
                                if (diferencia > 200) {
                                        Assert.fail("Monto incorrecto para " + linea +
                                                        ". Esperado: " + decimalFormat.format(montoEsperado) +
                                                        ", Response: " + decimalFormat.format(montoResponse) +
                                                        ", Diferencia: " + diferencia);
                                } else {
                                        logger.info(linea + " correcto: " + decimalFormat.format(montoResponse));
                                }
                        }
                }

                logger.info("Validación de montos exitosa");
               
        }

}
