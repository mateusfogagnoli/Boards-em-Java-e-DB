package br.usuario.clinica.ui;

import java.sql.SQLException;
import java.util.List;

import br.usuario.clinica.config.DatabaseConnection;
import br.usuario.clinica.model.Board;
import br.usuario.clinica.model.BoardColumn;
import br.usuario.clinica.service.BoardService;
import br.usuario.clinica.util.InputUtil;

/**
 * Menu principal da aplicação
 */
public class MainMenu {
    private final BoardService boardService;

    public MainMenu() {
        this.boardService = new BoardService();
    }

    /**
     * Exibe o menu principal e processa as opções
     */
    public void show() {
        System.out.println("[*] Inicializando banco de dados...");
        DatabaseConnection.initializeDatabase();

        boolean running = true;
        while (running) {
            displayMenu();
            String choice = InputUtil.readLine("\nEscolha uma opção: ").trim();

            try {
                switch (choice) {
                    case "1":
                        createNewBoard();
                        break;
                    case "2":
                        selectBoard();
                        break;
                    case "3":
                        deleteBoards();
                        break;
                    case "4":
                        running = false;
                        System.out.println("\n[OK] Até logo!");
                        break;
                    default:
                        System.out.println("[ERRO] Opção inválida! Tente novamente.");
                }
            } catch (SQLException e) {
                System.err.println("[ERRO] Erro ao processar a opção: " + e.getMessage());
            }

            if (running && !choice.equals("2")) {
                InputUtil.pause("Pressione ENTER para continuar...");
            }
        }
    }

    /**
     * Exibe o menu principal
     */
    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("MENU PRINCIPAL - GERENCIADOR DE BOARDS");
        System.out.println("=".repeat(50));
        System.out.println("1. Criar um novo board");
        System.out.println("2. Selecionar board");
        System.out.println("3. Excluir boards");
        System.out.println("4. Sair");
        System.out.println("=".repeat(50));
    }

    /**
     * Cria um novo board
     */
    private void createNewBoard() throws SQLException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("[+] CRIAR NOVO BOARD");
        System.out.println("=".repeat(50));

        String boardName = InputUtil.readLineNonEmpty("Nome do board: ");

        System.out.println("\n[*] Configurar colunas:");
        System.out.println("1. Usar configuração padrão (recomendado)");
        System.out.println("2. Configurar colunas customizadas");

        String columnsChoice = InputUtil.readLine("Escolha: ").trim();

        List<BoardColumn> columns;
        if (columnsChoice.equals("2")) {
            columns = createCustomColumns();
        } else {
            columns = BoardService.createDefaultColumns();
            System.out.println("\n[OK] Colunas padrão selecionadas:");
            for (var col : columns) {
                System.out.println("  - " + col.getName() + " (" + col.getType().getDisplayName() + ")");
            }
        }

        Long boardId = boardService.createBoard(boardName, columns);

        if (boardId != null) {
            System.out.println("\n[OK] Board criado com sucesso!");
            System.out.println("ID: " + boardId);
        } else {
            System.out.println("\n[ERRO] Erro ao criar o board!");
        }
    }

    /**
     * Cria colunas customizadas
     */
    private List<BoardColumn> createCustomColumns() {
        List<BoardColumn> columns = new java.util.ArrayList<>();

        System.out.println("\nConfigure no mínimo 3 colunas com os seguintes tipos:");
        System.out.println("- Inicial (exatamente 1)");
        System.out.println("- Pendente (0 ou mais)");
        System.out.println("- Final (exatamente 1)");
        System.out.println("- Cancelamento (exatamente 1)");

        String addMore = "sim";
        int order = 1;

        while (addMore.equalsIgnoreCase("sim")) {
            System.out.println("\n--- Coluna " + order + " ---");
            String columnName = InputUtil.readLineNonEmpty("Nome da coluna: ");

            System.out.println("Tipos disponíveis: Inicial, Pendente, Final, Cancelamento");
            String type = InputUtil.readLineNonEmpty("Tipo da coluna: ");

            BoardColumn column = BoardService.createCustomColumn(columnName, order, type);

            if (column != null) {
                columns.add(column);
                System.out.println("[OK] Coluna adicionada!");
                order++;

                if (order > 1) {
                    addMore = InputUtil.readLine("Adicionar mais coluna? (sim/não): ").trim();
                } else {
                    addMore = "sim";
                }
            } else {
                System.out.println("[ERRO] Erro ao adicionar coluna!");
            }
        }

        return columns;
    }

    /**
     * Seleciona um board e abre o menu de manipulação
     */
    private void selectBoard() throws SQLException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("[*] SELECIONAR BOARD");
        System.out.println("=".repeat(50));

        List<Board> boards = boardService.getAllBoards();

        if (boards.isEmpty()) {
            System.out.println("[ERRO] Nenhum board encontrado!");
            return;
        }

        System.out.println("\nBoards disponíveis:\n");
        for (int i = 0; i < boards.size(); i++) {
            Board board = boards.get(i);
            System.out.printf("%d. %s (ID: %d, Colunas: %d)\n",
                    i + 1, board.getName(), board.getId(), board.getColumns().size());
        }

        int choice = InputUtil.readInt("\nEscolha um board (número): ") - 1;

        if (choice >= 0 && choice < boards.size()) {
            Board selectedBoard = boards.get(choice);
            BoardManipulationMenu boardMenu = new BoardManipulationMenu(selectedBoard, boardService);
            boardMenu.show();
        } else {
            System.out.println("[ERRO] Opção inválida!");
        }
    }

    /**
     * Deleta boards
     */
    private void deleteBoards() throws SQLException {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("[X] EXCLUIR BOARDS");
        System.out.println("=".repeat(50));

        List<Board> boards = boardService.getAllBoards();

        if (boards.isEmpty()) {
            System.out.println("[ERRO] Nenhum board para excluir!");
            return;
        }

        System.out.println("\nBoards disponíveis:\n");
        for (int i = 0; i < boards.size(); i++) {
            System.out.printf("%d. %s (ID: %d)\n",
                    i + 1, boards.get(i).getName(), boards.get(i).getId());
        }

        int choice = InputUtil.readInt("\nEscolha um board para excluir (número): ") - 1;

        if (choice >= 0 && choice < boards.size()) {
            Board selectedBoard = boards.get(choice);
            String confirm = InputUtil.readLine("\nTem certeza que deseja excluir '" + selectedBoard.getName() + "'? (sim/não): ").trim();

            if (confirm.equalsIgnoreCase("sim")) {
                if (boardService.deleteBoard(selectedBoard.getId())) {
                    System.out.println("[OK] Board excluído com sucesso!");
                } else {
                    System.out.println("[ERRO] Erro ao excluir o board!");
                }
            }
        } else {
            System.out.println("[ERRO] Opção inválida!");
        }
    }
}
