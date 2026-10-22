package br.unesp.fct.sgfp.controller;

import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.view.TelaCadastrarContaBancaria;
import br.unesp.fct.sgfp.view.TelaConsultaContaBancaria;
import br.unesp.fct.sgfp.view.TelaConta;
import br.unesp.fct.sgfp.view.TelaEditarContaBancaria;

import java.util.ArrayList;

/**
 * Controlador GRASP do caso de uso de Contas Bancárias derivado do Astah.
 */
public class ControladorContaBancaria {
	private TelaCadastrarContaBancaria telaCadastrarContaBancaria;
	private TelaConsultaContaBancaria telaConsultaContaBancaria;
	private TelaEditarContaBancaria telaEditarContaBancaria;
	private TelaConta telaConta;
	private PerfilDeUsuario perfil;

	public ControladorContaBancaria() {
		if (br.unesp.fct.sgfp.model.Sessao.getInstancia().isAutenticado()) {
			this.perfil = br.unesp.fct.sgfp.model.Sessao.getInstancia().getPerfilAtivo();
		}
	}

	public ControladorContaBancaria(PerfilDeUsuario perfil) {
		this.perfil = perfil;
	}

	public void setTelaCadastrarContaBancaria(TelaCadastrarContaBancaria telaCadastrarContaBancaria) {
		this.telaCadastrarContaBancaria = telaCadastrarContaBancaria;
	}

	public void setTelaConsultaContaBancaria(TelaConsultaContaBancaria telaConsultaContaBancaria) {
		this.telaConsultaContaBancaria = telaConsultaContaBancaria;
	}

	public void setTelaEditarContaBancaria(TelaEditarContaBancaria telaEditarContaBancaria) {
		this.telaEditarContaBancaria = telaEditarContaBancaria;
	}

	public void setTelaConta(TelaConta telaConta) {
		this.telaConta = telaConta;
	}

	public void setPerfil(PerfilDeUsuario perfil) {
		this.perfil = perfil;
	}

	private PerfilDeUsuario getPerfilAtual() {
		if (this.perfil != null) {
			return this.perfil;
		}
		if (br.unesp.fct.sgfp.model.Sessao.getInstancia().isAutenticado()) {
			return br.unesp.fct.sgfp.model.Sessao.getInstancia().getPerfilAtivo();
		}
		return null;
	}

	/**
	 * Altera a conta bancária ativa listando todas as contas do perfil (RF3.5).
	 */
	public void alterarContaBancaria() {
		PerfilDeUsuario p = br.unesp.fct.sgfp.model.Sessao.getInstancia().getPerfilAtivo();
		if (p == null) {
			p = getPerfilAtual();
		}

		if (p != null) {
			ArrayList<ContaBancaria> contas = p.obterContas();

			if (telaConta != null) {
				telaConta.exibirListaContas(contas);
			}
		}
	}

