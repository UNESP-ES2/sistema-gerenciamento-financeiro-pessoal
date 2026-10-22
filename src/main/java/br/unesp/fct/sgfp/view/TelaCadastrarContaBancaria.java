package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;

/**
 * Esqueleto da classe TelaCadastrarContaBancaria gerado a partir do Astah (DCD).
 */
public class TelaCadastrarContaBancaria {

	private ControladorContaBancaria controlador;
	private String mensagemErro;
	private boolean confirmado = false;

	// Campos de entrada para modo headless / interface
	private String nomeDaContaBancaria;
	private String tipo;
	private Float saldo;
	private java.util.Date dataDeReferencia;
	private String nomeDoBanco;
	private int numeroDaAgencia;
	private int numeroDaConta;

	public TelaCadastrarContaBancaria() {
		this.controlador = new ControladorContaBancaria();
		this.controlador.setTelaCadastrarContaBancaria(this);
	}

	public TelaCadastrarContaBancaria(ControladorContaBancaria controlador) {
		this.controlador = controlador;
		if (this.controlador != null) {
			this.controlador.setTelaCadastrarContaBancaria(this);
		}
	}

	public void preencherCampos(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		this.tipo = tipo;
		this.saldo = saldo;
		this.dataDeReferencia = dataDeReferencia;
		this.nomeDoBanco = nomeDoBanco;
		this.numeroDaAgencia = numeroDaAgencia;
		this.numeroDaConta = numeroDaConta;
	}

	public void preencherCampos(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		this.tipo = tipo;
		this.saldo = saldo;
		this.dataDeReferencia = dataDeReferencia;
		this.nomeDoBanco = null;
		this.numeroDaAgencia = 0;
		this.numeroDaConta = 0;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	/**
	 * Ação de cadastro disparada pelo Usuário na tela.
	 * Delega para o ControladorContaBancaria conforme o tipo da conta (RF3.1).
	 */
	public void cadastrarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaCadastrarContaBancaria(this);
		}

		if (tipo != null && tipo.equalsIgnoreCase("carteira de dinheiro")) {
			controlador.cadastrarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);
		} else {
			controlador.cadastrarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
		}
	}

	/**
	 * Exibe mensagem de erro na tela quando os dados informados forem inválidos.
	 */
	public void exibirMensagemErro(String mensagem) {
		this.mensagemErro = mensagem;
		this.confirmado = false;
	}

	/**
	 * Exibe mensagem de confirmação após cadastro da conta com sucesso.
	 */
	public void exibirMensagemConfirmacao() {
		this.confirmado = true;
		this.mensagemErro = null;
	}

}
