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

	public void validarEntradas() {

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
