import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

/**@author Miquel Andreu Rossello Mas
 * @version 1.4
 * @since 13/01/21 El SO se encarga de todas las funcionalidades relacionadas con el mismo como con el software
 * añadido. Permite instalar, desinstalar, abrir y cerrar programas. Tiene la opción de formatear el ordenador entero
 * (SO y programas) y de poder iniciarse con los programas que se instalaron en la sesión anterior. Para acceder
 * a todas las funcionalidades, se muestra un menú simple con un número asignado a cada funcionalidad.
 * PRÓXIMAMENTE: capacidad de tener dos SO instalados en el mismo ordenador (se elegirá con cual bootear con un
 * "programa" similar a GRUB. */

public class SistemaOperativo {
    //archivos
    private final File archivoSO = new File("SO.txt");
    private final File archivosProgramasA = new File("programasA.txt");
    private final File archivosProgramasB = new File("programasB.txt");
    private File archivoProgramas; //ruta de programas en uso

    Scanner sc = new Scanner(System.in); //var escaner

    //programas base
    private final Software s1 = new Software("VBox", 1.2, 10000, 1000);
    private final Software s2 = new Software("IntelliJ", 15.3, 5000, 500);
    private final Software s3 = new Software("GIMP", 4.65, 40000, 300);

    //atributos
    private String nombre; //nombre del sistema operativo
    private String version; //version del SO
    private String arquitectura; //arquitectura de procesador
    private boolean onlyCommands;
    private double espacioRequerido; //almacenamiento minimo necesario
    private double memRequerida; //memoria ram requerida
    private ArrayList<Software> programas; //lista de programas instalados
    private ArrayList<Software> softwares; //lista de Software instalable
    private ArrayList<String> menu; //lista de opciones del menu
    private double almacenamientoPC; //almacenamiento libre del que dispone el ordenador (despues de restar los requisitos del SO)
    private double memoriaPC; // memoria ram libre que dispone el ordenador (despues de restar los requisitos del SO)
    private boolean apagar; //segnal de apagado del ordenador (true==apagar ordenador en el siguiente ciclo, false==no apagar)

    //constructor para SO base
    public SistemaOperativo(String nombre, String version, String arquitectura, boolean onlyCommands,
                            double espacioRequerido, double memRequerida) {
        this.nombre = nombre;
        this.version = version;
        this.arquitectura = arquitectura;
        this.onlyCommands = onlyCommands;
        this.espacioRequerido = espacioRequerido;
        this.memRequerida = memRequerida;
    }

    //constructor para SO instalables
    public SistemaOperativo(String nombre, String version, String arquitectura, boolean onlyCommands,
                            double espacioRequerido, double memRequerida, double almacenamienoPC, double memoriaPC) {
        this.nombre = nombre;
        this.version = version;
        this.arquitectura = arquitectura;
        this.onlyCommands = onlyCommands;
        this.espacioRequerido = espacioRequerido;
        this.memRequerida = memRequerida;
        this.programas = new ArrayList<Software>();
        this.softwares = new ArrayList<Software>();
        llenarSoftwares();
        this.menu = new ArrayList<String>();
        crearMenu();
        this.almacenamientoPC = almacenamienoPC;
        this.memoriaPC = memoriaPC;
        this.archivoProgramas = archivosProgramasA;
        recogerProgramasInstalados();
    }
    //metodos
    //crea el menu
    private void crearMenu(){
        this.menu.clear(); //se resetea el menu

        getMenu().add("Instalar un programa");

        if (getProgramas().size()>0){ //si hay programas instalados se desbloquean las opciones que necesitan de programas
            getMenu().add("Desinstalar un programa");
            getMenu().add("Abrir un programa");
            getMenu().add("Cerrar un programa");
            getMenu().add("Mostrar programas instalados");
        }

//        getMenu().add("Instalar mas memoria RAM"); //funcionalidad no disponible por el momento
//        getMenu().add("Instalar mas capacidad de disco duro"); //funcionalidad no disponible por el momento
        getMenu().add("Formatear el ordenador");
        getMenu().add("Apagar el ordenador");
    }

    //muestra el menu por pantalla
    public void mostrarMenu(){
        //se muestra el estado de los recursos del ordenador
        System.out.println("Almacenamiento libre: " + getAlmacenamientoPC() + " MB");
        System.out.println("Memoria: " + getMemoriaPC() + " MB");

        crearMenu();

        System.out.println("Usted quiere: ");
        for (int i = 0; i < getMenu().size(); i++){ //muestra todas las opciones posibles
            System.out.println(getMenu().get(i) + " (" + i + ")");
        }
        recogerEntradaMenu();
    }

