package br.unesp.fct.sgfp.model;

import java.util.Date;

/**
 * Esqueleto da classe ContaBancaria gerado a partir do Astah (DCD).
 * Ajustada como classe base abstrata para permitir a herança polimórfica das modalidades.
 */
public abstract class ContaBancaria {

	private String nomeDaContaBancaria;

	private String tipo;

	private Float saldo;

	private Date dataDeReferencia;

	private String status;

	/**
	 * Valida entradas para contas bancárias (Conta Corrente e Poupança).
	 * Atende a RN03 (campos bancários obrigatórios) e RN04 (saldo e data de referência obrigatórios).
	 */
	public static boolean validarEntradas(String nomeDaContaBancaria, String tipo, Float saldo, Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		if (nomeDaContaBancaria == null || nomeDaContaBancaria.trim().isEmpty()) {
			return false;
		}
		if (tipo == null || (!tipo.equalsIgnoreCase("conta corrente") && !tipo.equalsIgnoreCase("poupanca") && !tipo.equalsIgnoreCase("poupança"))) {
			return false;
		}
		if (saldo == null || dataDeReferencia == null) {
			return false; // RN04
		}
		if (nomeDoBanco == null || nomeDoBanco.trim().isEmpty()) {
			return false; // RN03
		}
		if (numeroDaAgencia <= 0 || numeroDaConta <= 0) {
			return false; // RN03
		}
		return true;
	}

	/**
	 * Valida entradas para carteira de dinheiro.
	 * Atende a RN02 (não-negatividade do saldo inicial) e RN04 (saldo e data de referência obrigatórios).
	 */
	public static boolean validarEntradas(String nomeDaContaBancaria, String tipo, Float saldo, Date dataDeReferencia) {
		if (nomeDaContaBancaria == null || nomeDaContaBancaria.trim().isEmpty()) {
			return false;
		}
		if (tipo == null || !tipo.equalsIgnoreCase("carteira de dinheiro")) {
			return false;
		}
		if (saldo == null || dataDeReferencia == null) {
			return false; // RN04
		}
		if (saldo < 0) {
			return false; // RN02: Carteira de dinheiro não pode ser inicializada com saldo negativo
		}
		return true;
	}

	public void validarEntradas() {
		// Método sem argumentos mantido do esqueleto do DCD
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNomeDaContaBancaria() {
		return nomeDaContaBancaria;
	}

	public void setNomeDaContaBancaria(String nomeDaContaBancaria) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Float getSaldo() {
		return saldo;
	}

	public void setSaldo(Float saldo) {
		this.saldo = saldo;
	}

	public Date getDataDeReferencia() {
		return dataDeReferencia;
	}

	public void setDataDeReferencia(Date dataDeReferencia) {
		this.dataDeReferencia = dataDeReferencia;
	}

	public String getStatus() {
		return status;
	}

}
