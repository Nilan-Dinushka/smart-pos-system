package lk.ijse.dep11.pos.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class SingleConnectionDataSource {
    private static SingleConnectionDataSource instance;
    private Connection connection;
    private SingleConnectionDataSource(){

        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/application.properties"));
            String url = properties.getProperty("db.datasource.url");
            String username = properties.getProperty("db.datasource.username");
            String password = properties.getProperty("db.datasource.password");
            connection = DriverManager.getConnection(url, username, password);
            getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void generateScheme() throws Exception {
        URL url = getClass().getResource("/schema.sql");
        Path path = Paths.get(url.toURI());
        String dbScript = Files.readAllLines(path).stream().reduce((prevLine, currentLine) -> prevLine + currentLine).get();
        connection.createStatement().execute(dbScript);
    }

    public static SingleConnectionDataSource getInstance() {
        return (instance == null) ? instance = new SingleConnectionDataSource() : instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
