package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
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

	public void preencherCampos(String nome, String senha, String confirmacao) {
		this.nomeEntrada = nome;
		this.senhaEntrada = senha;
		this.confirmacaoEntrada = confirmacao;
	}

	public String getMensagem() {
		return labelMensagem != null ? labelMensagem.getText() : this.mensagem;
	}

	/**
	 * Ação de edição acionada pelo usuário na tela.
	 */
	@FXML
	public void botaoEditar() {
		String nome = campoNome != null ? campoNome.getText() : (nomeEntrada != null ? nomeEntrada : "");
		String senha = campoSenha != null ? campoSenha.getText() : (senhaEntrada != null ? senhaEntrada : "");
		String confirmacao = campoConfirmacaoSenha != null ? campoConfirmacaoSenha.getText() : (confirmacaoEntrada != null ? confirmacaoEntrada : "");

		// RF1.1 / RF1.6: valida confirmação se preenchida
		if (!senha.isEmpty() && !confirmacao.isEmpty() && !senha.equals(confirmacao)) {
			falhaDeEdicao();
			return;
		}

		if (controlador != null) {
			controlador.setTelaEditarPerfil(this);
			boolean verificar = controlador.editarPerfil(nome, senha);

			if (verificar) {
				this.mensagem = "Perfil atualizado com sucesso!";
				if (labelMensagem != null) {
					labelMensagem.setText(this.mensagem);
				}
				fecharJanela();
			}
		}
	}

	/**
	 * Exibe mensagem de erro quando as credenciais não atenderem aos critérios.
	 */
	public void falhaDeEdicao() {
		this.mensagem = "Falha na edição: dados inválidos.";
		if (labelMensagem != null) {
			labelMensagem.setText(this.mensagem);
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
