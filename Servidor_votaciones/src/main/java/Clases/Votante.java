package Clases;

import java.io.Serializable;

public class Votante implements Serializable {

    private String cedula;
    private String nombres;
    private String apellidos;
    private String codigo_cne;
    private String clave;
    private int[] periodos = {1, 2, 3};

    public Votante(String cedula_c) {
        this.cedula = cedula_c;
    }

    public Votante(String cedula, String nombres, String apellidos) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public Votante(String cedula, String nombres, String apellidos, String codigo_cne, String clave) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.codigo_cne = codigo_cne;
        this.clave = clave;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCodigo_cne() {
        return codigo_cne;
    }

    public String getClave() {
        return clave;
    }

    public int[] getPeriodos() {
        return periodos;
    }
}
