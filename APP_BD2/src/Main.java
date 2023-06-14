import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String user = "postgres";
        String pass = "2118";

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BD_EVENTOS", user, pass);
            System.out.println("conexion realizada con exito!");
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}