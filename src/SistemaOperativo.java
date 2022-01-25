import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/**@author Miquel Andreu Rossello Mas
 * @version 1.6
 * @since 13/01/21 El SO se encarga de todas las funcionalidades relacionadas con el mismo como con el software
 * añadido. Permite instalar, desinstalar, abrir y cerrar programas. Tiene la opción de formatear el ordenador entero
 * (SO y programas) y de poder iniciarse con los programas que se instalaron en la sesión anterior. Para acceder
 * a todas las funcionalidades, se muestra un menú simple con un número asignado a cada funcionalidad. También contiene
 * las funciones de mostrar los SO instalados en el sistema así como instalar nuevos o desinstalar uno ya existente.
 */

public class SistemaOperativo {
    //archivos
    private final File archivoSO = new File("SO.txt"); //archivo principal de los SO
    private final File archivoSOB = new File("SOB.txt"); //archivo secundario de los SO
    private File archivoSistemasOperativos; //archivo en uso de SO
    private final File archivosProgramasA = new File("programasA.txt"); //ruta programasA
    private final File archivosProgramasB = new File("programasB.txt"); //ruta programasB
    private File archivoProgramas; //ruta de programas en uso

    Scanner sc = new Scanner(System.in); //var escaner

    //programas base
    private final Software s1 = new Software("VBox", 1.2, 10000, 1000);
    private final Software s2 = new Software("IntelliJ", 15.3, 5000, 500);
    private final Software s3 = new Software("GIMP", 4.65, 40000, 300);
    private final Software[] softwares = {s1,s2,s3}; //lista de Software instalable

    //sistemas operativos base
    private static final SistemaOperativo CustomOS = new SistemaOperativo("CustomOS", "20.04_FocalFossa", "x86", false, 50000, 2000);
    private static final SistemaOperativo Ubuntu = new SistemaOperativo("Ubuntu", "20.04_FocalFossa", "x86", false, 25000, 4096);
    private static final SistemaOperativo Windows10 = new SistemaOperativo("Windows10", "10", "x86", false, 40000, 4000);
    private static final SistemaOperativo ArchLinux = new SistemaOperativo("ArchLinux", "5.15.12", "x86", true, 10000, 512);
    private static final SistemaOperativo[] SOs = {CustomOS,Ubuntu,Windows10,ArchLinux};
    private ArrayList<SistemaOperativo> sistemasOperativos = new ArrayList<SistemaOperativo>();

