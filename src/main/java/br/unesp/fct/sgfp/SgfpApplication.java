package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.NavegadorCenas;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Ponto de entrada da aplicação desktop SGFP em JavaFX.
 * Inicializa o NavegadorCenas e direciona para a tela de autenticação ou dashboard.
 */
public class SgfpApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        NavegadorCenas.setPalcoPrincipal(stage);

        if (Sessao.getInstancia().isAutenticado()) {
            NavegadorCenas.irParaDashboard();
        } else {
            NavegadorCenas.irParaLogin();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
