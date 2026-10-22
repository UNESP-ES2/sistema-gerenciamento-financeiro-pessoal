package br.unesp.fct.sgfp.model;

/**
 * Controle de sessão autenticada monousuário, derivado do Astah (DCD).
 * Atende aos requisitos RF1.3, RF1.3.4 (sessão ativa após cadastro) e RF1.7 (desconectar).
 */
public class Sessao {

	private static Sessao instancia;

	private boolean isAutenticado;
	private PerfilDeUsuario perfilAtivo;
	private ContaBancaria contaAtiva;
	private final java.util.List<ObservadorContaAtiva> observadores = new java.util.ArrayList<>();

	public Sessao() {
		this.isAutenticado = false;
	}

	/**
	 * Padrão Singleton para gerenciar a sessão única da aplicação desktop (RF1.2).
	 */
	public static synchronized Sessao getInstancia() {
		if (instancia == null) {
			instancia = new Sessao();
		}
		return instancia;
	}

	/**
	 * Estabelece a sessão como autenticada e associa o perfil ativo (RF1.3.4).
	 */
	public void autenticar(String nome, String senha) {
		this.isAutenticado = true;
		this.perfilAtivo = new PerfilDeUsuario(nome, senha);
	}

	/**
	 * Encerra a sessão ativa e descarta os dados em memória (RF1.7).
	 */
	public void desconectar() {
		this.isAutenticado = false;
		this.perfilAtivo = null;
		this.contaAtiva = null;
		this.observadores.clear();
	}

	public void adicionarObservador(ObservadorContaAtiva observador) {
		if (observador != null && !this.observadores.contains(observador)) {
			this.observadores.add(observador);
		}
	}

	public void removerObservador(ObservadorContaAtiva observador) {
		this.observadores.remove(observador);
	}

	public PerfilDeUsuario getPerfilAtivo() {
		return perfilAtivo;
	}

	public void setPerfilAtivo(PerfilDeUsuario perfilAtivo) {
		this.perfilAtivo = perfilAtivo;
		this.isAutenticado = (perfilAtivo != null);
	}

	public void setContaAtiva(ContaBancaria conta) {
		this.contaAtiva = conta;
		for (ObservadorContaAtiva obs : this.observadores) {
			obs.atualizarVisualizacao(conta);
		}
	}

	public void setContaAtiva() {
		this.contaAtiva = null;
	}

	public boolean isAutenticado() {
		return isAutenticado;
	}

	public ContaBancaria getContaAtiva() {
		return contaAtiva;
	}

}
