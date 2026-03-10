package br.com.bichanovet.dao;

import br.com.bichanovet.model.ItemVenda;
import br.com.bichanovet.model.Produto;
import br.com.bichanovet.model.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VendaDAO {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    public VendaDAO() {
        criarTabelasETriggers();
    }

    private void criarTabelasETriggers() {
        String sqlVendas = "CREATE TABLE IF NOT EXISTS vendas ("
                + "id_venda INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_cliente INTEGER NOT NULL,"
                + "data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "valor_total REAL DEFAULT 0,"
                + "forma_pagamento TEXT,"
                + "observacoes TEXT,"
                + "FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)"
                + ")";

        String sqlItens = "CREATE TABLE IF NOT EXISTS itens_venda ("
                + "id_item INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_venda INTEGER NOT NULL,"
                + "id_produto INTEGER NOT NULL,"
                + "quantidade INTEGER NOT NULL CHECK (quantidade > 0),"
                + "preco_unitario REAL NOT NULL CHECK (preco_unitario >= 0),"
                + "subtotal REAL NOT NULL,"
                + "FOREIGN KEY (id_venda) REFERENCES vendas(id_venda) ON DELETE CASCADE,"
                + "FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)"
                + ")";

        String trgSubtotal = "CREATE TRIGGER IF NOT EXISTS trg_calcular_subtotal "
                + "AFTER INSERT ON itens_venda "
                + "BEGIN "
                + "UPDATE itens_venda SET subtotal = quantidade * preco_unitario WHERE id_item = NEW.id_item; "
                + "END;";

        String trgTotal = "CREATE TRIGGER IF NOT EXISTS trg_atualizar_total_venda "
                + "AFTER INSERT ON itens_venda "
                + "BEGIN "
                + "UPDATE vendas SET valor_total = (SELECT SUM(subtotal) FROM itens_venda WHERE id_venda = NEW.id_venda) "
                + "WHERE id_venda = NEW.id_venda; "
                + "END;";

        String trgEstoque = "CREATE TRIGGER IF NOT EXISTS trg_baixa_estoque "
                + "AFTER INSERT ON itens_venda "
                + "BEGIN "
                + "UPDATE produtos SET estoque = estoque - NEW.quantidade "
                + "WHERE id_produto = NEW.id_produto AND tipo = 'PRODUTO'; "
                + "END;";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmtVendas = conn.prepareStatement(sqlVendas);
             PreparedStatement stmtItens = conn.prepareStatement(sqlItens);
             PreparedStatement stmtSub = conn.prepareStatement(trgSubtotal);
             PreparedStatement stmtTot = conn.prepareStatement(trgTotal);
             PreparedStatement stmtEst = conn.prepareStatement(trgEstoque)) {

            stmtVendas.execute();
            stmtItens.execute();
            stmtSub.execute();
            stmtTot.execute();
            stmtEst.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int inserirVendaComItens(Venda venda, List<ItemVenda> itens) {
        String sqlVenda = "INSERT INTO vendas (id_cliente, data_venda, forma_pagamento, observacoes) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false);

            int idVenda;
            try (PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stmtVenda.setInt(1, venda.getIdCliente());
                Timestamp data = venda.getDataVenda() == null ? new Timestamp(System.currentTimeMillis())
                        : new Timestamp(venda.getDataVenda().getTime());
                stmtVenda.setTimestamp(2, data);
                stmtVenda.setString(3, venda.getFormaPagamento());
                stmtVenda.setString(4, venda.getObservacoes());
                stmtVenda.executeUpdate();

                try (ResultSet rs = stmtVenda.getGeneratedKeys()) {
                    rs.next();
                    idVenda = rs.getInt(1);
                }
            }

            try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                for (ItemVenda item : itens) {
                    double subtotal = item.getQuantidade() * item.getPrecoUnitario();
                    stmtItem.setInt(1, idVenda);
                    stmtItem.setInt(2, item.getIdProduto());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setDouble(4, item.getPrecoUnitario());
                    stmtItem.setDouble(5, subtotal);
                    stmtItem.executeUpdate();
                }
            }

            conn.commit();
            return idVenda;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void atualizarVendaComItens(Venda venda, List<ItemVenda> novosItens) {
        String sqlVenda = "UPDATE vendas SET id_cliente=?, data_venda=?, forma_pagamento=?, observacoes=?, valor_total=0 WHERE id_venda=?";
        String sqlItens = "DELETE FROM itens_venda WHERE id_venda=?";
        String sqlItem = "INSERT INTO itens_venda (id_venda, id_produto, quantidade, preco_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false);

            List<ItemVenda> itensAntigos = listarItensPorVenda(venda.getIdVenda());
            restaurarEstoque(itensAntigos);

            try (PreparedStatement stmtVenda = conn.prepareStatement(sqlVenda)) {
                stmtVenda.setInt(1, venda.getIdCliente());
                Timestamp data = venda.getDataVenda() == null ? new Timestamp(System.currentTimeMillis())
                        : new Timestamp(venda.getDataVenda().getTime());
                stmtVenda.setTimestamp(2, data);
                stmtVenda.setString(3, venda.getFormaPagamento());
                stmtVenda.setString(4, venda.getObservacoes());
                stmtVenda.setInt(5, venda.getIdVenda());
                stmtVenda.executeUpdate();
            }

            try (PreparedStatement stmtDel = conn.prepareStatement(sqlItens)) {
                stmtDel.setInt(1, venda.getIdVenda());
                stmtDel.executeUpdate();
            }

            try (PreparedStatement stmtItem = conn.prepareStatement(sqlItem)) {
                for (ItemVenda item : novosItens) {
                    double subtotal = item.getQuantidade() * item.getPrecoUnitario();
                    stmtItem.setInt(1, venda.getIdVenda());
                    stmtItem.setInt(2, item.getIdProduto());
                    stmtItem.setInt(3, item.getQuantidade());
                    stmtItem.setDouble(4, item.getPrecoUnitario());
                    stmtItem.setDouble(5, subtotal);
                    stmtItem.executeUpdate();
                }
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluirVenda(int idVenda) {
        List<ItemVenda> itens = listarItensPorVenda(idVenda);
        restaurarEstoque(itens);

        String sql = "DELETE FROM vendas WHERE id_venda=?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Venda> listarVendas() {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT * FROM vendas ORDER BY id_venda";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venda venda = new Venda();
                venda.setIdVenda(rs.getInt("id_venda"));
                venda.setIdCliente(rs.getInt("id_cliente"));
                venda.setDataVenda(rs.getTimestamp("data_venda"));
                venda.setValorTotal(rs.getDouble("valor_total"));
                venda.setFormaPagamento(rs.getString("forma_pagamento"));
                venda.setObservacoes(rs.getString("observacoes"));
                lista.add(venda);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<ItemVenda> listarItensPorVenda(int idVenda) {
        List<ItemVenda> lista = new ArrayList<>();
        String sql = "SELECT * FROM itens_venda WHERE id_venda=?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenda);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemVenda item = new ItemVenda();
                    item.setIdItem(rs.getInt("id_item"));
                    item.setIdVenda(rs.getInt("id_venda"));
                    item.setIdProduto(rs.getInt("id_produto"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                    item.setSubtotal(rs.getDouble("subtotal"));
                    lista.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    private void restaurarEstoque(List<ItemVenda> itens) {
        for (ItemVenda item : itens) {
            Produto produto = produtoDAO.buscarPorId(item.getIdProduto());
            if (produto != null && "PRODUTO".equalsIgnoreCase(produto.getTipo())) {
                produtoDAO.atualizarEstoque(produto.getIdProduto(), item.getQuantidade());
            }
        }
    }
}