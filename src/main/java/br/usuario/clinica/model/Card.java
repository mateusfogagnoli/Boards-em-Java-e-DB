package br.usuario.clinica.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe que representa um card em um board
 */
public class Card {
    private Long id;
    private Long boardId;
    private Long columnId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private boolean blocked;
    private LocalDateTime enteredColumnAt;
    private LocalDateTime leftColumnAt;

    public Card() {
        this.createdAt = LocalDateTime.now();
    }

    public Card(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.blocked = false;
    }

    public Card(Long id, Long boardId, Long columnId, String title, String description,
                LocalDateTime createdAt, boolean blocked) {
        this.id = id;
        this.boardId = boardId;
        this.columnId = columnId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.blocked = blocked;
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

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public LocalDateTime getEnteredColumnAt() {
        return enteredColumnAt;
    }

    public void setEnteredColumnAt(LocalDateTime enteredColumnAt) {
        this.enteredColumnAt = enteredColumnAt;
    }

    public LocalDateTime getLeftColumnAt() {
        return leftColumnAt;
    }

    public void setLeftColumnAt(LocalDateTime leftColumnAt) {
        this.leftColumnAt = leftColumnAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", blocked=" + blocked +
                '}';
    }
}
