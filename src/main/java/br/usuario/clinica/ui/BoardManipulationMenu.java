package br.usuario.clinica.ui;

import java.sql.SQLException;

import br.usuario.clinica.model.Board;
import br.usuario.clinica.model.BoardColumn;
import br.usuario.clinica.model.Card;
import br.usuario.clinica.service.BoardService;
import br.usuario.clinica.service.CardService;
import br.usuario.clinica.service.ReportService;
import br.usuario.clinica.util.InputUtil;

/**
 * Menu para manipulação de um board específico
 */
public class BoardManipulationMenu {
    private final Board board;
    private final BoardService boardService;
    private final CardService cardService;
    private final ReportService reportService;

    public BoardManipulationMenu(Board board, BoardService boardService) {
        this.board = board;
        this.boardService = boardService;
        this.cardService = new CardService();
        this.reportService = new ReportService();
    }

    /**
     * Exibe o menu de manipulação do board
     */
    public void show() {
        boolean running = true;
        while (running) {
            try {
                displayBoardStatus();
                displayMenu();
                String choice = InputUtil.readLine("\nEscolha uma opção: ").trim();

                switch (choice) {
                    case "1":
                        createCard();
                        break;
                    case "2":
                        moveCard();
                        break;
                    case "3":
                        cancelCard();
                        break;
                    case "4":
                        blockCard();
                        break;
                    case "5":
                        unblockCard();
                        break;
                    case "6":
                        showReports();
                        break;
                    case "7":
                        running = false;
                        System.out.println("\n[OK] Retornando ao menu principal...");
                        break;
                    default:
                        System.out.println("[ERRO] Opção inválida! Tente novamente.");
                }

                if (running && !choice.equals("6")) {
                    InputUtil.pause("\nPressione ENTER para continuar...");
                }
            } catch (SQLException e) {
                System.err.println("[ERRO] Erro ao processar a opção: " + e.getMessage());
                InputUtil.pause("Pressione ENTER para continuar...");
            }
        }
    }

    /**
     * Exibe o status atual do board
     */
    private void displayBoardStatus() throws SQLException {
        // Recarregar o board do banco
        board.setColumns(new br.usuario.clinica.dao.BoardColumnDAO().getColumnsByBoardId(board.getId()));

        System.out.println("\n" + "=".repeat(70));
        System.out.println("BOARD: " + board.getName());
        System.out.println("=".repeat(70));

        for (BoardColumn column : board.getColumns()) {
            System.out.println("\n📌 " + column.getName() + " [" + column.getType().getDisplayName() + "]");
            System.out.println("   " + "-".repeat(65));

            if (column.getCards().isEmpty()) {
                System.out.println("   (vazio)");
            } else {
                for (Card card : column.getCards()) {
                    String status = card.isBlocked() ? " [BLOQ]" : "";
                    System.out.printf("   * [%d] %s%s\n", card.getId(), card.getTitle(), status);
                }
            }
        }
        System.out.println("\n" + "=".repeat(70));
    }

    /**
     * Exibe o menu de opções
     */
    private void displayMenu() {
        System.out.println("\n[*] OPCOES DE MANIPULACAO");
        System.out.println("=".repeat(70));
        System.out.println("1. Criar novo card");
        System.out.println("2. Mover card para próxima coluna");
        System.out.println("3. Cancelar card");
        System.out.println("4. Bloquear card");
        System.out.println("5. Desbloquear card");
        System.out.println("6. Gerar relatórios");
        System.out.println("7. Fechar board");
        System.out.println("=".repeat(70));
    }

    /**
     * Cria um novo card
     */
    private void createCard() throws SQLException {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("[+] CRIAR NOVO CARD");
        System.out.println("=".repeat(70));

        String title = InputUtil.readLineNonEmpty("Título do card: ");
        String description = InputUtil.readLine("Descrição do card (opcional): ");

        BoardColumn initialColumn = board.getInitialColumn();
        if (initialColumn == null) {
            System.out.println("[ERRO] Coluna inicial não encontrada!");
            return;
        }

        Card card = new Card(title, description);
        card.setBoardId(board.getId());
        card.setColumnId(initialColumn.getId());
        card.setEnteredColumnAt(java.time.LocalDateTime.now());

        Long cardId = cardService.createCard(card);

        if (cardId != null) {
            System.out.println("[OK] Card criado com sucesso!");
            initialColumn.addCard(card);
        } else {
            System.out.println("[ERRO] Erro ao criar o card!");
        }
    }

    /**
     * Move um card para a próxima coluna
     */
    private void moveCard() throws SQLException {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("[>] MOVER CARD PARA PROXIMA COLUNA");
        System.out.println("=".repeat(70));

        long cardId = InputUtil.readLong("ID do card para mover: ");
        Card card = board.findCardById(cardId);

        if (card == null) {
            System.out.println("[ERRO] Card não encontrado!");
            return;
        }

        if (cardService.moveCardToNextColumn(board, card)) {
            System.out.println("[OK] Card movido com sucesso!");
        }
    }

    /**
     * Cancela um card
     */
    private void cancelCard() throws SQLException {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("[X] CANCELAR CARD");
        System.out.println("=".repeat(70));

        long cardId = InputUtil.readLong("ID do card para cancelar: ");
        Card card = board.findCardById(cardId);

        if (card == null) {
            System.out.println("[ERRO] Card não encontrado!");
            return;
        }

        if (cardService.cancelCard(board, card)) {
            System.out.println("[OK] Card cancelado com sucesso!");
        }
    }

    /**
     * Bloqueia um card
     */
    private void blockCard() throws SQLException {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("[L] BLOQUEAR CARD");
        System.out.println("=".repeat(70));

        long cardId = InputUtil.readLong("ID do card para bloquear: ");
        Card card = board.findCardById(cardId);

        if (card == null) {
            System.out.println("[ERRO] Card não encontrado!");
            return;
        }

        String reason = InputUtil.readLineNonEmpty("Motivo do bloqueio: ");
        if (cardService.blockCard(card, reason)) {
            System.out.println("[OK] Card bloqueado com sucesso!");
        }
    }

    /**
     * Desbloqueia um card
     */
    private void unblockCard() throws SQLException {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("[U] DESBLOQUEAR CARD");
        System.out.println("=".repeat(70));

        long cardId = InputUtil.readLong("ID do card para desbloquear: ");
        Card card = board.findCardById(cardId);

        if (card == null) {
            System.out.println("[ERRO] Card não encontrado!");
            return;
        }

        String reason = InputUtil.readLineNonEmpty("Motivo do desbloqueio: ");
        if (cardService.unblockCard(card, reason)) {
            System.out.println("[OK] Card desbloqueado com sucesso!");
        }
    }

    /**
     * Exibe os relatórios
     */
    private void showReports() throws SQLException {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("[R] RELATORIOS");
        System.out.println("=".repeat(70));
        System.out.println("1. Relatório de tempo de conclusão");
        System.out.println("2. Relatório de bloqueios");
        System.out.println("3. Relatório completo");
        System.out.println("4. Voltar");
        System.out.println("=".repeat(70));

        String choice = InputUtil.readLine("Escolha uma opção: ").trim();

        switch (choice) {
            case "1":
                reportService.generateCompletionTimeReport(board);
                break;
            case "2":
                reportService.generateBlockageReportReport(board);
                break;
            case "3":
                reportService.generateFullReport(board);
                break;
            case "4":
                break;
            default:
                System.out.println("[ERRO] Opcao invalida!");
        }

        if (!choice.equals("4")) {
            InputUtil.pause("Pressione ENTER para voltar ao menu...");
        }
    }
}
