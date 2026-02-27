package br.usuario.clinica.service;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import br.usuario.clinica.dao.CardBlockHistoryDAO;
import br.usuario.clinica.dao.CardDAO;
import br.usuario.clinica.model.Board;
import br.usuario.clinica.model.Card;
import br.usuario.clinica.model.CardBlockHistory;

/**
 * Serviço para gerar relatórios do board
 */
public class ReportService {
    private final CardDAO cardDAO;
    private final CardBlockHistoryDAO blockHistoryDAO;

    public ReportService() {
        this.cardDAO = new CardDAO();
        this.blockHistoryDAO = new CardBlockHistoryDAO();
    }

    /**
     * Gera um relatório de tempo de conclusão por coluna
     */
    public void generateCompletionTimeReport(Board board) throws SQLException {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("RELATORIO DE TEMPO DE CONCLUSAO - " + board.getName());
        System.out.println("=".repeat(70));

        List<Card> allCards = cardDAO.getCardsByBoardId(board.getId());

        if (allCards.isEmpty()) {
            System.out.println("Nenhum card encontrado neste board.");
            return;
        }

        System.out.printf("%-20s | %-15s | %-20s | %-15s\n", "CARD", "COLUNA ATUAL", "TEMPO NA COLUNA", "TOTAL DESDE CRIAÇÃO");
        System.out.println("-".repeat(70));

        for (Card card : allCards) {
            String columnName = board.getColumnById(card.getColumnId()).getName();
            
            long timeInColumn = 0;
            if (card.getEnteredColumnAt() != null) {
                LocalDateTime endTime = card.getLeftColumnAt() != null ? 
                        card.getLeftColumnAt() : LocalDateTime.now();
                timeInColumn = Duration.between(card.getEnteredColumnAt(), endTime).toHours();
            }

            long totalTime = Duration.between(card.getCreatedAt(), LocalDateTime.now()).toHours();

            System.out.printf("%-20s | %-15s | %-20d h | %-15d h\n", 
                    card.getTitle(), columnName, timeInColumn, totalTime);
        }

        System.out.println("=".repeat(70) + "\n");
    }

    /**
     * Gera um relatório de bloqueios de cards
     */
    public void generateBlockageReportReport(Board board) throws SQLException {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RELATORIO DE BLOQUEIOS - " + board.getName());
        System.out.println("=".repeat(80));

        List<Card> allCards = cardDAO.getCardsByBoardId(board.getId());

        if (allCards.isEmpty()) {
            System.out.println("Nenhum card encontrado neste board.");
            return;
        }

        boolean hasBlockages = false;

        for (Card card : allCards) {
            List<CardBlockHistory> blockHistories = blockHistoryDAO.getBlockHistoryByCardId(card.getId());

            if (!blockHistories.isEmpty()) {
                if (!hasBlockages) {
                    hasBlockages = true;
                    System.out.printf("%-25s | %-30s | %-20s | %-25s\n", 
                            "CARD", "MOTIVO DO BLOQUEIO", "TEMPO BLOQUEADO", "MOTIVO DESBLOQUEIO");
                    System.out.println("-".repeat(80));
                }

                for (CardBlockHistory history : blockHistories) {
                    String timeLocked = "Ainda bloqueado";
                    if (history.getUnblockedAt() != null) {
                        long hours = Duration.between(
                                history.getBlockedAt(), 
                                history.getUnblockedAt()
                        ).toHours();
                        timeLocked = hours + " h";
                    }

                    String unblockReason = history.getUnblockReason() != null ? 
                            history.getUnblockReason() : "N/A";

                    System.out.printf("%-25s | %-30s | %-20s | %-25s\n",
                            card.getTitle(),
                            history.getBlockReason(),
                            timeLocked,
                            unblockReason);
                }
            }
        }

        if (!hasBlockages) {
            System.out.println("Nenhum card bloqueado encontrado neste board.");
        }

        System.out.println("=".repeat(80) + "\n");
    }

    /**
     * Gera um relatório completo do board
     */
    public void generateFullReport(Board board) throws SQLException {
        System.out.println("\n" + "#".repeat(70));
        System.out.println("# RELATORIO COMPLETO DO BOARD: " + board.getName());
        System.out.println("#".repeat(70));

        // Resumo geral
        System.out.println("\nRESUMO DO BOARD");
        System.out.println("-".repeat(70));
        System.out.println("ID do Board: " + board.getId());
        System.out.println("Data de Criação: " + board.getCreatedAt());
        System.out.println("Total de Colunas: " + board.getColumns().size());

        int totalCards = (int) board.getColumns().stream()
                .mapToLong(col -> col.getCards().size())
                .sum();
        System.out.println("Total de Cards: " + totalCards);

        // Detalhamento por coluna
        System.out.println("\nCARDS POR COLUNA");
        System.out.println("-".repeat(70));
        for (var column : board.getColumns()) {
            System.out.printf("%-25s [%s]: %d card(s)\n", 
                    column.getName(), column.getType().getDisplayName(), column.getCards().size());
        }

        // Relatório completo
        generateCompletionTimeReport(board);
        generateBlockageReportReport(board);
    }
}
