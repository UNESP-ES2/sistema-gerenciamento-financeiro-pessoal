package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.ContaBancaria;

/**
 * Tela de Edição de Conta Bancária derivada do Astah (Seção 3.1.3).
 */
public class TelaEditarContaBancaria {

	private ControladorContaBancaria controlador;
	private ContaBancaria contaCarregada;
	private String mensagemErro;
	private boolean confirmado = false;

	// Campos de formulário para edição
	private String nomeDaContaBancaria;
	private String tipo;
	private Float saldo;
	private java.util.Date dataDeReferencia;
	private String nomeDoBanco;
	private int numeroDaAgencia;
	private int numeroDaConta;

	public TelaEditarContaBancaria() {
		this.controlador = new ControladorContaBancaria();
		this.controlador.setTelaEditarContaBancaria(this);
	}

	public TelaEditarContaBancaria(ControladorContaBancaria controlador) {
		this.controlador = controlador;
		if (this.controlador != null) {
			this.controlador.setTelaEditarContaBancaria(this);
		}
	}

	public void preencherCamposParaEdicao(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		this.tipo = tipo;
		this.saldo = saldo;
		this.dataDeReferencia = dataDeReferencia;
		this.nomeDoBanco = nomeDoBanco;
		this.numeroDaAgencia = numeroDaAgencia;
		this.numeroDaConta = numeroDaConta;
	}

	public void preencherCamposParaEdicao(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		this.tipo = tipo;
		this.saldo = saldo;
		this.dataDeReferencia = dataDeReferencia;
		this.nomeDoBanco = null;
		this.numeroDaAgencia = 0;
		this.numeroDaConta = 0;
	}

	public ContaBancaria getContaCarregada() {
		return contaCarregada;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	/**
	 * Solicita os dados atuais da conta para pré-carregar na tela de edição (RF3.3).
	 */
	public void obterConta(String nomeDaContaBancaria) {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaEditarContaBancaria(this);
		}
		controlador.obterConta(nomeDaContaBancaria);
	}

	/**
	 * Carrega os dados da conta na interface.
	 */
	public void exibirContaBancaria(ContaBancaria conta) {
		this.contaCarregada = conta;
	}

	/**
	 * Ação acionada pelo Usuário para submeter as alterações cadastrais da conta (RF3.3).
	 */
	public void editarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaEditarContaBancaria(this);
		}

		if (tipo != null && tipo.equalsIgnoreCase("carteira de dinheiro")) {
			controlador.atualizarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);
		} else {
			controlador.atualizarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
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
	 * Exibe mensagem de confirmação após atualização da conta com sucesso.
	 */
	public void exibirMensagemConfirmacao() {
		this.confirmado = true;
		this.mensagemErro = null;
	}

}
