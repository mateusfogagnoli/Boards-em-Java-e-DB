package br.usuario.clinica.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.usuario.clinica.config.DatabaseConnection;
import br.usuario.clinica.enums.ColumnType;
import br.usuario.clinica.model.BoardColumn;

/**
 * Data Access Object para gerenciar operações de Colunas no banco de dados
 */
public class BoardColumnDAO {

    /**
     * Salva uma nova coluna no banco de dados
     */
    public Long saveColumn(BoardColumn column) throws SQLException {
        String sql = "INSERT INTO columns (board_id, name, order_num, type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, column.getBoardId());
            pstmt.setString(2, column.getName());
            pstmt.setInt(3, column.getOrder());
            pstmt.setString(4, column.getType().name());

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
     * Busca uma coluna pelo ID
     */
    public BoardColumn getColumnById(Long columnId) throws SQLException {
        String sql = "SELECT id, board_id, name, order_num, type FROM columns WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, columnId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BoardColumn column = new BoardColumn(
                            rs.getLong("id"),
                            rs.getLong("board_id"),
                            rs.getString("name"),
                            rs.getInt("order_num"),
                            ColumnType.valueOf(rs.getString("type"))
                    );

                    // Carregar cards da coluna
                    column.setCards(new CardDAO().getCardsByColumnId(columnId));

                    return column;
                }
            }
        }
        return null;
    }

    /**
     * Retorna todas as colunas de um board
     */
    public List<BoardColumn> getColumnsByBoardId(Long boardId) throws SQLException {
        List<BoardColumn> columns = new ArrayList<>();
        String sql = "SELECT id, board_id, name, order_num, type FROM columns WHERE board_id = ? ORDER BY order_num ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, boardId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardColumn column = new BoardColumn(
                            rs.getLong("id"),
                            rs.getLong("board_id"),
                            rs.getString("name"),
                            rs.getInt("order_num"),
                            ColumnType.valueOf(rs.getString("type"))
                    );

                    // Carregar cards da coluna
                    column.setCards(new CardDAO().getCardsByColumnId(column.getId()));

                    columns.add(column);
                }
            }
        }
        return columns;
    }

    /**
     * Deleta uma coluna
     */
    public boolean deleteColumn(Long columnId) throws SQLException {
        String sql = "DELETE FROM columns WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, columnId);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        }
    }

    /**
     * Atualiza uma coluna
     */
    public boolean updateColumn(BoardColumn column) throws SQLException {
        String sql = "UPDATE columns SET name = ?, order_num = ?, type = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, column.getName());
            pstmt.setInt(2, column.getOrder());
            pstmt.setString(3, column.getType().name());
            pstmt.setLong(4, column.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
