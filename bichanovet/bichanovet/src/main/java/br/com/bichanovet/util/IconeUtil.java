package br.com.bichanovet.util;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

public class IconeUtil {

    // 🎨 Cor padrão Vira-Lata Caramelo
    private static final Color COR_CARAMELO = new Color(196, 138, 75);

    // 🔠 Fonte padrão
    private static final Font FONTE_PADRAO = new Font("Segoe UI Emoji", Font.BOLD, 18);

    // 🐾 Emojis
    private static final String CLIENTE = "👤 ";
    private static final String PET = "🐾 ";
    private static final String PRODUTO = "🛍 ";
    private static final String VENDA = "💰 ";
    private static final String RELATORIO = "📊 ";
    private static final String SAIR = "🚪 ";

    public static void aplicarIconeCliente(JButton botao) {
        configurar(botao, CLIENTE);
    }

    public static void aplicarIconePet(JButton botao) {
        configurar(botao, PET);
    }

    public static void aplicarIconeProduto(JButton botao) {
        configurar(botao, PRODUTO);
    }

    public static void aplicarIconeVenda(JButton botao) {
        configurar(botao, VENDA);
    }

    public static void aplicarIconeRelatorio(JButton botao) {
        configurar(botao, RELATORIO);
    }

    public static void aplicarIconeSair(JButton botao) {
        configurar(botao, SAIR);
    }

    private static void configurar(JButton botao, String emoji) {

        botao.setText(emoji + botao.getText());
        botao.setFont(FONTE_PADRAO);

        botao.setBackground(COR_CARAMELO);
        botao.setForeground(Color.WHITE);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);

        botao.setHorizontalAlignment(JButton.CENTER);
        botao.setVerticalAlignment(JButton.CENTER);
    }
}