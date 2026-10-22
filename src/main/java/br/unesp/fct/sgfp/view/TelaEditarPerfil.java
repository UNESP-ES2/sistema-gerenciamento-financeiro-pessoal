package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import  br.unesp.fct.sgfp.model.PerfilDeUsuario;
import  br.unesp.fct.sgfp.model.Sessao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Classe de apresentação TelaEditarPerfil derivada do Astah (DCD e Colaboração).
 */
public class TelaEditarPerfil {

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

	public TelaEditarPerfil() {
		this.controlador = new ControladorPerfil();
	}

	public TelaEditarPerfil(ControladorPerfil controlador) {
		this.controlador = controlador;
	}

	@FXML
	public void initialize() {
		if (campoNome != null && Sessao.getInstancia().isAutenticado()) {
			PerfilDeUsuario ativo = Sessao.getInstancia().getPerfilAtivo();
			if (ativo != null && ativo.getNome() != null) {
				campoNome.setText(ativo.getNome());
			}
		}
	}

	public void preencherCampos(String nome, String senha, String confirmacao) {
		this.nomeEntrada = nome;
		this.senhaEntrada = senha;
		this.confirmacaoEntrada = confirmacao;
	}

	public String getMensagem() {
		return labelMensagem != null ? labelMensagem.getText() : this.mensagem;
	}

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
	 * Ação de edição acionada pelo usuário na tela.
	 */
	@FXML
	public void botaoEditar() {
		String nome = (campoNome != null && campoNome.getText() != null) ? campoNome.getText().trim() : (nomeEntrada != null ? nomeEntrada : "");
		String senha = (campoSenha != null && campoSenha.getText() != null) ? campoSenha.getText() : (senhaEntrada != null ? senhaEntrada : "");
		String confirmacao = (campoConfirmacaoSenha != null && campoConfirmacaoSenha.getText() != null) ? campoConfirmacaoSenha.getText() : (confirmacaoEntrada != null ? confirmacaoEntrada : "");

		// Validações defensivas de interface com feedbacks imediatos (RF1.6, RNF5.3)
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
				exibirMensagemErro("Defina a nova senha de acesso (4 a 18 caracteres).");
				return;
			}
			if (senha.contains(" ")) {
				exibirMensagemErro("A nova senha não pode conter espaços.");
				return;
			}
			if (senha.length() < 4 || senha.length() > 18) {
				exibirMensagemErro("A nova senha deve ter entre 4 e 18 caracteres.");
				return;
			}
			if (confirmacao.isEmpty()) {
				exibirMensagemErro("Preencha o campo de confirmação da nova senha.");
				return;
			}
			if (!senha.equals(confirmacao)) {
				exibirMensagemErro("A confirmação da nova senha não confere com a senha digitada.");
				return;
			}
		} else {
			// Compatibilidade headless com testes unitários
			if (!senha.isEmpty() && !confirmacao.isEmpty() && !senha.equals(confirmacao)) {
				falhaDeEdicao();
				return;
			}
		}

		if (controlador == null) {
			controlador = new ControladorPerfil();
		}

		controlador.setTelaEditarPerfil(this);
		boolean verificar = controlador.editarPerfil(nome, senha);

		if (verificar) {
			this.mensagem = "Perfil atualizado com sucesso!";
			if (labelMensagem != null) {
				labelMensagem.getStyleClass().removeAll("banner-error", "banner-info");
				if (!labelMensagem.getStyleClass().contains("banner-success")) {
					labelMensagem.getStyleClass().add("banner-success");
				}
				labelMensagem.setText(this.mensagem);
				labelMensagem.setVisible(true);
				labelMensagem.setManaged(true);
			}
			fecharJanela();
		} else {
			if (this.mensagem == null || this.mensagem.isEmpty() || this.mensagem.equals("Perfil atualizado com sucesso!")) {
				falhaDeEdicao();
			}
		}
	}

	/**
	 * Exibe mensagem de erro quando as credenciais não atenderem aos critérios.
	 */
	public void falhaDeEdicao() {
		this.mensagem = "Falha na edição: dados inválidos.";
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

	@FXML
	public void botaoCancelar() {
		if (campoNome != null) campoNome.clear();
		if (campoSenha != null) campoSenha.clear();
		if (campoConfirmacaoSenha != null) campoConfirmacaoSenha.clear();
		fecharJanela();
	}

	private void fecharJanela() {
		if (campoNome != null && campoNome.getScene() != null && campoNome.getScene().getWindow() != null) {
			Stage stage = (Stage) campoNome.getScene().getWindow();
			stage.close();
		}
	}

}
