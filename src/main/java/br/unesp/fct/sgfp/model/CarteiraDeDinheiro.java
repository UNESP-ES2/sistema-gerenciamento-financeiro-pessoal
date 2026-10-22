package br.unesp.fct.sgfp.model;

/**
 * Esqueleto da classe CarteiraDeDinheiro gerado a partir do Astah (DCD).
 */
public class CarteiraDeDinheiro extends ContaBancaria {

	public CarteiraDeDinheiro() {
	}

	public CarteiraDeDinheiro(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		setNomeDaContaBancaria(nomeDaContaBancaria);
		setTipo(tipo);
		setSaldo(saldo);
		setDataDeReferencia(dataDeReferencia);
		setStatus("ativo");
	}

	/**
	 * Fábrica polimórfica para criação e inicialização de Carteira de Dinheiro (RF3.1, RN02).
	 */
	public static CarteiraDeDinheiro criar(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		return new CarteiraDeDinheiro(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);
	}

	public CarteiraDeDinheiro criar() {
		return new CarteiraDeDinheiro();
	}

}
