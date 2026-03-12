package br.com.bichanovet.dao;

import br.com.bichanovet.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public CategoriaDAO() {
        criarTabelaSeNaoExistir();
        inserirCategoriasPadrao();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS categorias ("
                + "id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome_categoria TEXT NOT NULL UNIQUE"
                + ")";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inserirCategoriasPadrao() {
        String sql = "INSERT OR IGNORE INTO categorias (nome_categoria) VALUES (?)";
        String[] categorias = {
                "Ra\u00e7\u00e3o",
                "Acess\u00f3rios",
                "Banho e Tosa",
                "Castra\u00e7\u00e3o",
                "Medicamentos"
        };

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String nome : categorias) {
                stmt.setString(1, nome);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nome_categoria";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                categoria.setNomeCategoria(rs.getString("nome_categoria"));
                lista.add(categoria);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}