package br.usuario.clinica.model;

import java.time.LocalDateTime;

/**
 * Classe que representa o histórico de bloqueio de um card
 */
public class CardBlockHistory {
    private Long id;
    private Long cardId;
    private LocalDateTime blockedAt;
    private LocalDateTime unblockedAt;
    private String blockReason;
    private String unblockReason;
    private boolean active;

    public CardBlockHistory() {
    }

    public CardBlockHistory(Long cardId, String blockReason) {
        this.cardId = cardId;
        this.blockReason = blockReason;
        this.blockedAt = LocalDateTime.now();
        this.active = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public LocalDateTime getUnblockedAt() {
        return unblockedAt;
    }

    public void setUnblockedAt(LocalDateTime unblockedAt) {
        this.unblockedAt = unblockedAt;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }

    public String getUnblockReason() {
        return unblockReason;
    }

    public void setUnblockReason(String unblockReason) {
        this.unblockReason = unblockReason;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getBlockDurationInSeconds() {
        if (unblockedAt == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.SECONDS.between(blockedAt, unblockedAt);
    }

    @Override
    public String toString() {
        return "CardBlockHistory{" +
                "id=" + id +
                ", cardId=" + cardId +
                ", blockReason='" + blockReason + '\'' +
                ", active=" + active +
                '}';
    }
}
