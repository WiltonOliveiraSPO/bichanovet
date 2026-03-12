package br.com.bichanovet.model;

public class ItemNotaFiscal {

    private String descricao;
    private int quantidade;
    private double preco;
    private double subtotal;

    public ItemNotaFiscal(String descricao, int quantidade, double preco, double subtotal) {
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.preco = preco;
        this.subtotal = subtotal;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPreco() {
        return preco;
    }

    public double getSubtotal() {
        return subtotal;
    }
}