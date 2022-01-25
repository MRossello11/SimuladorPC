import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**@author Miquel Andreu Rossello Mas
 * @version 1.6
 * @since 13/01/21
 * @description El Ordenador se encarga del encendido del mismo y de la secuencia de inicio. Estos,
 * son los métodos encargados de la puesta en marcha (comprobar que haya SO y elegir desde cual bootear
 * si hay mas de un SO instalado o instalar uno si no se encuentra ningún SO).
 **/

//nota: todas las medidas de almacenamiento se encuentran en MB

public class Ordenador {
    //objeto File donde se guardara la informacion del Sistema Operativo
    File archivoSO = new File("SO.txt");
    File archivoSOB = new File("SOB.txt");
    private File archivoSistemasOperativos;

    Scanner sc = new Scanner(System.in); //var escaner

    //variables estaticas (sistemas operativos base)
    private static final SistemaOperativo CustomOS = new SistemaOperativo("CustomOS", "20.04_FocalFossa", "x86", false, 50000, 2000);
    private static final SistemaOperativo Ubuntu = new SistemaOperativo("Ubuntu", "20.04_FocalFossa", "x86", false, 25000, 4096);
    private static final SistemaOperativo Windows10 = new SistemaOperativo("Windows10", "10", "x86", false, 40000, 4000);
    private static final SistemaOperativo ArchLinux = new SistemaOperativo("ArchLinux", "5.15.12", "x86", true, 10000, 512);
    private static final SistemaOperativo[] SOs = {CustomOS,Ubuntu,Windows10,ArchLinux};

    //atributos
    private String nombre; //nombre del ordenador
    private double memRam; //memoria ram libre
    private double ramTotal; //memoria ram total
    private double capacidadHddLibre; //capacidad hdd libre
    private double hddTotal; //capacidad total del hdd
    private String arquitecturaCpu; //arquitectura que usa la cpu
    private SistemaOperativo so; //so del ordenador
    private ArrayList<SistemaOperativo> sistemasOperativos = new ArrayList<SistemaOperativo>(); //array de sistemasOperativos instalados
    private boolean encendido; //indicador del estado del ordenador (true==encendido, false==apagado)


    //constructor (PC sin SO)
    public Ordenador(String nombre, double memoria, double almacenamiento) {
        this.nombre = nombre;
        this.memRam = memoria;
        this.ramTotal = memoria;
        this.capacidadHddLibre = almacenamiento;
        this.hddTotal = almacenamiento;
        this.encendido = false;
        this.archivoSistemasOperativos = archivoSO;
    }

    //metodos
    /**SECUENCIA DE INICIO*/
    //pone en marcha el ordenador
    public void encender(){
        setEncendido(true); //se enciende el ordenador
        comprobarSO(); //comprobacion de si hay SO instalado
    }

    //comprueba si hay SO instalado o no
    private void comprobarSO(){
        //comprobacion de si hay SO instalado
        try{
            BufferedReader bff = new BufferedReader(new FileReader(archivoSO));
            setArchivoSistemasOperativos(archivoSO);
            //se mira si el archivo esta vacio
            if (!bff.ready()){
                //si lo esta, se asigna como ruta en uso la secundaria
                bff = new BufferedReader(new FileReader(archivoSOB));
                setArchivoSistemasOperativos(archivoSOB);
            }
            //si el archivo esta vacio, quiere decir que no hay SO
            if (bff.readLine()==null){ //el archivo esta vacio
                System.out.println("No se encontro ningun Sistema Operativo instalado");
                encenderPCPrimeraVez();

            } else {
                encenderPC(); //encendido normal
            }

            bff.close();

        } catch (IOException e){
            try { //en caso de que no existan los archivos, estos se crean
                FileWriter fw = new FileWriter(archivoSO);
                fw.close();
                fw = new FileWriter(archivoSOB);
                fw.close();
                comprobarSO(); //se llama a si mismo para continuar la secuencia de inicio
            } catch (IOException e1){
                System.out.println("Error de comprobacion");
            }
        }
    }

    //para encender el ordenador cuando no hay SO instalado
    private void encenderPCPrimeraVez(){
        //al ser la primera vez que se enciende el PC, se tiene que instalar un SO antes de ir al bucle de ejecucion
        instalarSO();
        ordenadorEnEjecucion();
    }

