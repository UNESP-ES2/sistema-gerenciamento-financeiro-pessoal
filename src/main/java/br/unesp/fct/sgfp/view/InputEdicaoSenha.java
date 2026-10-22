package br.unesp.fct.sgfp.view;

import javafx.scene.control.TextInputDialog;
import java.util.Optional;

/**
 * Caixa de diálogo para inserção da nova senha no fluxo de recuperação de acesso.
 * Representa a lifeline ': InputEdicaoSenha' no Diagrama de Colaboração 'Esqueci Senha'.
 */
public class InputEdicaoSenha {

	private String novaSenha;

	public InputEdicaoSenha() {
	}

	public InputEdicaoSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	/**
	 * Abre caixa de diálogo Java para capturar a nova senha (ou retorna valor programático em testes headless).
	 */
	public String editarSenha() {
		if (this.novaSenha != null) {
			return this.novaSenha;
		}

		try {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Redefinir Senha");
			dialog.setHeaderText("Recuperação de Acesso");
			dialog.setContentText("Digite a nova senha:");
			Optional<String> result = dialog.showAndWait();
			return result.orElse("");
		} catch (Exception e) {
			// Em ambiente headless ou sem toolkit gráfico
			return this.novaSenha != null ? this.novaSenha : "";
		}
	}

}
