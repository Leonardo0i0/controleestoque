package telaEstoque;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class controleTelaEstoque {

    // Campos de Texto
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtTipo;
    @FXML private TextField txtPreco;
    @FXML private TextField txtVencimentoMes;
    @FXML private TextField txtFiltro;
    @FXML private Label testando;

    // Tabela
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colCodigo;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Integer> colQuantidade;
    @FXML private TableColumn<Produto, String> colTipo;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, String> colVencimentoMes;

    // Lista de produtos
    private final ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();
    private FilteredList<Produto> listaFiltrada;

    // Tela de alerta
    private void mostrarAlerta(String titulo, String mensagem) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Produtos de exemplo
    void carregarProdutosExemplo() {
        Produto p1 = new Produto("001", "Arroz", 10, "Alimento", 25.50, "20");
        Produto p2 = new Produto("002", "Feijão", 20, "Alimento", 15.90, "15");
        Produto p3 = new Produto("003", "Sabonete", 50, "Higiene", 3.20, "18");

        listaProdutos.addAll(p1, p2, p3);
    }

    @FXML
    public void initialize() {
        carregarProdutosExemplo();

        // Configuração das colunas
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colVencimentoMes.setCellValueFactory(new PropertyValueFactory<>("vencimentoMes"));

        // Lista filtrada
        listaFiltrada = new FilteredList<>(listaProdutos, p -> true);
        tabelaProdutos.setItems(listaFiltrada);

        // Filtro em tempo real
        txtFiltro.textProperty().addListener((obs, oldValue, newValue) -> {
            listaFiltrada.setPredicate(produto -> {
                if (newValue == null || newValue.isBlank()) return true;
                String filtro = newValue.toLowerCase();
                return produto.getCodigo().toLowerCase().contains(filtro) ||
                        produto.getNome().toLowerCase().contains(filtro) ||
                        produto.getTipo().toLowerCase().contains(filtro);
            });
        });

        // Context Menu (Botão direito)
        javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();

        javafx.scene.control.MenuItem editarItem = new javafx.scene.control.MenuItem("Editar");
        editarItem.setOnAction(e -> {
            Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                txtCodigo.setText(selecionado.getCodigo());
                txtNome.setText(selecionado.getNome());
                txtQuantidade.setText(String.valueOf(selecionado.getQuantidade()));
                txtTipo.setText(selecionado.getTipo());
                txtPreco.setText(String.format("%.2f", selecionado.getPreco()).replace(".", ","));
                txtVencimentoMes.setText(selecionado.getVencimentoMes());
            }
        });

        javafx.scene.control.MenuItem deletarItem = new javafx.scene.control.MenuItem("Deletar");
        deletarItem.setOnAction(e -> deletarProduto());

        contextMenu.getItems().addAll(editarItem, deletarItem);
        tabelaProdutos.setContextMenu(contextMenu);
    }

    // Salvar ou editar produto
    @FXML
    protected void salvarProduto() {
        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (produtoSelecionado != null) {
            // Edita produto existente
            if (!txtNome.getText().isBlank()) produtoSelecionado.setNome(txtNome.getText());
            if (!txtQuantidade.getText().isBlank()) produtoSelecionado.setQuantidade(Integer.parseInt(txtQuantidade.getText()));
            if (!txtTipo.getText().isBlank()) produtoSelecionado.setTipo(txtTipo.getText());
            if (!txtPreco.getText().isBlank()) produtoSelecionado.setPreco(Double.parseDouble(txtPreco.getText().replace(",", ".")));
            if (!txtVencimentoMes.getText().isBlank()) produtoSelecionado.setVencimentoMes(txtVencimentoMes.getText());
            tabelaProdutos.refresh();
        } else {
            // Novos valores
            String codigoNovo = txtCodigo.getText();
            String nomeNovo = txtNome.getText();

            // Flags para duplicados
            boolean codigoExiste = listaProdutos.stream().anyMatch(p -> p.getCodigo().equals(codigoNovo));
            boolean nomeExiste = listaProdutos.stream().anyMatch(p -> p.getNome().equalsIgnoreCase(nomeNovo));

            if (codigoExiste) {
                mostrarAlerta("Aviso", "Já existe um produto com esse CÓDIGO!");
            } else if (nomeExiste) {
                mostrarAlerta("Aviso", "Já existe um produto com esse NOME!");
            } else {
                Produto produto = new Produto(
                        codigoNovo,
                        nomeNovo,
                        Integer.parseInt(txtQuantidade.getText()),
                        txtTipo.getText(),
                        Double.parseDouble(txtPreco.getText()),
                        txtVencimentoMes.getText()
                );
                listaProdutos.add(produto);
            }
        }

        limparCampos();
    }

    // Deletar produto
    @FXML
    protected void deletarProduto() {
        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            listaProdutos.remove(produtoSelecionado);
            limparCampos();
        }
    }

    // Limpar campos
    @FXML
    protected void limparCampos() {
        txtCodigo.clear();
        txtNome.clear();
        txtQuantidade.clear();
        txtTipo.clear();
        txtPreco.clear();
        txtVencimentoMes.clear();
        tabelaProdutos.getSelectionModel().clearSelection();
    }

    // Limpar filtro
    @FXML
    protected void limparFiltro() {
        txtFiltro.clear();
    }
}