	/**
	 * Cadastra conta bancária das modalidades Conta Corrente ou Poupança (RF3.1, RN03, RN04).
	 */
	public void cadastrarContaBancaria(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		boolean validacao = ContaBancaria.validarEntradas(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);

		if (!validacao) {
			if (telaCadastrarContaBancaria != null) {
				telaCadastrarContaBancaria.exibirMensagemErro("Dados bancários inválidos.");
			}
			return;
		}

		PerfilDeUsuario perfilAtual = getPerfilAtual();
		if (perfilAtual != null) {
			ContaBancaria conta = perfilAtual.criarConta(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
			perfilAtual.adicionarConta(conta);
		} else {
			if (telaCadastrarContaBancaria != null) {
				telaCadastrarContaBancaria.exibirMensagemErro("Nenhum perfil ativo na sessão.");
			}
			return;
		}

		if (telaCadastrarContaBancaria != null) {
			telaCadastrarContaBancaria.exibirMensagemConfirmacao();
		}
	}

	/**
	 * Cadastra conta da modalidade Carteira de Dinheiro (RF3.1, RN02, RN04).
	 */
	public void cadastrarContaBancaria(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		boolean validacao = ContaBancaria.validarEntradas(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);

		if (!validacao) {
			if (telaCadastrarContaBancaria != null) {
				telaCadastrarContaBancaria.exibirMensagemErro("Dados da carteira inválidos ou saldo inicial negativo.");
			}
			return;
		}

		PerfilDeUsuario perfilAtual = getPerfilAtual();
		if (perfilAtual != null) {
			ContaBancaria conta = perfilAtual.criarConta(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);
			perfilAtual.adicionarConta(conta);
		} else {
			if (telaCadastrarContaBancaria != null) {
				telaCadastrarContaBancaria.exibirMensagemErro("Nenhum perfil ativo na sessão.");
			}
			return;
		}

		if (telaCadastrarContaBancaria != null) {
			telaCadastrarContaBancaria.exibirMensagemConfirmacao();
		}
	}

	public void cadastrarContaBancaria() {
		// Sobrecarga sem argumentos prevista no DCD
	}

	/**
	 * Inativa a conta bancária preservando histórico e saldos (RF3.6, RN05).
	 */
	public void inativarContaBancaria(String nomeDaContaBancaria) {
		//TODO: Isto de perfilAtual foi adicionado para o código funcionar, mas não está previsto no diagrama.
		PerfilDeUsuario perfilAtual = getPerfilAtual();

		if (perfilAtual != null) {
			ContaBancaria conta = perfilAtual.buscarContaBancaria(nomeDaContaBancaria);

			if (conta != null) {
				conta.setStatus("inativa");
			}
		}
	}

	/**
	 * Reativa a conta bancária preservando histórico e saldos (RF3.7, RN05).
	 */
	public void reativarContaBancaria(String nomeDaContaBancaria) {
		//TODO: Isto de perfilAtual foi adicionado para o código funcionar, mas não está previsto no diagrama.
		PerfilDeUsuario perfilAtual = getPerfilAtual();

		if (perfilAtual != null) {
			ContaBancaria conta = perfilAtual.buscarContaBancaria(nomeDaContaBancaria);

			if (conta != null) {
				conta.setStatus("ativa");
			}
		}
	}

	/**
	 * Consulta a conta bancária pelo nome através do PerfilDeUsuario e envia para exibição na tela (RF3.2).
	 */
	public void consultarContaBancaria(String nomeDaContaBancaria) {
		//TODO: Isto de perfilAtual foi adicionado para o código funcionar, mas não está previsto no diagrama.
		PerfilDeUsuario perfilAtual = getPerfilAtual();

		if (perfilAtual != null) {
			ContaBancaria conta = perfilAtual.buscarContaBancaria(nomeDaContaBancaria);

			if (telaConsultaContaBancaria != null) {
				telaConsultaContaBancaria.exibirContaBancaria(conta);
			}
		}
	}

	/**
	 * Busca a conta bancária no PerfilDeUsuario e envia para exibição na tela de edição (RF3.3).
	 */
	public void obterConta(String nomeDaContaBancaria) {
		//TODO: Isto de perfilAtual foi adicionado para o código funcionar, mas não está previsto no diagrama.
		PerfilDeUsuario perfilAtual = getPerfilAtual();

		if (perfilAtual != null) {
			ContaBancaria conta = perfilAtual.buscarContaBancaria(nomeDaContaBancaria);

			if (telaEditarContaBancaria != null) {
				telaEditarContaBancaria.exibirContaBancaria(conta);
			}
		}
	}

	/**
	 * Atualiza os dados de Conta Corrente ou Poupança (RF3.3, RN03, RN04).
	 */
	public void atualizarContaBancaria(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		boolean validacao = ContaBancaria.validarEntradas(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);

		if (!validacao) {
			if (telaEditarContaBancaria != null) {
				telaEditarContaBancaria.exibirMensagemErro("Dados bancários inválidos.");
			}
			return;
		}

		PerfilDeUsuario perfilAtual = getPerfilAtual();
		if (perfilAtual != null) {
			perfilAtual.atualizarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
		}

		if (telaEditarContaBancaria != null) {
			telaEditarContaBancaria.exibirMensagemConfirmacao();
		}
	}

	/**
	 * Atualiza os dados de Carteira de Dinheiro (RF3.3, RN02, RN04).
	 */
	public void atualizarContaBancaria(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		boolean validacao = ContaBancaria.validarEntradas(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);

		if (!validacao) {
			if (telaEditarContaBancaria != null) {
				telaEditarContaBancaria.exibirMensagemErro("Dados da carteira inválidos ou saldo inicial negativo.");
			}
			return;
		}

		PerfilDeUsuario perfilAtual = getPerfilAtual();
		if (perfilAtual != null) {
			perfilAtual.atualizarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);
		}

		if (telaEditarContaBancaria != null) {
			telaEditarContaBancaria.exibirMensagemConfirmacao();
		}
	}

	public void selecionarContaBancaria() {

	}

}
