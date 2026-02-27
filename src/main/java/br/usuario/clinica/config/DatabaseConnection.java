package br.usuario.clinica.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe para gerenciar a conexão com o banco de dados PostgreSQL
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/board_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtém uma conexão com o banco de dados
     * @return Connection objeto da conexão
     * @throws SQLException se houver erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Inicializa o banco de dados criando as tabelas necessárias
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Criar tabela de boards
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS boards (" +
                            "id SERIAL PRIMARY KEY," +
                            "name VARCHAR(255) NOT NULL," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

            // Criar tabela de colunas
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS columns (" +
                            "id SERIAL PRIMARY KEY," +
                            "board_id INTEGER NOT NULL REFERENCES boards(id) ON DELETE CASCADE," +
                            "name VARCHAR(255) NOT NULL," +
                            "order_num INTEGER NOT NULL," +
                            "type VARCHAR(50) NOT NULL," +
                            "UNIQUE(board_id, order_num)" +
                            ")"
            );

            // Criar tabela de cards
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS cards (" +
                            "id SERIAL PRIMARY KEY," +
                            "board_id INTEGER NOT NULL REFERENCES boards(id) ON DELETE CASCADE," +
                            "column_id INTEGER NOT NULL REFERENCES columns(id) ON DELETE CASCADE," +
                            "title VARCHAR(255) NOT NULL," +
                            "description TEXT," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "blocked BOOLEAN DEFAULT FALSE," +
                            "entered_column_at TIMESTAMP," +
                            "left_column_at TIMESTAMP" +
                            ")"
            );

            // Criar tabela de histórico de bloqueio
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS card_block_history (" +
                            "id SERIAL PRIMARY KEY," +
                            "card_id INTEGER NOT NULL REFERENCES cards(id) ON DELETE CASCADE," +
                            "blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "unblocked_at TIMESTAMP," +
                            "block_reason VARCHAR(500)," +
                            "unblock_reason VARCHAR(500)," +
                            "active BOOLEAN DEFAULT TRUE" +
                            ")"
            );

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Testa a conexão com o banco de dados
     * @return true se a conexão foi bem-sucedida, false caso contrário
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
