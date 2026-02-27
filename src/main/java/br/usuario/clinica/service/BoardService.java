package br.usuario.clinica.service;

import java.sql.SQLException;
import java.util.List;

import br.usuario.clinica.dao.BoardColumnDAO;
import br.usuario.clinica.dao.BoardDAO;
import br.usuario.clinica.enums.ColumnType;
import br.usuario.clinica.model.Board;
import br.usuario.clinica.model.BoardColumn;

/**
 * Serviço para gerenciar operações relacionadas a Boards
 */
public class BoardService {
    private final BoardDAO boardDAO;
    private final BoardColumnDAO columnDAO;

    public BoardService() {
        this.boardDAO = new BoardDAO();
        this.columnDAO = new BoardColumnDAO();
    }

    /**
     * Cria um novo board com as colunas padrão
     */
    public Long createBoard(String boardName, List<BoardColumn> columns) throws SQLException {
        // Validar estrutura
        Board tempBoard = new Board(boardName);
        tempBoard.setColumns(columns);

        if (!tempBoard.isValidStructure()) {
            System.out.println("[ERRO] Estrutura de board inválida!");
            System.out.println("   - Deve ter 3 ou mais colunas");
            System.out.println("   - Coluna Inicial deve ser a primeira");
            System.out.println("   - Coluna Final deve ser a penúltima");
            System.out.println("   - Coluna Cancelamento deve ser a última");
            return null;
        }

        // Salvar board
        Board board = new Board(boardName);
        Long boardId = boardDAO.saveBoard(board);

        if (boardId != null) {
            // Salvar colunas
            for (BoardColumn column : columns) {
                column.setBoardId(boardId);
                Long columnId = columnDAO.saveColumn(column);
                column.setId(columnId);
            }

            System.out.println("[OK] Board criado com ID: " + boardId);
            System.out.println("[OK] Colunas criadas: " + columns.size());
        }

        return boardId;
    }

    /**
     * Recupera um board pelo ID
     */
    public Board getBoardById(Long boardId) throws SQLException {
        return boardDAO.getBoardById(boardId);
    }

    /**
     * Lista todos os boards
     */
    public List<Board> getAllBoards() throws SQLException {
        return boardDAO.getAllBoards();
    }

    /**
     * Deleta um board
     */
    public boolean deleteBoard(Long boardId) throws SQLException {
        boolean success = boardDAO.deleteBoard(boardId);
        if (success) {
            System.out.println("[OK] Board deletado com sucesso");
        }
        return success;
    }

    /**
     * Cria as colunas padrão para um novo board
     */
    public static List<BoardColumn> createDefaultColumns() {
        List<BoardColumn> columns = new java.util.ArrayList<>();

        columns.add(new BoardColumn("A Fazer", 1, ColumnType.INICIAL));
        columns.add(new BoardColumn("Em Progresso", 2, ColumnType.PENDENTE));
        columns.add(new BoardColumn("Revisão", 3, ColumnType.PENDENTE));
        columns.add(new BoardColumn("Concluído", 4, ColumnType.FINAL));
        columns.add(new BoardColumn("Cancelado", 5, ColumnType.CANCELAMENTO));

        return columns;
    }

    /**
     * Cria colunas customizadas
     */
    public static BoardColumn createCustomColumn(String name, int order, String type) {
        try {
            ColumnType columnType = ColumnType.fromString(type);
            return new BoardColumn(name, order, columnType);
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO] Tipo de coluna inválido: " + type);
            return null;
        }
    }
}
