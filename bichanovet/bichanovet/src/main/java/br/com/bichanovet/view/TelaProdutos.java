package br.com.bichanovet.view;

import br.com.bichanovet.dao.CategoriaDAO;
import br.com.bichanovet.dao.ProdutoDAO;
import br.com.bichanovet.model.Categoria;
import br.com.bichanovet.model.Produto;
import br.com.bichanovet.util.BotaoCarameloUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class TelaProdutos extends BaseFrame {

    private JTextField txtId;
    private JTextField txtNomeProduto;
    private JComboBox<Categoria> cbCategoria;
    private JComboBox<String> cbTipo;
    private JTextField txtPreco;
    private JTextField txtEstoque;
    private JCheckBox chkAtivo;

    private final ProdutoDAO dao = new ProdutoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private List<Produto> lista;
    private int indice = -1;

    public TelaProdutos() {
        configurarJanela();
        inicializarComponentes();
        carregarCategorias();
        carregarLista();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Produtos e Servicos");
        setSize(700, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);
    }

    private void inicializarComponentes() {
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(BotaoCarameloUtil.COR_FUNDO_PAINEL);

        JButton btnNovo = BotaoCarameloUtil.criarBotao("🆕 Novo");
        JButton btnSalvar = BotaoCarameloUtil.criarBotao("💾 Salvar");
        JButton btnAtualizar = BotaoCarameloUtil.criarBotao("✏️ Atualizar");
        JButton btnExcluir = BotaoCarameloUtil.criarBotao("🗑 Excluir");

        JButton btnPrimeiro = BotaoCarameloUtil.criarBotao("⏮");
        JButton btnAnterior = BotaoCarameloUtil.criarBotao("◀️");
        JButton btnProximo = BotaoCarameloUtil.criarBotao("▶️");
        JButton btnUltimo = BotaoCarameloUtil.criarBotao("⏭");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnPrimeiro);
        painelBotoes.add(btnAnterior);
        painelBotoes.add(btnProximo);
        painelBotoes.add(btnUltimo);

        add(painelBotoes, BorderLayout.NORTH);

        JPanel painel = new JPanel(new GridLayout(7, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painel.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);

        txtId = new JTextField();
        txtId.setEditable(false);

        txtNomeProduto = new JTextField();
        cbCategoria = new JComboBox<>();
        cbTipo = new JComboBox<>(new String[]{"PRODUTO", "SERVICO"});
        txtPreco = new JTextField();
        txtEstoque = new JTextField("0");
        chkAtivo = new JCheckBox("Ativo", true);
        chkAtivo.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);

        painel.add(new JLabel("ID:"));
        painel.add(txtId);
        painel.add(new JLabel("Nome Produto:"));
        painel.add(txtNomeProduto);
        painel.add(new JLabel("Categoria:"));
        painel.add(cbCategoria);
        painel.add(new JLabel("Tipo:"));
        painel.add(cbTipo);
        painel.add(new JLabel("Preco:"));
        painel.add(txtPreco);
        painel.add(new JLabel("Estoque:"));
        painel.add(txtEstoque);
        painel.add(new JLabel("Status:"));
        painel.add(chkAtivo);

        add(painel, BorderLayout.CENTER);

        btnNovo.addActionListener(e -> limparCampos());
        btnSalvar.addActionListener(e -> salvar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());

        btnPrimeiro.addActionListener(e -> navegar(0));
        btnAnterior.addActionListener(e -> navegar(indice - 1));
        btnProximo.addActionListener(e -> navegar(indice + 1));
        btnUltimo.addActionListener(e -> {
            if (lista != null && !lista.isEmpty()) {
                navegar(lista.size() - 1);
            }
        });
    }

    private void carregarCategorias() {
        cbCategoria.removeAllItems();
        List<Categoria> categorias = categoriaDAO.listar();
        for (Categoria categoria : categorias) {
            cbCategoria.addItem(categoria);
        }
    }

    private void carregarLista() {
        lista = dao.listar();
        if (lista != null && !lista.isEmpty()) {
            indice = 0;
            mostrarRegistro();
        } else {
            indice = -1;
            limparCampos();
        }
    }

    private void mostrarRegistro() {
        Produto produto = lista.get(indice);

        txtId.setText(String.valueOf(produto.getIdProduto()));
        txtNomeProduto.setText(produto.getNomeProduto());
        selecionarCategoria(produto.getIdCategoria());
        cbTipo.setSelectedItem(produto.getTipo());
        txtPreco.setText(String.valueOf(produto.getPreco()));
        txtEstoque.setText(String.valueOf(produto.getEstoque()));
        chkAtivo.setSelected(produto.getAtivo() != null && produto.getAtivo() == 1);
    }

    private void selecionarCategoria(Integer idCategoria) {
        for (int i = 0; i < cbCategoria.getItemCount(); i++) {
            Categoria categoria = cbCategoria.getItemAt(i);
            if (categoria != null && categoria.getIdCategoria() != null && categoria.getIdCategoria().equals(idCategoria)) {
                cbCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void navegar(int novoIndice) {
        if (lista != null && novoIndice >= 0 && novoIndice < lista.size()) {
            indice = novoIndice;
            mostrarRegistro();
        }
    }

    private void salvar() {
        Produto produto = construirProdutoTela();
        if (produto == null) {
            return;
        }

        dao.inserir(produto);
        carregarLista();

        if (lista != null && !lista.isEmpty()) {
            indice = lista.size() - 1;
            mostrarRegistro();
        }
    }

    private void atualizar() {
        if (txtId.getText().isEmpty()) {
            return;
        }

        Produto produto = construirProdutoTela();
        if (produto == null) {
            return;
        }

        produto.setIdProduto(Integer.parseInt(txtId.getText()));
        dao.atualizar(produto);
        carregarLista();
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            return;
        }

        dao.excluir(Integer.parseInt(txtId.getText()));
        carregarLista();
    }

    private Produto construirProdutoTela() {
        if (txtNomeProduto.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do produto/servico.");
            txtNomeProduto.requestFocus();
            return null;
        }

        Categoria categoria = (Categoria) cbCategoria.getSelectedItem();
        if (categoria == null || categoria.getIdCategoria() == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria.");
            cbCategoria.requestFocus();
            return null;
        }

        try {
            double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
            int estoque = Integer.parseInt(txtEstoque.getText().trim());

            if (preco < 0) {
                JOptionPane.showMessageDialog(this, "Preco nao pode ser negativo.");
                return null;
            }

            Produto produto = new Produto();
            produto.setNomeProduto(txtNomeProduto.getText().trim());
            produto.setIdCategoria(categoria.getIdCategoria());
            produto.setTipo((String) cbTipo.getSelectedItem());
            produto.setPreco(preco);
            produto.setEstoque(estoque);
            produto.setAtivo(chkAtivo.isSelected() ? 1 : 0);
            return produto;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe preco e estoque validos.");
            return null;
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNomeProduto.setText("");
        if (cbCategoria.getItemCount() > 0) {
            cbCategoria.setSelectedIndex(0);
        }
        cbTipo.setSelectedItem("PRODUTO");
        txtPreco.setText("");
        txtEstoque.setText("0");
        chkAtivo.setSelected(true);
    }
}