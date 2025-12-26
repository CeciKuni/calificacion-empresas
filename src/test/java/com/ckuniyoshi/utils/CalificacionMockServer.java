package com.ckuniyoshi.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CalificacionMockServer {

    private static WireMockServer wireMockServer;
    private static int port;

    public static void iniciarServidor() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            port = Integer.parseInt(ConfigReader.getProperty("wireMockPort"));
            wireMockServer = new WireMockServer(WireMockConfiguration.options()
                    .port(port)
                    .usingFilesUnderDirectory("src/test/resources"));
            wireMockServer.start();
            WireMock.configureFor("localhost", port);
            configurarStubCalificacion();
        }
    }

    public static void detenerServidor() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    private static void configurarStubCalificacion() {
        // Stub para documento aprobado (27325760457)
        stubFor(post(urlPathEqualTo("/calificacion"))
                .withRequestBody(containing("27325760457"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseAprobado())));

        // Stub para documento rechazado (27258680524)
        stubFor(post(urlPathEqualTo("/calificacion"))
                .withRequestBody(containing("27258680524"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseRechazado())));

        // Stub para enriquecimientos aprobado (27325760457)
        stubFor(post(urlPathEqualTo("/enriquecimientos"))
                .withRequestBody(containing("27325760457"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseEnriquecimiento())));

        // Stub para enriquecimientos rechazado (27258680524)
        stubFor(post(urlPathEqualTo("/enriquecimientos"))
                .withRequestBody(containing("27258680524"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseEnriquecimientoRechazado())));

        // Stub para error 500 (20111222333)
        stubFor(post(urlPathEqualTo("/calificacion"))
                .withRequestBody(containing("20111222333"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseError500())));

        stubFor(post(urlPathEqualTo("/enriquecimientos"))
                .withRequestBody(containing("20111222333"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseError500())));
    }

    public static void configurarRespuestaRechazada() {
        wireMockServer.resetAll();
        stubFor(post(urlPathMatching("/calificacion/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseRechazado())));
        stubFor(post(urlPathMatching("/enriquecimientos/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseEnriquecimiento())));
    }

    public static void configurarEnriquecimientoRechazado() {
        wireMockServer.resetAll();
        stubFor(post(urlPathMatching("/calificacion/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseAprobado())));
        stubFor(post(urlPathMatching("/enriquecimientos/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseEnriquecimientoRechazado())));
    }

    private static String getResponseAprobado() {
        return """
                {
                    "identificacion": {
                        "numero_documento": "27325760457"
                    },
                    "politica": {
                        "descripcion": "OFERTA1"
                    },
                    "estado": {
                        "descripcion": "Solicitud Aprobada"
                    },
                    "oferta": {
                        "detalle_oferta": [
                            {
                                "producto": "Linea 1",
                                "monto": 25000000
                            },
                            {
                                "producto": "Linea 2",
                                "monto": 40000000
                            },
                             {
                                "producto": "Linea 3",
                                "monto": 60000000
                            }
                        ]
                    },
                    "controles": [
                        {
                            "nombre": "Control 1",
                            "resultado": 1
                        },
                        {
                            "nombre": "Control 2",
                            "resultado": 1
                        }
                    ]
                }
                """;
    }

    private static String getResponseRechazado() {
        return """
                {
                    "identificacion": {
                        "numero_documento": "27258680524"
                    },
                    "politica": {
                        "descripcion": "OFERTA1"
                    },
                    "estado": {
                        "descripcion": "Solicitud Rechazada"
                    },
                    "oferta": {
                        "detalle_oferta": []
                    },
                    "controles": [
                        {
                            "nombre": "Control 1",
                            "resultado": 2
                        },
                        {
                            "nombre": "Control 2",
                            "resultado": 1
                        }
                    ]
                }
                """;
    }

    private static String getResponseEnriquecimiento() {
        return """
                {
                    "detalle_documento": {
                        "numero_documento": 27325760457
                    },
                    "controles": {
                        "control1": "1",
                        "control2": "1"
                    }
                }
                """;
    }

    private static String getResponseEnriquecimientoRechazado() {
        return """
                {
                    "detalle_documento": {
                        "numero_documento": 27258680524
                    },
                    "controles": {
                        "control1": "2",
                        "control2": "1"
                    }
                }
                """;
    }

    private static String getResponseError500() {
        return """
                {
                    "error": "Internal Server Error",
                    "message": "Error interno del servidor",
                    "status": 500
                }
                """;
    }

    public static int getPuerto() {
        return port;
    }
}
