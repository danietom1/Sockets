import java.io.*;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {

        final String HOST ="192.168.0.14";
        final int PUERTO = 5000;

        DataInputStream in;
        DataOutputStream out;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            Socket CSock = new Socket(HOST,PUERTO);

            in = new DataInputStream(CSock.getInputStream());
            out = new DataOutputStream(CSock.getOutputStream());

            System.out.println("Seleccione su solicitud");
            System.out.println("1 - Para traer informacion del cliente. Estructura de solicitud: '1, Id del cliente'");
            System.out.println("2 - Para agregar saldo a una cuenta. Estructura de solicitud: '2, Id del cliente, saldo a agregar'");
            System.out.println("3 - Para restar saldo a una cuenta. Estructura de solicitud: '3, Id del cliente, saldo a restar'");
            System.out.println("4 - Salir");

            String str = br.readLine();

            //out.writeUTF("Mensaje desde Cliente");
            out.writeUTF(str);
            //Procesando
            System.out.println("Procesando");

            String[] cadena = null;
            cadena = str.split(",");

            switch (Integer.parseInt(cadena[0])){
                case 1:
                    System.out.println("ID Nombre Apellido Saldo");
                    System.out.println(in.readUTF());
                    break;
                default:
                    System.out.println("ID Nombre Apellido SaldoAnterior");
                    System.out.println(in.readUTF());
                    System.out.println("ID Nombre Apellido SaldoNuevo");
                    System.out.println(in.readUTF());
                    break;
            }


            CSock.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
