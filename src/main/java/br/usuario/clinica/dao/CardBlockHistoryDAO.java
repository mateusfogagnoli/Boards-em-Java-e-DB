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
import br.usuario.clinica.model.CardBlockHistory;

/**
 * Data Access Object para gerenciar operações de Histórico de Bloqueio de Cards
 */
public class CardBlockHistoryDAO {

    /**
     * Salva um novo registro de bloqueio
     */
    public Long saveBlockHistory(CardBlockHistory history) throws SQLException {
        String sql = "INSERT INTO card_block_history (card_id, blocked_at, block_reason, active) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, history.getCardId());
            pstmt.setTimestamp(2, Timestamp.valueOf(history.getBlockedAt()));
            pstmt.setString(3, history.getBlockReason());
            pstmt.setBoolean(4, history.isActive());

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
     * Busca um registro de bloqueio pelo ID
     */
    public CardBlockHistory getBlockHistoryById(Long historyId) throws SQLException {
        String sql = "SELECT id, card_id, blocked_at, unblocked_at, block_reason, unblock_reason, active " +
                "FROM card_block_history WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, historyId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHistory(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retorna o histórico de bloqueio ativo de um card (se houver)
     */
    public CardBlockHistory getActiveBlockHistory(Long cardId) throws SQLException {
        String sql = "SELECT id, card_id, blocked_at, unblocked_at, block_reason, unblock_reason, active " +
                "FROM card_block_history WHERE card_id = ? AND active = TRUE LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHistory(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retorna todo o histórico de bloqueio de um card
     */
    public List<CardBlockHistory> getBlockHistoryByCardId(Long cardId) throws SQLException {
        List<CardBlockHistory> histories = new ArrayList<>();
        String sql = "SELECT id, card_id, blocked_at, unblocked_at, block_reason, unblock_reason, active " +
                "FROM card_block_history WHERE card_id = ? ORDER BY blocked_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, cardId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    histories.add(mapResultSetToHistory(rs));
                }
            }
        }
        return histories;
    }

    /**
     * Atualiza um registro de bloqueio (principalmente para adicionar data de desbloqueio)
     */
    public boolean updateBlockHistory(CardBlockHistory history) throws SQLException {
        String sql = "UPDATE card_block_history SET unblocked_at = ?, unblock_reason = ?, active = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, history.getUnblockedAt() != null ? 
                    Timestamp.valueOf(history.getUnblockedAt()) : null);
            pstmt.setString(2, history.getUnblockReason());
            pstmt.setBoolean(3, history.isActive());
            pstmt.setLong(4, history.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Mapeia um ResultSet para CardBlockHistory
     */
    private CardBlockHistory mapResultSetToHistory(ResultSet rs) throws SQLException {
        CardBlockHistory history = new CardBlockHistory();
        history.setId(rs.getLong("id"));
        history.setCardId(rs.getLong("card_id"));
        history.setBlockedAt(rs.getTimestamp("blocked_at").toLocalDateTime());
        history.setBlockReason(rs.getString("block_reason"));
        history.setActive(rs.getBoolean("active"));

        Timestamp unblockedAt = rs.getTimestamp("unblocked_at");
        if (unblockedAt != null) {
            history.setUnblockedAt(unblockedAt.toLocalDateTime());
        }

        String unblockReason = rs.getString("unblock_reason");
        if (unblockReason != null) {
            history.setUnblockReason(unblockReason);
        }

        return history;
    }
}
