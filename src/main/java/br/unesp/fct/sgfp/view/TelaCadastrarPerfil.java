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
	 * Ação de cadastro acionada pelo usuário na tela.
	 */
	@FXML
	public void botaoCadastrar() {
		String nome = campoNome != null ? campoNome.getText() : (nomeEntrada != null ? nomeEntrada : "");
		String senha = campoSenha != null ? campoSenha.getText() : (senhaEntrada != null ? senhaEntrada : "");
		String confirmacao = campoConfirmacaoSenha != null ? campoConfirmacaoSenha.getText() : (confirmacaoEntrada != null ? confirmacaoEntrada : "");

		// RF1.1: validação de confirmação de senha
		if (!senha.isEmpty() && !confirmacao.isEmpty() && !senha.equals(confirmacao)) {
			falhaDeCadastro();
			return;
		}

		if (controlador != null) {
			controlador.setTelaCadastrarPerfil(this);
			boolean verificar = controlador.cadastrarPerfil(nome, senha);

			if (verificar) {
				this.mensagem = "Perfil cadastrado com sucesso!";
				if (labelMensagem != null) {
					labelMensagem.setText(this.mensagem);
				}

				fecharJanela();
			}
		}
	}

	/**
	 * Exibe mensagem de falha quando as credenciais não atendem aos critérios.
	 */
	public void falhaDeCadastro() {
		this.mensagem = "Falha no cadastro: entradas inválidas.";
		if (labelMensagem != null) {
			labelMensagem.setText(this.mensagem);
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
		fecharJanela();
	}

	private void fecharJanela() {
		if (campoNome != null && campoNome.getScene() != null && campoNome.getScene().getWindow() != null) {
			Stage stage = (Stage) campoNome.getScene().getWindow();
			stage.close();
		}
	}

}
