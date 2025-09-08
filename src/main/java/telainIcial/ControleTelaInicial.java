package telainIcial;

import conexao.CRUD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ControleTelaInicial {
    String Usuario = "leonardo";
    String Senha = "12345";

    @FXML private TextField usuario;
    @FXML private PasswordField senha;
    @FXML private Label testando;
    CRUD conn = new CRUD();

    public void initialize() {
        // Permitir pressionar Enter para logar
        usuario.setOnAction(e -> login());
        senha.setOnAction(e -> login());
    }

    @FXML
    private void login() {
        if(conn.validarUsuario(usuario.getText(), senha.getText())){
            try {
                // Carrega a tela de estoque
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/telaEstoque.fxml"));
                Parent root = loader.load();

                // Cria nova janela
                Stage stage = new Stage();
                stage.setTitle("Sistema de Estoque");
                stage.setScene(new Scene(root, 800, 600)); // define tamanho da tela
                stage.show();

                // Fecha a tela de login
                Stage stageLogin = (Stage) usuario.getScene().getWindow();
                stageLogin.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            testando.setText("Usuário ou senha incorretos!");
            usuario.clear();
            senha.clear();
        }
    }
}