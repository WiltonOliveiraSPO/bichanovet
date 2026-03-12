package br.com.bichanovet.view;

import br.com.bichanovet.dao.RelatorioVendasDAO;
import br.com.bichanovet.model.RelatorioVendaRow;
import br.com.bichanovet.util.BotaoCarameloUtil;
import br.com.bichanovet.util.RelatorioVendasExcelUtil;
import com.toedter.calendar.JDateChooser;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TelaRelatorios extends BaseFrame {

    private JDateChooser dataInicio;
    private JDateChooser dataFim;
    private JTable tabela;
    private DefaultTableModel modelo;

    private final RelatorioVendasDAO dao = new RelatorioVendasDAO();

    public TelaRelatorios() {
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Relatorio de Vendas");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);
    }

    private void inicializarComponentes() {
        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        painelTopo.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);

        dataInicio = new JDateChooser(new Date());
        dataFim = new JDateChooser(new Date());
        dataInicio.setDateFormatString("dd/MM/yyyy");
        dataFim.setDateFormatString("dd/MM/yyyy");

        painelTopo.add(new JLabel("Data inicio:"));
        painelTopo.add(dataInicio);
        painelTopo.add(new JLabel("Data fim:"));
        painelTopo.add(dataFim);

        JButton btnExcel = criarBotaoExcel();
        btnExcel.addActionListener(e -> gerarRelatorio());

        painelTopo.add(btnExcel);

        add(painelTopo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"Data", "ID Venda", "Cliente", "CPF", "Telefone", "Email", "Endereco", "Forma Pagamento", "Valor Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Vendas no Periodo"));
        add(scroll, BorderLayout.CENTER);

        JButton btnAtualizar = BotaoCarameloUtil.criarBotao("Atualizar");
        btnAtualizar.addActionListener(e -> carregarGrid());

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.setBackground(BotaoCarameloUtil.COR_FUNDO_TELA);
        painelBotao.add(btnAtualizar);

        add(painelBotao, BorderLayout.SOUTH);
    }

    private JButton criarBotaoExcel() {
        JButton btn = new JButton("EXCEL");
        try {
            ImageIcon icon = new ImageIcon("C:/bichanovet/icons/excel.jpg");
            Image img = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
            btn.setText("");
        } catch (Exception e) {
            btn.setText("EXCEL");
        }
        BotaoCarameloUtil.aplicarEstilo(btn);
        return btn;
    }

    private void carregarGrid() {
        Date inicio = dataInicio.getDate();
        Date fim = dataFim.getDate();

        if (inicio == null || fim == null) {
            JOptionPane.showMessageDialog(this, "Informe as datas inicial e final.");
            return;
        }

        if (inicio.after(fim)) {
            JOptionPane.showMessageDialog(this, "Data inicial deve ser menor ou igual a data final.");
            return;
        }

        List<RelatorioVendaRow> dados = dao.listarPorPeriodo(inicio, fim);
        modelo.setRowCount(0);

        if (dados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma venda encontrada no periodo.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat df = new DecimalFormat("0.00");

        for (RelatorioVendaRow r : dados) {
            modelo.addRow(new Object[]{
                    sdf.format(r.getDataVenda()),
                    r.getIdVenda(),
                    r.getNomeCliente(),
                    r.getCpf(),
                    r.getTelefone(),
                    r.getEmail(),
                    r.getEndereco(),
                    r.getFormaPagamento(),
                    df.format(r.getValorTotal())
            });
        }
    }

    private void gerarRelatorio() {
        Date inicio = dataInicio.getDate();
        Date fim = dataFim.getDate();

        if (inicio == null || fim == null) {
            JOptionPane.showMessageDialog(this, "Informe as datas inicial e final.");
            return;
        }

        if (inicio.after(fim)) {
            JOptionPane.showMessageDialog(this, "Data inicial deve ser menor ou igual a data final.");
            return;
        }

        List<RelatorioVendaRow> dados = dao.listarPorPeriodo(inicio, fim);
        if (dados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma venda encontrada no periodo.");
            return;
        }

        try {
            File arquivo = RelatorioVendasExcelUtil.gerarRelatorio(dados);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatorio: " + e.getMessage());
        }
    }
}