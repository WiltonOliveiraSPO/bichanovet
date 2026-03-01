package br.com.bichanovet.model;

import java.util.Date;

public class Pet {

    private int idPet;
    private Cliente cliente;
    private String nomePet;
    private String especie;
    private String raca;
    private String sexo;
    private Date dataNascimento;
    private String observacoes;

    public int getIdPet() { return idPet; }
    public void setIdPet(int idPet) { this.idPet = idPet; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getNomePet() { return nomePet; }
    public void setNomePet(String nomePet) { this.nomePet = nomePet; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public Date getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
