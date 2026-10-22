package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Classe de apresentação TelaAutenticacaoPerfil derivada do Astah (DCD e Colaboração).
 */
public class TelaAutenticacaoPerfil {

	@FXML
	private TextField campoNome;

	@FXML
	private PasswordField campoSenha;

	@FXML
	private Label labelMensagem;

	private ControladorPerfil controlador;
	private String nomeEntrada;
	private String senhaEntrada;
	private String mensagem;

	public TelaAutenticacaoPerfil() {
		this.controlador = new ControladorPerfil();
	}

	public TelaAutenticacaoPerfil(ControladorPerfil controlador) {
		this.controlador = controlador;
	}

	public void preencherCampos(String nome, String senha) {
		this.nomeEntrada = nome;
		this.senhaEntrada = senha;
	}

	public String getMensagem() {
		return labelMensagem != null ? labelMensagem.getText() : this.mensagem;
	}

	/**
	 * Ação de autenticação acionada pelo usuário na tela.
	 */
	@FXML
	public void botaoAutenticar() {
		String nome = (campoNome != null && campoNome.getText() != null) ? campoNome.getText().trim() : (nomeEntrada != null ? nomeEntrada : "");
		String senha = (campoSenha != null && campoSenha.getText() != null) ? campoSenha.getText() : (senhaEntrada != null ? senhaEntrada : "");

		if (controlador != null) {
			controlador.setTelaAutenticacaoPerfil(this);
			boolean verificar = controlador.autenticarPerfil(nome, senha);

			if (verificar) {
				this.mensagem = "Autenticado com sucesso!";
				if (labelMensagem != null) {
					labelMensagem.getStyleClass().removeAll("banner-error", "banner-info");
					if (!labelMensagem.getStyleClass().contains("banner-success")) {
						labelMensagem.getStyleClass().add("banner-success");
					}
					labelMensagem.setText(this.mensagem);
				}
				if (NavegadorCenas.getPalcoPrincipal() != null) {
					NavegadorCenas.irParaDashboard();
				} else {
					fecharJanela();
				}
			}
		}
	}

	/**
	 * Exibe mensagem de erro quando as credenciais forem inválidas.
	 */
	public void falhaDeAutenticacao() {
		this.mensagem = "Usuário ou senha inválidos.";
		if (labelMensagem != null) {
			// RF1.5: falha de autenticação genérica por segurança
			labelMensagem.getStyleClass().removeAll("banner-success", "banner-info");
			if (!labelMensagem.getStyleClass().contains("banner-error")) {
				labelMensagem.getStyleClass().add("banner-error");
			}
			labelMensagem.setText(this.mensagem);
		}
	}

	@FXML
	public void botaoCancelar() {
		if (campoNome != null) campoNome.clear();
		if (campoSenha != null) campoSenha.clear();
		fecharJanela();
	}

	@FXML
	public void botaoCriarPerfil() {
		NavegadorCenas.irParaCadastro();
	}

	/**
	 * Ação de recuperação de senha acionada pelo usuário na tela.
	 */
	@FXML
	public void botaoEsqueceuSenha() {
		String nome = (campoNome != null && campoNome.getText() != null) ? campoNome.getText().trim() : (nomeEntrada != null ? nomeEntrada : "");

		if (nome.isEmpty()) {
			this.mensagem = "Informe seu nome de usuário no campo acima para redefinir a senha.";
			if (labelMensagem != null) {
				labelMensagem.getStyleClass().removeAll("banner-success", "banner-error");
				if (!labelMensagem.getStyleClass().contains("banner-warning")) {
					labelMensagem.getStyleClass().add("banner-warning");
				}
				labelMensagem.setText(this.mensagem);
			}
			return;
		}

		if (controlador != null) {
			controlador.setTelaAutenticacaoPerfil(this);
			boolean verificar = controlador.esqueceuSenha(nome);

			if (verificar) {
				this.mensagem = "Senha redefinida e autenticado com sucesso!";
				if (labelMensagem != null) {
					labelMensagem.getStyleClass().removeAll("banner-error", "banner-warning");
					if (!labelMensagem.getStyleClass().contains("banner-success")) {
						labelMensagem.getStyleClass().add("banner-success");
					}
					labelMensagem.setText(this.mensagem);
				}
				if (NavegadorCenas.getPalcoPrincipal() != null) {
					NavegadorCenas.irParaDashboard();
				} else {
					fecharJanela();
				}
			} else if (this.mensagem == null || this.mensagem.isEmpty()) {
				this.mensagem = "Usuário não encontrado.";
				if (labelMensagem != null) {
					labelMensagem.getStyleClass().removeAll("banner-success", "banner-warning");
					if (!labelMensagem.getStyleClass().contains("banner-error")) {
						labelMensagem.getStyleClass().add("banner-error");
					}
					labelMensagem.setText(this.mensagem);
				}
			}
		}
	}

	/**
	 * Exibe mensagem de erro quando a nova senha não atende aos critérios (RF1.1.2).
	 */
	public void falhaDeEdicao() {
		this.mensagem = "Falha na edição da senha: senha inválida.";
		if (labelMensagem != null) {
			labelMensagem.setText(this.mensagem);
		}
	}

	/**
	 * Inicia o fluxo de exibição e entrada da autenticação de perfil.
	 */
	public void iniciarAutenticacao() {
		// Ponto de entrada para exibição da tela de autenticação
	}

	private void fecharJanela() {
		if (campoNome != null && campoNome.getScene() != null && campoNome.getScene().getWindow() != null) {
			Stage stage = (Stage) campoNome.getScene().getWindow();
			stage.close();
		}
	}

}
