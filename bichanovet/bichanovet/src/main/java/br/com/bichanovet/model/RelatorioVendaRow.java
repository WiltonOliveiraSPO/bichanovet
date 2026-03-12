package br.com.bichanovet.model;

import java.util.Date;

public class RelatorioVendaRow {

    private Integer idVenda;
    private Date dataVenda;
    private Double valorTotal;
    private String formaPagamento;
    private String nomeCliente;
    private String cpf;
    private String telefone;
    private String email;
    private String endereco;

    public Integer getIdVenda() { return idVenda; }
    public void setIdVenda(Integer idVenda) { this.idVenda = idVenda; }

    public Date getDataVenda() { return dataVenda; }
    public void setDataVenda(Date dataVenda) { this.dataVenda = dataVenda; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
}