package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;

/**
 * Tela de listagem e gerenciamento de contas bancárias derivada do Astah (Seção 3.1.3).
 */
public class TelasContaBancaria {

	private ControladorContaBancaria controlador;
	private String nomeDaContaBancaria;

	public TelasContaBancaria() {
		this.controlador = new ControladorContaBancaria();
	}

	public TelasContaBancaria(ControladorContaBancaria controlador) {
		this.controlador = controlador;
	}

	public void setNomeDaContaBancaria(String nomeDaContaBancaria) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
	}

	public String getNomeDaContaBancaria() {
		return nomeDaContaBancaria;
	}

	/**
	 * Ação acionada pelo usuário para inativar a conta selecionada (RF3.6, RN05).
	 */
	public void inativarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
		}
		controlador.inativarContaBancaria(this.nomeDaContaBancaria);
	}

	public void inativarContaBancaria(String nomeDaContaBancaria) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		inativarContaBancaria();
	}

}
