package br.usuario.clinica.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.usuario.clinica.enums.ColumnType;

/**
 * Classe que representa um board (quadro Kanban)
 */
public class Board {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private List<BoardColumn> columns;

    public Board() {
        this.columns = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public Board(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public Board(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.columns = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<BoardColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<BoardColumn> columns) {
        this.columns = columns;
        // Ordenar as colunas por order
        this.columns.sort((c1, c2) -> Integer.compare(c1.getOrder(), c2.getOrder()));
    }

    public void addColumn(BoardColumn column) {
        this.columns.add(column);
        this.columns.sort((c1, c2) -> Integer.compare(c1.getOrder(), c2.getOrder()));
    }

    public BoardColumn getInitialColumn() {
        return this.columns.stream()
                .filter(col -> col.getType() == ColumnType.INICIAL)
                .findFirst()
                .orElse(null);
    }

    public BoardColumn getFinalColumn() {
        return this.columns.stream()
                .filter(col -> col.getType() == ColumnType.FINAL)
                .findFirst()
                .orElse(null);
    }

    public BoardColumn getCancelledColumn() {
        return this.columns.stream()
                .filter(col -> col.getType() == ColumnType.CANCELAMENTO)
                .findFirst()
                .orElse(null);
    }

    public BoardColumn getColumnById(Long columnId) {
        return this.columns.stream()
                .filter(col -> col.getId().equals(columnId))
                .findFirst()
                .orElse(null);
    }

    public BoardColumn getColumnByOrder(int order) {
        return this.columns.stream()
                .filter(col -> col.getOrder() == order)
                .findFirst()
                .orElse(null);
    }

    public Card findCardById(Long cardId) {
        return this.columns.stream()
                .flatMap(col -> col.getCards().stream())
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElse(null);
    }

    // Validations
    public boolean isValidStructure() {
        // Verificar se tem coluna inicial, final e cancelamento
        boolean hasInitial = this.columns.stream().anyMatch(col -> col.getType() == ColumnType.INICIAL);
        boolean hasFinal = this.columns.stream().anyMatch(col -> col.getType() == ColumnType.FINAL);
        boolean hasCancelled = this.columns.stream().anyMatch(col -> col.getType() == ColumnType.CANCELAMENTO);

        // Verificar se só tem 1 de cada tipo obrigatório
        long initialCount = this.columns.stream().filter(col -> col.getType() == ColumnType.INICIAL).count();
        long finalCount = this.columns.stream().filter(col -> col.getType() == ColumnType.FINAL).count();
        long cancelledCount = this.columns.stream().filter(col -> col.getType() == ColumnType.CANCELAMENTO).count();

        if (initialCount > 1 || finalCount > 1 || cancelledCount > 1) {
            return false;
        }

        // Verificar se inicial é primeira, final é penúltima e cancelamento é última
        if (!hasInitial || !hasFinal || !hasCancelled) {
            return false;
        }

        BoardColumn initialCol = getInitialColumn();
        BoardColumn finalCol = getFinalColumn();
        BoardColumn cancelledCol = getCancelledColumn();

        if (initialCol.getOrder() != 1) {
            return false;
        }

        if (finalCol.getOrder() != columns.size() - 1) {
            return false;
        }

        if (cancelledCol.getOrder() != columns.size()) {
            return false;
        }

        return columns.size() >= 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", columnsCount=" + columns.size() +
                '}';
    }
}
