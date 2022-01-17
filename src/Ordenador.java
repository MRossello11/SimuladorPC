import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**@author Miquel Andreu Rossello Mas
 * @version 1.3
 * @since 13/01/21
 * @description El Ordenador se encarga del encendido del mismo y de la secuencia de inicio. Estos,
 * son los métodos encargados de la puesta en marcha (comprobar que haya SO y bootear desde el mismo
 * o instalar uno si no se encuentra ningún SO).
 * PRÓXIMAMENTE: se añadirán funcionalidades como el cambio de memoria RAM y de almacenamiento. Incluso
 * será posible añadir más de un disco duro.*/

//nota: todas las medidas de almacenamiento se encuentran en MB

public class Ordenador {
    //rutas (cambiar la primera vez que se ejecuta en un ordenador distinto)
    private static final String ruta = "I:\\DAM\\Primero\\entornos\\SimuladorPC1.3\\src\\SO.txt";
    Scanner sc = new Scanner(System.in); //var escaner
    //variables estaticas (sistemas operativos base)
    private static final SistemaOperativo CustomOS = new SistemaOperativo("CustomOS", "20.04_FocalFossa", "x86", false, 50000, 2000);
    private static final SistemaOperativo Ubuntu = new SistemaOperativo("Ubuntu", "20.04_FocalFossa", "x86", false, 25000, 4096);
    private static final SistemaOperativo Windows10 = new SistemaOperativo("Windows10", "10", "x86", false, 40000, 4000);
    private static final SistemaOperativo ArchLinux = new SistemaOperativo("ArchLinux", "5.15.12", "x86", true, 10000, 512);
    private static final SistemaOperativo[] sistemasOperativos = {CustomOS,Ubuntu,Windows10,ArchLinux};

    //atributos
    private String nombre; //nombre del ordenador
    private double memRam; //memoria ram libre
    private double ramTotal; //memoria ram total
    private double capacidadHddLibre; //capacidad hdd libre
    private double hddTotal; //capacidad total del hdd
    private String arquitecturaCpu; //arquitectura que usa la cpu
    private SistemaOperativo so; //so del ordenador
    private boolean encendido; //indicador del estado del ordenador (true==encendido, false==apagado)


    //constructor (PC sin SO)
    public Ordenador(String nombre, double memoria, double almacenamiento) {
        this.nombre = nombre;
        this.memRam = memoria;
        this.ramTotal = memoria;
        this.capacidadHddLibre = almacenamiento;
        this.hddTotal = almacenamiento;
        this.encendido = false;

    }

    //constructor (PC con SO)
    public Ordenador(String nombre, double memoria, double almacenamiento,
                     String nombreSO, String version, String arquitectura, boolean onlyCommands,
                     double espacioRequerido, double memRequerida){

        this.nombre = nombre;
        this.memRam = memoria;
        this.ramTotal = memoria;
        this.capacidadHddLibre = almacenamiento;
        this.hddTotal = almacenamiento;
        this.encendido = false;
        this.so = new SistemaOperativo(nombreSO, version, arquitectura, onlyCommands, espacioRequerido, memRequerida, almacenamiento, memoria);
    }


    //metodos
    /**SECUENCIA DE INICIO*/
    //pone en marcha el ordenador
    public void encender(){
        setEncendido(true); //se enciende el ordenador
        comprobarSO(); //comprobacion de si hay SO instalado
    }

    //comprueba si hay SO instalado o no
    public void comprobarSO(){
        //comprobacion de si hay SO instalado
        try(FileReader fr = new FileReader(ruta)){
            BufferedReader bff = new BufferedReader(fr);
            if (bff.readLine()==null){ //el archivo esta vacio
                System.out.println("No se encontro ningun Sistema Operativo instalado");
                encenderPCPrimeraVez();
            } else {
                encenderPC();
            }
        } catch (IOException e){
            System.out.println("Error, No se encontro el archivo SO.txt");
        }
    }

    //para encender el ordenador cuando no hay SO instalado
    public void encenderPCPrimeraVez(){
        instalarSO();
        ordenadorEnEjecucion();
    }

