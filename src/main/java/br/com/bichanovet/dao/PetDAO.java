package br.com.bichanovet.dao;

import br.com.bichanovet.model.Pet;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PetDAO {

    public PetDAO() {
        criarTabelaSeNaoExistir();
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS pets ("
                + "id_pet INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_cliente INTEGER NOT NULL,"
                + "nome_pet TEXT NOT NULL,"
                + "especie TEXT NOT NULL,"
                + "raca TEXT,"
                + "sexo TEXT CHECK (sexo IN ('M','F')),"
                + "data_nascimento DATE,"
                + "observacoes TEXT,"
                + "FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE"
                + ")";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inserir(Pet pet) {
        String sql = "INSERT INTO pets (id_cliente, nome_pet, especie, raca, sexo, data_nascimento, observacoes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pet.getIdCliente());
            stmt.setString(2, pet.getNomePet());
            stmt.setString(3, pet.getEspecie());
            stmt.setString(4, pet.getRaca());
            stmt.setString(5, pet.getSexo());
            stmt.setDate(6, pet.getDataNascimento() == null ? null : new Date(pet.getDataNascimento().getTime()));
            stmt.setString(7, pet.getObservacoes());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Pet pet) {
        String sql = "UPDATE pets SET id_cliente=?, nome_pet=?, especie=?, raca=?, sexo=?, data_nascimento=?, observacoes=? "
                + "WHERE id_pet=?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pet.getIdCliente());
            stmt.setString(2, pet.getNomePet());
            stmt.setString(3, pet.getEspecie());
            stmt.setString(4, pet.getRaca());
            stmt.setString(5, pet.getSexo());
            stmt.setDate(6, pet.getDataNascimento() == null ? null : new Date(pet.getDataNascimento().getTime()));
            stmt.setString(7, pet.getObservacoes());
            stmt.setInt(8, pet.getIdPet());

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluir(int idPet) {
        String sql = "DELETE FROM pets WHERE id_pet=?";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPet);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pet> listar() {
        List<Pet> lista = new ArrayList<>();
        String sql = "SELECT * FROM pets ORDER BY id_pet";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pet pet = new Pet();
                pet.setIdPet(rs.getInt("id_pet"));
                pet.setIdCliente(rs.getInt("id_cliente"));
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

    public List<Pet> listarPorCliente(int idCliente) {
        List<Pet> lista = new ArrayList<>();
        String sql = "SELECT * FROM pets WHERE id_cliente=? ORDER BY nome_pet";

        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pet pet = new Pet();
                    pet.setIdPet(rs.getInt("id_pet"));
                    pet.setIdCliente(rs.getInt("id_cliente"));
                    pet.setNomePet(rs.getString("nome_pet"));
                    pet.setEspecie(rs.getString("especie"));
                    pet.setRaca(rs.getString("raca"));
                    pet.setSexo(rs.getString("sexo"));
                    pet.setDataNascimento(rs.getDate("data_nascimento"));
                    pet.setObservacoes(rs.getString("observacoes"));
                    lista.add(pet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}