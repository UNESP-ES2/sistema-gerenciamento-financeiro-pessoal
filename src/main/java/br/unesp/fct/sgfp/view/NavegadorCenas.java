package br.unesp.fct.sgfp.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gerenciador centralizado de navegação entre cenas JavaFX para janela única.
 * Permite transição suave entre Autenticação, Cadastro e Dashboard Principal.
 */
public class NavegadorCenas {

	private static Stage palcoPrincipal;

	public static void setPalcoPrincipal(Stage stage) {
		palcoPrincipal = stage;
	}

	public static Stage getPalcoPrincipal() {
		return palcoPrincipal;
	}

	public static void irParaLogin() {
		trocarCena("/br/unesp/fct/sgfp/auth-view.fxml", "SGFP - Acesso ao Sistema", 500, 600);
	}

	public static void irParaCadastro() {
		trocarCena("/br/unesp/fct/sgfp/cadastrar-perfil-view.fxml", "SGFP - Criar Perfil", 500, 620);
	}

	public static void irParaDashboard() {
		trocarCena("/br/unesp/fct/sgfp/dashboard-view.fxml", "SGFP - Gestão Financeira Pessoal", 960, 680);
	}

	private static void trocarCena(String caminhoFxml, String titulo, double largura, double altura) {
		if (palcoPrincipal == null) {
			return; // Em execução de testes headless, não faz nada
		}

		try {
			FXMLLoader loader = new FXMLLoader(NavegadorCenas.class.getResource(caminhoFxml));
			Parent raiz = loader.load();
			Scene novaCena = new Scene(raiz, largura, altura);

			palcoPrincipal.setTitle(titulo);
			palcoPrincipal.setScene(novaCena);
			palcoPrincipal.centerOnScreen();
			palcoPrincipal.show();
		} catch (Exception e) {
			System.err.println("Erro ao carregar FXML [" + caminhoFxml + "]: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
