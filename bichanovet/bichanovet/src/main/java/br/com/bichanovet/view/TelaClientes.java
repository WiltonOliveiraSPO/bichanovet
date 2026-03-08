package br.com.bichanovet.view;

import br.com.bichanovet.dao.ClienteDAO;
import br.com.bichanovet.dao.PetDAO;
import br.com.bichanovet.model.Cliente;
import br.com.bichanovet.model.Pet;
import br.com.bichanovet.util.BotaoCarameloUtil;
import com.toedter.calendar.JDateChooser;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class TelaClientes extends BaseFrame {

    private JTextField txtId, txtNome, txtTelefone, txtEmail, txtEndereco;
    private JFormattedTextField txtCpf;
    private JDateChooser dateCadastro;

    private JTable tabelaPets;
    private DefaultTableModel modeloPets;

    private final ClienteDAO dao = new ClienteDAO();
    private final PetDAO petDAO = new PetDAO();
    private List<Cliente> lista;
    private int indice = -1;

    public TelaClientes() {
        configurarJanela();
        inicializarComponentes();
        carregarLista();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Clientes");
        setSize(800, 650);
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

        txtNome = new JTextField();

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            txtCpf = new JFormattedTextField(mascaraCpf);
        } catch (Exception e) {
            txtCpf = new JFormattedTextField();
        }

        txtTelefone = new JTextField();
        txtEmail = new JTextField();
        txtEndereco = new JTextField();
        dateCadastro = new JDateChooser();

        painel.add(new JLabel("ID:"));
        painel.add(txtId);
        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);
        painel.add(new JLabel("CPF:"));
        painel.add(txtCpf);
        painel.add(new JLabel("Telefone:"));
        painel.add(txtTelefone);
        painel.add(new JLabel("Email:"));
        painel.add(txtEmail);
        painel.add(new JLabel("Endereco:"));
        painel.add(txtEndereco);
        painel.add(new JLabel("Data Cadastro:"));
        painel.add(dateCadastro);

        add(painel, BorderLayout.CENTER);

        modeloPets = new DefaultTableModel(new Object[]{"ID Pet", "Nome", "Especie", "Raca", "Sexo", "Nascimento"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaPets = new JTable(modeloPets);
        tabelaPets.setBackground(new Color(255, 248, 237));
        tabelaPets.setSelectionBackground(new Color(224, 181, 129));

        tabelaPets.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabelaPets.getSelectedRow();
                    if (row >= 0) {
                        Object idValor = tabelaPets.getValueAt(row, 0);
                        if (idValor != null) {
                            int idPet = Integer.parseInt(idValor.toString());
                            TelaPets telaPets = new TelaPets();
                            telaPets.selecionarPetPorId(idPet);
                            telaPets.setVisible(true);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPets = new JScrollPane(tabelaPets);
        scrollPets.setBorder(BorderFactory.createTitledBorder("Pets do Cliente"));
        scrollPets.getViewport().setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);
        scrollPets.setPreferredSize(new java.awt.Dimension(700, 210));

        add(scrollPets, BorderLayout.SOUTH);

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

        Cliente c = lista.get(indice);

        txtId.setText(String.valueOf(c.getIdCliente()));
        txtNome.setText(c.getNome());
        txtCpf.setText(c.getCpf());
        txtTelefone.setText(c.getTelefone());
        txtEmail.setText(c.getEmail());
        txtEndereco.setText(c.getEndereco());
        dateCadastro.setDate(c.getDataCadastro());

        carregarGridPets(c.getIdCliente());
    }

    private void carregarGridPets(Integer idCliente) {
        modeloPets.setRowCount(0);

        if (idCliente == null) {
            return;
        }

        List<Pet> pets = petDAO.listarPorCliente(idCliente);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Pet pet : pets) {
            String dataNascimento = pet.getDataNascimento() == null ? "" : sdf.format(pet.getDataNascimento());
            modeloPets.addRow(new Object[]{
                    pet.getIdPet(),
                    pet.getNomePet(),
                    pet.getEspecie(),
                    pet.getRaca(),
                    pet.getSexo(),
                    dataNascimento
            });
        }
    }

    private void navegar(int novoIndice) {
        if (lista != null && novoIndice >= 0 && novoIndice < lista.size()) {
            indice = novoIndice;
            mostrarRegistro();
        }
    }

    private void salvar() {

        Cliente c = new Cliente();
        c.setNome(txtNome.getText());
        c.setCpf(txtCpf.getText());
        c.setTelefone(txtTelefone.getText());
        c.setEmail(txtEmail.getText());
        c.setEndereco(txtEndereco.getText());
        c.setDataCadastro(dateCadastro.getDate());

        dao.inserir(c);

        carregarLista();

        if (lista != null && !lista.isEmpty()) {
            indice = lista.size() - 1;
            mostrarRegistro();
        }
    }

    private void atualizar() {

        if (!txtId.getText().isEmpty()) {

            Cliente c = new Cliente();
            c.setIdCliente(Integer.parseInt(txtId.getText()));
            c.setNome(txtNome.getText());
            c.setCpf(txtCpf.getText());
            c.setTelefone(txtTelefone.getText());
            c.setEmail(txtEmail.getText());
            c.setEndereco(txtEndereco.getText());
            c.setDataCadastro(dateCadastro.getDate());

            dao.atualizar(c);
            carregarLista();
        }
    }

    private void excluir() {

        if (!txtId.getText().isEmpty()) {
            dao.excluir(Integer.parseInt(txtId.getText()));
            carregarLista();
        }
    }

    private void limparCampos() {
        txtId.setText("");
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtEndereco.setText("");
        dateCadastro.setDate(null);
        modeloPets.setRowCount(0);
    }
}