    //instalar sistema operativo
    private void instalarSO(){
        System.out.println("Selecciona que SO instalar: ");

        for (int i = 0; i < SOs.length; i++){ //se muestran todas las opciones de SO que hay
            System.out.println(SOs[i].getNombre() + " (" + i + ")");
        }

        int eleccion = sc.nextInt(); //se recoge la eleccion del usuario
        SistemaOperativo soTemp = SOs[eleccion]; //objeto SO temporal

        //evaluacion de si hay los recursos necesarios para instalar el SO en el ordenador
        if (soTemp.getEspacioRequerido()<=getCapacidadHddLibre() && soTemp.getMemRequerida()<=getMemRam()){

            //se restan los recursos usados
            this.capacidadHddLibre -= soTemp.getEspacioRequerido();
            this.memRam -= soTemp.getMemRequerida(); //se interpreta que la ram requerida por el SO siempre esta en uso

            //se recogen los datos en el archivo SO.txt para el proximo encendido
            try{
                BufferedReader bf = new BufferedReader(new FileReader(getArchivoSistemasOperativos()));

                //se comprueba si el archivo esta vacio (y si lo esta, cambiar la ruta estandar)
                if (!bf.ready()){
                    if (getArchivoSistemasOperativos().equals(archivoSO)){
                        setArchivoSistemasOperativos(archivoSOB);
                    } else {
                        setArchivoSistemasOperativos(archivoSOB);
                    }
                }
                bf.close();

                //se escriben los datos del sistema operativo para el siguiente boot
                FileWriter fw = new FileWriter(getArchivoSistemasOperativos(), true);

                fw.write(soTemp.getNombre()+"\n");
                fw.write(soTemp.getVersion()+"\n");
                fw.write(soTemp.getArquitectura()+"\n");
                fw.write(soTemp.isOnlyCommands() +"\n");
                fw.write(soTemp.getEspacioRequerido() +"\n");
                fw.write(soTemp.getMemRequerida() +"\n");
                fw.write("EOOS\n"); //(End Of Operating System) para diferenciar entre sistemas operativos
                fw.close(); //cierre del stream

            } catch (IOException e){
                System.out.println("Error de escritura de lo atributos del SO");
            }

            //creacion del sistema operativo
            this.so = new SistemaOperativo(soTemp.getNombre(), soTemp.getVersion(), soTemp.getArquitectura(),
                    soTemp.isOnlyCommands(), soTemp.getEspacioRequerido(), soTemp.getMemRequerida(),
                    getHddTotal()- soTemp.getEspacioRequerido(), getRamTotal()-soTemp.getMemRequerida(), true);

            System.out.println("Instalacion completada");

        } else {
            System.out.println("Error, no hay recursos suficientes");
        }
    }

    //para encender el ordenador cuando hay SO
    private void encenderPC() {
        int numSO = recogerSO(); //se recoge el sistema operativo y se cuenta cuantos hay
        if (numSO>1){ //si hay mas de un sistema operativo, se muestran los que hay para elegir
            setSo(getSistemasOperativos().get(elegirSO())); //se asigna la eleccion al sistema operativo principal
        } else { //si solo hay un sistema operativo
            setSo(getSistemasOperativos().get(0));
        }
        //se restan los recursos que ocupa el SO
        this.capacidadHddLibre -= getSo().getEspacioRequerido();
        this.memRam -= getSo().getMemRequerida();

        //empieza la ejecucion del ordenador
        ordenadorEnEjecucion();
    }

    //bucle de ejecucion
    public void ordenadorEnEjecucion(){

        System.out.println("Bienvenido a " + getSo().getNombre() + " version " + getSo().getVersion());
        //mientras el ordenador este encendido se muestra el menu del SO (y se eligen las opciones)
        while(!isEncendido() || !getSo().isApagar()){
            getSo().mostrarMenu();
        }
        System.out.println("Sistema apagado"); //ultimo mensaje
    }

    //lee y recoge la informacion de los Sistemas Operativos instalados en el archivo
    private int recogerSO(){

        try{
            BufferedReader br = new BufferedReader(new FileReader(getArchivoSistemasOperativos()));

            String leerLogs = br.readLine();
            ArrayList<String> datosSO = new ArrayList<String>(); //array que almacenara temporalmente los datos del SO

            int contadorSO = 1; //cuenta la cantidad de SO que hay instalados en el sistema

            //se lee hasta llegar al final del documento
            while (leerLogs!=null){
                datosSO.add(leerLogs);
                leerLogs = br.readLine();
                if (leerLogs.equals("EOOS")){ //si se llega al final del SO, se comprueba si este es el ultimo
                    leerLogs = br.readLine();
                    if (leerLogs!=null){
                        contadorSO++; //si no es el ultimo, hay mas SO instalados (se aumenta el contador)
                    }
                }
            }

            br.close();

            //se crean tantos objeto SistemaOperativo como se hayan encontrado
            for (int i = 0; i<contadorSO; i++){
                SistemaOperativo soTemp = new SistemaOperativo(datosSO.get(0),datosSO.get(1),datosSO.get(2),Boolean.parseBoolean(datosSO.get(3)),
                        Double.parseDouble(datosSO.get(4)), Double.parseDouble(datosSO.get(5)),
                        getHddTotal()-Double.parseDouble(datosSO.get(4)), getRamTotal()-Double.parseDouble(datosSO.get(5)), true);
                this.sistemasOperativos.add(soTemp); //se agnade el objeto al array de sistemasOperativos instalados
                datosSO.subList(0, 6).clear(); //se limpian los datos del anterior sistema operativo (soTemp)
            }

            return contadorSO; //se devuelve el numero de SO instalados para tenerlo en cuenta a la hora de elegir cual bootear
        } catch (IOException e){
            System.out.println("Error recogiendo el SO");
            return 0;
        }
    }

    //muestra los Sistemas Operativos que se pueden instalar
    public int elegirSO(){
        System.out.println("Elija un sistema operativo desde el que bootear: ");
        for (int i = 0; i<getSistemasOperativos().size();i++){
            System.out.println(getSistemasOperativos().get(i) + " (" + i + ")");
        }
        return sc.nextInt();
    }
    /**Fin de la secuencia de inicio*/

    //envia una segnal de apagado del sistema
    public void apagar(){
        this.setEncendido(false);
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