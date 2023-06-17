import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.security.cert.CollectionCertStoreParameters;
import java.sql.*;
import java.util.Scanner;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Ingresar extends Main {
    Scanner sc = new Scanner(System.in);
    boolean sw = false;

    public void ingresa_menu(int opcion) throws SQLException {
        switch (opcion) {
            case 1:
                sw=false;
                ingresa_facultad();
                break;
            case 2:
                sw=false;
                ingresa_area();
                break;
            case 3:
                sw=false;
                ingresa_programa();
                break;
            case 4:
                sw=false;
                ingresa_empleado();
                break;
            case 5:
                sw=false;
                ingresa_tipos_empleado();
                break;
            case 6:
                sw=false;
                ingresa_tipos_contratacion();
                break;
            case 7:
                sw=false;
                ingresa_sede();
                break;
            case 8:
                sw=false;
                ingresa_ciudad();
                break;
            case 9:
                sw=false;
                ingresa_departamento();
                break;
            case 10:
                sw=false;
                ingresa_pais();
                break;
        }
    }

//--------------------------------------------CONEXIONES--------------------------------------------------------------
    // conexion para postgres

    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BD_EVENTOS", user, pass);
            return connection;
        } catch (Exception e) {
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
    private static boolean verificarExistencia(Connection connection, int DATO, String N_coleccion,String Codigo) throws SQLException {
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
    private static boolean verificarExistenciaS(Connection connection, String DATO, String N_coleccion,String nom_columna) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + N_coleccion + " WHERE"+ nom_columna +" = ?";
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
    public void ingresa_facultad() throws SQLException {

        //Conexion a postgres
        Connection connection = getConnection();

        //Conexion en mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionFacultades = mongoDatabase.getCollection("FACULTADES");
        //Insertar datos en PostgreSQL
        String postgresInsertQuery = "INSERT INTO facultades (codigo, nombre, ubicacion, nro_telefono, id_decano) VALUES (?, ?, ?, ?, ?) ON CONFLICT (codigo) DO NOTHING";



        do {
            // Datos para insertar
            System.out.println("Por favor ingrese los siguientes datos sobre la facultad");
            System.out.println("Codigo: ");
            int codigoF = sc.nextInt();
            sc.nextLine();
            System.out.println("nombre: ");
            String nombreF = sc.nextLine();
            System.out.println("ubicacion: ");
            String ubicacionF = sc.nextLine();
            System.out.println("numero de telefono: ");
            String nro_telefonoF = sc.nextLine();
            System.out.println("ide del decano: ");
            String id_decanoF = sc.nextLine();
            if (!verificarExistencia(connection, codigoF, "FACULTADES","codigo")) {
                if (verificarExistenciaS(connection, id_decanoF, "EMPLEADOS","id_empleado")) {
                    //preparar la consulta
                    PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                    // Inserción en PostgreSQL
                    postgresStatement.setInt(1, codigoF);
                    postgresStatement.setString(2, nombreF);
                    postgresStatement.setString(3, ubicacionF);
                    postgresStatement.setString(4, nro_telefonoF);
                    postgresStatement.setString(5, id_decanoF);
                    postgresStatement.executeUpdate();
                    System.out.println("Datos insertados en PostgreSQL\n");

                    //Ejecutar la consulta
                    int filasAfectadas = postgresStatement.executeUpdate();
                    System.out.println("Filas afectadas: " + filasAfectadas);

                    //Cerrar la conexión
                    postgresStatement.close();
                    connection.close();

                    // Inserción en MongoDB
                    Document document = new Document();
                    document.append("CODIGO", codigoF);
                    document.append("NOMBRE", nombreF);
                    document.append("UBICACION", ubicacionF);
                    document.append("NRO_TELEFONO", nro_telefonoF);
                    document.append("ID_DECANO", id_decanoF);

                    mongoCollectionFacultades.insertOne(document);

                    System.out.println("Documento insertado en MongoDB");
                    sw = true;
                } else {
                    System.out.println("El id del decano no existe");
                    System.out.println("Por favor ingrese otro id");
                }
            } else {
                System.out.println("El codigo de la facultad ya existe");
                System.out.println("Por favor ingrese otro codigo");
            }
        } while (sw = false);
    }


    //--------------------------------------INGRESAR AREA-----------------------------------------------------

    public void ingresa_area() throws SQLException {

        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionAreas = mongoDatabase.getCollection("AREAS");
        do {
            // Datos para insertar
            System.out.println("Por favor ingrese los siguientes datos sobre AREA:");
            System.out.println("Codigo: ");
            int codigoA = sc.nextInt();
            sc.nextLine();
            System.out.println("Nombre");
            String nombreA = sc.nextLine();
            System.out.println("Codigo de la facultad: ");
            int codigo_facultadA = sc.nextInt();
            sc.nextLine();
            System.out.println("Codigo del coordinador: ");
            String codigo_coordinador = sc.nextLine();


            if (verificarExistenciaS(connection, codigo_coordinador, "EMPLEADOS","id_empleado")) {
                if (verificarExistencia(connection, codigo_facultadA, "FACULTADES","codigo")) {
                    if (!verificarExistencia(connection, codigoA, "AREAS","codigo")) {
                        //Insertar datos en PostgreSQL
                        String postgresInsertQuery = "INSERT INTO areas (codigo, nombre, facultades_codigo, id_coordinador) VALUES (?, ?, ?, ?) ON CONFLICT (codigo) DO NOTHING";


                        //preparar la consulta
                        PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                        // Inserción en PostgreSQL
                        postgresStatement.setInt(1, codigoA);
                        postgresStatement.setString(2, nombreA);
                        postgresStatement.setInt(3, codigo_facultadA);
                        postgresStatement.setString(4, codigo_coordinador);
                        postgresStatement.executeUpdate();
                        System.out.println("Datos insertados en PostgreSQL\n");

                        //Ejecutar la consulta
                        int filasAfectadas = postgresStatement.executeUpdate();
                        System.out.println("Filas afectadas: " + filasAfectadas);

                        //Cerrar la conexión
                        postgresStatement.close();
                        connection.close();

                        // Inserción en MongoDB
                        Document document = new Document();
                        document.append("CODIGO", codigoA);
                        document.append("NOMBRE", nombreA);
                        document.append("CODIGO_FACULTAD", codigo_facultadA);
                        document.append("CODIGO_DEPARTAMENTO", codigo_coordinador);

                        mongoCollectionAreas.insertOne(document);

                        System.out.println("Documento insertado en MongoDB");
                        sw = true;

                    } else {
                        System.out.println("El codigo de la facultad existe,ingrese otro.\n");
                    }
                } else {
                    System.out.println("El codigo del coordinador no existe,ingrese otro.\n");
                }
            } else {
                System.out.println("El codigo del coordinador no existe,ingrese otro.\n");
            }
        }while(sw = false);

    }


    //--------------------------------------INGRESAR PROGRAMA-----------------------------------------------------
    public void ingresa_programa() throws SQLException {
        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionProgramas = mongoDatabase.getCollection("PROGRAMAS");
        do {
            // Datos para insertar
            System.out.println("Por favor ingrese los siguientes datos sobre PROGRAMAS:");
            System.out.println("Codigo: ");
            int codigoP = sc.nextInt();
            sc.nextLine();
            System.out.println("Nombre");
            String nombreP = sc.nextLine();
            System.out.println("Codigo de la area: ");
            int codigo_areaP = sc.nextInt();
            sc.nextLine();

            if (verificarExistencia(connection, codigo_areaP, "AREAS","codigo")) {
                if (!verificarExistencia(connection, codigoP, "PROGRAMAS","codigo")) {
                    //Insertar datos en PostgreSQL
                    String postgresInsertQuery = "INSERT INTO programas (codigo, nombre, areas_codigo) VALUES (?, ?, ?) ON CONFLICT (codigo) DO NOTHING";

                    //preparar la consulta
                    PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                    // Inserción en PostgreSQL
                    postgresStatement.setInt(1, codigoP);
                    postgresStatement.setString(2, nombreP);
                    postgresStatement.setInt(3, codigo_areaP);
                    postgresStatement.executeUpdate();
                    System.out.println("Datos insertados en PostgreSQL\n");

                    //Ejecutar la consulta
                    int filasAfectadas = postgresStatement.executeUpdate();
                    System.out.println("Filas afectadas: " + filasAfectadas);

                    //Cerrar la conexión
                    postgresStatement.close();
                    connection.close();

                    // Inserción en MongoDB
                    Document document = new Document();
                    document.append("CODIGO", codigoP);
                    document.append("NOMBRE", nombreP);
                    document.append("CODIGO_FACULTAD", codigo_areaP);

                    mongoCollectionProgramas.insertOne(document);

                    System.out.println("Documento insertado en MongoDB");
                    sw= true;

                } else {
                    System.out.println("El codigo del programa existe,ingrese otro.\n");
                }
            } else {
                System.out.println("El codigo del area no existe,ingrese otro.\n");
            }

        }while(sw = false);
    }


    //--------------------------------------INGRESAR TIPO DE EMPLEADO-----------------------------------------------------
    public void ingresa_tipos_empleado() throws SQLException {
        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionTipoEmpleado = mongoDatabase.getCollection("TIPOS_EMPLEADOS");
        do {
            // Datos para insertar
            System.out.println("Por favor ingrese los siguientes datos sobre TIPO DE EMPLEADOS:");
            System.out.println("Nombre");
            String nombreP = sc.nextLine();

            if (!verificarExistenciaS(connection, nombreP, "TIPOS_EMPLEADOS","nombre")) {
                //Insertar datos en PostgreSQL
                String postgresInsertQuery = "INSERT INTO tipos_empleados (nombre) VALUES (?) ON CONFLICT (nombre) DO NOTHING";

                //preparar la consulta
                PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                // Inserción en PostgreSQL
                postgresStatement.setString(1, nombreP);
                postgresStatement.executeUpdate();
                System.out.println("Datos insertados en PostgreSQL\n");

                //Cerrar la conexión
                postgresStatement.close();
                connection.close();

                // Inserción en MongoDB
                Document document = new Document();
                document.append("NOMBRE", nombreP);

                mongoCollectionTipoEmpleado.insertOne(document);

                System.out.println("Documento insertado en MongoDB");
                sw = true;

            } else {
                System.out.println("El nombre del tipo de empleado existe,ingrese otro.\n");
            }
        }while(sw = false);

    }


    //--------------------------------------INGRESAR SEDE-----------------------------------------------------

    public void ingresa_sede() throws SQLException {

        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar

        MongoCollection<Document> mongoCollectionSedes = mongoDatabase.getCollection("SEDES");


        do {
            // Solicitar la información necesaria para la inserción
            System.out.println("Por favor ingrese los siguientes datos sobre la SEDE");
            System.out.println("Código: ");
            int codigoS = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente

            System.out.println("Nombre: ");
            String nombreS = sc.nextLine();

            System.out.println("Codigo de la ciudad: ");
            int cod_ciudadS = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente

            // Verificar la existencia del PAIS antes de la inserción
            if (verificarExistencia(connection, cod_ciudadS, "CIUDADES","codigo")) {

                if (verificarExistencia(connection, codigoS, "SEDES","codigo")) {

                    //Insertar datos en PostgreSQL
                    String postgresInsertQuery = "INSERT INTO sedes (codigo, nombre, cod_ciudad) VALUES (?,?,?)";

                    //preparar la consulta
                    PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                    // Inserción en PostgreSQL
                    postgresStatement.setInt(1, codigoS);
                    postgresStatement.setString(2, nombreS);
                    postgresStatement.setInt(3, cod_ciudadS);
                    postgresStatement.executeUpdate();
                    System.out.println("Datos insertados en PostgreSQL\n");

                    //Cerrar la conexión
                    postgresStatement.close();
                    connection.close();

                    // Inserción en MongoDB
                    Document document = new Document();
                    document.append("CODIGO", codigoS);
                    document.append("NOMBRE", nombreS);
                    document.append("COD_CIUDAD", cod_ciudadS);

                    mongoCollectionSedes.insertOne(document);

                    System.out.println("Documento insertado en MongoDB");
                    sw= true;
                } else {
                    System.out.println("La CIUDAD no existe en la base de datos");
                }

            } else {
                System.out.println("La CIUDAD no existe en la base de datos");
            }
        }while(sw = false);

    }

    public void ingresa_tipos_contratacion() throws SQLException {
        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionTipoContratacion = mongoDatabase.getCollection("TIPOS_CONTRATACION");

        do {
            // Datos para insertar
            System.out.println("Por favor ingrese los siguientes datos sobre TIPO DE CONTRATACION:");
            System.out.println("Nombre");
            String nombreP = sc.nextLine();

            if (!verificarExistenciaS(connection, nombreP, "TIPOS_CONTRATACION","nombre")) {
                //Insertar datos en PostgreSQL
                String postgresInsertQuery = "INSERT INTO tipos_contratacion (nombre) VALUES (?) ON CONFLICT (nombre) DO NOTHING";

                //preparar la consulta
                PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                // Inserción en PostgreSQL
                postgresStatement.setString(1, nombreP);
                postgresStatement.executeUpdate();
                System.out.println("Datos insertados en PostgreSQL\n");

                //Ejecutar la consulta
                int filasAfectadas = postgresStatement.executeUpdate();
                System.out.println("Filas afectadas: " + filasAfectadas);

                //Cerrar la conexión
                postgresStatement.close();
                connection.close();

                // Inserción en MongoDB
                Document document = new Document();
                document.append("NOMBRE", nombreP);

                mongoCollectionTipoContratacion.insertOne(document);

                System.out.println("Documento insertado en MongoDB");
                sw= true;

            } else {
                System.out.println("El nombre del tipo de contratacion existe,ingrese otro.\n");
            }
        }while(sw = false);
    }

    public void ingresa_ciudad() throws SQLException {

        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionCiudades = mongoDatabase.getCollection("CIUDADES");

        do {
            // Solicitar la información necesaria para la inserción
            System.out.println("Por favor ingrese los siguientes datos sobre la CUIDAD");
            System.out.println("Código: ");
            int codigoC = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente

            System.out.println("Nombre: ");
            String nombreC = sc.nextLine();

            System.out.println("Código del Departamento: ");
            int cod_dptoC = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente

            // Verificar la existencia del PAIS antes de la inserción
            if (verificarExistencia(connection, cod_dptoC, "DEPARTAMENTOS","codigo")) {

                if (!verificarExistencia(connection, codigoC, "CIUDADES","codigo")) {

                    //Insertar datos en PostgreSQL
                    String postgresInsertQuery = "INSERT INTO ciudades (codigo, nombre, cod_dpto) VALUES (?,?,?)";

                    //preparar la consulta
                    PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                    // Inserción en PostgreSQL
                    postgresStatement.setInt(1, codigoC);
                    postgresStatement.setString(2, nombreC);
                    postgresStatement.setInt(3, cod_dptoC);
                    postgresStatement.executeUpdate();
                    System.out.println("Datos insertados en PostgreSQL\n");

                    //Cerrar la conexión
                    postgresStatement.close();
                    connection.close();

                    // Inserción en MongoDB
                    Document document = new Document();
                    document.append("CODIGO", codigoC);
                    document.append("NOMBRE", nombreC);
                    document.append("COD_DPTO", cod_dptoC);

                    mongoCollectionCiudades.insertOne(document);

                    System.out.println("Documento insertado en MongoDB");
                    sw= true;
                } else {
                    System.out.println("El codigo de CIUDAD ya existe en la base de datos");
                }
            } else {
                System.out.println("El DEPARTAMENTO no existe en la base de datos");
            }
        }while(sw = false);
    }


    public void ingresa_departamento() throws SQLException {

        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionDepartamentos = mongoDatabase.getCollection("DEPARTAMENTOS");

        do {
            // Solicitar la información necesaria para la inserción
            System.out.println("Por favor ingrese los siguientes datos sobre el DEPARTAMENTO");
            System.out.println("Código: ");
            int codigoD = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente
            System.out.println("Nombre: ");
            String nombreD = sc.nextLine();

            System.out.println("Codigo del país: ");
            int cod_paisD = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente

            // Verificar la existencia del PAIS antes de la inserción
            if (verificarExistencia(connection, cod_paisD, "PAISES","codigo")) {

                if (!verificarExistencia(connection, codigoD, "DEPARTAMENTOS","codigo")) {

                    //Insertar datos en PostgreSQL
                    String postgresInsertQuery = "INSERT INTO DEPARTAMENTOS (codigo, nombre, cod_pais) VALUES (?,?,?)";

                    //preparar la consulta
                    PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                    // Inserción en PostgreSQL
                    postgresStatement.setInt(1, codigoD);
                    postgresStatement.setString(2, nombreD);
                    postgresStatement.setInt(3, cod_paisD);
                    postgresStatement.executeUpdate();
                    System.out.println("Datos insertados en PostgreSQL\n");

                    //Cerrar la conexión
                    postgresStatement.close();
                    connection.close();

                    // Inserción en MongoDB
                    Document document = new Document();
                    document.append("CODIGO", codigoD);
                    document.append("NOMBRE", nombreD);
                    document.append("COD_PAIS", cod_paisD);

                    mongoCollectionDepartamentos.insertOne(document);

                    System.out.println("Documento insertado en MongoDB");
                    sw= true;
                } else {
                    System.out.println("El codigo del DEPARTAMENTO existe en la base de datos");
                }
            } else {
                System.out.println("El PAIS no existe en la base de datos");
            }
        }while(sw = false);
    }

    public void ingresa_empleado() throws SQLException {

        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionEmpleados = mongoDatabase.getCollection("EMPLEADOS");

        do {
            // Solicitar la información necesaria para la inserción
            System.out.println("Por favor ingrese los siguientes datos sobre el EMPLEADO");
            System.out.println("Identificación: ");
            int identificacionE = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente
            System.out.println("Nombre: ");
            String nombreE = sc.nextLine();
            System.out.println("Apellido: ");
            String apellidoE = sc.nextLine();
            System.out.println("Email: ");
            String emailE = sc.nextLine();
            System.out.println("Tipo de contratación: ");
            String tipoContratacionE = sc.nextLine();
            System.out.println("Tipo de empleado: ");
            String tipoEmpleadoE = sc.nextLine();
            System.out.println("Codigo de facultad: ");
            int codFacultadE = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente
            System.out.println("Codigo de sede: ");
            int codSedeE = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente
            System.out.println("Lugar de nacimiento (Ingrese Codigo de ciudad): ");
            int codCiudadE = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente

            // Verificar la existencia del PAIS antes de la inserción
            if (verificarExistenciaS(connection, tipoContratacionE, "TIPOS_CONTRACION","nombre")) {

                if (verificarExistenciaS(connection, tipoEmpleadoE, "TIPOS_EMPLEADO","nombre")) {

                    if (verificarExistencia(connection, codFacultadE, "FACULTADES","codigo")) {

                        if (verificarExistencia(connection, codSedeE, "SEDES","codigo")) {

                            if (verificarExistencia(connection, codCiudadE, "CIUDADES","codigo")) {

                                if (!verificarExistencia(connection, identificacionE, "EMPLEADOS","identificacion")) {

                                    //Insertar datos en PostgreSQL
                                    String postgresInsertQuery = "INSERT INTO empleados (IDENTIFICACION, NOMBRES, APELLIDOS, EMAIL, TIPO_CONTRATACION, TIPO_EMPLEADO, COD_FACULTAD, COD_SEDE, LUGAR_NACIMIENTO) VALUES (?,?,?,?,?,?,?,?,?)";

                                    //preparar la consulta
                                    PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                                    // Inserción en PostgreSQL
                                    postgresStatement.setInt(1, identificacionE);
                                    postgresStatement.setString(2, nombreE);
                                    postgresStatement.setString(2, apellidoE);
                                    postgresStatement.setString(2, emailE);
                                    postgresStatement.setString(2, tipoContratacionE);
                                    postgresStatement.setString(2, tipoEmpleadoE);
                                    postgresStatement.setInt(3, codFacultadE);
                                    postgresStatement.setInt(3, codSedeE);
                                    postgresStatement.setInt(3, codCiudadE);
                                    postgresStatement.executeUpdate();
                                    System.out.println("Datos insertados en PostgreSQL\n");

                                    //Cerrar la conexión
                                    postgresStatement.close();
                                    connection.close();

                                    // Inserción en MongoDB
                                    Document document = new Document();
                                    document.append("IDENTIFICACIÓN", identificacionE);
                                    document.append("NOMBRE", nombreE);
                                    document.append("APELLIDO", apellidoE);
                                    document.append("EMAIL", emailE);
                                    document.append("TIPO_CONTRATACIÓN", tipoContratacionE);
                                    document.append("TIPO_EMPLEADO", tipoEmpleadoE);
                                    document.append("COD_FACULTAD", codFacultadE);
                                    document.append("CODIGO_SEDE", codSedeE);
                                    document.append("LUGAR_NACIMIENTO", codCiudadE);

                                    mongoCollectionEmpleados.insertOne(document);

                                    System.out.println("Documento insertado en MongoDB");
                                    sw=true;
                                } else {
                                    System.out.println("El codigo del EMPLEADO ya existe en la base de datos");
                                }

                            } else {
                                System.out.println("La CIUDAD no existe en la base de datos");
                            }

                        } else {
                            System.out.println("La SEDE no existe en la base de datos");
                        }

                    } else {
                        System.out.println("La FACULTAD no existe en la base de datos");
                    }

                } else {
                    System.out.println("El TIPO DE EMPLEADO no existe en la base de datos");
                }

            } else {
                System.out.println("El TIPO DE CONTRATACIÓN no existe en la base de datos");
            }
        }while(sw = false);
    }

    public void ingresa_pais() throws SQLException {

        //Conexion postgres
        Connection connection = getConnection();
        //Conexion mongo
        MongoDatabase mongoDatabase = getMongoConnection();

        //Datos para ingresar
        MongoCollection<Document> mongoCollectionPaises = mongoDatabase.getCollection("PAISES");

        do{
            // Solicitar la información necesaria para la inserción
            System.out.println("Por favor ingrese los siguientes datos sobre el DEPARTAMENTO");
            System.out.println("Código: ");
            int codigoP = sc.nextInt();
            sc.nextLine(); // Consumir el salto de línea pendiente

            System.out.println("Nombre: ");
            String nombreP = sc.nextLine();

            // Verificar la existencia del PAIS antes de la inserción
            //Si No encuentra el codigo del PAIS entra al If
            if (!verificarExistencia(connection, codigoP, "PAISES","codigo")) {

                //Insertar datos en PostgreSQL
                String postgresInsertQuery = "INSERT INTO paises (codigo, nombre) VALUES (?,?)";

                //preparar la consulta
                PreparedStatement postgresStatement = connection.prepareStatement(postgresInsertQuery);

                // Inserción en PostgreSQL
                postgresStatement.setInt(1, codigoP);
                postgresStatement.setString(2, nombreP);
                postgresStatement.executeUpdate();
                System.out.println("Datos insertados en PostgreSQL\n");

                //Cerrar la conexión
                postgresStatement.close();
                connection.close();

                // Inserción en MongoDB
                Document document = new Document();
                document.append("CODIGO", codigoP);
                document.append("NOMBRE", nombreP);

                mongoCollectionPaises.insertOne(document);

                System.out.println("Documento insertado en MongoDB");
                sw=true;
            } else {
                System.out.println("El CODIGO del pais ya existe en la base de datos");
            }
        }while (sw = false);
    }
}