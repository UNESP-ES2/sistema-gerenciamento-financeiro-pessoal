package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Classe de apresentação TelaCadastrarPerfil
 */
public class TelaCadastrarPerfil {

	@FXML
	private TextField campoNome;

	@FXML
	private PasswordField campoSenha;

	@FXML
	private PasswordField campoConfirmacaoSenha;

	@FXML
	private Label labelMensagem;

	private ControladorPerfil controlador;
	private String nomeEntrada;
	private String senhaEntrada;
	private String confirmacaoEntrada;
	private String mensagem;

	public TelaCadastrarPerfil() {
		this.controlador = new ControladorPerfil();
	}

	public TelaCadastrarPerfil(ControladorPerfil controlador) {
		this.controlador = controlador;
	}

	public void preencherCampos(String nome, String senha, String confirmacao) {
		this.nomeEntrada = nome;
		this.senhaEntrada = senha;
		this.confirmacaoEntrada = confirmacao;
	}

	public String getMensagem() {
		return labelMensagem != null ? labelMensagem.getText() : this.mensagem;
	}

	/**
	 * Exibe mensagem de erro na tela quando os dados informados forem inválidos (RNF5.3).
	 */
	public void exibirMensagemErro(String mensagem) {
		this.mensagem = mensagem;
		if (labelMensagem != null) {
			labelMensagem.getStyleClass().removeAll("banner-success", "banner-info");
			if (!labelMensagem.getStyleClass().contains("banner-error")) {
				labelMensagem.getStyleClass().add("banner-error");
			}
			labelMensagem.setText(this.mensagem);
			labelMensagem.setVisible(true);
			labelMensagem.setManaged(true);
		}
	}

	/**
	 * Ação de cadastro acionada pelo usuário na tela.
	 */
	@FXML
	public void botaoCadastrar() {
		String nome = (campoNome != null && campoNome.getText() != null) ? campoNome.getText().trim() : (nomeEntrada != null ? nomeEntrada : "");
		String senha = (campoSenha != null && campoSenha.getText() != null) ? campoSenha.getText() : (senhaEntrada != null ? senhaEntrada : "");
		String confirmacao = (campoConfirmacaoSenha != null && campoConfirmacaoSenha.getText() != null) ? campoConfirmacaoSenha.getText() : (confirmacaoEntrada != null ? confirmacaoEntrada : "");

		// Validações defensivas de interface com feedbacks amigáveis e imediatos (RF1.1, RNF5.3)
		if (campoNome != null && campoSenha != null) {
			if (nome.isEmpty()) {
				exibirMensagemErro("Informe o nome de usuário (4 a 18 letras, sem espaços).");
				return;
			}
			if (nome.contains(" ")) {
				exibirMensagemErro("O nome de usuário não pode conter espaços.");
				return;
			}
			if (!nome.matches("^[a-zA-ZÀ-ÿ]+$")) {
				exibirMensagemErro("O nome de usuário deve conter apenas letras, sem números ou símbolos.");
				return;
			}
			if (nome.length() < 4 || nome.length() > 18) {
				exibirMensagemErro("O nome de usuário deve ter entre 4 e 18 letras.");
				return;
			}
			if (senha.isEmpty()) {
				exibirMensagemErro("Defina sua senha de acesso (4 a 18 caracteres).");
				return;
			}
			if (senha.contains(" ")) {
				exibirMensagemErro("A senha não pode conter espaços.");
				return;
			}
			if (senha.length() < 4 || senha.length() > 18) {
				exibirMensagemErro("A senha deve ter entre 4 e 18 caracteres.");
				return;
			}
			if (confirmacao.isEmpty()) {
				exibirMensagemErro("Preencha o campo de confirmação da senha.");
				return;
			}
			if (!senha.equals(confirmacao)) {
				exibirMensagemErro("A confirmação da senha não confere com a senha digitada.");
				return;
			}
		} else {
			// Compatibilidade headless com testes unitários
			if (!senha.isEmpty() && !confirmacao.isEmpty() && !senha.equals(confirmacao)) {
				falhaDeCadastro();
				return;
			}
		}

		if (controlador == null) {
			controlador = new ControladorPerfil();
		}

		controlador.setTelaCadastrarPerfil(this);
		boolean verificar = controlador.cadastrarPerfil(nome, senha);

		if (verificar) {
			this.mensagem = "Perfil cadastrado com sucesso!";
			if (labelMensagem != null) {
				labelMensagem.getStyleClass().removeAll("banner-error", "banner-info");
				if (!labelMensagem.getStyleClass().contains("banner-success")) {
					labelMensagem.getStyleClass().add("banner-success");
				}
				labelMensagem.setText(this.mensagem);
				labelMensagem.setVisible(true);
				labelMensagem.setManaged(true);
			}

			if (NavegadorCenas.getPalcoPrincipal() != null) {
				NavegadorCenas.irParaDashboard();
			} else {
				fecharJanela();
			}
		} else {
			if (this.mensagem == null || this.mensagem.isEmpty() || this.mensagem.equals("Perfil cadastrado com sucesso!")) {
				falhaDeCadastro();
			}
		}
	}

	/**
	 * Exibe mensagem de falha quando as credenciais não atendem aos critérios.
	 */
	public void falhaDeCadastro() {
		this.mensagem = "Falha no cadastro: entradas inválidas.";
		if (labelMensagem != null) {
			labelMensagem.getStyleClass().removeAll("banner-success", "banner-info");
			if (!labelMensagem.getStyleClass().contains("banner-error")) {
				labelMensagem.getStyleClass().add("banner-error");
			}
			labelMensagem.setText(this.mensagem);
			labelMensagem.setVisible(true);
			labelMensagem.setManaged(true);
		}
	}

	/**
	 * Alias de compatibilidade com interfaces que utilizam botaoConfirmar.
	 */
	@FXML
	public void botaoConfirmar() {
		botaoCadastrar();
	}

	/**
	 * Mensagem Alternativa: Ator clica em Cancelar (RF1.3.6).
	 * Retorna à navegação pública sem gravar ou autenticar nada.
	 */
	@FXML
	public void botaoCancelar() {
		if (campoNome != null) campoNome.clear();
		if (campoSenha != null) campoSenha.clear();
		if (campoConfirmacaoSenha != null) campoConfirmacaoSenha.clear();
		if (NavegadorCenas.getPalcoPrincipal() != null) {
			NavegadorCenas.irParaLogin();
		} else {
			fecharJanela();
		}
	}

	private void fecharJanela() {
		if (campoNome != null && campoNome.getScene() != null && campoNome.getScene().getWindow() != null) {
			Stage stage = (Stage) campoNome.getScene().getWindow();
			stage.close();
		}
	}

}