    //instalar sistema operativo
    public void instalarSO(){
        System.out.println("Selecciona que SO instalar: ");

        for (int i = 0; i < sistemasOperativos.length; i++){ //se muestran todas las opciones de SO que hay
            System.out.println(sistemasOperativos[i].getNombre() + " (" + i + ")");
        }

        int eleccion = sc.nextInt(); //se recoge la eleccion del usuario
        SistemaOperativo soTemp = sistemasOperativos[eleccion]; //objeto SO temporal

        //evaluacion de si hay los recursos necesarios para instalar el SO en el ordenador
        if (soTemp.getEspacioRequerido()<=getCapacidadHddLibre() && soTemp.getMemRequerida()<=getMemRam()){
            //se restan los recursos usados
            this.capacidadHddLibre -= soTemp.getEspacioRequerido();
            this.memRam -= soTemp.getMemRequerida();

            //creacion del sistema operativo
            this.so = new SistemaOperativo(soTemp.getNombre(), soTemp.getVersion(), soTemp.getArquitectura(),
                    soTemp.isOnlyCommands(), soTemp.getEspacioRequerido(), soTemp.getMemRequerida(),
                    getHddTotal()- soTemp.getEspacioRequerido(), getRamTotal()-soTemp.getMemRequerida());

            //se recogen los datos en el archivo SO.txt para el proximo encendido
            try(FileWriter fw = new FileWriter(ruta, true)){
                fw.write(getSo().getNombre()+"\n");
                fw.write(getSo().getVersion()+"\n");
                fw.write(getSo().getArquitectura()+"\n");
                fw.write(getSo().isOnlyCommands() +"\n");
                fw.write(getSo().getEspacioRequerido() +"\n");
                fw.write(getSo().getMemRequerida() +"\n");
                fw.write("EOOS"); //(End Of Operating System) para diferenciar entre sistemas operativos

            } catch (IOException e){
                System.out.println("Error");
            }

            System.out.println("Instalacion completada");

        } else {
            System.out.println("Error, no hay recursos suficientes");
        }
    }

    //para encender el ordenador cuando hay SO
    public void encenderPC() throws IOException {
        FileReader fr = new FileReader(ruta);
        BufferedReader br = new BufferedReader(fr);
        String leerLogs = br.readLine();
        ArrayList<String> datosSO = new ArrayList<String>();

        while (leerLogs!=null){
            datosSO.add(leerLogs);
            leerLogs = br.readLine();
        }
        fr.close();
        //se asignan los valores recogidos
        this.so = new SistemaOperativo(datosSO.get(0),datosSO.get(1),datosSO.get(2),Boolean.parseBoolean(datosSO.get(3)),
                Double.parseDouble(datosSO.get(4)), Double.parseDouble(datosSO.get(5)),
                getHddTotal()-Double.parseDouble(datosSO.get(4)), getRamTotal()-Double.parseDouble(datosSO.get(5)));

        this.capacidadHddLibre -= getSo().getEspacioRequerido();
        this.memRam -= getSo().getMemRequerida();

        ordenadorEnEjecucion();
    }

    //bucle de ejecucion
    public void ordenadorEnEjecucion(){

        System.out.println("Bienvenido a " + getSo().getNombre() + " version " + getSo().getVersion());
        while(!isEncendido() || !getSo().isApagar()){
            getSo().mostrarMenu();
        }
        System.out.println("Shutdown");
    }
    /**Fin de la secuencia de inicio*/

    public void apagar(){
        this.setEncendido(false);
    }

    //cambia el espacio libre
    public void cambiarcapacidadHddLibre(double c){
        this.capacidadHddLibre += c; //se cambia (suma o resta) la capacidad del hdd
        if (this.capacidadHddLibre < 0){ //combrobacion de que no ha quedado en negativo
            System.out.println("No hay espacio suficiente en el disco duro"); //se avisa al usuario
            this.capacidadHddLibre += -c; //se suma lo que se resto

        }else{ //si se ha podido cambiar la capacidad, se muestra por pantalla cuanto queda
            System.out.println("Espacio libre en el disco duro: " + this.capacidadHddLibre + " MB");
        }
    }

    //cambia la capacidad de la ram en uso
    public void cambiarCapacidadRam(double c){
        this.memRam += c; //se cambia la capacidad de ram libre
        if (this.memRam < 0){ //evaluacion de que la ram es suficiente
            System.out.println("No hay memoria suficiente"); //se avisa si no hay ram suficiente
            this.memRam += -c; //se revierten los cambios

        }else{ //si todo ha ido bien
            System.out.println("Memoria en uso: " + (this.ramTotal - this.  memRam) + " MB"); //se muestra la ram en uso
        }
    }

