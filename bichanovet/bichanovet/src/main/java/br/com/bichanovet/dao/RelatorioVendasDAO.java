package br.com.bichanovet.dao;

import br.com.bichanovet.model.RelatorioVendaRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RelatorioVendasDAO {

    public List<RelatorioVendaRow> listarPorPeriodo(Date inicio, Date fim) {
        List<RelatorioVendaRow> lista = new ArrayList<>();
        String sql = "SELECT v.id_venda, v.data_venda, v.valor_total, v.forma_pagamento, "
                + "c.nome, c.cpf, c.telefone, c.email, c.endereco "
                + "FROM vendas v INNER JOIN clientes c ON c.id_cliente = v.id_cliente "
                + "WHERE v.data_venda BETWEEN ? AND ? "
                + "ORDER BY v.data_venda";

        Date inicioDia = inicio;
        Date fimDia = new Date(fim.getTime());
        fimDia.setHours(23);
        fimDia.setMinutes(59);
        fimDia.setSeconds(59);

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(inicioDia.getTime()));
            stmt.setTimestamp(2, new Timestamp(fimDia.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RelatorioVendaRow row = new RelatorioVendaRow();
                    row.setIdVenda(rs.getInt("id_venda"));
                    row.setDataVenda(rs.getTimestamp("data_venda"));
                    row.setValorTotal(rs.getDouble("valor_total"));
                    row.setFormaPagamento(rs.getString("forma_pagamento"));
                    row.setNomeCliente(rs.getString("nome"));
                    row.setCpf(rs.getString("cpf"));
                    row.setTelefone(rs.getString("telefone"));
                    row.setEmail(rs.getString("email"));
                    row.setEndereco(rs.getString("endereco"));
                    lista.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}