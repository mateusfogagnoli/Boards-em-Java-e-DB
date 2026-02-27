package br.usuario.clinica.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.usuario.clinica.enums.ColumnType;

/**
 * Classe que representa uma coluna em um board
 */
public class BoardColumn {
    private Long id;
    private Long boardId;
    private String name;
    private int order;
    private ColumnType type;
    private List<Card> cards;

    public BoardColumn() {
        this.cards = new ArrayList<>();
    }

    public BoardColumn(String name, int order, ColumnType type) {
        this.name = name;
        this.order = order;
        this.type = type;
        this.cards = new ArrayList<>();
    }

    public BoardColumn(Long id, Long boardId, String name, int order, ColumnType type) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.order = order;
        this.type = type;
        this.cards = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void addCard(Card card) {
        this.cards.add(card);
        card.setColumnId(this.id);
        card.setEnteredColumnAt(java.time.LocalDateTime.now());
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
    }

    public Card findCardById(Long cardId) {
        return this.cards.stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardColumn column = (BoardColumn) o;
        return Objects.equals(id, column.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BoardColumn{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", cardsCount=" + cards.size() +
                '}';
    }
}
