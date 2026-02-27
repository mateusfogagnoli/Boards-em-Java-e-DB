package br.usuario.clinica.service;

import java.sql.SQLException;
import java.time.LocalDateTime;

import br.usuario.clinica.dao.CardBlockHistoryDAO;
import br.usuario.clinica.dao.CardDAO;
import br.usuario.clinica.enums.ColumnType;
import br.usuario.clinica.model.Board;
import br.usuario.clinica.model.BoardColumn;
import br.usuario.clinica.model.Card;
import br.usuario.clinica.model.CardBlockHistory;

/**
 * Serviço para gerenciar operações relacionadas a Cards
 */
public class CardService {
    private final CardDAO cardDAO;
    private final CardBlockHistoryDAO blockHistoryDAO;

    public CardService() {
        this.cardDAO = new CardDAO();
        this.blockHistoryDAO = new CardBlockHistoryDAO();
    }

    /**
     * Move um card para a próxima coluna, seguindo as regras de movimentação
     */
    public boolean moveCardToNextColumn(Board board, Card card) throws SQLException {
        // Verificar se o card está bloqueado
        if (card.isBlocked()) {
            System.out.println("❌ Card está bloqueado! Desbloqueie-o antes de mover.");
            return false;
        }

        BoardColumn currentColumn = board.getColumnById(card.getColumnId());
        if (currentColumn == null) {
            System.out.println("❌ Coluna atual não encontrada!");
            return false;
        }

        // Se a coluna atual é a final, não pode mover
        if (currentColumn.getType() == ColumnType.FINAL) {
            System.out.println("❌ O card já está na coluna final!");
            return false;
        }

        // Encontrar a próxima coluna
        BoardColumn nextColumn = board.getColumnByOrder(currentColumn.getOrder() + 1);
        if (nextColumn == null) {
            System.out.println("❌ Não há próxima coluna!");
            return false;
        }

        // Se estiver em uma coluna não-cancelamento tentando se mover, deve ir para a próxima sequencial
        if (currentColumn.getType() != ColumnType.CANCELAMENTO && 
                nextColumn.getType() == ColumnType.CANCELAMENTO) {
            // Não pode pular direto para cancelamento
            System.out.println("❌ Não pode pular para a coluna de cancelamento! Use a opção de cancelar.");
            return false;
        }

        // Mover o card
        return moveCard(board, card, nextColumn);
    }

    /**
     * Move um card para a coluna de cancelamento
     */
    public boolean cancelCard(Board board, Card card) throws SQLException {
        // Verificar se o card está bloqueado
        if (card.isBlocked()) {
            System.out.println("[ERRO] Card está bloqueado! Desbloqueie-o antes de cancelar.");
            return false;
        }

        BoardColumn currentColumn = board.getColumnById(card.getColumnId());
        if (currentColumn == null) {
            System.out.println("[ERRO] Coluna atual não encontrada!");
            return false;
        }

        // Não pode cancelar a partir da coluna final
        if (currentColumn.getType() == ColumnType.FINAL) {
            System.out.println("[ERRO] Não pode cancelar um card que já está finalizado!");
            return false;
        }

        // Não pode cancelar a partir da coluna de cancelamento
        if (currentColumn.getType() == ColumnType.CANCELAMENTO) {
            System.out.println("[ERRO] O card já está cancelado!");
            return false;
        }

        BoardColumn cancelledColumn = board.getCancelledColumn();
        if (cancelledColumn == null) {
            System.out.println("[ERRO] Coluna de cancelamento não encontrada!");
            return false;
        }

        return moveCard(board, card, cancelledColumn);
    }

    /**
     * Move um card para uma coluna específica
     */
    private boolean moveCard(Board board, Card card, BoardColumn targetColumn) throws SQLException {
        // Remover da coluna atual
        BoardColumn currentColumn = board.getColumnById(card.getColumnId());
        if (currentColumn != null) {
            currentColumn.removeCard(card);
            card.setLeftColumnAt(LocalDateTime.now());
        }

        // Adicionar à nova coluna
        card.setColumnId(targetColumn.getId());
        card.setEnteredColumnAt(LocalDateTime.now());
        targetColumn.addCard(card);

        // Atualizar no banco de dados
        boolean success = cardDAO.updateCard(card);

        if (success) {
            System.out.println("[OK] Card movido de " + currentColumn.getName() + 
                             " para " + targetColumn.getName());
        }

        return success;
    }

    /**
     * Bloqueia um card com um motivo
     */
    public boolean blockCard(Card card, String reason) throws SQLException {
        if (card.isBlocked()) {
            System.out.println("[ERRO] Card já está bloqueado!");
            return false;
        }

        card.setBlocked(true);
        boolean success = cardDAO.updateCard(card);

        if (success) {
            // Criar registro no histórico
            CardBlockHistory history = new CardBlockHistory(card.getId(), reason);
            blockHistoryDAO.saveBlockHistory(history);
            System.out.println("[OK] Card bloqueado com motivo: " + reason);
        }

        return success;
    }

    /**
     * Desbloqueia um card com um motivo
     */
    public boolean unblockCard(Card card, String reason) throws SQLException {
        if (!card.isBlocked()) {
            System.out.println("[ERRO] Card não está bloqueado!");
            return false;
        }

        card.setBlocked(false);
        boolean success = cardDAO.updateCard(card);

        if (success) {
            // Atualizar o registro ativo no histórico
            CardBlockHistory activeBlock = blockHistoryDAO.getActiveBlockHistory(card.getId());
            if (activeBlock != null) {
                activeBlock.setUnblockedAt(LocalDateTime.now());
                activeBlock.setUnblockReason(reason);
                activeBlock.setActive(false);
                blockHistoryDAO.updateBlockHistory(activeBlock);
            }
            System.out.println("[OK] Card desbloqueado com motivo: " + reason);
        }

        return success;
    }

    /**
     * Cria um novo card em uma coluna
     */
    public Long createCard(Card card) throws SQLException {
        Long cardId = cardDAO.saveCard(card);
        if (cardId != null) {
            card.setId(cardId);
            System.out.println("[OK] Card criado com ID: " + cardId);
        }
        return cardId;
    }

    /**
     * Deleta um card
     */
    public boolean deleteCard(Long cardId) throws SQLException {
        boolean success = cardDAO.deleteCard(cardId);
        if (success) {
            System.out.println("[OK] Card deletado com sucesso");
        }
        return success;
    }
}
