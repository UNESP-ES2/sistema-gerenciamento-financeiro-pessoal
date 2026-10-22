package br.unesp.fct.sgfp.model;

import java.util.ArrayList;

/**
 * Entidade de domínio PerfilDeUsuario gerada a partir do Astah (DCD).
 * Representa o titular da base financeira e agrega as contas bancárias (Especialista da Informação).
 */
public class PerfilDeUsuario {

	private String nome;
	private String senha;
	private ArrayList<ContaBancaria> contas = new ArrayList<>();

	public PerfilDeUsuario() {
	}

	public PerfilDeUsuario(String nome, String senha) {
		this.nome = nome;
		this.senha = senha;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public ArrayList<ContaBancaria> obterContas() {
		return contas;
	}

	public ContaBancaria buscarContaBancaria(String nomeDaContaBancaria) {
		if (nomeDaContaBancaria == null) return null;
		for (ContaBancaria c : contas) {
			if (nomeDaContaBancaria.equalsIgnoreCase(c.getNomeDaContaBancaria())) {
				return c;
			}
		}
		return null;
	}

	public void buscarContaBancaria() {
		// Sobrecarga sem argumentos prevista no DCD
	}

	/**
	 * Atualiza os atributos da conta bancária existente in-place (RF3.3, RF3.4).
	 */
	public void atualizarContaBancaria(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		//TODO: Adicionado mesmo que não esteja previsto no diagrama.
		ContaBancaria conta = buscarContaBancaria(nomeDaContaBancaria);
		if (conta != null) {
			conta.setTipo(tipo);
			conta.setSaldo(saldo);
			conta.setDataDeReferencia(dataDeReferencia);
			if (conta instanceof ContaCorrente cc) {
				cc.setNomeDoBanco(nomeDoBanco);
				cc.setNumeroDaAgencia(numeroDaAgencia);
				cc.setNumeroDaConta(numeroDaConta);
			} else if (conta instanceof Poupanca p) {
				p.setNomeDoBanco(nomeDoBanco);
				p.setNumeroDaAgencia(numeroDaAgencia);
				p.setNumeroDaConta(numeroDaConta);
			}
		}
	}

	/**
	 * Atualiza os atributos da carteira de dinheiro existente in-place (RF3.3, RF3.4).
	 */
	public void atualizarContaBancaria(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		ContaBancaria conta = buscarContaBancaria(nomeDaContaBancaria);
		if (conta != null) {
			conta.setTipo(tipo);
			conta.setSaldo(saldo);
			conta.setDataDeReferencia(dataDeReferencia);
		}
	}

	public void atualizarContaBancaria() {
		// Sobrecarga sem argumentos prevista no DCD
	}

	/**
	 * Cria instâncias polimórficas de contas bancárias (Conta Corrente ou Poupança) (RF3.1, RN03, RN04).
	 */
	public ContaBancaria criarConta(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		if (tipo != null && tipo.equalsIgnoreCase("conta corrente")) {
			return ContaCorrente.criar(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
		} else if (tipo != null && (tipo.equalsIgnoreCase("poupanca") || tipo.equalsIgnoreCase("poupança"))) {
			return Poupanca.criar(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
		}
		return null;
	}

	/**
	 * Cria instâncias polimórficas de Carteira de Dinheiro (RF3.1, RN02, RN04).
	 */
	public ContaBancaria criarConta(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		if (tipo != null && tipo.equalsIgnoreCase("carteira de dinheiro")) {
			return CarteiraDeDinheiro.criar(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);
		}
		return null;
	}

	public ContaBancaria criarConta() {
		// Método fábrica previsto no DCD
		return null;
	}

	public void adicionarConta(ContaBancaria conta) {
		if (conta != null && !contas.contains(conta)) {
			contas.add(conta);
		}
	}

	public void adicionarConta() {
		// Sobrecarga sem argumentos prevista no DCD
	}

}
