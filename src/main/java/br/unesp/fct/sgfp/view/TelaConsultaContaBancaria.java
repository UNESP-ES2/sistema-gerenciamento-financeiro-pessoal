package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.ContaBancaria;

/**
 * Tela de Consulta de Conta Bancária derivada do Astah (Seção 3.1.3).
 */
public class TelaConsultaContaBancaria {

	private ControladorContaBancaria controlador;
	private String nomeDaContaBancaria;
	private ContaBancaria contaExibida;

	public TelaConsultaContaBancaria() {
		this.controlador = new ControladorContaBancaria();
		this.controlador.setTelaConsultaContaBancaria(this);
	}

	public TelaConsultaContaBancaria(ControladorContaBancaria controlador) {
		this.controlador = controlador;
		if (this.controlador != null) {
			this.controlador.setTelaConsultaContaBancaria(this);
		}
	}

	public void preencherCampoBusca(String nomeDaContaBancaria) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
	}

	public String getNomeDaContaBancaria() {
		return nomeDaContaBancaria;
	}

	public ContaBancaria getContaExibida() {
		return contaExibida;
	}

	/**
	 * Ponto de entrada acionado pelo usuário para iniciar a consulta na tela.
	 */
	public void iniciarConsultaDeContaBancaria() {
		consultarContaBancaria();
	}

	/**
	 * Dispara a consulta da conta no ControladorContaBancaria com o nome informado na tela (RF3.2).
	 */
	public void consultarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaConsultaContaBancaria(this);
		}
		controlador.consultarContaBancaria(this.nomeDaContaBancaria);
	}

	/**
	 * Recebe a conta localizada pelo controlador e armazena para exibição na tela.
	 */
	public void exibirContaBancaria(ContaBancaria conta) {
		this.contaExibida = conta;
	}

}