    //recoge y ejecuta la eleccion del menu
    public void recogerEntradaMenu(){
        //segun la opcion, se ejecuta una accion u otra
        int eleccion = sc.nextInt();
        try{
            switch (getMenu().get(eleccion)){
                case "Instalar un programa":
                    instalarPrograma();
                    break;

                case "Desinstalar un programa":
                    desinstalarPrograma();
                    break;

                case "Abrir un programa":
                    abrirPrograma();
                    break;

                case "Cerrar un programa":
                    cerrarPrograma();
                    break;

                case "Mostrar programas instalados":
                    mostrarProgramas();
                    break;

                /*case "Instalar mas memoria RAM":
                    System.out.println("Mas ram");
                    break;

                case "Instalar mas capacidad de disco duro":
                    System.out.println("mas hdd");
                    break;*/

                case "Formatear el ordenador":
                    formatear();
                    break;

                case "Apagar el ordenador":
                    apagarOrdenador();
                    break;

                default:
                    System.out.println("Opcion no contemplada");
            }
        } catch (IndexOutOfBoundsException e){ //en caso de que se elija un numero demasiado alto
            System.out.println("Opcion no contemplada");
        }
    }

    //secuencia de apagado del ordenador
    public void apagarOrdenador(){
        for (int i = 0; i < getProgramas().size(); i++){
            getProgramas().get(i).setEnEjecucion(false);//se cierran todos los programas
        }
        setApagar(true); //se lanza la segnal de apagado
    }

    //instala un programa
    public void instalarPrograma(){
        System.out.println("Elija el programa que quiera instalar: ");

        for (int i = 0; i < getSoftwares().size(); i++){ //se muestran todos los programas instalables
            System.out.println(getSoftwares().get(i) + " (" + i + ")");
        }
        System.out.println("Cancelar la operacion " + " ("+getSoftwares().size()+")");

        int eleccion = sc.nextInt();

        //como la opcion de cancelar la operacion esta fuera de los indices del array softwares,
        //lanzara una excepcion y se aprovecha eso para evitar instalar un programa
        try {
            //evaluar que el programa no este instalado
            if (!getSoftwares().get(eleccion).isInstalado()){
                Software sTemp = getSoftwares().get(eleccion);

                //comprobar que se puede instalar (hay recursos suficientes)
                if (sTemp.getEspacioRequerido()<=getAlmacenamientoPC()){
                    //se restan los recursos consumidos
                    cambiarAlmacenamiento(sTemp.getEspacioRequerido(), false);
                    getProgramas().add(sTemp); //se agnade el programa a la lista de instalados
                    getProgramas().get(getProgramas().size()-1).setInstalado(true); //se asigna como instalado

                    //se registra el programa en almacenamiento (programasA.txt)
                    try(FileWriter fw = new FileWriter(getArchivoProgramas(),true)){
                        fw.write(sTemp.getNombre()+"\n");
                        fw.write((sTemp.getVersion())+"\n");
                        fw.write((sTemp.getEspacioRequerido())+"\n");
                        fw.write((sTemp.getMemRequerida())+"\n");
                        fw.write("true"+"\n");
                        fw.write("EOP\n"); //(End Of Program) codigo que indica el fin de los atributos de un programa


                    } catch (IOException e){
                        System.out.println("ERROR");
                    }

                }else { //si no hay recursos libres suficientes
                    System.out.println("No hay recursos suficientes");
                }

            } else { //en caso de que ya este instalado
                System.out.println("Programa ya instalado en el sistema");
            }

        } catch (IndexOutOfBoundsException e){
            System.out.println("Cancelando la operacion...");
        }
    }

