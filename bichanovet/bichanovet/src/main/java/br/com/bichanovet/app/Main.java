package br.com.bichanovet.app;

import javax.swing.SwingUtilities;
import br.com.bichanovet.view.TelaInicial;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaInicial tela = new TelaInicial();
            tela.setVisible(true);
        });
    }
}