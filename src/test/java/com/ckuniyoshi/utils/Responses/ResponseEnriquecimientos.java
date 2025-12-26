package com.ckuniyoshi.utils.Responses;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseEnriquecimientos extends BaseResponseBody {

   
    public ResponseEnriquecimientos(Response response) {
        super(response);
    }

    public Long getNumeroDocumento() {
        return findInResponse().getLong("detalle_documento.numero_documento");
    }

    public String getControl1() {
        return findInResponse().getString("controles.control1");
    }

    public String getControl2() {
        return findInResponse().getString("controles.control2");
    }

    public List<Map<String, Integer>> getControles() {
        List<Map<String, Integer>> controles = new ArrayList<>();
        
        String control1Str = getControl1();
        String control2Str = getControl2();
        
        if (control1Str != null) {
            Map<String, Integer> control1 = new HashMap<>();
            control1.put("control1", Integer.parseInt(control1Str));
            controles.add(control1);
        }
        
        if (control2Str != null) {
            Map<String, Integer> control2 = new HashMap<>();
            control2.put("control2", Integer.parseInt(control2Str));
            controles.add(control2);
        }
        
        return controles;
    }

    public boolean tieneControlesRechazados() {
        String control1 = getControl1();
        String control2 = getControl2();
        return (control1 != null && control1.equals("2")) || (control2 != null && control2.equals("2"));
    }
}
