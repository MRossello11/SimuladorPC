/**
 * @author Miquel Andreu Rossello Mas
 * @version 1.1
 * @since 13/01/21
 * @description Software por ahora no tiene mucha más funcionalidad a parte de consumir recursos del ordenador
 * PRÓXIMAMENTE: añadir alguna funcionalidad a la hora de ejecutar un programa*/

public class Software {
    //atributos
    private String nombre; //nombre del programa
    private double version; //version del programa
    private double espacioRequerido; //almacenamiento que ocupa
    private double memRequerida; //ram que necesita para ejecutarse
    private boolean enEjecucion; //true==ejecutandose, false==no se esta ejecutando
    private boolean instalado; //true==ya esta instalado en el SO, false==no esta instalado

    //constructor basico
    public Software(String nombre, double version, double espacioRequerido, double memRequerida) {
        this.nombre = nombre;
        this.version = version;
        this.espacioRequerido = espacioRequerido;
        this.memRequerida = memRequerida;
        this.enEjecucion = false;
        this.instalado = false;
    }

    //constructor para instalaciones y desisntalaciones
    public Software(String nombre, double version, double espacioRequerido, double memRequerida, boolean instalar) {
        this.nombre = nombre;
        this.version = version;
        this.espacioRequerido = espacioRequerido;
        this.memRequerida = memRequerida;
        this.enEjecucion = false;
        this.instalado = instalar;
    }

    @Override
    public String toString(){
        return getNombre() + " version " + getVersion();
    }

    //getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public double getEspacioRequerido() {
        return espacioRequerido;
    }

    public void setEspacioRequerido(double espacioRequerido) {
        this.espacioRequerido = espacioRequerido;
    }

    public double getMemRequerida() {
        return memRequerida;
    }

    public void setMemRequerida(double memRequerida) {
        this.memRequerida = memRequerida;
    }

    public boolean isEnEjecucion() {
        return enEjecucion;
    }

    public void setEnEjecucion(boolean enEjecucion) {
        this.enEjecucion = enEjecucion;
    }

    public boolean isInstalado() {
        return instalado;
    }

    public void setInstalado(boolean instalado) {
        this.instalado = instalado;
    }
}
