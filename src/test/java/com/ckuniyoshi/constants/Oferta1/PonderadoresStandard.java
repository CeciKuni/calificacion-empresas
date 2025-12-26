package com.ckuniyoshi.constants.Oferta1;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ckuniyoshi.constants.LineasPoliticas;

public class PonderadoresStandard {

    public static Map<String, Double> getponderadoresStandard() {
        Map<String, Double> ponderadores = new LinkedHashMap<>();

           
                ponderadores.put(LineasPoliticas.LINEA1, 0.5);
                ponderadores.put(LineasPoliticas.LINEA2, 0.8);
                ponderadores.put(LineasPoliticas.LINEA3, 1.2);   
                
        return ponderadores;
    }


}
