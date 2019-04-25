
package Clases;

public class Cliente {
    private String direccion;
    private int solicitudes;
    public Cliente(String direccion_c) {
        this.direccion = direccion_c;
        solicitudes = 1;
    }
    public void aumentar_solicitudes(){
        this.solicitudes++;
    }
    public void reiniciar_solicitudes(){
        this.solicitudes = 0;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public int getSolicitudes() {
        return this.solicitudes;
    }
    
}
