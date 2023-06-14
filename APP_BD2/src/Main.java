import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        System.out.println("Bienvenido a la aplicacion de base de datos eventos");
        System.out.println("Por favor ingrese su usuario y contraseña de postgresql");
        System.out.println("Usuario: ");
        Scanner sc = new Scanner(System.in);
        String user = sc.nextLine();
        System.out.println("Contraseña: ");
        String pass = sc.nextLine();


        System.out.println("-------------MENU-------------\n"+
                "1. Crear un evento\n"+
                "2. Modificar un evento\n"+
                "3. Eliminar un evento\n"+
                "4. Consultar un evento\n"+
                "5. Consultar todos los eventos\n"+
                "6. Salir\n");

        int opcion = sc.nextInt();


        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BD_EVENTOS", user, pass);
            System.out.println("conexion realizada con exito!");
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }catch (MongoException e){
            throw new RuntimeException(e);
        }
    }
    public int Menu_coleccion(){
        System.out.println("1- Facultades"+
                "2- Areas"+
                "3- Programas"+
                "4- Empleados"+
                "5- Tipos de empleados"+
                "6- Tipos de contratacion"+
                "7- Sedes"+
                "8- Ciudades"+
                "9- Departamentos"+
                "10- Paises");
        Scanner sc = new Scanner(System.in);
        int opcion = sc.nextInt();
        return opcion;
    }
}