    //atributos
    private String nombre; //nombre del sistema operativo
    private String version; //version del SO
    private String arquitectura; //arquitectura de procesador
    private boolean onlyCommands;
    private double espacioRequerido; //almacenamiento minimo necesario
    private double memRequerida; //memoria ram requerida
    private ArrayList<Software> programas; //lista de programas instalados
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
                            double espacioRequerido, double memRequerida, double almacenamienoPC, double memoriaPC, boolean SOPrincipal) {
        this.nombre = nombre;
        this.version = version;
        this.arquitectura = arquitectura;
        this.onlyCommands = onlyCommands;
        this.espacioRequerido = espacioRequerido;
        this.memRequerida = memRequerida;
        this.programas = new ArrayList<Software>();
        this.menu = new ArrayList<String>();
        crearMenu();
        this.almacenamientoPC = almacenamienoPC;
        this.memoriaPC = memoriaPC;
        this.archivoProgramas = archivosProgramasA;
        this.archivoSistemasOperativos = archivoSO;
        recogerProgramasInstalados(); //se recogen los programas que se pueden haber instalado anteriormente
        recogerSO(SOPrincipal); //SOPrincipal indica que este SO es el que se va a ejecutar
    }
    //metodos
    //crea el menu
    private void crearMenu(){
        this.menu.clear(); //se resetea el menu

        getMenu().add("Instalar un programa");

        if (getProgramas().size()>0){ //si hay programas instalados se desbloquean las opciones relacionadas con los programas
            getMenu().add("Desinstalar un programa");
            getMenu().add("Abrir un programa");
            getMenu().add("Cerrar un programa");
            getMenu().add("Mostrar programas instalados");
        }

        getMenu().add("Formatear el ordenador");
        getMenu().add("Instalar otro SO");
        getMenu().add("Desinstalar un SO");
        getMenu().add("Mostrar SO instalados");
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

    //recoge y ejecuta la eleccion del usuario en el menu
    public void recogerEntradaMenu(){
        try{
            int eleccion = sc.nextInt();

            switch (getMenu().get(eleccion)) { //segun la opcion, se ejecuta una accion u otra
                case "Instalar un programa" -> instalarPrograma();
                case "Desinstalar un programa" -> desinstalarPrograma();
                case "Abrir un programa" -> abrirPrograma();
                case "Cerrar un programa" -> cerrarPrograma();
                case "Mostrar programas instalados" -> mostrarProgramas();
                case "Formatear el ordenador" -> formatear();
                case "Instalar otro SO" -> instalarOtroSO();
                case "Desinstalar un SO" -> desinstalarSO();
                case "Apagar el ordenador" -> apagarOrdenador();
                case "Mostrar SO instalados" -> mostrarSO();
                default -> System.out.println("Opcion no contemplada");
            }
        } catch (IndexOutOfBoundsException e){ //en caso de que se elija un numero demasiado alto
            System.out.println("Opcion no contemplada..");
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

        for (int i = 0; i < softwares.length; i++){ //se muestran todos los programas instalables
            System.out.println(softwares[i] + " (" + i + ")");
        }
        System.out.println("Cancelar la operacion " + " ("+softwares.length+")");

        int eleccionPrograma = sc.nextInt(); //recogida de la eleccion del usuario

        //por si el usuario decide cancelar la operacion
        if (eleccionPrograma==softwares.length){
            System.out.println("Cancelando operacion");
            return;
        }

        //evaluar que el programa no este instalado
        boolean instalado = false;
        for (int i = 0; i<getProgramas().size(); i++){
            //si el programa esta instalado asigna como instalado y se sale del bucle
            if (getProgramas().get(i).getNombre().equals(softwares[eleccionPrograma].getNombre())){
                instalado = true;
                break;
            }
        }
        //si el programa no esta instalado
        if (!instalado){
            Software sTemp = softwares[eleccionPrograma];
            //comprobar que se puede instalar (hay recursos suficientes)
            if (sTemp.getEspacioRequerido()<=getAlmacenamientoPC()){
                //se restan los recursos consumidos
                cambiarAlmacenamiento(sTemp.getEspacioRequerido(), false);
                getProgramas().add(sTemp); //se agnade el programa a la lista de instalados
                getProgramas().get(getProgramas().size()-1).setInstalado(true); //se asigna como instalado
                //se registra el programa en almacenamiento
                try(FileWriter fw = new FileWriter(getArchivoProgramas(),true)){
                    fw.write(sTemp.getNombre()+"\n");
                    fw.write((sTemp.getVersion())+"\n");
                    fw.write((sTemp.getEspacioRequerido())+"\n");
                    fw.write((sTemp.getMemRequerida())+"\n");
                    fw.write("true"+"\n");
                    fw.write("EOP\n"); //(End Of Program) codigo que indica el fin de los atributos de un programa


                } catch (IOException e){
                    System.out.println("Error en la instalacion");
                }
                System.out.println("---Instalacion completada---");
            }else { //si no hay recursos libres suficientes
                System.out.println("No hay recursos suficientes");
            }
        } else { //en caso de que ya este instalado
            System.out.println("Programa ya instalado en el sistema");
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
        int numPrograma = eleccionPrograma(); //se recoge el programa elegido

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
        for (int i = 0; i < getProgramas().size(); i++){ //muestra de los programas instalados
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

    //recoge los programas que estan instalados despues de un encendido sin formateo
    private void recogerProgramasInstalados(){
        try {
            BufferedReader bf = new BufferedReader(new FileReader(getArchivoProgramas()));
            if (!bf.ready()){ //si el archivo esta vacio se prueba con el siguiente
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

            bf.close();
        } catch (IOException e){
            try{ //en caso de que no existan los archivos de los programas, se crearan
                FileWriter fw = new FileWriter(archivosProgramasA);
                fw.close();
                fw = new FileWriter(archivosProgramasB);
                fw.close();
            } catch (IOException e1){
                System.out.println("Error en la recogida de los programas");
            }
        }
    }

    //elimina todos los programas y SO instalados (resetea los archivos)
    public void formatear(){
        System.out.println("Esta seguro de formatear el equipo? [S/N]");

        String eleccion = sc.next();

        if (eleccion.equalsIgnoreCase("S")){

            try{
                //borrado programas A
                FileWriter fw = new FileWriter(archivosProgramasA);
                fw.close();

                //borrado programas B
                fw = new FileWriter(archivosProgramasB);
                fw.close();

                //borrado SO archivo principal
                fw = new FileWriter(archivoSO);
                fw.close();

                //borrado SO archivo secundario
                fw = new FileWriter(archivoSOB);
                fw.close();

                setApagar(true); //se apaga el ordenador para hacer efectivos los cambios
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

    //permite instalar un SO (en conjunto con el ya instalado o sustituir el existente)
    public void instalarOtroSO(){
        //se muestran los SO que se pueden instalar
        System.out.println("Elija el SO a instalar:");
        for (int i = 0; i< SOs.length; i++){
            System.out.println(SOs[i] + " (" + i + ")");
        }

        int eleccionSO = sc.nextInt();

        System.out.println("Quiere reemplazar el SO ya existente? [S/N]");
        boolean reemplazar = sc.next().equalsIgnoreCase("S");
        try{
            FileWriter fw;
            if (reemplazar){ //si se quiere reemplazar el SO
                fw = new FileWriter(getArchivoSistemasOperativos()); //se sobreescribe el archivo del SO
            } else {
                fw = new FileWriter(getArchivoSistemasOperativos(), true); //se agnade el SO al ya existente
            }

            //se crea el objeto SO temporal
            SistemaOperativo temp = SOs[eleccionSO];
            //se escriben los datos del SO al archivo
            fw.write(temp.getNombre()+"\n");
            fw.write(temp.getVersion()+"\n");
            fw.write(temp.getArquitectura()+"\n");
            fw.write(temp.isOnlyCommands() +"\n");
            fw.write(temp.getEspacioRequerido() +"\n");
            fw.write(temp.getMemRequerida() +"\n");
            fw.write("EOOS\n"); //(End Of Operating System) para diferenciar entre sistemas operativos
            fw.close();

            System.out.println("SO instalado, en el siguiente encendido podra bootear desde el mismo");

            //se pregunta si el usuario quiere apagar el ordenador para que se hagan efectivos los cambios
            System.out.println("Desea apagar el ordenador? [S/N]");
            boolean apagar = sc.next().equalsIgnoreCase("S");

            setApagar(apagar);

        } catch (IOException e){
            System.out.println("Error de dual boot");
        }
    }

    //desinstala un SO del sistema
    public void desinstalarSO(){
        System.out.println("Elija el SO a desinstalar: ");
        for (int i = 0; i<getSistemasOperativos().size(); i++){
            System.out.println(getSistemasOperativos().get(i) + " (" + i + ")");
        }
        int eleccionSO = sc.nextInt();

        //se liberan los recursos
        cambiarAlmacenamiento(getSistemasOperativos().get(eleccionSO).getEspacioRequerido(), true);

        //se elige la ruta de destino como la ruta contraria al archivo en uso al momento
        File rutaNueva;
        if (Objects.equals(getArchivoSistemasOperativos(), archivoSO)){
            rutaNueva = archivoSOB;
        } else{
            rutaNueva = archivoSO;
        }

        try(FileWriter fw = new FileWriter(rutaNueva, true)){
            BufferedReader br = new BufferedReader(new FileReader(getArchivoSistemasOperativos()));
            String lector = br.readLine();
            //bucle para encontrar el inicio de los atributos del programa deseado (se busca el programa por el nombre)
            while (lector!=null){
                if (lector.equals(getSistemasOperativos().get(eleccionSO).getNombre())){ //si se encuentra el registro
                    while(!lector.equals("EOOS")){ //se lee hasta que se termina el registro
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

            fw.close();
            setArchivoSistemasOperativos(rutaNueva);

            //se limpia el archivo antiguo
            if (rutaNueva.equals(archivoSO)){
                FileWriter fw1 = new FileWriter(archivoSOB);
            } else {
                FileWriter fw1 = new FileWriter(archivoSO);
            }
            System.out.println("El equipo va a reiniciarse para aplicar los cambios");
            setApagar(true);
        } catch (IOException e){
            System.out.println("Error al desinstalar un SO");
        }
    }

    //recoge los SO instalados en el sistema (similar al metodo con el mismo nombre de la clase Ordenador)
    private void recogerSO(boolean SOprincipal){
        //comprobacion de que no es el SO principal
        if (!SOprincipal){return;}

        try{
            BufferedReader br = new BufferedReader(new FileReader(archivoSO));
            if (!br.ready()){ //se comprueba que el archivo no este vacio
                //si el archivo esta vacio, se prueba con el otro
                br = new BufferedReader(new FileReader(archivoSOB));
                setArchivoSistemasOperativos(archivoSOB);
            }

            String leerLogs = br.readLine();
            ArrayList<String> datosSO = new ArrayList<String>();
            int contadorOS = 1;

            while (leerLogs!=null){
                datosSO.add(leerLogs);
                leerLogs = br.readLine();
                if (leerLogs.equals("EOOS")){
                    leerLogs = br.readLine();
                    if (leerLogs!=null){
                        contadorOS++;
                    }
                }
            }

            br.close();

            for (int i = 0; i<contadorOS; i++){
                SistemaOperativo soTemp = new SistemaOperativo(datosSO.get(0),datosSO.get(1),datosSO.get(2),Boolean.parseBoolean(datosSO.get(3)),
                        Double.parseDouble(datosSO.get(4)), Double.parseDouble(datosSO.get(5)),
                        getAlmacenamientoPC(), getMemoriaPC(), false);
                this.sistemasOperativos.add(soTemp);
                datosSO.subList(0, 6).clear();
            }

        } catch (IOException e){
            System.out.println("Error recogiendo el SO");
        }
    }

    //muestra todos los SistemasOperativos instalados en el sistema
    public void mostrarSO(){
        System.out.println("Sistemas operativos instalados en el sistema");
        for (int i = 0; i<getSistemasOperativos().size();i++){
            System.out.println(getSistemasOperativos().get(i));
        }
        System.out.println("------------------------------");
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

    public File getArchivoProgramas() {
        return archivoProgramas;
    }

    public void setArchivoProgramas(File archivoProgramas) {
        this.archivoProgramas = archivoProgramas;
    }

    public ArrayList<SistemaOperativo> getSistemasOperativos() {
        return sistemasOperativos;
    }

    public void setSistemasOperativos(ArrayList<SistemaOperativo> sistemasOperativos) {
        this.sistemasOperativos = sistemasOperativos;
    }

    public File getArchivoSistemasOperativos() {
        return archivoSistemasOperativos;
    }

    public void setArchivoSistemasOperativos(File archivoSistemasOperativos) {
        this.archivoSistemasOperativos = archivoSistemasOperativos;
    }
}