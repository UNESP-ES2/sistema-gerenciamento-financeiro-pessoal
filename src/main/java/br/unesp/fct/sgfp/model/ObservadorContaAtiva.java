package br.unesp.fct.sgfp.model;

/**
 * Interface de Observer para notificações de alteração de estado da Sessão (Padrão Observer - Aula 03).
 * Utilizada para atualizar telas quando a conta ativa é alterada.
 */
public interface ObservadorContaAtiva {

	/**
	 * Notifica o observador sobre a nova conta ativa selecionada (RF3.5).
	 *
	 * @param conta a conta bancária agora ativa na sessão
	 */
	void atualizarVisualizacao(ContaBancaria conta);

}
