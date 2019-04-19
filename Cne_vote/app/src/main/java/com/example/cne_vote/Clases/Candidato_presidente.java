package com.example.cne_vote.Clases;

public class Candidato_presidente {

    private String codigo;
    private String nombres;
    private String apellidos;
    private String partido;

    public Candidato_presidente(String codigo, String nombres, String apellidos, String partido){
        this.codigo = codigo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.partido = partido;
    }

    public String getPartido() {
        return partido;
    }

    public String getCodigo() { return codigo; }

    public String getNombres() { return nombres; }

    public String getApellidos() { return apellidos; }

}
