
package Clases;

import java.io.Serializable;

public class Mensaje implements Serializable {


    private String mensaje_texto;
    //private Votante votante;

    public Mensaje(String mensaje_c) {
        this.mensaje_texto = mensaje_c;
    }

    public String getMensaje_texto() {
        return mensaje_texto;
    }

    
}
