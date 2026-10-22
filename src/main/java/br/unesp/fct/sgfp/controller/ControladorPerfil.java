package br.unesp.fct.sgfp.controller;

import br.unesp.fct.sgfp.model.RepositorioDeCredenciais;
import br.unesp.fct.sgfp.model.Sessao;

import br.unesp.fct.sgfp.view.InputEdicaoSenha;
import br.unesp.fct.sgfp.view.TelaAutenticacaoPerfil;
import br.unesp.fct.sgfp.view.TelaCadastrarPerfil;
import br.unesp.fct.sgfp.view.TelaEditarPerfil;

/**
 * Controlador GRASP do caso de uso de Perfil e Governança, derivado do Astah.
 * Coordena as operações entre a View, o RepositorioDeCredenciais (SQLite) e a Sessao.
 */
public class ControladorPerfil {

	private final RepositorioDeCredenciais repositorio;
	private final Sessao sessao;
	private TelaCadastrarPerfil telaCadastrarPerfil;
	private TelaAutenticacaoPerfil telaAutenticacaoPerfil;
	private TelaEditarPerfil telaEditarPerfil;
	private InputEdicaoSenha inputEdicaoSenha = new InputEdicaoSenha();

	public ControladorPerfil() {
		this.repositorio = new RepositorioDeCredenciais();
		this.sessao = Sessao.getInstancia();
	}

	public ControladorPerfil(RepositorioDeCredenciais repositorio, Sessao sessao) {
		this.repositorio = repositorio;
		this.sessao = sessao;
	}

	public ControladorPerfil(TelaCadastrarPerfil telaCadastrarPerfil) {
		this();
		this.telaCadastrarPerfil = telaCadastrarPerfil;
	}

	public void setTelaCadastrarPerfil(TelaCadastrarPerfil telaCadastrarPerfil) {
		this.telaCadastrarPerfil = telaCadastrarPerfil;
	}

	public void setTelaAutenticacaoPerfil(TelaAutenticacaoPerfil telaAutenticacaoPerfil) {
		this.telaAutenticacaoPerfil = telaAutenticacaoPerfil;
	}

	public void setTelaEditarPerfil(TelaEditarPerfil telaEditarPerfil) {
		this.telaEditarPerfil = telaEditarPerfil;
	}

	public void setInputEdicaoSenha(InputEdicaoSenha inputEdicaoSenha) {
		this.inputEdicaoSenha = inputEdicaoSenha;
	}

	/**
	 * Cadastra o perfil local no SQLite e estabelece a sessão ativa (RF1.1, RF1.2, RF1.3.4).
	 */
	public boolean cadastrarPerfil(String nome, String senha) {
		boolean validacao = validarEntradas(nome, senha);

		if (!validacao) {
			if (telaCadastrarPerfil != null) {
				telaCadastrarPerfil.falhaDeCadastro();
			}
			return false;
		}

		boolean verificar = repositorio.criarPerfil(nome, senha);

		if (verificar) {
			sessao.autenticar(nome, senha);
		}

		return verificar;
	}

	/**
	 * Valida as entradas de nome (RF1.1.1) e senha (RF1.1.2).
	 */
	public boolean validarEntradas(String nome, String senha) {
		if (nome == null || senha == null) {
			return false;
		}

		// RF1.1.1: somente letras, tamanho 4..18, não nulo, sem números ou especiais
		if (nome.length() < 4 || nome.length() > 18 || !nome.matches("^[a-zA-ZÀ-ÿ]+$")) {
			return false;
		}

		// RF1.1.2: tamanho 4..18, sem espaços
		return validarSenha(senha);
	}

	/**
	 * Valida se a senha atende ao RF1.1.2 (tamanho entre 4 e 18 caracteres e sem espaços).
	 */
	public boolean validarSenha(String senha) {
		if (senha == null) {
			return false;
		}
		return senha.length() >= 4 && senha.length() <= 18 && !senha.contains(" ");
	}

	/**
	 * Autentica o usuário no perfil local e ativa a Sessão (RF1.3.2, RF1.5).
	 */
	public boolean autenticarPerfil(String nome, String senha) {
		if (nome == null || senha == null) {
			if (telaAutenticacaoPerfil != null) {
				telaAutenticacaoPerfil.falhaDeAutenticacao();
			}
			return false;
		}

		boolean verificar = repositorio.verificarCredenciais(nome, senha);

		if (!verificar) {
			if (telaAutenticacaoPerfil != null) {
				telaAutenticacaoPerfil.falhaDeAutenticacao();
			}
			// RF1.5: falha de autenticação genérica sem indicar qual credencial está errada
			return false;
		}

		sessao.autenticar(nome, senha);

		return verificar;
	}

	/**
	 * Altera os dados de nome e senha mediante validação sintática (RF1.6).
	 */
	public boolean editarPerfil(String nome, String senha) {
		boolean validacao = validarEntradas(nome, senha);

		if (!validacao) {
			if (telaEditarPerfil != null) {
				telaEditarPerfil.falhaDeEdicao();
			}
			return false;
		}

		boolean verificar = repositorio.salvarCredenciais(nome, senha);

		if (verificar && sessao.isAutenticado()) {
			sessao.getPerfilAtivo().setNome(nome);
			sessao.getPerfilAtivo().setSenha(senha);
		}

		return verificar;
	}

	/**
	 * Valida a existência do usuário para o fluxo de recuperação de senha (RF1.8).
	 */
	public boolean esqueceuSenha(String nome) {
		if (nome == null || nome.isBlank()) {
			return false;
		}

		boolean verificar = repositorio.verificarNome(nome);

		if (verificar) {
			if (inputEdicaoSenha == null) {
				inputEdicaoSenha = new InputEdicaoSenha();
			}
			String senha = inputEdicaoSenha.editarSenha();

			boolean validacao = validarSenha(senha);

			if (!validacao) {
				if (telaAutenticacaoPerfil != null) {
					telaAutenticacaoPerfil.falhaDeEdicao();
				}
				return false;
			}

			repositorio.salvarSenha(senha);
			sessao.autenticar(nome, senha);

			return true;
		}

		return false;
	}

	/**
	 * Encerra a sessão ativa e descarta os dados exibidos (RF1.7).
	 */
	public void botaoDesconectar() {
		sessao.desconectar();
	}

}
