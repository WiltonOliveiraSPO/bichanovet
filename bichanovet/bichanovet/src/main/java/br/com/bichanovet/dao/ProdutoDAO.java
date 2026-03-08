package br.com.bichanovet.dao;

import br.com.bichanovet.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public ProdutoDAO() {
        new CategoriaDAO();
        criarTabelaSeNaoExistir();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS produtos ("
                + "id_produto INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome_produto TEXT NOT NULL,"
                + "id_categoria INTEGER NOT NULL,"
                + "tipo TEXT NOT NULL CHECK (tipo IN ('PRODUTO','SERVICO')),"
                + "preco REAL NOT NULL CHECK (preco >= 0),"
                + "estoque INTEGER DEFAULT 0,"
                + "ativo INTEGER DEFAULT 1,"
                + "FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)"
                + ")";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inserir(Produto produto) {
        String sql = "INSERT INTO produtos (nome_produto, id_categoria, tipo, preco, estoque, ativo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNomeProduto());
            stmt.setInt(2, produto.getIdCategoria());
            stmt.setString(3, produto.getTipo());
            stmt.setDouble(4, produto.getPreco());
            stmt.setInt(5, produto.getEstoque());
            stmt.setInt(6, produto.getAtivo());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome_produto=?, id_categoria=?, tipo=?, preco=?, estoque=?, ativo=? WHERE id_produto=?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNomeProduto());
            stmt.setInt(2, produto.getIdCategoria());
            stmt.setString(3, produto.getTipo());
            stmt.setDouble(4, produto.getPreco());
            stmt.setInt(5, produto.getEstoque());
            stmt.setInt(6, produto.getAtivo());
            stmt.setInt(7, produto.getIdProduto());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluir(int idProduto) {
        String sql = "DELETE FROM produtos WHERE id_produto=?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProduto);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Produto> listar() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY id_produto";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("id_produto"));
                produto.setNomeProduto(rs.getString("nome_produto"));
                produto.setIdCategoria(rs.getInt("id_categoria"));
                produto.setTipo(rs.getString("tipo"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setEstoque(rs.getInt("estoque"));
                produto.setAtivo(rs.getInt("ativo"));
                lista.add(produto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}