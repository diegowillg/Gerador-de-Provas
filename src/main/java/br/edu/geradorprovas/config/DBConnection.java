package br.edu.geradorprovas.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Classe responsavel por abrir conexoes com o banco MySQL.
 * Ela le as configuracoes do arquivo db.properties e entrega uma Connection
 * pronta para ser usada pelos DAOs.
 */
public final class DBConnection {

    private static final Properties PROPS = new Properties();

    static {
        // Carrega as propriedades do banco uma unica vez quando a classe e iniciada.
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("Arquivo db.properties não encontrado em src/main/resources");
            }
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Falha ao carregar configuração do banco: " + e.getMessage());
        }
    }

    private DBConnection() {
    }

    /**
     * Abre uma conexao com o banco de dados.
     * O SET NAMES garante que acentos sejam gravados e lidos em UTF-8.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(
                PROPS.getProperty("db.url"),
                PROPS.getProperty("db.user"),
                PROPS.getProperty("db.password")
        );
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        return conn;
    }
}
