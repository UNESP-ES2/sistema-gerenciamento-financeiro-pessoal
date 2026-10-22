package br.unesp.fct.sgfp.model;

/**
 * Esqueleto da classe Poupanca gerado a partir do Astah (DCD).
 */
public class Poupanca extends ContaBancaria {

	private String nomeDoBanco;

	private int numeroDaAgencia;

	private int numeroDaConta;

	public Poupanca() {
	}

	public Poupanca(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		setNomeDaContaBancaria(nomeDaContaBancaria);
		setTipo(tipo);
		setSaldo(saldo);
		setDataDeReferencia(dataDeReferencia);
		setStatus("ativo");
		this.nomeDoBanco = nomeDoBanco;
		this.numeroDaAgencia = numeroDaAgencia;
		this.numeroDaConta = numeroDaConta;
	}

	/**
	 * Fábrica polimórfica para criação e inicialização de Poupança (RF3.1).
	 */
	public static Poupanca criar(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		return new Poupanca(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
	}

	public Poupanca criar() {
		return new Poupanca();
	}

	public String getNomeDoBanco() {
		return nomeDoBanco;
	}

	public void setNomeDoBanco(String nomeDoBanco) {
		this.nomeDoBanco = nomeDoBanco;
	}

	public int getNumeroDaAgencia() {
		return numeroDaAgencia;
	}

	public void setNumeroDaAgencia(int numeroDaAgencia) {
		this.numeroDaAgencia = numeroDaAgencia;
	}

	public int getNumeroDaConta() {
		return numeroDaConta;
	}

	public void setNumeroDaConta(int numeroDaConta) {
		this.numeroDaConta = numeroDaConta;
	}

}