    //desinstalar un programa
    public void desinstalarPrograma(){
        int numPrograma = eleccionPrograma(); //se elige el programa

        if (numPrograma==-1){ //codigo de cancelado
            System.out.println("Cancelando la desisntalacion del programa");
        } else {
            //se liberan los recursos
            cambiarAlmacenamiento(getProgramas().get(numPrograma).getEspacioRequerido(),true);

            //se elige la ruta de destino como la ruta contraria al archivo en uso al momento
            File rutaNueva;
            if (Objects.equals(getArchivoProgramas(), archivosProgramasA)){
                rutaNueva = archivosProgramasB;
            } else{
                rutaNueva = archivosProgramasA;
            }

            //se copia el registro en el archivo contrario (de A a B o viceversa) excluyendo el registro del programa a desinstalar
            try(FileWriter fw = new FileWriter(rutaNueva, true)){
                FileReader fr = new FileReader(getArchivoProgramas());
                BufferedReader br = new BufferedReader(fr);
                String lector = br.readLine();

                //bucle para encontrar el inicio de los atributos del programa deseado (se busca el programa por el nombre)
                while (lector!=null){
                    if (lector.equals(getProgramas().get(numPrograma).getNombre())){ //si se encuentra el registro
                        while(!lector.equals("EOP")){ //se lee hasta que se termina el registro
                            lector = br.readLine();  //se ignora el registro
                        }
                        lector = br.readLine(); //se lee el principo del siguiente programa (si hay)
                    } else {
                        if (lector == null){ //en caso de que el programa a desisnstalar es el ultimo
                            break;
                        } else{
                            fw.write(lector+"\n");
                            lector = br.readLine();
                        }
                    }
                }
                setArchivoProgramas(rutaNueva); //se cambia la ruta en la que se instalaran los programas
                //se limpia el archivo antiguo
                if (rutaNueva.equals(archivosProgramasA)){
                    FileWriter fw1 = new FileWriter(archivosProgramasB);
                } else {
                    FileWriter fw1 = new FileWriter(archivosProgramasA);
                }
            } catch (IOException e){
                System.out.println("Error eliminando el software de los registros");
            }

            //si el programa estaba en ejecucion en el momento de desinstalarlo, se libera la memoria que ocupaba
            if (getProgramas().get(numPrograma).isEnEjecucion()){
                getProgramas().get(numPrograma).setEnEjecucion(false); //se cierra el programa
                //se liberan los recursos
                cambiarRam(getProgramas().get(numPrograma).getMemRequerida(),true);

            }

            //se elimina el programa como instalado
            getProgramas().remove(numPrograma);
            System.out.println("Programa desinstalado");
        }
    }

    //abre un programa
    public void abrirPrograma(){
        int numPrograma = eleccionPrograma();

        if (numPrograma==-1){ //evaluacion de si se eligio cancelar la operacion
            System.out.println("Cancelando la operacion");
        } else {
            //comprobacion de que hay recursos suficientes
            if (getProgramas().get(numPrograma).getMemRequerida()<=getMemoriaPC()){
                //se resta la ram libre
                cambiarRam(getProgramas().get(numPrograma).getMemRequerida(), false);
                //se ejecuta el programa
                getProgramas().get(numPrograma).setEnEjecucion(true);

            } else { //si no hay recursos suficientes
                System.out.println("No hay recursos suficientes para abrir el programa");
            }
        }

    }

    //cierra un programa
    public void cerrarPrograma(){
        //se elige el programa
        int numPrograma = eleccionPrograma();

        if (numPrograma==-1){ //si se ha elegido cancelar la operacion
            System.out.println("Cancelando la operacion");

        } else {
            //se cierra el programa
            getProgramas().get(numPrograma).setEnEjecucion(false);

            //se liberan los recursos que ocupaba el programa
            cambiarRam(getProgramas().get(numPrograma).getMemRequerida(), true);
            System.out.println("Programa cerrado");
        }
    }

    //recoge la eleccion de un programa instalado
    private int eleccionPrograma(){
        System.out.println("Elija un programa: ");
        for (int i = 0; i < getProgramas().size(); i++){
            System.out.println(getProgramas().get(i).getNombre() + " (" + i + ")");
        }
        System.out.println("Cancelar la operacion " + "("+getProgramas().size()+")");

        int eleccion = sc.nextInt(); //se recoge la eleccion

        //evaluacion de si el usuario quiere cancelar la operacion o no
        if (eleccion==getProgramas().size()){
            return -1; //-1 representa un codigo de cancelacion de la operacion
        } else {
            return eleccion; //si el usuario quiere continuar, se devuelve el indice del programa
        }
    }

    //cambia el almacenamiento en uso
    public void cambiarAlmacenamiento(double cantidad, boolean aumentar){
        if (aumentar){ //la opcion de aumentar, suma la cantidad especificada al almacenamiento libre (se libera memoria)
            this.almacenamientoPC += cantidad;
        } else { //de lo contrario, se resta (se ocupa almacenamiento)
            this.almacenamientoPC -= cantidad;
        }
    }

