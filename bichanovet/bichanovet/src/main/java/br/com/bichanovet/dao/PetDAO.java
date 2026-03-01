package br.com.bichanovet.dao;

import br.com.bichanovet.model.*;
import java.sql.*;
import java.util.*;

public class PetDAO {

    public void inserir(Pet pet) {

        String sql = """
            INSERT INTO pets 
            (id_cliente, nome_pet, especie, raca, sexo, data_nascimento, observacoes)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pet.getCliente().getIdCliente());
            stmt.setString(2, pet.getNomePet());
            stmt.setString(3, pet.getEspecie());
            stmt.setString(4, pet.getRaca());
            stmt.setString(5, pet.getSexo());
            stmt.setDate(6, new java.sql.Date(pet.getDataNascimento().getTime()));
            stmt.setString(7, pet.getObservacoes());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pet> listar() {

        List<Pet> lista = new ArrayList<>();

        String sql = """
            SELECT p.*, c.id_cliente, c.nome 
            FROM pets p
            JOIN clientes c ON p.id_cliente = c.id_cliente
        """;

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("id_cliente"));
                cliente.setNome(rs.getString("nome"));

                Pet pet = new Pet();
                pet.setIdPet(rs.getInt("id_pet"));
                pet.setCliente(cliente);
                pet.setNomePet(rs.getString("nome_pet"));
                pet.setEspecie(rs.getString("especie"));
                pet.setRaca(rs.getString("raca"));
                pet.setSexo(rs.getString("sexo"));
                pet.setDataNascimento(rs.getDate("data_nascimento"));
                pet.setObservacoes(rs.getString("observacoes"));

                lista.add(pet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void excluir(int id) {
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt =
                     conn.prepareStatement("DELETE FROM pets WHERE id_pet = ?")) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pet> listarPorCliente(int idCliente) {

    List<Pet> lista = new ArrayList<>();

    String sql = """
        SELECT * FROM pets
        WHERE id_cliente = ?
    """;

    try (Connection conn = ConexaoSQLite.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idCliente);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {

            Pet p = new Pet();
            p.setIdPet(rs.getInt("id_pet"));
            p.setNomePet(rs.getString("nome_pet"));
            p.setEspecie(rs.getString("especie"));
            p.setRaca(rs.getString("raca"));
            p.setSexo(rs.getString("sexo"));
            p.setDataNascimento(rs.getDate("data_nascimento"));
            p.setObservacoes(rs.getString("observacoes"));

            lista.add(p);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return lista;
}
}
