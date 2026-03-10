package br.com.bichanovet.model;

public class ItemVenda {

    private Integer idItem;
    private Integer idVenda;
    private Integer idProduto;
    private Integer quantidade;
    private Double precoUnitario;
    private Double subtotal;

    public Integer getIdItem() { return idItem; }
    public void setIdItem(Integer idItem) { this.idItem = idItem; }

    public Integer getIdVenda() { return idVenda; }
    public void setIdVenda(Integer idVenda) { this.idVenda = idVenda; }

    public Integer getIdProduto() { return idProduto; }
    public void setIdProduto(Integer idProduto) { this.idProduto = idProduto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(Double precoUnitario) { this.precoUnitario = precoUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}