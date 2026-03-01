package br.com.bichanovet.view;

import br.com.bichanovet.dao.ClienteDAO;
import br.com.bichanovet.dao.PetDAO;
import br.com.bichanovet.model.Cliente;
import br.com.bichanovet.model.Pet;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaPets extends BaseFrame {

    private JComboBox<Cliente> cbCliente;
    private JTextField txtId, txtNome, txtEspecie, txtRaca;
    private JComboBox<String> cbSexo;
    private JDateChooser dateNascimento;
    private JTextArea txtObs;

    private List<Pet> lista;
    private int indice = -1;

    private PetDAO dao = new PetDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();

    public TelaPets() {
        configurarJanela();
        inicializarComponentes();
        carregarClientes();
        carregarLista();
    }

    private void configurarJanela() {
        setTitle("🐾 Cadastro de Pets");
        setSize(650, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {

        // ================= BOTÕES =================
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JButton btnNovo = new JButton("🆕 Novo");
        JButton btnSalvar = new JButton("💾 Salvar");
        JButton btnAtualizar = new JButton("✏ Atualizar");
        JButton btnExcluir = new JButton("🗑 Excluir");

        JButton btnPrimeiro = new JButton("⏮");
        JButton btnAnterior = new JButton("⬅");
        JButton btnProximo = new JButton("➡");
        JButton btnUltimo = new JButton("⏭");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnPrimeiro);
        painelBotoes.add(btnAnterior);
        painelBotoes.add(btnProximo);
        painelBotoes.add(btnUltimo);

        add(painelBotoes, BorderLayout.NORTH);

        // ================= FORMULÁRIO =================
        JPanel painel = new JPanel(new GridLayout(8,2,10,10));
        painel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        txtId = new JTextField();
        txtId.setEditable(false);

        cbCliente = new JComboBox<>();

        txtNome = new JTextField();
        txtEspecie = new JTextField();
        txtRaca = new JTextField();

        cbSexo = new JComboBox<>(new String[]{"M","F"});

        dateNascimento = new JDateChooser();

        txtObs = new JTextArea(3,20);
        JScrollPane scrollObs = new JScrollPane(txtObs);

        painel.add(new JLabel("ID Pet:"));
        painel.add(txtId);

        painel.add(new JLabel("Cliente:"));
        painel.add(cbCliente);

        painel.add(new JLabel("Nome do Pet:"));
        painel.add(txtNome);

        painel.add(new JLabel("Espécie:"));
        painel.add(txtEspecie);

        painel.add(new JLabel("Raça:"));
        painel.add(txtRaca);

        painel.add(new JLabel("Sexo:"));
        painel.add(cbSexo);

        painel.add(new JLabel("Data Nascimento:"));
        painel.add(dateNascimento);

        painel.add(new JLabel("Observações:"));
        painel.add(scrollObs);

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

    // ================= CARREGAMENTOS =================

    private void carregarClientes() {

        List<Cliente> clientes = clienteDAO.listar();
        cbCliente.removeAllItems();

        for (Cliente c : clientes) {
            cbCliente.addItem(c);
        }
    }

    private void carregarLista() {

        lista = dao.listar();

        if (lista != null && !lista.isEmpty()) {

            if (indice < 0) indice = 0;
            if (indice >= lista.size()) indice = lista.size() - 1;

            mostrarRegistro();
        } else {
            limparCampos();
            indice = -1;
        }
    }

    private void mostrarRegistro() {

        Pet p = lista.get(indice);

        txtId.setText(String.valueOf(p.getIdPet()));
        cbCliente.setSelectedItem(p.getCliente());
        txtNome.setText(p.getNomePet());
        txtEspecie.setText(p.getEspecie());
        txtRaca.setText(p.getRaca());
        cbSexo.setSelectedItem(p.getSexo());
        dateNascimento.setDate(p.getDataNascimento());
        txtObs.setText(p.getObservacoes());
    }

    private void navegar(int novoIndice) {

        if (lista != null && novoIndice >= 0 && novoIndice < lista.size()) {
            indice = novoIndice;
            mostrarRegistro();
        }
    }

    // ================= CRUD =================

    private void salvar() {

        Pet p = new Pet();

        p.setCliente((Cliente) cbCliente.getSelectedItem());
        p.setNomePet(txtNome.getText());
        p.setEspecie(txtEspecie.getText());
        p.setRaca(txtRaca.getText());
        p.setSexo(cbSexo.getSelectedItem().toString());
        p.setDataNascimento(dateNascimento.getDate());
        p.setObservacoes(txtObs.getText());

        dao.inserir(p);

        carregarLista();

        if (lista != null && !lista.isEmpty()) {
            indice = lista.size() - 1;
            mostrarRegistro();
        }
    }

    private void atualizar() {

        if (!txtId.getText().isEmpty()) {

            Pet p = new Pet();
            p.setIdPet(Integer.parseInt(txtId.getText()));
            p.setCliente((Cliente) cbCliente.getSelectedItem());
            p.setNomePet(txtNome.getText());
            p.setEspecie(txtEspecie.getText());
            p.setRaca(txtRaca.getText());
            p.setSexo(cbSexo.getSelectedItem().toString());
            p.setDataNascimento(dateNascimento.getDate());
            p.setObservacoes(txtObs.getText());

            dao.atualizar(p);
            carregarLista();
        }
    }

    private void excluir() {

        if (!txtId.getText().isEmpty()) {

            dao.excluir(Integer.parseInt(txtId.getText()));

            carregarLista();

            if (lista != null && !lista.isEmpty()) {

                if (indice >= lista.size()) {
                    indice = lista.size() - 1;
                }

                mostrarRegistro();

            } else {
                limparCampos();
            }
        }
    }

    private void limparCampos() {

        txtId.setText("");
        txtNome.setText("");
        txtEspecie.setText("");
        txtRaca.setText("");
        txtObs.setText("");
        dateNascimento.setDate(null);

        if (cbCliente.getItemCount() > 0)
            cbCliente.setSelectedIndex(0);

        indice = -1;
    }
}
