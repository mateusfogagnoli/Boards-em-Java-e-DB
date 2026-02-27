package br.usuario.clinica.util;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Classe utilitária para entrada de dados
 */
public class InputUtil {
    private static final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    /**
     * Lê uma linha de entrada do usuário
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Lê um número inteiro
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número válido!");
            }
        }
    }

    /**
     * Lê um número long
     */
    public static long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Por favor, digite um número válido!");
            }
        }
    }

    /**
     * Lê uma entrada com validação
     */
    public static String readLineNonEmpty(String prompt) {
        String input;
        do {
            input = readLine(prompt);
            if (input.isEmpty()) {
                System.out.println("[ERRO] Este campo não pode estar vazio!");
            }
        } while (input.isEmpty());
        return input;
    }

    /**
     * Pausa a execução
     */
    public static void pause(String message) {
        System.out.print(message);
        scanner.nextLine();
    }

    /**
     * Limpa o scanner
     */
    public static void close() {
        scanner.close();
    }
}
