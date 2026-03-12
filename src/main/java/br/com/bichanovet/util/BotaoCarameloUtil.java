package br.com.bichanovet.util;

import javax.swing.JButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotaoCarameloUtil {

    public static final Color COR_FUNDO_TELA = new Color(248, 236, 214);
    public static final Color COR_FUNDO_PAINEL = new Color(242, 223, 191);

    private static final Color COR_BOTAO = new Color(166, 103, 56);
    private static final Color COR_BOTAO_HOVER = new Color(191, 123, 72);
    private static final Color COR_BORDA = new Color(129, 76, 39);

    private BotaoCarameloUtil() {
    }

    public static JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        aplicarEstilo(botao);
        return botao;
    }

    public static void aplicarEstilo(JButton botao) {
        botao.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        botao.setForeground(Color.WHITE);
        botao.setBackground(COR_BOTAO);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new CompoundBorder(
                new LineBorder(COR_BORDA, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(COR_BOTAO_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(COR_BOTAO);
            }
        });
    }
}