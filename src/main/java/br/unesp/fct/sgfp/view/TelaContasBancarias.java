package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;

/**
 * Representação nominal da lifeline ': TelaContasBancarias' do Diagrama de Colaboração inativarContaBancaria.
 */
public class TelaContasBancarias extends TelasContaBancaria {

	public TelaContasBancarias() {
		super();
	}

	public TelaContasBancarias(ControladorContaBancaria controlador) {
		super(controlador);
	}

}
