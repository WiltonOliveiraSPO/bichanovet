package br.com.bichanovet.model;

import java.util.Date;

public class Venda {

    private Integer idVenda;
    private Integer idCliente;
    private Date dataVenda;
    private Double valorTotal;
    private String formaPagamento;
    private String observacoes;

    public Integer getIdVenda() { return idVenda; }
    public void setIdVenda(Integer idVenda) { this.idVenda = idVenda; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public Date getDataVenda() { return dataVenda; }
    public void setDataVenda(Date dataVenda) { this.dataVenda = dataVenda; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}