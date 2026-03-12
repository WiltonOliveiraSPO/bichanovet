package br.com.bichanovet.view;

import br.com.bichanovet.dao.ClienteDAO;
import br.com.bichanovet.dao.ProdutoDAO;
import br.com.bichanovet.dao.VendaDAO;
import br.com.bichanovet.model.Cliente;
import br.com.bichanovet.model.ItemNotaFiscal;
import br.com.bichanovet.model.ItemVenda;
import br.com.bichanovet.model.Produto;
import br.com.bichanovet.model.Venda;
import br.com.bichanovet.util.BotaoCarameloUtil;
import br.com.bichanovet.util.NotaFiscalPdfUtil;
import com.toedter.calendar.JDateChooser;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TelaVendas extends BaseFrame {

    private JTextField txtId;
    private JComboBox<ClienteItem> cbCliente;
    private JDateChooser dateVenda;
    private JComboBox<String> cbFormaPagamento;
    private JTextField txtObservacoes;
    private JLabel lblTotal;

    private JComboBox<ProdutoItem> cbProduto;
    private JTextField txtQuantidade;
    private JTextField txtPreco;
    private JTable tabelaItens;
    private DefaultTableModel modeloItens;

    private final VendaDAO vendaDAO = new VendaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private final List<ItemCache> itemCache = new ArrayList<>();
    private boolean atualizandoTabela = false;

    private List<Venda> lista;
    private int indice = -1;

    public TelaVendas() {
        configurarJanela();
        inicializarComponentes();
        carregarClientes();
        carregarProdutos();
        carregarLista();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Vendas");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);
    }

    private void inicializarComponentes() {
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        painelBotoes.setBackground(BotaoCarameloUtil.COR_FUNDO_PAINEL);

        JPanel painelCrud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelCrud.setBackground(BotaoCarameloUtil.COR_FUNDO_PAINEL);

        JButton btnNovo = BotaoCarameloUtil.criarBotao("🆕 Novo");
        JButton btnSalvar = BotaoCarameloUtil.criarBotao("💾 Salvar");
        JButton btnAtualizar = BotaoCarameloUtil.criarBotao("✏️ Atualizar");
        JButton btnExcluir = BotaoCarameloUtil.criarBotao("🗑 Excluir");

        painelCrud.add(btnNovo);
        painelCrud.add(btnSalvar);
        painelCrud.add(btnAtualizar);
        painelCrud.add(btnExcluir);

        JPanel painelNav = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelNav.setBackground(BotaoCarameloUtil.COR_FUNDO_PAINEL);

        JButton btnPrimeiro = BotaoCarameloUtil.criarBotao("⏮");
        JButton btnAnterior = BotaoCarameloUtil.criarBotao("◀️");
        JButton btnProximo = BotaoCarameloUtil.criarBotao("▶️");
        JButton btnUltimo = BotaoCarameloUtil.criarBotao("⏭");

        painelNav.add(btnPrimeiro);
        painelNav.add(btnAnterior);
        painelNav.add(btnProximo);
        painelNav.add(btnUltimo);

        painelBotoes.add(painelCrud);
        painelBotoes.add(painelNav);

        add(painelBotoes, BorderLayout.NORTH);

        JPanel painelDados = new JPanel(new GridLayout(5, 2, 6, 8));
        painelDados.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        painelDados.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);

        txtId = new JTextField();
        txtId.setEditable(false);
        cbCliente = new JComboBox<>();
        dateVenda = new JDateChooser(new Date());
        cbFormaPagamento = new JComboBox<>(new String[]{"DINHEIRO", "CARTAO", "PIX", "OUTRO"});
                txtObservacoes = new JTextField();

        aplicarPaddingCampo(txtId);
        aplicarPaddingCampo(txtObservacoes);
        aplicarPaddingCampo(txtQuantidade);
        aplicarPaddingCampo(txtPreco);
        aplicarPaddingData(dateVenda);
        painelDados.add(criarLabel("ID Venda:"));
        painelDados.add(txtId);
        painelDados.add(criarLabel("Cliente:"));
        painelDados.add(cbCliente);
        painelDados.add(criarLabel("Data Venda:"));
        painelDados.add(dateVenda);
        painelDados.add(criarLabel("Forma Pagamento:"));
        painelDados.add(cbFormaPagamento);
        painelDados.add(criarLabel("Observacoes:"));
        painelDados.add(txtObservacoes);

        JPanel painelItens = new JPanel(new BorderLayout());
        painelItens.setBorder(BorderFactory.createTitledBorder("Itens da Venda"));
        painelItens.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);

        JPanel painelAddItem = new JPanel(new GridLayout(2, 4, 6, 8));
        painelAddItem.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelAddItem.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);

        cbProduto = new JComboBox<>();
        txtQuantidade = new JTextField("1");
        txtPreco = new JTextField();

        JButton btnAdicionar = BotaoCarameloUtil.criarBotao("+ Adicionar");
        JButton btnRemover = BotaoCarameloUtil.criarBotao("- Remover");

        painelAddItem.add(criarLabel("Produto/Servico:"));
        painelAddItem.add(cbProduto);
        painelAddItem.add(criarLabel("Quantidade:"));
        painelAddItem.add(txtQuantidade);
        painelAddItem.add(criarLabel("Preco Unitario:"));
        painelAddItem.add(txtPreco);
        painelAddItem.add(btnAdicionar);
        painelAddItem.add(btnRemover);

        modeloItens = new DefaultTableModel(new Object[]{"ID Produto", "Descricao", "Tipo", "Qtd", "Preco", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };

        modeloItens.addTableModelListener(e -> {
            if (atualizandoTabela || e.getType() != TableModelEvent.UPDATE) {
                return;
            }
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && (col == 3 || col == 4)) {
                validarEdicaoLinha(row);
            }
        });

        tabelaItens = new JTable(modeloItens);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);

        JPanel painelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelTotal.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);

        JButton btnPdf = criarBotaoPdf();
        lblTotal = criarLabel("Total: 0,00");

        painelTotal.add(btnPdf);
        painelTotal.add(lblTotal);

        painelItens.add(painelAddItem, BorderLayout.NORTH);
        painelItens.add(scrollItens, BorderLayout.CENTER);
        painelItens.add(painelTotal, BorderLayout.SOUTH);

        JPanel painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);
        JPanel painelDadosWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelDadosWrapper.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);
        painelDadosWrapper.add(painelDados);
        painelConteudo.add(painelDadosWrapper, BorderLayout.NORTH);
        painelConteudo.add(painelItens, BorderLayout.CENTER);

        add(painelConteudo, BorderLayout.CENTER);

        cbProduto.addActionListener(e -> preencherPrecoProduto());
        btnAdicionar.addActionListener(e -> adicionarItem());
        btnRemover.addActionListener(e -> removerItemSelecionado());

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

        btnPdf.addActionListener(e -> emitirNotaFiscalPdf());
    }

    private void aplicarPaddingCampo(JComponent campo) {
        if (campo == null) {
            return;
        }
        campo.setBorder(new CompoundBorder(campo.getBorder(), new EmptyBorder(2, 6, 2, 6)));
    }

    private void aplicarPaddingData(JDateChooser chooser) {
        if (chooser == null) {
            return;
        }
        Component editor = chooser.getDateEditor().getUiComponent();
        if (editor instanceof JComponent) {
            aplicarPaddingCampo((JComponent) editor);
        }
    }

    private JButton criarBotaoPdf() {
        JButton btn = new JButton("PDF");
        try {
            ImageIcon icon = new ImageIcon("C:/bichanovet/icons/pdf_relatorio.jpg");
            Image img = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
            btn.setText("");
        } catch (Exception e) {
            btn.setText("PDF");
        }
        BotaoCarameloUtil.aplicarEstilo(btn);
        return btn;
    }

    private void emitirNotaFiscalPdf() {
        ClienteItem clienteItem = (ClienteItem) cbCliente.getSelectedItem();
        if (clienteItem == null || clienteItem.idCliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.");
            return;
        }

        if (modeloItens.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um item.");
            return;
        }

        Cliente cliente = clienteDAO.buscarPorId(clienteItem.idCliente);
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "Cliente nao encontrado.");
            return;
        }

        Venda venda = new Venda();
        venda.setIdCliente(clienteItem.idCliente);
        venda.setDataVenda(dateVenda.getDate());
        venda.setFormaPagamento((String) cbFormaPagamento.getSelectedItem());
        venda.setObservacoes(txtObservacoes.getText().trim());

        double total = calcularTotalTabela();
        List<ItemNotaFiscal> itens = construirItensNotaFiscal();

        try {
            File arquivo = NotaFiscalPdfUtil.gerarNota(venda, cliente, total, itens);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + e.getMessage());
        }
    }

    private List<ItemNotaFiscal> construirItensNotaFiscal() {
        List<ItemNotaFiscal> itens = new ArrayList<>();
        for (int i = 0; i < modeloItens.getRowCount(); i++) {
            String descricao = modeloItens.getValueAt(i, 1).toString();
            String tipo = modeloItens.getValueAt(i, 2).toString();
            int quantidade = Integer.parseInt(modeloItens.getValueAt(i, 3).toString());
            double preco = Double.parseDouble(modeloItens.getValueAt(i, 4).toString());
            double subtotal = Double.parseDouble(modeloItens.getValueAt(i, 5).toString());
            itens.add(new ItemNotaFiscal(descricao + " (" + tipo + ")", quantidade, preco, subtotal));
        }
        return itens;
    }

    private void carregarClientes() {
        cbCliente.removeAllItems();
        List<Cliente> clientes = clienteDAO.listar();
        for (Cliente c : clientes) {
            cbCliente.addItem(new ClienteItem(c.getIdCliente(), c.getNome()));
        }
    }

    private void carregarProdutos() {
        cbProduto.removeAllItems();
        List<Produto> produtos = produtoDAO.listarAtivos();
        for (Produto p : produtos) {
            cbProduto.addItem(new ProdutoItem(p.getIdProduto(), p.getNomeProduto(), p.getTipo(), p.getPreco(), p.getEstoque()));
        }
        preencherPrecoProduto();
    }

    private void preencherPrecoProduto() {
        ProdutoItem item = (ProdutoItem) cbProduto.getSelectedItem();
        if (item != null) {
            txtPreco.setText(String.valueOf(item.preco));
        }
    }

    private void carregarLista() {
        lista = vendaDAO.listarVendas();
        if (lista != null && !lista.isEmpty()) {
            indice = 0;
            mostrarRegistro();
        } else {
            indice = -1;
            limparCampos();
        }
    }

    private void mostrarRegistro() {
        Venda venda = lista.get(indice);
        txtId.setText(String.valueOf(venda.getIdVenda()));
        selecionarCliente(venda.getIdCliente());
        dateVenda.setDate(venda.getDataVenda());
        cbFormaPagamento.setSelectedItem(venda.getFormaPagamento());
        txtObservacoes.setText(venda.getObservacoes());
        carregarItensVenda(venda.getIdVenda());
    }

    private void carregarItensVenda(int idVenda) {
        modeloItens.setRowCount(0);
        itemCache.clear();

        List<ItemVenda> itens = vendaDAO.listarItensPorVenda(idVenda);
        for (ItemVenda item : itens) {
            Produto produto = produtoDAO.buscarPorId(item.getIdProduto());
            String nome = produto == null ? "" : produto.getNomeProduto();
            String tipo = produto == null ? "" : produto.getTipo();
            int estoque = produto == null ? 0 : produto.getEstoque();

            modeloItens.addRow(new Object[]{
                    item.getIdProduto(),
                    nome,
                    tipo,
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal()
            });

            itemCache.add(new ItemCache(item.getIdProduto(), tipo, estoque, item.getQuantidade(), item.getPrecoUnitario()));
        }
        atualizarTotalTabela();
    }

    private void selecionarCliente(Integer idCliente) {
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            ClienteItem item = cbCliente.getItemAt(i);
            if (item != null && item.idCliente != null && item.idCliente.equals(idCliente)) {
                cbCliente.setSelectedIndex(i);
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

    private void adicionarItem() {
        ProdutoItem produto = (ProdutoItem) cbProduto.getSelectedItem();
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto ou servico.");
            return;
        }

        int quantidade;
        double preco;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            preco = Double.parseDouble(txtPreco.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade ou preco invalido.");
            return;
        }

        if (quantidade <= 0 || preco < 0) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero e preco nao negativo.");
            return;
        }

        if (produtoEhProduto(produto.tipo)) {
            int total = somarQuantidadeProduto(produto.idProduto, -1, quantidade);
            if (total > produto.estoque) {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente para este produto.");
                return;
            }
        }

        double subtotal = quantidade * preco;
        modeloItens.addRow(new Object[]{
                produto.idProduto,
                produto.nome,
                produto.tipo,
                quantidade,
                preco,
                subtotal
        });

        itemCache.add(new ItemCache(produto.idProduto, produto.tipo, produto.estoque, quantidade, preco));
        atualizarTotalTabela();
    }

    private void removerItemSelecionado() {
        int row = tabelaItens.getSelectedRow();
        if (row >= 0) {
            modeloItens.removeRow(row);
            if (row < itemCache.size()) {
                itemCache.remove(row);
            }
            atualizarTotalTabela();
        }
    }

    private void validarEdicaoLinha(int row) {
        if (row < 0 || row >= itemCache.size()) {
            return;
        }

        ItemCache cache = itemCache.get(row);
        int quantidade;
        double preco;

        try {
            quantidade = Integer.parseInt(modeloItens.getValueAt(row, 3).toString());
            preco = Double.parseDouble(modeloItens.getValueAt(row, 4).toString());
        } catch (NumberFormatException e) {
            reverterLinha(row, cache);
            JOptionPane.showMessageDialog(this, "Quantidade ou preco invalido.");
            return;
        }

        if (quantidade <= 0 || preco < 0) {
            reverterLinha(row, cache);
            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero e preco nao negativo.");
            return;
        }

        if (produtoEhProduto(cache.tipo)) {
            int total = somarQuantidadeProduto(cache.idProduto, row, quantidade);
            if (total > cache.estoque) {
                reverterLinha(row, cache);
                JOptionPane.showMessageDialog(this, "Estoque insuficiente para este produto.");
                return;
            }
        }

        double subtotal = quantidade * preco;
        atualizarLinha(row, quantidade, preco, subtotal);
        cache.quantidade = quantidade;
        cache.preco = preco;
        atualizarTotalTabela();
    }

    private void atualizarLinha(int row, int quantidade, double preco, double subtotal) {
        atualizandoTabela = true;
        modeloItens.setValueAt(quantidade, row, 3);
        modeloItens.setValueAt(preco, row, 4);
        modeloItens.setValueAt(subtotal, row, 5);
        atualizandoTabela = false;
    }

    private void reverterLinha(int row, ItemCache cache) {
        double subtotal = cache.quantidade * cache.preco;
        atualizarLinha(row, cache.quantidade, cache.preco, subtotal);
    }

    private int somarQuantidadeProduto(int idProduto, int rowAtual, int novaQuantidade) {
        int total = 0;
        for (int i = 0; i < modeloItens.getRowCount(); i++) {
            int idLinha = Integer.parseInt(modeloItens.getValueAt(i, 0).toString());
            if (idLinha == idProduto) {
                if (i == rowAtual) {
                    total += novaQuantidade;
                } else {
                    total += Integer.parseInt(modeloItens.getValueAt(i, 3).toString());
                }
            }
        }
        return total;
    }

    private boolean produtoEhProduto(String tipo) {
        return tipo != null && tipo.equalsIgnoreCase("PRODUTO");
    }

    private void atualizarTotalTabela() {
        double total = 0;
        for (int i = 0; i < modeloItens.getRowCount(); i++) {
            total += Double.parseDouble(modeloItens.getValueAt(i, 5).toString());
        }
        DecimalFormat df = new DecimalFormat("0.00");
        lblTotal.setText("Total: " + df.format(total));
    }

    private double calcularTotalTabela() {
        double total = 0;
        for (int i = 0; i < modeloItens.getRowCount(); i++) {
            total += Double.parseDouble(modeloItens.getValueAt(i, 5).toString());
        }
        return total;
    }

    private void salvar() {
        Venda venda = construirVenda();
        if (venda == null) {
            return;
        }

        List<ItemVenda> itens = construirItens();
        if (itens.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um item.");
            return;
        }

        int idVenda = vendaDAO.inserirVendaComItens(venda, itens);
        if (idVenda > 0) {
            carregarLista();
            indice = lista.size() - 1;
            mostrarRegistro();
        }
    }

    private void atualizar() {
        if (txtId.getText().isEmpty()) {
            return;
        }

        Venda venda = construirVenda();
        if (venda == null) {
            return;
        }

        List<ItemVenda> itens = construirItens();
        if (itens.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um item.");
            return;
        }

        venda.setIdVenda(Integer.parseInt(txtId.getText()));
        vendaDAO.atualizarVendaComItens(venda, itens);
        carregarLista();
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            return;
        }

        vendaDAO.excluirVenda(Integer.parseInt(txtId.getText()));
        carregarLista();
    }

    private Venda construirVenda() {
        ClienteItem cliente = (ClienteItem) cbCliente.getSelectedItem();
        if (cliente == null || cliente.idCliente == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.");
            return null;
        }

        Venda venda = new Venda();
        venda.setIdCliente(cliente.idCliente);
        venda.setDataVenda(dateVenda.getDate());
        venda.setFormaPagamento((String) cbFormaPagamento.getSelectedItem());
        venda.setObservacoes(txtObservacoes.getText().trim());
        return venda;
    }

    private List<ItemVenda> construirItens() {
        List<ItemVenda> itens = new ArrayList<>();
        for (int i = 0; i < modeloItens.getRowCount(); i++) {
            ItemVenda item = new ItemVenda();
            item.setIdProduto(Integer.parseInt(modeloItens.getValueAt(i, 0).toString()));
            item.setQuantidade(Integer.parseInt(modeloItens.getValueAt(i, 3).toString()));
            item.setPrecoUnitario(Double.parseDouble(modeloItens.getValueAt(i, 4).toString()));
            itens.add(item);
        }
        return itens;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private void limparCampos() {
        txtId.setText("");
        txtObservacoes.setText("");
        txtQuantidade.setText("1");
        txtPreco.setText("");
        modeloItens.setRowCount(0);
        itemCache.clear();
        lblTotal.setText("Total: 0,00");
        dateVenda.setDate(new Date());
        if (cbCliente.getItemCount() > 0) {
            cbCliente.setSelectedIndex(0);
        }
    }

    private static class ClienteItem {
        private final Integer idCliente;
        private final String nome;

        ClienteItem(Integer idCliente, String nome) {
            this.idCliente = idCliente;
            this.nome = nome;
        }

        @Override
        public String toString() {
            return idCliente + " - " + nome;
        }
    }

    private static class ProdutoItem {
        private final Integer idProduto;
        private final String nome;
        private final String tipo;
        private final double preco;
        private final int estoque;

        ProdutoItem(Integer idProduto, String nome, String tipo, double preco, int estoque) {
            this.idProduto = idProduto;
            this.nome = nome;
            this.tipo = tipo;
            this.preco = preco;
            this.estoque = estoque;
        }

        @Override
        public String toString() {
            return nome + " (" + tipo + ")";
        }
    }

    private static class ItemCache {
        private final int idProduto;
        private final String tipo;
        private final int estoque;
        private int quantidade;
        private double preco;

        ItemCache(int idProduto, String tipo, int estoque, int quantidade, double preco) {
            this.idProduto = idProduto;
            this.tipo = tipo;
            this.estoque = estoque;
            this.quantidade = quantidade;
            this.preco = preco;
        }
    }
}