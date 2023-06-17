import java.sql.SQLException;

import com.mongodb.MongoException;

import java.util.Scanner;

public class Main {
    String user="postgres";
    String pass="2118";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("Bienvenido a la aplicacion de base de datos eventos");


        try {
            int opcion;
            do {
                System.out.println("Seleccione una opción:");
                System.out.println("1. Ingresar");
                System.out.println("2. Modificar");
                System.out.println("3. Eliminar");
                System.out.println("4. Consultar");
                System.out.println("5. Consultar todos los eventos");
                System.out.println("6. Salir");

                opcion = sc.nextInt();

                switch (opcion) {
                    case 1:
                        System.out.println("¿Qué desea ingresar?");
                        ingresar ingresa = new ingresar();
                        ingresa.ingresa_menu(Menu_coleccion());
                        break;
                    case 2:
                        System.out.println("¿Qué desea modificar?");
                        /*Modificar modifica = new Modificar();
                        modifica.modificar_menu(Menu_coleccion());*/
                        break;
                    case 3:
                        System.out.println("¿Qué desea eliminar?");
                        eliminar elimina = new eliminar();
                        elimina.elimina_menu(Menu_coleccion());
                        break;
                    case 4:
                        System.out.println("¿Qué desea consultar?");
                        consultar consulta = new consultar();
                        consulta.consultar_menu(Menu_coleccion());
                        break;
                    case 5:
                        System.out.println("¿Desea consultar todos los eventos?");
                        // Agrega la lógica para consultar todos los eventos
                        break;
                    case 6:
                        System.out.println("Gracias por usar la aplicación");
                        break;
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
            } while (opcion != 6);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (MongoException e) {
            throw new RuntimeException(e);
        }
    }

    public static int Menu_coleccion() {
        System.out.println("1- Facultades\n");
        System.out.println("2- Areas\n");
        System.out.println("3- Programas\n");
        System.out.println("4- Empleados\n");
        System.out.println("5- Tipos de empleados\n");
        System.out.println("6- Tipos de contratacion\n");
        System.out.println("7- Sedes\n");
        System.out.println("8- Ciudades\n");
        System.out.println("9- Departamentos\n");
        System.out.println("10- Paises\n");
        Scanner sc = new Scanner(System.in);
        int opcion = sc.nextInt();
        return opcion;
    }
}