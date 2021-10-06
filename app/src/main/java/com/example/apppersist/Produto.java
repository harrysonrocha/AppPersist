package com.example.apppersist;

public class Produto {

    public int id;

    public String nome, categoria;

    public double quantidade;



    public Produto(String nome, String categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }



    @Override
    public String toString() {

        if (nome.equals("Empty list/Lista vazia...")) {
            return nome;
        } else {

            return nome + "  |  " + quantidade + "  |  " + categoria;
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getQuantidade() { return quantidade; }

    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }


}

