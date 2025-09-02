package telaEstoque;

import javafx.beans.property.*;

public class Produto {
    private final StringProperty codigo;
    private final StringProperty nome;
    private final IntegerProperty quantidade;
    private final StringProperty tipo;
    private final DoubleProperty preco;
    private final StringProperty vencimentoMes;

    public Produto(String codigo, String nome, int quantidade, String tipo, double preco, String vencimentoMes) {
        this.codigo = new SimpleStringProperty(codigo);
        this.nome = new SimpleStringProperty(nome);
        this.quantidade = new SimpleIntegerProperty(quantidade);
        this.tipo = new SimpleStringProperty(tipo);
        this.preco = new SimpleDoubleProperty(preco);
        this.vencimentoMes = new SimpleStringProperty(vencimentoMes);
    }

    // Getters tradicionais
    public String getCodigo() { return codigo.get(); }
    public String getNome() { return nome.get(); }
    public int getQuantidade() { return quantidade.get(); }
    public String getTipo() { return tipo.get(); }
    public double getPreco() { return preco.get(); }
    public String getVencimentoMes() { return vencimentoMes.get(); }

    // Setters
    public void setCodigo(String value) { codigo.set(value); }
    public void setNome(String value) { nome.set(value); }
    public void setQuantidade(int value) { quantidade.set(value); }
    public void setTipo(String value) { tipo.set(value); }
    public void setPreco(double value) { preco.set(value); }
    public void setVencimentoMes(String value) { vencimentoMes.set(value); }

    // Propriedades (necessárias para TableView/TreeTableView)
    public StringProperty codigoProperty() { return codigo; }
    public StringProperty nomeProperty() { return nome; }
    public IntegerProperty quantidadeProperty() { return quantidade; }
    public StringProperty tipoProperty() { return tipo; }
    public DoubleProperty precoProperty() { return preco; }
    public StringProperty vencimentoMesProperty() { return vencimentoMes; }
}
