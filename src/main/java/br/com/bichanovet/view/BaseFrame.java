package br.com.bichanovet.view;

import javax.swing.*;
import java.awt.*;

public class BaseFrame extends JFrame {

    public BaseFrame() {
        aplicarIconePadrao();
    }

    private void aplicarIconePadrao() {

        try {
            ImageIcon icon = new ImageIcon("C:/bichanovet/icons/caramelo.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.out.println("Erro ao carregar ícone.");
        }
    }
}