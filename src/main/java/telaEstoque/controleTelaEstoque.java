package telaEstoque;

import conexao.CRUD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class controleTelaEstoque {
    CRUD conn = new CRUD();

    // Campos de Texto
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtTipo;
    @FXML private TextField txtPreco;
    @FXML private TextField txtVencimentoMes;
    @FXML private TextField txtFiltro;
    @FXML private Label testando;
    @FXML private ComboBox<String> cbTipo;

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

    // Método auxiliar para atualizar a tabela
    private void atualizarTabela() {
        listaProdutos.clear();
        listaProdutos.addAll(conn.listarProdutosDB());
    }

    @FXML
    public void initialize() {
        // Carregar opções pré-definidas no ComboBox
        cbTipo.getItems().addAll("Bebida", "Limpeza", "Alimento", "Higiene", "Utilidades");

        // Deixar vazio no início e texto do prompt
        cbTipo.setValue(null);
        cbTipo.setPromptText("Selecione o tipo");

        // Configuração das colunas da tabela
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
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editarItem = new MenuItem("Editar");
        editarItem.setOnAction(e -> {
            Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                txtCodigo.setText(selecionado.getCodigo());
                txtCodigo.setDisable(true);
                txtNome.setText(selecionado.getNome());
                txtQuantidade.setText(String.valueOf(selecionado.getQuantidade()));
                cbTipo.setValue(selecionado.getTipo()); // Corrigido para ComboBox
                txtPreco.setText(String.format("%.2f", selecionado.getPreco()).replace(".", ","));
                txtVencimentoMes.setText(selecionado.getVencimentoMes());
            }
        });

        MenuItem deletarItem = new MenuItem("Deletar");
        deletarItem.setOnAction(e -> deletarProduto());

        contextMenu.getItems().addAll(editarItem, deletarItem);
        tabelaProdutos.setContextMenu(contextMenu);

        // Carregar produtos do banco ao iniciar
        atualizarTabela();
    }

    // Salvar ou editar produto
    @FXML
    protected void salvarProduto() {
        // Aceitar só inteiros positivos
        txtCodigo.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null));

        txtQuantidade.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null));

        txtVencimentoMes.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null));

        // Aceitar decimal (números com ponto ou vírgula)
        txtPreco.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("[0-9]*([\\.,][0-9]{0,2})?")) ? change : null));

        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (produtoSelecionado != null) {
            // Edita produto existente
            if (!txtNome.getText().isBlank()) produtoSelecionado.setNome(txtNome.getText());
            if (!txtQuantidade.getText().isBlank()) produtoSelecionado.setQuantidade(Integer.parseInt(txtQuantidade.getText()));
            if (cbTipo.getValue() != null && !cbTipo.getValue().equals("Selecionar item")) {
                produtoSelecionado.setTipo(cbTipo.getValue());
            }
            if (!txtPreco.getText().isBlank()) produtoSelecionado.setPreco(Double.parseDouble(txtPreco.getText().replace(",", ".")));
            if (!txtVencimentoMes.getText().isBlank() && Integer.parseInt(txtVencimentoMes.getText()) <= Integer.parseInt(txtQuantidade.getText())) produtoSelecionado.setVencimentoMes(txtVencimentoMes.getText());

            if(Integer.parseInt(txtVencimentoMes.getText()) > Integer.parseInt(txtQuantidade.getText())){
                mostrarAlerta("Aviso", "Quantidades a Vencer esta maior que a Quantidade no estoque");
            }else {
            conn.atualizarProduto(produtoSelecionado);
            }
            atualizarTabela();
            txtCodigo.setDisable(false);
        } else {
            // Novos valores
            String codigoNovo = txtCodigo.getText();
            String nomeNovo = txtNome.getText();
            String tipoNovo = cbTipo.getValue();

            // Verifica se escolheu tipo válido
            if (tipoNovo == null || tipoNovo.equals("Selecionar item")) {
                mostrarAlerta("Aviso", "Selecione um tipo de produto válido!");
                return;
            }

            // Flags para duplicados
            boolean codigoExiste = listaProdutos.stream().anyMatch(p -> p.getCodigo().equals(codigoNovo));
            boolean nomeExiste = listaProdutos.stream().anyMatch(p -> p.getNome().equalsIgnoreCase(nomeNovo));

            if (codigoExiste) {
                mostrarAlerta("Aviso", "Já existe um produto com esse CÓDIGO!");
            } else if (nomeExiste) {
                mostrarAlerta("Aviso", "Já existe um produto com esse NOME!");
            } if(Integer.parseInt(txtVencimentoMes.getText()) > Integer.parseInt(txtQuantidade.getText())){
                mostrarAlerta("Aviso", "Quantidades a Vencer esta maior que a Quantidade no estoque");
            }else {
                // Inserir no banco
                conn.adicionarProduto(
                        Integer.parseInt(codigoNovo),
                        nomeNovo,
                        tipoNovo,
                        Integer.parseInt(txtQuantidade.getText()),
                        Double.parseDouble(txtPreco.getText().replace(",", ".")),
                        Integer.parseInt(txtVencimentoMes.getText())
                );
            }
            atualizarTabela();
        }

        limparCampos();
    }

    // Deletar produto
    @FXML
    protected void deletarProduto() {
        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            conn.deletarProduto(Integer.parseInt(produtoSelecionado.getCodigo()));
            limparCampos();
            atualizarTabela();
        }
    }

    // Limpar campos
    @FXML
    protected void limparCampos() {
        txtCodigo.clear();
        txtCodigo.setDisable(false);
        txtNome.clear();
        txtQuantidade.clear();
        cbTipo.setValue(null);
        cbTipo.setPromptText("Selecione o tipo");
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