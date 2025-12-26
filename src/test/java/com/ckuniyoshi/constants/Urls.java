package com.ckuniyoshi.constants;

public class Urls {

    public static final String ENRIQUECIMIENTOS = "ENRIQUECIMIENTOS";
    public static final String CALIFICACION = "CALIFICACION";

    private static String testBaseUrlEnriquecimiento;
    private static String testBaseUrlCalificacion;

    public static void setUrls(String testBaseUrlEnriquecimiento, String testBaseUrlCalificacion) {
        Urls.testBaseUrlEnriquecimiento = testBaseUrlEnriquecimiento;
        Urls.testBaseUrlCalificacion = testBaseUrlCalificacion;
    }

    public static String getUrl(String service) {
        String baseUrl = "";

        switch (service) {
            case ENRIQUECIMIENTOS:
                baseUrl = testBaseUrlEnriquecimiento;
                break;
            case CALIFICACION:
                baseUrl = testBaseUrlCalificacion;
                break;
            default:
                return null;
        }

        switch (service) {
            case ENRIQUECIMIENTOS:
                return baseUrl + "/enriquecimientos";
            case CALIFICACION:
                return baseUrl + "/calificacion";
            default:
                return null;
        }
    }
}
