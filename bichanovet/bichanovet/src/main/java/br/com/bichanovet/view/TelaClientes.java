package br.com.bichanovet.view;

import br.com.bichanovet.dao.ClienteDAO;
import br.com.bichanovet.model.Cliente;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class TelaClientes extends BaseFrame {

    private JTextField txtId, txtNome, txtTelefone, txtEmail, txtEndereco;
    private JFormattedTextField txtCpf;
    private JDateChooser dateCadastro;

    private ClienteDAO dao = new ClienteDAO();
    private List<Cliente> lista;
    private int indice = -1;

    private JTable tabelaPets;
    private DefaultTableModel modeloPets;
    private PetDAO petDAO = new PetDAO();

    public TelaClientes() {
        configurarJanela();
        inicializarComponentes();
        add(painel, BorderLayout.CENTER);
        carregarLista();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Clientes");
        setSize(700, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {

        // ================= BOTÕES SUPERIORES =================
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnExcluir = new JButton("Excluir");

        JButton btnPrimeiro = new JButton("|<");
        JButton btnAnterior = new JButton("<");
        JButton btnProximo = new JButton(">");
        JButton btnUltimo = new JButton(">|");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnPrimeiro);
        painelBotoes.add(btnAnterior);
        painelBotoes.add(btnProximo);
        painelBotoes.add(btnUltimo);

        add(painelBotoes, BorderLayout.NORTH);

        // ================= DADOS =================
        JPanel painel = new JPanel(new GridLayout(7,2,10,10));
        painel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

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
        painel.add(new JLabel("Endereço:"));
        painel.add(txtEndereco);
        painel.add(new JLabel("Data Cadastro:"));
        painel.add(dateCadastro);

        add(painel, BorderLayout.CENTER);

        // ================= AÇÕES =================
        btnNovo.addActionListener(e -> limparCampos());
        btnSalvar.addActionListener(e -> salvar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());

        btnPrimeiro.addActionListener(e -> navegar(0));
        btnAnterior.addActionListener(e -> navegar(indice - 1));
        btnProximo.addActionListener(e -> navegar(indice + 1));
        btnUltimo.addActionListener(e -> navegar(lista.size() - 1));
    }

    // ================= TABELA DE PETS =================
    modeloPets = new DefaultTableModel(
        new Object[]{"ID", "Nome", "Espécie", "Raça", "Sexo", "Nascimento"}, 0
    );

    tabelaPets = new JTable(modeloPets);

    JScrollPane scrollPets = new JScrollPane(tabelaPets);
    scrollPets.setBorder(BorderFactory.createTitledBorder("🐾 Pets do Cliente"));

    add(scrollPets, BorderLayout.SOUTH);

    private void carregarLista() {
        lista = dao.listar();
        if (!lista.isEmpty()) {
            indice = 0;
            mostrarRegistro();
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

        // Ir automaticamente para o último registro inserido
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
            limparCampos();
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
    }
private void carregarPetsDoCliente(int idCliente) {

    modeloPets.setRowCount(0);

    List<Pet> pets = petDAO.listarPorCliente(idCliente);

    for (Pet p : pets) {

        modeloPets.addRow(new Object[]{
                p.getIdPet(),
                p.getNomePet(),
                p.getEspecie(),
                p.getRaca(),
                p.getSexo(),
                p.getDataNascimento()
        });
    }
}
}
