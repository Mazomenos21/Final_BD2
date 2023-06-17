import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import java.sql.*;
import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class Consultar extends Main {
    //Escaner
    Scanner sc = new Scanner(System.in);


    //------------------------------------MENU------------------------------------------------------------------------
    public void consultar_menu(int opcion) throws SQLException {
        switch (opcion) {
            case 1:
                consultar_facultad();
                break;
            case 2:
                consultar_area();
                break;
            case 3:
                consultar_programa();
                break;
            case 4:
                consultar_empleado();
                break;
            case 5:
                consultar_tipos_empleado();
                break;
            case 6:
                consultar_tipos_contratacion();
                break;
            case 7:
                consultar_sede();
                break;
            case 8:
                consultar_ciudad();
                break;
            case 9:
                consultar_departamento();
                break;
            case 10:
                consultar_pais();
                break;
        }
    }

//--------------------------------------------CONEXIONES--------------------------------------------------------------
    // conexion para postgres

    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BD_EVENTOS", user, pass);
            return connection;
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos");
        }
        return null;
    }


    // conexion para mongo

    public MongoDatabase getMongoConnection() throws SQLException {
        String mongoConnectionString = "mongodb://localhost:27017/BD_EVENTOS";
        MongoClientURI mongoUri = new MongoClientURI(mongoConnectionString);
        MongoClient mongoClient = new MongoClient(mongoUri);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("BD_EVENTOS");
        return mongoDatabase;
    }


    //------------------------------------------FIN CONEXIONES------------------------------------------------------------


//------------------------------VERIFICAR EXISTENCIA DE DATOS EN POSTGRESQL----------------------------------------------


    // Verificar la existencia
    private static boolean verificarExistencia(Connection connection, int DATO, String N_coleccion, String Codigo) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + N_coleccion + " WHERE " + Codigo + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, DATO);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    // Verificar la existencia
    private static boolean verificarExistenciaS(Connection connection, String DATO, String N_coleccion, String nom_columna) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + N_coleccion + " WHERE" + nom_columna + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, DATO);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String count = resultSet.getString(1);
                    return Integer.parseInt(count) > 0;
                }
            }
        }
        return false;
    }

