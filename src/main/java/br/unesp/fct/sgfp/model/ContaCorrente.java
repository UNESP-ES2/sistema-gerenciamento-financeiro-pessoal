package br.unesp.fct.sgfp.model;

/**
 * Esqueleto da classe ContaCorrente gerado a partir do Astah (DCD).
 */
public class ContaCorrente extends ContaBancaria {

	private String nomeDoBanco;

	private int numeroDaAgencia;

	private int numeroDaConta;

	public ContaCorrente() {
	}

	public ContaCorrente(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
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
	 * Fábrica polimórfica para criação e inicialização de Conta Corrente (RF3.1).
	 */
	public static ContaCorrente criar(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		return new ContaCorrente(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
	}

	public ContaCorrente criar() {
		return new ContaCorrente();
	}

	/**
	 * Seleciona a conta bancária no perfil ativo e define como conta ativa da sessão (RF3.5).
	 */
	public void selecionarContaBancaria(ContaBancaria conta) {
		if (conta == null) {
			return;
		}

		ContaBancaria contaLocalizada = null;
		PerfilDeUsuario perfil = Sessao.getInstancia().getPerfilAtivo();
		if (perfil != null) {
			contaLocalizada = perfil.buscarContaBancaria(conta.getNomeDaContaBancaria());
		}

		if (contaLocalizada == null) {
			contaLocalizada = conta;
		}

		Sessao.getInstancia().setContaAtiva(contaLocalizada);
	}

	public void selecionarContaBancaria() {
		selecionarContaBancaria(this);
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
