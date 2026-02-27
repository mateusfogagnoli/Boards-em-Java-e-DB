package br.usuario.clinica;

import br.usuario.clinica.ui.MainMenu;

/**
 * Classe principal da aplicação - Gerenciador de Boards (Kanban)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("═════════════════════════════════════════════════════════════════════");
        System.out.println("GERENCIADOR DE BOARDS KANBAN");
        System.out.println("");
        System.out.println("Uma aplicação Java para gerenciar boards Kanban com persistência");
        System.out.println("em banco de dados PostgreSQL");
        System.out.println("═════════════════════════════════════════════════════════════════════\n");

        try {
            MainMenu mainMenu = new MainMenu();
            mainMenu.show();
        } catch (Exception e) {
            System.err.println("[ERRO] Erro fatal na aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}