package br.com.bichanovet.view;

import br.com.bichanovet.dao.ClienteDAO;
import br.com.bichanovet.dao.PetDAO;
import br.com.bichanovet.model.Cliente;
import br.com.bichanovet.model.Pet;
import br.com.bichanovet.util.BotaoCarameloUtil;
import com.toedter.calendar.JDateChooser;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class TelaPets extends BaseFrame {

    private JTextField txtId;
    private JTextField txtNomePet;
    private JTextField txtEspecie;
    private JTextField txtRaca;
    private JTextField txtObservacoes;
    private JComboBox<ClienteItem> cbCliente;
    private JComboBox<String> cbSexo;
    private JDateChooser dateNascimento;

    private final PetDAO dao = new PetDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private List<Pet> lista;
    private int indice = -1;

    public TelaPets() {
        configurarJanela();
        inicializarComponentes();
        carregarClientes();
        carregarLista();
    }

    public void selecionarPetPorId(int idPet) {
        if (lista == null || lista.isEmpty()) {
            return;
        }

        for (int i = 0; i < lista.size(); i++) {
            Pet pet = lista.get(i);
            if (pet.getIdPet() != null && pet.getIdPet() == idPet) {
                indice = i;
                mostrarRegistro();
                return;
            }
        }
    }

    private void configurarJanela() {
        setTitle("Cadastro de Pets");
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

        JPanel painel = new JPanel(new GridLayout(8, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painel.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);

        txtId = new JTextField();
        txtId.setEditable(false);

        cbCliente = new JComboBox<>();
        txtNomePet = new JTextField();
        txtEspecie = new JTextField();
        txtRaca = new JTextField();
        cbSexo = new JComboBox<>(new String[]{"", "M", "F"});
        dateNascimento = new JDateChooser();
        txtObservacoes = new JTextField();

        painel.add(new JLabel("ID:"));
        painel.add(txtId);
        painel.add(new JLabel("Cliente:"));
        painel.add(cbCliente);
        painel.add(new JLabel("Nome do Pet:"));
        painel.add(txtNomePet);
        painel.add(new JLabel("Especie:"));
        painel.add(txtEspecie);
        painel.add(new JLabel("Raca:"));
        painel.add(txtRaca);
        painel.add(new JLabel("Sexo (M/F):"));
        painel.add(cbSexo);
        painel.add(new JLabel("Data Nascimento:"));
        painel.add(dateNascimento);
        painel.add(new JLabel("Observacoes:"));
        painel.add(txtObservacoes);

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

    private void carregarClientes() {
        cbCliente.removeAllItems();

        List<Cliente> clientes = clienteDAO.listar();
        if (clientes.isEmpty()) {
            cbCliente.addItem(new ClienteItem(null, "Nenhum cliente cadastrado"));
            cbCliente.setEnabled(false);
            return;
        }

        for (Cliente cliente : clientes) {
            cbCliente.addItem(new ClienteItem(cliente.getIdCliente(), cliente.getNome()));
        }

        cbCliente.setEnabled(true);
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
        Pet pet = lista.get(indice);

        txtId.setText(String.valueOf(pet.getIdPet()));
        selecionarClientePorId(pet.getIdCliente());
        txtNomePet.setText(pet.getNomePet());
        txtEspecie.setText(pet.getEspecie());
        txtRaca.setText(pet.getRaca());
        cbSexo.setSelectedItem(pet.getSexo() == null ? "" : pet.getSexo());
        dateNascimento.setDate(pet.getDataNascimento());
        txtObservacoes.setText(pet.getObservacoes());
    }

    private void selecionarClientePorId(Integer idCliente) {
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            ClienteItem item = cbCliente.getItemAt(i);
            if (item != null && item.getIdCliente() != null && item.getIdCliente().equals(idCliente)) {
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

    private void salvar() {
        if (!validarFormulario()) {
            return;
        }

        Pet pet = construirPetSemId();
        dao.inserir(pet);
        carregarLista();

        if (lista != null && !lista.isEmpty()) {
            indice = lista.size() - 1;
            mostrarRegistro();
        }
    }

    private void atualizar() {
        if (txtId.getText().isEmpty() || !validarFormulario()) {
            return;
        }

        Pet pet = construirPetSemId();
        pet.setIdPet(Integer.parseInt(txtId.getText()));

        dao.atualizar(pet);
        carregarLista();
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) {
            return;
        }

        dao.excluir(Integer.parseInt(txtId.getText()));
        carregarLista();
    }

    private Pet construirPetSemId() {
        ClienteItem clienteSelecionado = (ClienteItem) cbCliente.getSelectedItem();

        Pet pet = new Pet();
        pet.setIdCliente(clienteSelecionado.getIdCliente());
        pet.setNomePet(txtNomePet.getText().trim());
        pet.setEspecie(txtEspecie.getText().trim());
        pet.setRaca(txtRaca.getText().trim());

        String sexo = (String) cbSexo.getSelectedItem();
        pet.setSexo((sexo == null || sexo.isBlank()) ? null : sexo);

        pet.setDataNascimento(dateNascimento.getDate());
        pet.setObservacoes(txtObservacoes.getText().trim());
        return pet;
    }

    private boolean validarFormulario() {
        ClienteItem clienteSelecionado = (ClienteItem) cbCliente.getSelectedItem();

        if (clienteSelecionado == null || clienteSelecionado.getIdCliente() == null) {
            JOptionPane.showMessageDialog(this, "Cadastre um cliente antes de cadastrar pets.");
            return false;
        }

        if (txtNomePet.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do pet.");
            txtNomePet.requestFocus();
            return false;
        }

        if (txtEspecie.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe a especie.");
            txtEspecie.requestFocus();
            return false;
        }

        return true;
    }

    private void limparCampos() {
        txtId.setText("");
        txtNomePet.setText("");
        txtEspecie.setText("");
        txtRaca.setText("");
        cbSexo.setSelectedItem("");
        dateNascimento.setDate(null);
        txtObservacoes.setText("");

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

        Integer getIdCliente() {
            return idCliente;
        }

        @Override
        public String toString() {
            if (idCliente == null) {
                return nome;
            }
            return idCliente + " - " + nome;
        }
    }
}