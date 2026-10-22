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

	public void atualizarContaBancaria() {
		// Operação prevista no DCD
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
