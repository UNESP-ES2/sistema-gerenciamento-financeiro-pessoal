package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.Poupanca;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Tela de Consulta de Conta Bancária derivada do Astah (Seção 3.1.3).
 */
public class TelaConsultaContaBancaria {

	@FXML
	private Label labelNome;

	@FXML
	private Label labelTipo;

	@FXML
	private Label labelSaldo;

	@FXML
	private Label labelData;

	@FXML
	private Label labelStatus;

	@FXML
	private VBox boxDetalhesBancarios;

	@FXML
	private Label labelBanco;

	@FXML
	private Label labelAgencia;

	@FXML
	private Label labelConta;

	private ControladorContaBancaria controlador;
	private String nomeDaContaBancaria;
	private ContaBancaria contaExibida;

	private final NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
	private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

	public TelaConsultaContaBancaria() {
		this.controlador = new ControladorContaBancaria();
		this.controlador.setTelaConsultaContaBancaria(this);
	}

	public TelaConsultaContaBancaria(ControladorContaBancaria controlador) {
		this.controlador = controlador;
		if (this.controlador != null) {
			this.controlador.setTelaConsultaContaBancaria(this);
		}
	}

	public void preencherCampoBusca(String nomeDaContaBancaria) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
	}

	public String getNomeDaContaBancaria() {
		return nomeDaContaBancaria;
	}

	public ContaBancaria getContaExibida() {
		return contaExibida;
	}

	/**
	 * Ponto de entrada acionado pelo usuário para iniciar a consulta na tela.
	 */
	public void iniciarConsultaDeContaBancaria() {
		consultarContaBancaria();
	}

	/**
	 * Dispara a consulta da conta no ControladorContaBancaria com o nome informado na tela (RF3.2).
	 */
	public void consultarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaConsultaContaBancaria(this);
		}
		controlador.consultarContaBancaria(this.nomeDaContaBancaria);
	}

	/**
	 * Recebe a conta localizada pelo controlador e armazena para exibição na tela.
	 */
	public void exibirContaBancaria(ContaBancaria conta) {
		this.contaExibida = conta;

		if (labelNome != null && conta != null) {
			labelNome.setText(conta.getNomeDaContaBancaria());
			if (labelTipo != null) {
				labelTipo.setText(formatarTipo(conta.getTipo()));
			}
			if (labelSaldo != null) {
				Float s = conta.getSaldo();
				labelSaldo.setText(formatoMoeda.format(s != null ? s : 0.0f));
			}
			if (labelData != null) {
				java.util.Date d = conta.getDataDeReferencia();
				labelData.setText(d != null ? formatoData.format(d) : "-");
			}
			if (labelStatus != null) {
				String st = conta.getStatus();
				if ("ativa".equalsIgnoreCase(st) || "ativo".equalsIgnoreCase(st)) {
					labelStatus.setText("ATIVA");
					labelStatus.getStyleClass().removeAll("badge-inactive");
					if (!labelStatus.getStyleClass().contains("badge-active")) {
						labelStatus.getStyleClass().add("badge-active");
					}
				} else {
					labelStatus.setText("INATIVA");
					labelStatus.getStyleClass().removeAll("badge-active");
					if (!labelStatus.getStyleClass().contains("badge-inactive")) {
						labelStatus.getStyleClass().add("badge-inactive");
					}
				}
			}

			if (boxDetalhesBancarios != null) {
				if (conta instanceof ContaCorrente cc) {
					boxDetalhesBancarios.setVisible(true);
					boxDetalhesBancarios.setManaged(true);
					if (labelBanco != null) labelBanco.setText(cc.getNomeDoBanco());
					if (labelAgencia != null) labelAgencia.setText(String.valueOf(cc.getNumeroDaAgencia()));
					if (labelConta != null) labelConta.setText(String.valueOf(cc.getNumeroDaConta()));
				} else if (conta instanceof Poupanca cp) {
					boxDetalhesBancarios.setVisible(true);
					boxDetalhesBancarios.setManaged(true);
					if (labelBanco != null) labelBanco.setText(cp.getNomeDoBanco());
					if (labelAgencia != null) labelAgencia.setText(String.valueOf(cp.getNumeroDaAgencia()));
					if (labelConta != null) labelConta.setText(String.valueOf(cp.getNumeroDaConta()));
				} else {
					boxDetalhesBancarios.setVisible(false);
					boxDetalhesBancarios.setManaged(false);
				}
			}
		}
	}

	private String formatarTipo(String tipo) {
		if (tipo == null) return "Conta";
		if (tipo.equalsIgnoreCase("conta corrente")) return "Conta Corrente";
		if (tipo.equalsIgnoreCase("poupanca")) return "Poupança";
		if (tipo.equalsIgnoreCase("carteira de dinheiro")) return "Carteira de Dinheiro";
		return tipo;
	}

	@FXML
	public void botaoFechar() {
		if (labelNome != null && labelNome.getScene() != null && labelNome.getScene().getWindow() != null) {
			((Stage) labelNome.getScene().getWindow()).close();
		}
	}

}
