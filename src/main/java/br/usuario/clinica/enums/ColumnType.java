package br.usuario.clinica.enums;

/**
 * Enum para representar os tipos de colunas em um board
 */
public enum ColumnType {
    INICIAL("Inicial"),
    PENDENTE("Pendente"),
    FINAL("Final"),
    CANCELAMENTO("Cancelamento");

    private final String displayName;

    ColumnType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ColumnType fromString(String value) {
        for (ColumnType type : ColumnType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de coluna inválido: " + value);
    }
}
