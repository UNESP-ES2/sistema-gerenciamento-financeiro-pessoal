package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.ObservadorContaAtiva;
import br.unesp.fct.sgfp.model.Sessao;

/**
 * Tela de exibição, seleção e reativação de contas bancárias derivada do Astah (Seções 3.1.2 e 3.1.3).
 * Atua como Observador de Sessao para atualização visual da conta ativa (RF3.5, RF3.7).
 */
public class TelaExibirContasBancarias implements ObservadorContaAtiva {

	private ControladorContaBancaria controlador;
	private String nomeDaContaBancaria;
	private ContaBancaria contaSelecionada;
	private ContaBancaria contaVisualizada;
	private ContaCorrente contaCorrenteRef;

	public TelaExibirContasBancarias() {
		this.controlador = new ControladorContaBancaria();
		this.contaCorrenteRef = new ContaCorrente();
		Sessao.getInstancia().adicionarObservador(this);
	}

	public TelaExibirContasBancarias(ControladorContaBancaria controlador) {
		this.controlador = controlador;
		this.contaCorrenteRef = new ContaCorrente();
		Sessao.getInstancia().adicionarObservador(this);
	}

	public void setNomeDaContaBancaria(String nomeDaContaBancaria) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
	}

	public String getNomeDaContaBancaria() {
		return nomeDaContaBancaria;
	}

	public void setContaSelecionada(ContaBancaria contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public ContaBancaria getContaSelecionada() {
		return contaSelecionada;
	}

	public ContaBancaria getContaVisualizada() {
		return contaVisualizada;
	}

	public void setContaCorrenteRef(ContaCorrente contaCorrenteRef) {
		this.contaCorrenteRef = contaCorrenteRef;
	}

	/**
	 * Ponto de entrada acionado pelo usuário para selecionar a conta bancária ativa (RF3.5).
	 */
	public void acaoSelecionarContaBancaria() {
		if (this.contaSelecionada != null) {
			selecionarContaBancaria(this.contaSelecionada);
		}
	}

	/**
	 * Dispara a seleção para a conta bancária (RF3.5).
	 */
	public void selecionarContaBancaria(ContaBancaria conta) {
		if (this.contaCorrenteRef == null) {
			this.contaCorrenteRef = new ContaCorrente();
		}
		this.contaCorrenteRef.selecionarContaBancaria(conta);
	}

	/**
	 * Callback disparado pela Sessão para atualizar visualmente a nova conta ativa (RF3.5).
	 */
	@Override
	public void atualizarVisualizacao(ContaBancaria conta) {
		this.contaVisualizada = conta;
	}

	/**
	 * Ação acionada pelo usuário para reativar a conta selecionada (RF3.7, RN05).
	 */
	public void reativarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
		}
		controlador.reativarContaBancaria(this.nomeDaContaBancaria);
	}

	public void reativarContaBancaria(String nomeDaContaBancaria) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		reativarContaBancaria();
	}

}
