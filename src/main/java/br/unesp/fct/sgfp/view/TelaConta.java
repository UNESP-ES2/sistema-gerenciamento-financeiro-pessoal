package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.ContaBancaria;

import java.util.ArrayList;

/**
 * Tela de listagem e alteração de conta ativa derivada do Astah (Seção 3.1.2).
 * Representa a lifeline ': TelaConta' no diagrama de colaboração alterarContaBancaria.
 */
public class TelaConta {

	private ControladorContaBancaria controlador;
	private ArrayList<ContaBancaria> contasExibidas = new ArrayList<>();

	public TelaConta() {
		this.controlador = new ControladorContaBancaria();
		this.controlador.setTelaConta(this);
	}

	public TelaConta(ControladorContaBancaria controlador) {
		this.controlador = controlador;
		if (this.controlador != null) {
			this.controlador.setTelaConta(this);
		}
	}

	public ArrayList<ContaBancaria> getContasExibidas() {
		return contasExibidas;
	}

	/**
	 * Ponto de entrada disparado pelo usuário para listar e alterar a conta ativa (RF3.5).
	 */
	public void acaoAlterarContaBancaria() {
		alterarContaBancaria();
	}

	public void alterarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaConta(this);
		}
		controlador.alterarContaBancaria();
	}

	/**
	 * Recebe as contas do perfil ativo para exibição e seleção na tela (RF3.5).
	 */
	public void exibirListaContas(ArrayList<ContaBancaria> contas) {
		this.contasExibidas = contas != null ? contas : new ArrayList<>();
	}

}
