package br.com.bichanovet.view;

import javax.swing.*;
import java.awt.*;
import br.com.bichanovet.util.*;
import br.com.bichanovet.view.TelaClientes;

public class TelaInicial extends JFrame {

    private JButton btnClientes;
    private JButton btnPets;
    private JButton btnProdutos;
    private JButton btnVendas;
    private JButton btnRelatorios;
    private JButton btnSair;

    public TelaInicial() {
        configurarJanela();
        inicializarComponentes();
        btnClientes.addActionListener(e -> {
            TelaClientes tela = new TelaClientes();
            tela.setVisible(true);
        });
        configurarIconeSistema();

        IconeUtil.aplicarIconeCliente(btnClientes);
        IconeUtil.aplicarIconePet(btnPets);
        IconeUtil.aplicarIconeProduto(btnProdutos);
        IconeUtil.aplicarIconeVenda(btnVendas);
        IconeUtil.aplicarIconeRelatorio(btnRelatorios);
        IconeUtil.aplicarIconeSair(btnSair);
    }

    private void configurarJanela() {
        setTitle("BichanoVet - Sistema para PetShop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void configurarIconeSistema() {
        ImageIcon icon = new ImageIcon("C:/bichanovet/icons/caramelo.png");
        setIconImage(icon.getImage());
    }

    private void inicializarComponentes() {

        // ================= PAINEL CENTRAL =================
        JPanel painelCentro = new JPanel();
        painelCentro.setLayout(new BoxLayout(painelCentro, BoxLayout.Y_AXIS));
        painelCentro.setBackground(new Color(245, 245, 245));

        painelCentro.add(Box.createVerticalGlue());

        // IMAGEM DO CARAMELO
        ImageIcon imagemOriginal = new ImageIcon("C:/bichanovet/icons/caramelo.png");
        Image imagemRedimensionada = imagemOriginal.getImage()
                .getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel lblImagem = new JLabel(new ImageIcon(imagemRedimensionada));
        lblImagem.setAlignmentX(Component.CENTER_ALIGNMENT);

        // TÍTULO
        JLabel lblTitulo = new JLabel("BICHANOVET");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 40));
        lblTitulo.setForeground(new Color(150, 90, 40)); // Tom caramelo
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelCentro.add(lblImagem);
        painelCentro.add(Box.createRigidArea(new Dimension(0, 20)));
        painelCentro.add(lblTitulo);

        painelCentro.add(Box.createVerticalGlue());

        add(painelCentro, BorderLayout.CENTER);

        // ================= PAINEL INFERIOR =================
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(1, 6, 20, 10));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        painelBotoes.setBackground(new Color(210, 140, 70)); // Caramelo

        btnClientes = new JButton("Clientes");
        btnPets = new JButton("Pets");
        btnProdutos = new JButton("Produtos");
        btnVendas = new JButton("Vendas");
        btnRelatorios = new JButton("Relatórios");
        btnSair = new JButton("Sair");

        estilizarBotao(btnClientes);
        estilizarBotao(btnPets);
        estilizarBotao(btnProdutos);
        estilizarBotao(btnVendas);
        estilizarBotao(btnRelatorios);
        estilizarBotao(btnSair);

        painelBotoes.add(btnClientes);
        painelBotoes.add(btnPets);
        painelBotoes.add(btnProdutos);
        painelBotoes.add(btnVendas);
        painelBotoes.add(btnRelatorios);
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.SOUTH);

        btnSair.addActionListener(e -> System.exit(0));
    }

    private void estilizarBotao(JButton botao) {
        botao.setFont(new Font("Arial", Font.BOLD, 16));
        botao.setFocusPainted(false);
        botao.setBackground(new Color(160, 82, 45)); // Marrom caramelo
        botao.setForeground(Color.WHITE);
    }
}