//------------------------------------------FIN VERIFICAR EXISTENCIA------------------------------------------------------


    //------------------------------------------CONSULTAR DATOS------------------------------------------------------------


    //--------------------------------------CONSULTAR FACULTAD-----------------------------------------------------
    public void consultar_facultad() throws SQLException {

        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("FACULTADES");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el código de la FACULTAD que desea consultar: ");
        int codigoF = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente


        //consultar postgres
        try {
            //consultar si existe el codigo en postgres
            String query = "SELECT codigo,nombre,nro_telefono,ubicacion,id_decano FROM facultades WHERE codigo = ?";

            // Valor a buscar
            int valorBuscado = codigoF;

            // Preparar la consulta
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, valorBuscado);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Recorrer los resultados y mostrarlos en pantalla
            while (resultSet.next()) {
                int codigo = resultSet.getInt("codigo");
                String nombre = resultSet.getString("nombre");
                String nro_telefono = resultSet.getString("nro_telefono");
                String ubicacion = resultSet.getString("ubicacion");
                String id_decano = resultSet.getString("id_decano");
                System.out.println("Los datos consultados en postgres son: \n");
                System.out.println("Codigo: " + codigo + "\n Nombre: " + nombre + "\n Nro_telefono: " + nro_telefono + "\n Ubicacion: " + ubicacion + "\n Id_decano: " + id_decano);
            }

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al consultar la facultad");
            e.printStackTrace();
        }
        try {

            // Consultar en MongoDB
            Document query = new Document("codigo", codigoF);
            MongoCursor<Document> cursor = mongoCollectionFacultades.find(query).iterator();

            // Recorrer los resultados y mostrarlos en pantalla
            while (cursor.hasNext()) {
                Document document = cursor.next();
                int codigo = document.getInteger("codigo");
                String nombre = document.getString("nombre");
                String nro_telefono = document.getString("nro_telefono");
                String ubicacion = document.getString("ubicacion");
                String id_decano = document.getString("id_decano");

                System.out.println("Los datos consultados en mongo son: ");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Nro_telefono: " + nro_telefono);
                System.out.println("Ubicacion: " + ubicacion);
                System.out.println("Id_decano: " + id_decano);
            }

            // Cerrar el cursor
            cursor.close();
        } catch (MongoException e) {
            System.out.println("Error al consultar la facultad en MongoDB");
            e.printStackTrace();
        }
    }


    //--------------------------------------CONSULTAR AREA-----------------------------------------------------

    public void consultar_area() throws SQLException {

        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("AREAS");

        // Solicitar el código del AREA que desea eliminar
        System.out.println("Ingrese el código del AREA que desea consultar: ");
        int codigoF = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente


        //consultar postgres
        try {
            //consultar si existe el codigo en postgres
            String query = "SELECT codigo,nombre,facultades_codigo,id_coordinador FROM areas WHERE codigo = ?";

            // Valor a buscar
            int valorBuscado = codigoF;

            // Preparar la consulta
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, valorBuscado);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Recorrer los resultados y mostrarlos en pantalla
            while (resultSet.next()) {
                int codigo = resultSet.getInt("codigo");
                String nombre = resultSet.getString("nombre");
                int facultades_codigo = resultSet.getInt("facultades_codigo");
                String id_coordinador = resultSet.getString("id_coordinador");
                System.out.println("Los datos consultados en postgres son: \n");
                System.out.println("Codigo: " + codigo + "\n Nombre: " + nombre + "\n Codigo de facultad: " + facultades_codigo + "\n Id del coordinador: " + id_coordinador);
            }

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al consultar el area");
            e.printStackTrace();
        }
        try {

            // Consultar en MongoDB
            Document query = new Document("codigo", codigoF);
            MongoCursor<Document> cursor = mongoCollectionFacultades.find(query).iterator();

            // Recorrer los resultados y mostrarlos en pantalla
            while (cursor.hasNext()) {
                Document document = cursor.next();
                int codigo = document.getInteger("codigo");
                String nombre = document.getString("nombre");
                int facultades_codigo = document.getInteger("facultades_codigo");
                String id_coordinador = document.getString("id_coordinador");


                System.out.println("Los datos consultados en mongo son: ");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Codigo de facultad: " + facultades_codigo);
                System.out.println("Id del coordinador: " + id_coordinador);

            }

            // Cerrar el cursor
            cursor.close();
        } catch (MongoException e) {
            System.out.println("Error al consultar el area en MongoDB");
            e.printStackTrace();
        }

    }


    //--------------------------------------CONSULTAR PROGRAMA-----------------------------------------------------
    public void consultar_programa() throws SQLException {

        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("PROGRAMAS");

        // Solicitar el código del PROGRAMA que desea eliminar
        System.out.println("Ingrese el código del PROGRAMA que desea consultar: ");
        int codigoF = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente


        //consultar postgres
        try {
            //consultar si existe el codigo en postgres
            String query = "SELECT codigo,nombre,areas_codigo FROM programas WHERE codigo = ?";

            // Valor a buscar
            int valorBuscado = codigoF;

            // Preparar la consulta
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, valorBuscado);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Recorrer los resultados y mostrarlos en pantalla
            while (resultSet.next()) {
                int codigo = resultSet.getInt("codigo");
                String nombre = resultSet.getString("nombre");
                int areas_codigo = resultSet.getInt("areas_codigo");
                System.out.println("Los datos consultados en postgres son: \n");
                System.out.println("Codigo: " + codigo + "\n Nombre: " + nombre + "\n Codigo del area" + areas_codigo);
            }

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al consultar la facultad");
            e.printStackTrace();
        }
        try {

            // Consultar en MongoDB
            Document query = new Document("codigo", codigoF);
            MongoCursor<Document> cursor = mongoCollectionFacultades.find(query).iterator();

            // Recorrer los resultados y mostrarlos en pantalla
            while (cursor.hasNext()) {
                Document document = cursor.next();
                int codigo = document.getInteger("codigo");
                String nombre = document.getString("nombre");
                int Codigo_areas = document.getInteger("facultades_codigo");


                System.out.println("Los datos consultados en mongo son: ");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Codigo de Area: " + Codigo_areas);

            }

            // Cerrar el cursor
            cursor.close();
        } catch (MongoException e) {
            System.out.println("Error al consultar el programa en MongoDB");
            e.printStackTrace();
        }


    }


    //--------------------------------------CONSULTAR TIPO DE CONTRTACION----------------------------------------------------
    public void consultar_tipos_contratacion() throws SQLException {
        System.out.println("NO HAY NECESIDAD DE BUSCAR EN TIPOS DE CONTRATACION PORQUE SOLO TIENE UN CAMPO, UTILICE MEJOR: 'MOSTRAR TODO'");
    }


    //--------------------------------------CONSULTAR SEDE-----------------------------------------------------

    public void consultar_sede() throws SQLException {
        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("SEDES");

        // Solicitar el código del PROGRAMA que desea eliminar
        System.out.println("Ingrese el código de la SEDE que desea consultar: ");
        int codigoF = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente


        //consultar postgres
        try {
            //consultar si existe el codigo en postgres
            String query = "SELECT codigo,nombre,cod_ciudad FROM sedes WHERE codigo = ?";

            // Valor a buscar
            int valorBuscado = codigoF;

            // Preparar la consulta
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, valorBuscado);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Recorrer los resultados y mostrarlos en pantalla
            while (resultSet.next()) {
                int codigo = resultSet.getInt("codigo");
                String nombre = resultSet.getString("nombre");
                int cod_ciudad = resultSet.getInt("cod_ciudad");
                System.out.println("Los datos consultados en postgres son: \n");
                System.out.println("Codigo: " + codigo + "\n Nombre: " + nombre + "\n Codigo de la ciudad" + cod_ciudad);
            }

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al consultar la facultad");
            e.printStackTrace();
        }
        try {

            // Consultar en MongoDB
            Document query = new Document("codigo", codigoF);
            MongoCursor<Document> cursor = mongoCollectionFacultades.find(query).iterator();

            // Recorrer los resultados y mostrarlos en pantalla
            while (cursor.hasNext()) {
                Document document = cursor.next();
                int codigo = document.getInteger("codigo");
                String nombre = document.getString("nombre");
                int codigo_ciudad = document.getInteger("cod_ciudad");


                System.out.println("Los datos consultados en mongo son: ");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Codigo de la ciudad: " + codigo_ciudad);

            }

            // Cerrar el cursor
            cursor.close();
        } catch (MongoException e) {
            System.out.println("Error al consultar el programa en MongoDB");
            e.printStackTrace();
        }


    }

    //--------------------------------------CONSULTAR TIPOS DE EMPLEADOS-----------------------------------------------------
    public void consultar_tipos_empleado() throws SQLException {
        System.out.println("NO HAY NECESIDAD DE BUSCAR EN TIPOS DE CONTRATACION PORQUE SOLO TIENE UN CAMPO, UTILICE MEJOR: 'MOSTRAR TODO'");
    }

    //--------------------------------------CONSULTAR CIUDAD-----------------------------------------------------
    public void consultar_ciudad() throws SQLException {
        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("CIUDAD");

        // Solicitar el código del PROGRAMA que desea eliminar
        System.out.println("Ingrese el código de la CIUDAD que desea consultar: ");
        int codigoF = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente


        //consultar postgres
        try {
            //consultar si existe el codigo en postgres
            String query = "SELECT codigo,nombre,cod_dpto FROM ciudades WHERE codigo = ?";

            // Valor a buscar
            int valorBuscado = codigoF;

            // Preparar la consulta
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, valorBuscado);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Recorrer los resultados y mostrarlos en pantalla
            while (resultSet.next()) {
                int codigo = resultSet.getInt("codigo");
                String nombre = resultSet.getString("nombre");
                int cod_departamento = resultSet.getInt("cod_departamento");
                System.out.println("Los datos consultados en postgres son: \n");
                System.out.println("Codigo: " + codigo + "\n Nombre: " + nombre + "\n Codigo del departamento" + cod_departamento);
            }

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al consultar la facultad");
            e.printStackTrace();
        }
        try {

            // Consultar en MongoDB
            Document query = new Document("codigo", codigoF);
            MongoCursor<Document> cursor = mongoCollectionFacultades.find(query).iterator();

            // Recorrer los resultados y mostrarlos en pantalla
            while (cursor.hasNext()) {
                Document document = cursor.next();
                int codigo = document.getInteger("codigo");
                String nombre = document.getString("nombre");
                int codigo_departamento = document.getInteger("cod_departamento");


                System.out.println("Los datos consultados en mongo son: ");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Codigo del departamento: " + codigo_departamento);

            }

            // Cerrar el cursor
            cursor.close();
        } catch (MongoException e) {
            System.out.println("Error al consultar el programa en MongoDB");
            e.printStackTrace();
        }

    }

    //--------------------------------------CONSULTAR DEPARTAMENTO-----------------------------------------------------
    public void consultar_departamento() throws SQLException {
        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("DEPARTAMENTO");

        // Solicitar el código del PROGRAMA que desea eliminar
        System.out.println("Ingrese el código del DEPARTAMENTO que desea consultar: ");
        int codigoF = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente


        //consultar postgres
        try {
            //consultar si existe el codigo en postgres
            String query = "SELECT codigo,nombre,cod_pais FROM departamentos WHERE codigo = ?";

            // Valor a buscar
            int valorBuscado = codigoF;

            // Preparar la consulta
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, valorBuscado);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Recorrer los resultados y mostrarlos en pantalla
            while (resultSet.next()) {
                int codigo = resultSet.getInt("codigo");
                String nombre = resultSet.getString("nombre");
                int cod_pais = resultSet.getInt("cod_pais");
                System.out.println("Los datos consultados en postgres son: \n");
                System.out.println("Codigo: " + codigo + "\n Nombre: " + nombre + "\n Codigo del pais" + cod_pais);
            }

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al consultar la facultad");
            e.printStackTrace();
        }
        try {

            // Consultar en MongoDB
            Document query = new Document("codigo", codigoF);
            MongoCursor<Document> cursor = mongoCollectionFacultades.find(query).iterator();

            // Recorrer los resultados y mostrarlos en pantalla
            while (cursor.hasNext()) {
                Document document = cursor.next();
                int codigo = document.getInteger("codigo");
                String nombre = document.getString("nombre");
                int codigo_pais = document.getInteger("cod_pais");


                System.out.println("Los datos consultados en mongo son: ");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);
                System.out.println("Codigo del pais: " + codigo_pais);

            }

            // Cerrar el cursor
            cursor.close();
        } catch (MongoException e) {
            System.out.println("Error al consultar el programa en MongoDB");
            e.printStackTrace();
        }

    }

    //--------------------------------------CONSULTAR EMPLEADO-----------------------------------------------------
    public void consultar_empleado() {
        try {
            // Conexión a PostgreSQL
            Connection connection = getConnection();

            // Conexión a MongoDB
            MongoDatabase mongoDatabase = getMongoConnection();
            MongoCollection<Document> mongoCollectionEmpleados = mongoDatabase.getCollection("EMPLEADOS");

            // Solicitar el código del empleado que desea consultar
            System.out.println("Ingrese el código del empleado que desea consultar: ");
            int codigoF = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente


            // Consultar en PostgreSQL
            try {
                // Consultar si existe el código en PostgreSQL
                String query = "SELECT identificacion, nombres, apellidos, email, tipo_contratacion, tipo_empleado, cod_facultad, cod_sede, lugar_nacimiento FROM empleados WHERE identificacion = ?";

                // Valor a buscar
                int valorBuscado = codigoF;

                // Preparar la consulta
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, valorBuscado);

                // Ejecutar la consulta
                ResultSet resultSet = statement.executeQuery();

                // Recorrer los resultados y mostrarlos en pantalla
                while (resultSet.next()) {
                    int identificacion = resultSet.getInt("identificacion");
                    String nombres = resultSet.getString("nombres");
                    String apellidos = resultSet.getString("apellidos");
                    String email = resultSet.getString("email");
                    String tipo_contratacion = resultSet.getString("tipo_contratacion");
                    String tipo_empleado = resultSet.getString("tipo_empleado");
                    int cod_facultad = resultSet.getInt("cod_facultad");
                    int cod_sede = resultSet.getInt("cod_sede");
                    String lugar_nacimiento = resultSet.getString("lugar_nacimiento");

                    System.out.println("Los datos consultados en PostgreSQL son: ");
                    System.out.println("Identificación: " + identificacion);
                    System.out.println("Nombres: " + nombres);
                    System.out.println("Apellidos: " + apellidos);
                    System.out.println("Email: " + email);
                    System.out.println("Tipo de Contratación: " + tipo_contratacion);
                    System.out.println("Tipo de Empleado: " + tipo_empleado);
                    System.out.println("Código de Facultad: " + cod_facultad);
                    System.out.println("Código de Sede: " + cod_sede);
                    System.out.println("Lugar de Nacimiento: " + lugar_nacimiento);
                }

                // Cerrar recursos
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error al consultar el empleado en PostgreSQL");
                e.printStackTrace();
            }

            // Consultar en MongoDB
            try {
                Document query = new Document("codigo", codigoF);
                MongoCursor<Document> cursor = mongoCollectionEmpleados.find(query).iterator();

                // Recorrer los resultados y mostrarlos en pantalla
                while (cursor.hasNext()) {
                    Document document = cursor.next();
                    int identificacion = document.getInteger("identificacion");
                    String nombre = document.getString("nombre");
                    String apellidos = document.getString("apellidos");
                    String email = document.getString("email");
                    String tipo_contratacion = document.getString("tipo_contratacion");
                    String tipo_empleado = document.getString("tipo_empleado");
                    int cod_facultad = document.getInteger("cod_facultad");
                    int cod_sede = document.getInteger("cod_sede");
                    String lugar_nacimiento = document.getString("lugar_nacimiento");

                    System.out.println("Los datos consultados en MongoDB son: ");
                    System.out.println("Identificación: " + identificacion);
                    System.out.println("Nombres: " + nombre);
                    System.out.println("Apellidos: " + apellidos);
                    System.out.println("Email: " + email);
                    System.out.println("Tipo de Contratación: " + tipo_contratacion);
                    System.out.println("Tipo de Empleado: " + tipo_empleado);
                    System.out.println("Código de Facultad: " + cod_facultad);
                    System.out.println("Código de Sede: " + cod_sede);
                    System.out.println("Lugar de Nacimiento: " + lugar_nacimiento);


                }

                // Cerrar el cursor
                cursor.close();
            } catch (MongoException e) {
                System.out.println("Error al consultar el empleado en MongoDB");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error en la conexión a la base de datos");
            e.printStackTrace();
        }
    }


    //--------------------------------------CONSULTAR PAIS-----------------------------------------------------
    public void consultar_pais() throws SQLException {

        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("PAIS");

        // Solicitar el código del PROGRAMA que desea eliminar
        System.out.println("Ingrese el código del PAIS que desea consultar: ");
        int codigoF = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente


        //consultar postgres
        try {
            //consultar si existe el codigo en postgres
            String query = "SELECT codigo,nombre FROM paises WHERE codigo = ?";

            // Valor a buscar
            int valorBuscado = codigoF;

            // Preparar la consulta
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, valorBuscado);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Recorrer los resultados y mostrarlos en pantalla
            while (resultSet.next()) {
                int codigo = resultSet.getInt("codigo");
                String nombre = resultSet.getString("nombre");
                System.out.println("Los datos consultados en postgres son: \n");
                System.out.println("Codigo: " + codigo + "\n Nombre: " + nombre);
            }

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al consultar la facultad");
            e.printStackTrace();
        }
        try {

            // Consultar en MongoDB
            Document query = new Document("codigo", codigoF);
            MongoCursor<Document> cursor = mongoCollectionFacultades.find(query).iterator();

            // Recorrer los resultados y mostrarlos en pantalla
            while (cursor.hasNext()) {
                Document document = cursor.next();
                int codigo = document.getInteger("codigo");
                String nombre = document.getString("nombre");


                System.out.println("Los datos consultados en mongo son: ");
                System.out.println("Codigo: " + codigo);
                System.out.println("Nombre: " + nombre);

            }

            // Cerrar el cursor
            cursor.close();
        } catch (MongoException e) {
            System.out.println("Error al consultar el programa en MongoDB");
            e.printStackTrace();
        }

    }
}