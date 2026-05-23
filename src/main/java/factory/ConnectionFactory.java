package factory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
        private static final String PASSWORD = System.getenv("DB_PASS");

    public static Connection getConnection() {
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Deu ruim na conexão. Veja o erro real acima.");
        }
    }

}
