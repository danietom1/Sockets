import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.logging.Logger;

public class Servidor {
    public static void main(String[] args) {

        //Socket config
        ServerSocket servidor1 = null;
        Socket  CSock = null;
        DataInputStream in;
        DataOutputStream out;

        final int PUERTO = 5000;





        try {
            servidor1 = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado");

            while (true){
                CSock = servidor1.accept();
                System.out.println("Cliente conectado");
                in = new DataInputStream(CSock.getInputStream());
                out = new DataOutputStream(CSock.getOutputStream());

                String mensaje = in.readUTF();
                System.out.println("Peticion recibida: "+mensaje);

                String[] cadena = null;
                cadena = mensaje.split(",");
                int opc = 0, cuenta = 0;
                double valor = 0;

                String respuesta = null;
                ResultSet set2 = null;

                switch (Integer.parseInt(cadena[0])){
                    case 1:
                        for (int i = 0; i < 2; i++) {
                            switch (i) {
                                case 0:
                                    opc = Integer.parseInt(cadena[i]);
                                    break;
                                case 1:
                                    cuenta = Integer.parseInt(cadena[i]);
                                    break;
                            }
                        }
                        if (cuenta > 0) {
                            respuesta = procesar(opc,cuenta,0);
                            out.writeUTF(respuesta);

                        }else {
                            out.writeUTF("Datos ingresados no validos");
                        }
                        break;
                    default:
                        for (int i = 0; i < 3; i++) {
                            switch (i) {
                                case 0:
                                    opc = Integer.parseInt(cadena[i]);
                                    break;
                                case 1:
                                    cuenta = Integer.parseInt(cadena[i]);
                                    break;
                                case 2:
                                    valor = Double.parseDouble(cadena[i]);
                                    break;
                            }
                        }
                        if (valor > 0) {
                            respuesta = procesar(1,cuenta,0);
                            out.writeUTF(respuesta);
                            respuesta = procesar(opc,cuenta,valor);
                            out.writeUTF(respuesta);
                        }else {
                            out.writeUTF("Datos ingresados no validos");
                        }
                        break;
                }



                CSock.close();
                System.out.println("Cliente desconectado");

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static String procesar(int opcion, int id, double valor) {
        //MySQL config
        String basedatos = "sockets";
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String psw = "Poli123*";
        String driver = "com.mysql.cj.jdbc.Driver";
        Connection cx;


        try {
            Class.forName(driver);
            cx= DriverManager.getConnection(url+basedatos,user,psw);
            System.out.println("Conectado a base de datos.");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Problema al conectar a la base de datos.");
            throw new RuntimeException(e);
        }

        String Respuesta = "";
        ResultSet SetTemp = null;
        switch (opcion){
            case 1:
                try {
                    Statement s = cx.createStatement();
                    SetTemp = s.executeQuery ("select * from Saldos where Saldo_id = "+id);

                    SetTemp.next();
                    Respuesta = SetTemp.getInt (1) + "  " + SetTemp.getString (2)+ "   " + SetTemp.getString(3)+" "+ SetTemp.getString(4);
                } catch (SQLException e) {
                    Respuesta = "Problema en la consulta";
                }

                break;
            case 2:
                try {
                    Statement s = cx.createStatement();
                    SetTemp = s.executeQuery ("select * from Saldos where Saldo_id = "+id);
                    SetTemp.next();
                    double Temp = SetTemp.getDouble(4);
                    Temp = Temp + valor;

                    PreparedStatement ps = cx.prepareStatement("Update Saldos set Saldo_valor = ? where Saldo_id = ?");
                    ps.setDouble(1,Temp);
                    ps.setInt(2,id);
                    ps.executeUpdate();

                    Statement c = cx.createStatement();
                    SetTemp = c.executeQuery ("select * from Saldos where Saldo_id = "+id);
                    SetTemp.next();
                    Respuesta = SetTemp.getInt (1) + "  " + SetTemp.getString (2)+ "   " + SetTemp.getString(3)+" "+ SetTemp.getString(4);


                } catch (SQLException e) {
                    System.out.println("Problema en la consulta");
                }
                break;
            case 3:
                try {
                    Statement s = cx.createStatement();
                    SetTemp = s.executeQuery ("select * from Saldos where Saldo_id = "+id);
                    SetTemp.next();
                    double Temp = SetTemp.getDouble(4);
                    Temp = Temp - valor;

                    if (Temp >= 0){
                        PreparedStatement ps = cx.prepareStatement("Update Saldos set Saldo_valor = ? where Saldo_id = ?");
                        ps.setDouble(1,Temp);
                        ps.setInt(2,id);
                        ps.executeUpdate();

                        Statement c = cx.createStatement();
                        SetTemp = c.executeQuery ("select * from Saldos where Saldo_id = "+id);
                        SetTemp.next();
                        Respuesta = SetTemp.getInt (1) + "  " + SetTemp.getString (2)+ "   " + SetTemp.getString(3)+" "+ SetTemp.getString(4);
                    }else {
                        Respuesta = "FONDOS INSUFICIENTES";
                    }


                } catch (SQLException e) {
                    System.out.println("Problema en la consulta");
                }
                break;
        }

        return Respuesta;
    }
}

