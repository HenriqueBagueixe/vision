package factory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASS");

    public static Connection getConnection() {
        System.out.println("DEBUG RENDER: URL=" + URL + ", USER=" + USER);
        if (URL == null || USER == null || PASSWORD == null) {
            throw new RuntimeException("ERRO CRÍTICO: Uma das variáveis de ambiente está nula!");
        }try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Deu ruim na conexão. Erro: " + e.getMessage());
        }
    }

}
