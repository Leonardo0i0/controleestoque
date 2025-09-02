package telainIcial;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TelaInicial extends Application {
    @Override
    public void start(Stage prymaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/telainicial.fxml"));
        prymaryStage.setTitle("Login!");
        prymaryStage.setScene(new Scene(root, 529, 307));
        prymaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

/*os arquivos estão assim
java ->org.example, telaEstoque,TelaInicial,resources
telaEstoque->controleTelaEstoque, Produto
TelaInicial->ControleTelaInicial,Telainicial
resources->view->telaEstoque.fxml,telainicial.fxml*/