    //modifica la capacidad total del hdd
    public void modificarCapacidadTotalHdd(double capacidad, boolean aumentar){
        if (isEncendido()){ //evaluacion de que el ordenador esta encendido
            System.out.println("Apague el ordenador antes de modificar la capacidad del hdd");
            System.out.print("Quiere apagar el ordenador y realizar los cambios? [S/N] ");
            String opcion = sc.next();

            if (opcion.equalsIgnoreCase("S")){ //si decide apagar el ordenador
                apagar(); //se apaga
                modificarCapacidadTotalHdd(capacidad, aumentar); //se llama a si mismo, pero el ordenador ahora esta apagado
            } else{ //si no decide apagar, no se pueden realizar los cambios
                System.out.println("Finalizando proceso de modificacion de la capacidad del HDD");
            }

        } else{ //si esta apagado, se podran realizar los cambios

            if (aumentar){//si se quiere aumentar la capacidad total
                hddTotal += capacidad; //se aumenta la capacidad total
            } else { //si se quiere disminuir la capacidad total
                if (getHddTotal()-capacidad<0){ //se comprueba si se puede restar la capacidad especificada
                    System.out.println("Error, no se puede quitar tanta capacidad"); //se avisa al usuario que no se puede realizar el cambio
                } else{ //si todo esta correcto, se efectua el cambio
                    hddTotal -= capacidad; //se resta la capacidad total
                }
            }
        }
    }

    //se modifica la cantidad total de ram
    public void modificarCapacidadTotalRam(double capacidad, boolean aumentar){
        if (isEncendido()){ //evaluacion de que el ordenador esta encendido
            System.out.println("Apague el ordenador antes de modificar la capacidad de la RAM");
            System.out.print("Quiere apagar el ordenador y realizar los cambios? [S/N] ");
            String opcion = sc.next();

            if (opcion.equalsIgnoreCase("S")){ //si decide apagar el ordenador
                apagar(); //se apaga
                modificarCapacidadTotalRam(capacidad, aumentar); //se llama a si mismo, pero el ordenador ahora esta apagado
            } else{ //si no decide apagar, no se pueden realizar los cambios
                System.out.println("Finalizando proceso de modificacion de la capacidad de la RAM");
            }

        } else{ //si esta apagado, se podran realizar los cambios

            if (aumentar){//si se quiere aumentar la capacidad total
                this.ramTotal += capacidad; //se aumenta la capacidad total
            } else { //si se quiere disminuir la capacidad total
                if (getRamTotal()-capacidad<0){ //se comprueba si se puede restar la capacidad especificada
                    System.out.println("Error, no se puede quitar tanta capacidad"); //se avisa al usuario que no se puede realizar el cambio
                } else{ //si todo esta correcto, se efectua el cambio
                    this.ramTotal -= capacidad; //se resta la capacidad total
                }
            }
        }
    }

    //getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getMemRam() {
        return memRam;
    }

    public void setMemRam(double memRam) {
        this.memRam = memRam;
    }

    public double getRamTotal() {
        return ramTotal;
    }

    public void setRamTotal(double ramTotal) {
        this.ramTotal = ramTotal;
    }

    public double getCapacidadHddLibre() {
        return capacidadHddLibre;
    }

    public void setCapacidadHddLibre(double capacidadHddLibre) {
        this.capacidadHddLibre = capacidadHddLibre;
    }

    public double getHddTotal() {
        return hddTotal;
    }

    public void setHddTotal(double hddTotal) {
        this.hddTotal = hddTotal;
    }

    public String getArquitecturaCpu() {
        return arquitecturaCpu;
    }

    public void setArquitecturaCpu(String arquitecturaCpu) {
        this.arquitecturaCpu = arquitecturaCpu;
    }

    public SistemaOperativo getSo() {
        return so;
    }

    public void setSo(SistemaOperativo so) {
        this.so = so;
    }

    public boolean isEncendido() {
        return encendido;
    }

    public void setEncendido(boolean encendido) {
        this.encendido = encendido;
    }
}