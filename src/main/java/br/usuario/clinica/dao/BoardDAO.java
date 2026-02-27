package br.usuario.clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.usuario.clinica.config.DatabaseConnection;
import br.usuario.clinica.model.Board;

/**
 * Data Access Object para gerenciar operações de Boards no banco de dados
 */
public class BoardDAO {

    /**
     * Salva um novo board no banco de dados
     */
    public Long saveBoard(Board board) throws SQLException {
        String sql = "INSERT INTO boards (name, created_at) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, board.getName());
            pstmt.setTimestamp(2, Timestamp.valueOf(board.getCreatedAt()));

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return null;
    }

    /**
     * Busca um board pelo ID
     */
    public Board getBoardById(Long boardId) throws SQLException {
        String sql = "SELECT id, name, created_at FROM boards WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, boardId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Board board = new Board(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    // Carregar colunas do board
                    board.setColumns(new BoardColumnDAO().getColumnsByBoardId(boardId));

                    return board;
                }
            }
        }
        return null;
    }

    /**
     * Retorna todos os boards
     */
    public List<Board> getAllBoards() throws SQLException {
        List<Board> boards = new ArrayList<>();
        String sql = "SELECT id, name, created_at FROM boards ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Board board = new Board(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );

                // Carregar colunas do board
                board.setColumns(new BoardColumnDAO().getColumnsByBoardId(board.getId()));

                boards.add(board);
            }
        }
        return boards;
    }

    /**
     * Deleta um board
     */
    public boolean deleteBoard(Long boardId) throws SQLException {
        String sql = "DELETE FROM boards WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, boardId);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        }
    }

    /**
     * Atualiza o nome de um board
     */
    public boolean updateBoard(Board board) throws SQLException {
        String sql = "UPDATE boards SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, board.getName());
            pstmt.setLong(2, board.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
