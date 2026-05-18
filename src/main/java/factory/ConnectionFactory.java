package factory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl" ;
    private static final String USER = "RM568292";
    private static final String PASSWORD = "270406";

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