    //cambia la ram en uso
    public void cambiarRam(double cantidad, boolean aumentar){
        if (aumentar){ //la opcion de aumentar, suma la cantidad especificada a la memoria en uso (se libera memoria)
            this.memoriaPC += cantidad;
        } else { //de lo contrario, se resta (se ocupa memoria)
            this.memoriaPC -= cantidad;
        }
    }

    //llena el arrayList softwares
    private void llenarSoftwares(){
        getSoftwares().add(s1);
        getSoftwares().add(s2);
        getSoftwares().add(s3);
    }

    //recoge los programas que estan instalados despues de un encendido sin formateo
    private void recogerProgramasInstalados(){
        try {

            BufferedReader bf = new BufferedReader(new FileReader(getArchivoProgramas()));
            if (bf.ready()==false){ //si el archivo esta vacio se prueba con el siguiente
                bf = new BufferedReader(new FileReader(archivosProgramasB));
                setArchivoProgramas(archivosProgramasB);
            }

            String leer = bf.readLine(); //lee linea por linea los atributos guardados en el archivo de texto
            ArrayList<String> datosPrograma = new ArrayList<String>(); //guardara temporalmente los datos del programa

            //bucle de recogida de los datos del software instalado
            while(leer!=null){
                //si se llega al final de los atributos del programa
                if (leer.equals("EOP")){
                    //se crea el objeto software
                    Software sTemp = new Software(datosPrograma.get(0),Double.parseDouble(datosPrograma.get(1)),Double.parseDouble(datosPrograma.get(2)),
                            Double.parseDouble(datosPrograma.get(3)), Boolean.parseBoolean(datosPrograma.get(4)));

                    getProgramas().add(sTemp); //se agnade el programa a instalados
                    datosPrograma.clear(); //se limpian los datos del programa
                    leer = bf.readLine(); //se lee la siguiente linea
                } else { //si aun hay atributos por leer
                    datosPrograma.add(leer); //se agnade el atributo leido
                    leer = bf.readLine();//se lee la siguiente linea
                }
            }


        } catch (IOException e){
            System.out.println("Ha habido un error recogiendo los programas instalados");
        }
    }

    //elimina todos los programas y SO instalados
    public void formatear(){
        System.out.println("Esta seguro de formatear el equipo? [S/N]");

        String eleccion = sc.next();

        if (eleccion.equalsIgnoreCase("S")){

            try{
                //borrado programas A
                FileWriter fw1 = new FileWriter(archivosProgramasA);
                fw1.close();

                //borrado programas B
                FileWriter fw2 = new FileWriter(archivosProgramasB);
                fw2.close();

                //borrado SO
                FileWriter fw3 = new FileWriter(archivoSO);
                fw3.close();

                setApagar(true); //se apaga el ordenador
            } catch (IOException e){
                System.out.println("Error");
            }


        } else {
            System.out.println("Cancelando la operacion");
        }
    }

    //muestra por pantalla los programas instalados
    public void mostrarProgramas(){
        for (int i = 0; i < getProgramas().size(); i++){
            System.out.println(getProgramas().get(i));
        }
        System.out.println("-------------");
    }

    @Override
    public String toString(){
        return getNombre() + " " + getVersion();
    }

    //getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getArquitectura() {
        return arquitectura;
    }

    public void setArquitectura(String arquitectura) {
        this.arquitectura = arquitectura;
    }

    public boolean isOnlyCommands() {
        return onlyCommands;
    }

    public void setOnlyCommands(boolean onlyCommands) {
        this.onlyCommands = onlyCommands;
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

    public ArrayList<Software> getProgramas() {
        return programas;
    }

    public void setProgramas(ArrayList<Software> programas) {
        this.programas = programas;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<String> menu) {
        this.menu = menu;
    }

    public double getAlmacenamientoPC() {
        return almacenamientoPC;
    }

    public void setAlmacenamientoPC(double almacenamientoPC) {
        this.almacenamientoPC = almacenamientoPC;
    }

    public double getMemoriaPC() {
        return memoriaPC;
    }

    public void setMemoriaPC(double memoriaPC) {
        this.memoriaPC = memoriaPC;
    }

    public boolean isApagar() {
        return apagar;
    }

    public void setApagar(boolean apagar) {
        this.apagar = apagar;
    }

    public ArrayList<Software> getSoftwares() {
        return softwares;
    }

    public File getArchivoProgramas() {
        return archivoProgramas;
    }

    public void setArchivoProgramas(File archivoProgramas) {
        this.archivoProgramas = archivoProgramas;
    }
}