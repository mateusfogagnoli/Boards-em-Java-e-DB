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
import br.usuario.clinica.model.Card;

/**
 * Data Access Object para gerenciar operações de Cards no banco de dados
 */
public class CardDAO {

    /**
     * Salva um novo card no banco de dados
     */
    public Long saveCard(Card card) throws SQLException {
        String sql = "INSERT INTO cards (board_id, column_id, title, description, created_at, blocked, entered_column_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, card.getBoardId());
            pstmt.setLong(2, card.getColumnId());
            pstmt.setString(3, card.getTitle());
            pstmt.setString(4, card.getDescription());
            pstmt.setTimestamp(5, Timestamp.valueOf(card.getCreatedAt()));
            pstmt.setBoolean(6, card.isBlocked());
            pstmt.setTimestamp(7, card.getEnteredColumnAt() != null ? 
                    Timestamp.valueOf(card.getEnteredColumnAt()) : 
                    Timestamp.valueOf(java.time.LocalDateTime.now()));

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
     * Busca um card pelo ID
     */
    public Card getCardById(Long cardId) throws SQLException {
        String sql = "SELECT id, board_id, column_id, title, description, created_at, blocked, " +
                "entered_column_at, left_column_at FROM cards WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCard(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retorna todos os cards de uma coluna
     */
    public List<Card> getCardsByColumnId(Long columnId) throws SQLException {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT id, board_id, column_id, title, description, created_at, blocked, " +
                "entered_column_at, left_column_at FROM cards WHERE column_id = ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, columnId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cards.add(mapResultSetToCard(rs));
                }
            }
        }
        return cards;
    }

    /**
     * Retorna todos os cards de um board
     */
    public List<Card> getCardsByBoardId(Long boardId) throws SQLException {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT id, board_id, column_id, title, description, created_at, blocked, " +
                "entered_column_at, left_column_at FROM cards WHERE board_id = ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, boardId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cards.add(mapResultSetToCard(rs));
                }
            }
        }
        return cards;
    }

    /**
     * Atualiza um card
     */
    public boolean updateCard(Card card) throws SQLException {
        String sql = "UPDATE cards SET column_id = ?, title = ?, description = ?, blocked = ?, " +
                "entered_column_at = ?, left_column_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, card.getColumnId());
            pstmt.setString(2, card.getTitle());
            pstmt.setString(3, card.getDescription());
            pstmt.setBoolean(4, card.isBlocked());
            pstmt.setTimestamp(5, card.getEnteredColumnAt() != null ? 
                    Timestamp.valueOf(card.getEnteredColumnAt()) : null);
            pstmt.setTimestamp(6, card.getLeftColumnAt() != null ? 
                    Timestamp.valueOf(card.getLeftColumnAt()) : null);
            pstmt.setLong(7, card.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Deleta um card
     */
    public boolean deleteCard(Long cardId) throws SQLException {
        String sql = "DELETE FROM cards WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardId);
            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        }
    }

    /**
     * Mapeia um ResultSet para um Card
     */
    private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        Card card = new Card(
                rs.getLong("id"),
                rs.getLong("board_id"),
                rs.getLong("column_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("blocked")
        );

        Timestamp enteredAt = rs.getTimestamp("entered_column_at");
        if (enteredAt != null) {
            card.setEnteredColumnAt(enteredAt.toLocalDateTime());
        }

        Timestamp leftAt = rs.getTimestamp("left_column_at");
        if (leftAt != null) {
            card.setLeftColumnAt(leftAt.toLocalDateTime());
        }

        return card;
    }
}
