import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import java.sql.*;
import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class Eliminar extends Main{
    Scanner sc = new Scanner(System.in);
    boolean sw = false;

    public void elimina_menu(int opcion) throws SQLException {
        switch (opcion) {
            case 1:
                sw = false;
                elimina_facultad();
                break;
            case 2:
                sw = false;
                elimina_area();
                break;
            case 3:
                sw = false;
                elimina_programa();
                break;
            case 4:
                sw = false;
                elimina_empleado();
                break;
            case 5:
                sw = false;
                elimina_tipos_empleado();
                break;
            case 6:
                sw = false;
                elimina_tipos_contratacion();
                break;
            case 7:
                sw = false;
                elimina_sede();
                break;
            case 8:
                sw = false;
                elimina_ciudad();
                break;
            case 9:
                sw = false;
                elimina_departamento();
                break;
            case 10:
                sw = false;
                elimina_pais();
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


    //------------------------------------------INGRESAR DATOS------------------------------------------------------------


    //--------------------------------------INGRESAR FACULTAD-----------------------------------------------------
    public void elimina_facultad() throws SQLException {

        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("FACULTADES");
        MongoCollection<Document> mongoCollectionAreas = mongoDatabase.getCollection("AREAS");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el código de la FACULTAD que desea eliminar: ");
        int codigoF = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay áreas asociadas a la facultad en PostgreSQL
        String postgresSelectQuery = "SELECT COUNT(*) FROM areas WHERE facultades_codigo = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectQuery);
            postgresSelectStatement.setInt(1, codigoF);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar la FACULTAD porque hay AREAS asociados a él en PostgreSQL");
                    return; // Salir del método si hay areas asociadas
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar EMPLEADOS asociados a la SEDE en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay áreas asociados a la facultad en MongoDB
        Document mongoAreasFilter = new Document("FACULTADES_CODIGO", codigoF);
        long count = mongoCollectionAreas.countDocuments(mongoAreasFilter);
        if (count > 0) {
            System.out.println("No se puede eliminar la FACULTAD porque hay AREAS asociadas a él en MongoDB");
            return; // Salir del método si hay areas asociados
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM facultades WHERE codigo = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setInt(1, codigoF);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos de la FACULTAD eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos de la FACULTAD para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos de la SEDE en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("CODIGO", codigoF);
        DeleteResult deleteResult = mongoCollectionFacultades.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos de la FACULTAD eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos de la FACULTAD para eliminar en MongoDB\n");
        }
    }


    //--------------------------------------INGRESAR AREA-----------------------------------------------------

    public void elimina_area() throws SQLException {

        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionAreas = mongoDatabase.getCollection("AREAS");
        MongoCollection<Document> mongoCollectionTipoProgramas = mongoDatabase.getCollection("PROGRAMAS");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el código del AREA que desea eliminar: ");
        int codigoA = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay programas asociados al áreas en PostgreSQL
        String postgresSelectQuery = "SELECT COUNT(*) FROM programas WHERE areas_codigo = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectQuery);
            postgresSelectStatement.setInt(1, codigoA);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar la AREAS porque hay PROGRAMAS asociados a ella en PostgreSQL");
                    return; // Salir del método si hay programas asociados
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar PROGRAMAS asociados a la AREA en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay programas asociados al área en MongoDB
        Document mongoProgramasFilter = new Document("AREAS_CODIGO", codigoA);
        long count = mongoCollectionTipoProgramas.countDocuments(mongoProgramasFilter);
        if (count > 0) {
            System.out.println("No se puede eliminar el AREA porque hay PROGRAMAS asociadas a él en MongoDB");
            return; // Salir del método si hay programas asociados
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM facultades WHERE codigo = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setInt(1, codigoA);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos de la FACULTAD eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos de la FACULTAD para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos de la SEDE en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("CODIGO", codigoA);
        DeleteResult deleteResult = mongoCollectionAreas.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos del ARl AREA eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos de la FACULTAD para eliminar en MongoDB\n");
        }
    }


    //--------------------------------------INGRESAR PROGRAMA-----------------------------------------------------
    public void elimina_programa() throws SQLException {
        // Conexion postgres
        Connection connection = getConnection();
        // Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionAreas = mongoDatabase.getCollection("AREAS");
        MongoCollection<Document> mongoCollectionTipoProgramas = mongoDatabase.getCollection("PROGRAMAS");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el nombre deL PROGRAMA que desea eliminar: ");
        String Programa = sc.nextLine();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay empleados asociadaos a la sede en PostgreSQL
        String postgresSelectQuery = "SELECT COUNT(*) FROM areas WHERE codigo = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectQuery);
            postgresSelectStatement.setString(1, Programa);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar el PROGRAMA porque hay AREAS asociadas a él en PostgreSQL");
                    return; // Salir del método si hay sedes asociadas
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar AREAS asociados al PROGRAMA en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay Areas asociados al PROGRAMA en MongoDB
        Document mongoSedesFilter = new Document("AREAS", Programa);
        long count = mongoCollectionTipoProgramas.countDocuments(mongoSedesFilter);
        if (count > 0) {
            System.out.println("No se puede eliminar el PROGRAMA porque hay AREAS asociadas a él en MongoDB");
            return; // Salir del método si hay empleados asociados
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM programas WHERE nombre = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setString(1, Programa);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos del PROGRAMA eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos del PROGRAMA para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos del PROGRAMA en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("NOMBRE", Programa);
        DeleteResult deleteResult = mongoCollectionTipoProgramas.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos del PROGRAMA eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos del PROGRAMA para eliminar en MongoDB\n");
        }

    }


    //--------------------------------------INGRESAR TIPO DE CONTRTACION----------------------------------------------------
    public void elimina_tipos_contratacion() throws SQLException {
        // Conexion postgres
        Connection connection = getConnection();
        // Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionEmpleados = mongoDatabase.getCollection("EMPLEADOS");
        MongoCollection<Document> mongoCollectionTipoContratacion = mongoDatabase.getCollection("TIPOS_CONTRATACION");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el nombre deL TIPO DE CONTRATACION que desea eliminar: ");
        String nombre_tipo_empleado = sc.nextLine();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay empleados asociadaos a la sede en PostgreSQL
        String postgresSelectQuery = "SELECT COUNT(*) FROM empleados WHERE tipo_contratacion = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectQuery);
            postgresSelectStatement.setString(1, nombre_tipo_empleado);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar el TIPO DE CONTRATACION porque hay EMPLEADOS asociados a él en PostgreSQL");
                    return; // Salir del método si hay sedes asociadas
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar EMPLEADOS asociados al TIPO DE CONTRATACION en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay empleados asociados a la sede en MongoDB
        Document mongoSedesFilter = new Document("TIPOS_CONTRATACION", nombre_tipo_empleado);
        long count = mongoCollectionTipoContratacion.countDocuments(mongoSedesFilter);
        if (count > 0) {
            System.out.println("No se puede eliminar el TIPOO DE CONTRATACION porque hay EMPLEADOS asociadas a él en MongoDB");
            return; // Salir del método si hay empleados asociados
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM tipos_contratacion WHERE nombre = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setString(1, nombre_tipo_empleado);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos del TIPO DE CONTRATACION eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos del TIPO DE CONTRATACION para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos del TIPO DE CONTRATACION en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("NOMBRE", nombre_tipo_empleado);
        DeleteResult deleteResult = mongoCollectionTipoContratacion.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos del TIPO DE CONTRATACION eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos del TIPO DE CONTRATACION para eliminar en MongoDB\n");
        }

    }


    //--------------------------------------INGRESAR SEDE-----------------------------------------------------

    public void elimina_sede() throws SQLException {

        // Conexion postgres
        Connection connection = getConnection();
        // Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionEmpleados = mongoDatabase.getCollection("EMPLEADOS");
        MongoCollection<Document> mongoCollectionSedes = mongoDatabase.getCollection("SEDES");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el código de la SEDE que desea eliminar: ");
        int codigoC = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay empleados asociadaos a la sede en PostgreSQL
        String postgresSelectQuery = "SELECT COUNT(*) FROM empleados WHERE lugar_nacimiento = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectQuery);
            postgresSelectStatement.setInt(1, codigoC);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar la SEDE porque hay EMPLEADOS asociados a él en PostgreSQL");
                    return; // Salir del método si hay sedes asociadas
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar EMPLEADOS asociados a la SEDE en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay empleados asociados a la sede en MongoDB
        Document mongoSedesFilter = new Document("SEDES", codigoC);
        long count = mongoCollectionSedes.countDocuments(mongoSedesFilter);
        if (count > 0) {
            System.out.println("No se puede eliminar la SEDE porque hay EMPLEADOS asociadas a él en MongoDB");
            return; // Salir del método si hay empleados asociados
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM sedes WHERE cod_ciudad = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setInt(1, codigoC);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos de la SEDE eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos de la SEDE para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos de la SEDE en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("CODIGO", codigoC);
        DeleteResult deleteResult = mongoCollectionSedes.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos de la SEDE eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos de la SEDE para eliminar en MongoDB\n");
        }
    }


    public void elimina_tipos_empleado() throws SQLException {
        // Conexion postgres
        Connection connection = getConnection();
        // Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionEmpleados = mongoDatabase.getCollection("EMPLEADOS");
        MongoCollection<Document> mongoCollectionTipoEmpleados = mongoDatabase.getCollection("TIPOS_EMPLEADOS");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el nombre deL TIPO DE EMEPLEADO que desea eliminar: ");
        String nombre_tipo_empleado = sc.nextLine();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay empleados asociadaos a la sede en PostgreSQL
        String postgresSelectQuery = "SELECT COUNT(*) FROM empleados WHERE tipo_empleado = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectQuery);
            postgresSelectStatement.setString(1, nombre_tipo_empleado);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar el TIPO DE EMPLEADO porque hay EMPLEADOS asociados a él en PostgreSQL");
                    return; // Salir del método si hay sedes asociadas
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar EMPLEADOS asociados al TIPO DE EMPLEADO en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay empleados asociados a la sede en MongoDB
        Document mongoSedesFilter = new Document("TIPOS_EMPLEADOS", nombre_tipo_empleado);
        long count = mongoCollectionTipoEmpleados.countDocuments(mongoSedesFilter);
        if (count > 0) {
            System.out.println("No se puede eliminar el TIPOO DE EMPLEADO porque hay EMPLEADOS asociadas a él en MongoDB");
            return; // Salir del método si hay empleados asociados
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM tipos_empleados WHERE nombre = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setString(1, nombre_tipo_empleado);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos del TIPO DE EMPLEADO eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos del TIPO DE EMPLEADO para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos del TIPO DE EMPLEADO en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("NOMBRE", nombre_tipo_empleado);
        DeleteResult deleteResult = mongoCollectionTipoEmpleados.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos del TIPO DE EMPLEADO eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos del TIPO DE EMPLEADO para eliminar en MongoDB\n");
        }

    }

    public void elimina_ciudad() throws SQLException {

        // Conexion postgres
        Connection connection = getConnection();
        // Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionCiudades = mongoDatabase.getCollection("CIUDADES");
        MongoCollection<Document> mongoCollectionSedes = mongoDatabase.getCollection("SEDES");
        MongoCollection<Document> mongoCollectionEmpleados = mongoDatabase.getCollection("EMPLEADOS");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el código de la CUIDAD que desea eliminar: ");
        int codigoC = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay sedes asociadaos a la ciudad en PostgreSQL
        String postgresSelectsede = "SELECT COUNT(*) FROM sedes WHERE cod_ciudad = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectsede);
            postgresSelectStatement.setInt(1, codigoC);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar la CIUDAD porque hay SEDES asociadas a él en PostgreSQL");
                    return; // Salir del método si hay sedes asociadas
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar SEDES asociadas a la CIUDAD en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay empleados asociadaos a la ciudad (Por Ciudad de nacimiento) en PostgreSQL
        String postgresSelectempleado = "SELECT COUNT(*) FROM empleados WHERE lugar_nacimiento = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectempleado);
            postgresSelectStatement.setInt(1, codigoC);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar la CIUDAD porque hay EMPLEADOS (Lugar de nacimiento) asociados a él en PostgreSQL");
                    return; // Salir del método si hay empleados asociados
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar EMPLEADOS asociadas a la CIUDAD en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay sedes asociadas a la ciudad en MongoDB
        Document mongoSedesFilter = new Document("COD_CIUDAD", codigoC);
        long count1 = mongoCollectionSedes.countDocuments(mongoSedesFilter);
        if (count1 > 0) {
            System.out.println("No se puede eliminar la CIUDAD porque hay SEDES asociadas a él en MongoDB");
            return; // Salir del método si hay sedes asociadas
        }

        // Verificar si hay empleados asociados a la ciudad en MongoDB
        Document mongoEmpleadosFilter = new Document("LUGAR_NACIMIENTO", codigoC);
        long count2 = mongoCollectionEmpleados.countDocuments(mongoEmpleadosFilter);
        if (count2 > 0) {
            System.out.println("No se puede eliminar la CIUDAD porque hay SEDES asociadas a él en MongoDB");
            return; // Salir del método si hay sedes asociadas
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM ciudades WHERE codigo = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setInt(1, codigoC);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos del DEPARTAMENTO eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos del DEPARTAMENTO para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos del DEPARTAMENTO en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("CODIGO", codigoC);
        DeleteResult deleteResult = mongoCollectionCiudades.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos de la CIUDAD eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos de la CIUDAD para eliminar en MongoDB\n");
        }
    }


    public void elimina_departamento() throws SQLException {

        // Conexion postgres
        Connection connection = getConnection();
        // Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionDepartamentos = mongoDatabase.getCollection("DEPARTAMENTOS");
        MongoCollection<Document> mongoCollectionCiudades = mongoDatabase.getCollection("CIUDADES");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el código del DEPARTAMENTO que desea eliminar: ");
        int codigoD = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay ciudades asociadaos al departamento en PostgreSQL
        String postgresSelectQuery = "SELECT COUNT(*) FROM ciudades WHERE cod_dpto = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectQuery);
            postgresSelectStatement.setInt(1, codigoD);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar el DEPARTAMENTO porque hay ciudades asociadas a él en PostgreSQL");
                    return; // Salir del método si hay ciudades asociadas
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar ciudades asociadas al departamento en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay ciudades asociadas al departamento en MongoDB
        Document mongoCiudadesFilter = new Document("COD_DPTO", codigoD);
        long count = mongoCollectionCiudades.countDocuments(mongoCiudadesFilter);
        if (count > 0) {
            System.out.println("No se puede eliminar el DEPARTAMENTO porque hay ciudades asociadas a él en MongoDB");
            return; // Salir del método si hay ciudades asociadas
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM departamentos WHERE codigo = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setInt(1, codigoD);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos del DEPARTAMENTO eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos del DEPARTAMENTO para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos del DEPARTAMENTO en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("CODIGO", codigoD);
        DeleteResult deleteResult = mongoCollectionDepartamentos.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos del DEPARTAMENTO eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos del DEPARTAMENTO para eliminar en MongoDB\n");
        }
    }

    public void elimina_empleado() throws SQLException {

        // Conexion postgres
        Connection connection = getConnection();
        // Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionEmpleados = mongoDatabase.getCollection("EMPLEADOS");
        MongoCollection<Document> mongoCollectionAreas = mongoDatabase.getCollection("AREAS");

        // Solicitar el código de identificación que desea eliminar
        System.out.println("Ingrese la IDENTIFICACIÓN del empleado que desea eliminar: ");
        int identificacionE = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay áreas asociadas al empleado en PostgreSQL
        String postgresSelectareas = "SELECT COUNT(*) FROM areas WHERE id_coordinador = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectareas);
            postgresSelectStatement.setInt(1, identificacionE);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar el empleado porque hay AREAS asociadas a él en PostgreSQL");
                    return; // Salir del método si hay áreas asociadas
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar AREAS asociadas al EMPLEADO en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay áreas asociadas al empleado en MongoDB
        Document mongoAreasFilter = new Document("ID_COORDINADOR", identificacionE);
        long count1 = mongoCollectionAreas.countDocuments(mongoAreasFilter);
        if (count1 > 0) {
            System.out.println("No se puede eliminar el EMPLEADO porque hay AREAS asociadas a él en MongoDB");
            return; // Salir del método si hay áreas asociadas
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM empleados WHERE identificacion = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setInt(1, identificacionE);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos del EMPLEADO eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos del EMPLEADO para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos del DEPARTAMENTO en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("CODIGO", identificacionE);
        DeleteResult deleteResult = mongoCollectionEmpleados.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos del EMPLEADO eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos del EMPLEADO para eliminar en MongoDB\n");
        }
    }

    public void elimina_pais() throws SQLException {

        // Conexion postgres
        Connection connection = getConnection();
        // Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        // Datos para ingresar
        MongoCollection<Document> mongoCollectionPaises = mongoDatabase.getCollection("PAISES");
        MongoCollection<Document> mongoCollectionDepartamentos = mongoDatabase.getCollection("DEPARTAMENTOS");

        // Solicitar el código del PAIS que desea eliminar
        System.out.println("Ingrese el código del PAIS que desea eliminar: ");
        int codigoP = sc.nextInt();
        sc.nextLine(); // Consumir el salto de línea pendiente

        // Verificar si hay departamentos asociados al país en PostgreSQL
        String postgresSelectQuery = "SELECT COUNT(*) FROM departamentos WHERE cod_pais = ?";
        try {
            PreparedStatement postgresSelectStatement = connection.prepareStatement(postgresSelectQuery);
            postgresSelectStatement.setInt(1, codigoP);
            ResultSet resultSet = postgresSelectStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0) {
                    System.out.println("No se puede eliminar el país porque hay departamentos asociados a él en PostgreSQL");
                    return; // Salir del método si hay departamentos asociados
                }
            }
            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            postgresSelectStatement.close();
        } catch (SQLException e) {
            System.out.println("Error al verificar departamentos asociados al país en PostgreSQL: " + e.getMessage());
            return; // Salir del método en caso de error
        }

        // Verificar si hay departamentos asociados al país en MongoDB
        Document mongoDepartamentosFilter = new Document("COD_PAIS", codigoP);
        long count = mongoCollectionDepartamentos.countDocuments(mongoDepartamentosFilter);
        if (count > 0) {
            System.out.println("No se puede eliminar el país porque hay departamentos asociados a él en MongoDB");
            return; // Salir del método si hay departamentos asociados
        }

        // Eliminación de datos en PostgreSQL
        String postgresDeleteQuery = "DELETE FROM paises WHERE codigo = ?";
        try {
            // Preparar la consulta
            PreparedStatement postgresStatement = connection.prepareStatement(postgresDeleteQuery);

            // Establecer el valor del parámetro
            postgresStatement.setInt(1, codigoP);

            // Ejecutar la eliminación
            int rowsDeleted = postgresStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Datos del PAIS eliminados en PostgreSQL\n");
            } else {
                System.out.println("No se encontraron datos del PAIS para eliminar en PostgreSQL\n");
            }

            // Cerrar la conexión
            postgresStatement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Error al eliminar los datos del PAIS en PostgreSQL: " + e.getMessage());
        }

        // Eliminación de datos en MongoDB
        Document mongoDeleteFilter = new Document("CODIGO", codigoP);
        DeleteResult deleteResult = mongoCollectionPaises.deleteMany(mongoDeleteFilter);
        if (deleteResult.getDeletedCount() > 0) {
            System.out.println("Documentos del PAIS eliminados en MongoDB\n");
        } else {
            System.out.println("No se encontraron documentos del PAIS para eliminar en MongoDB\n");
        }
    }
}
