package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Tela e Controlador FXML de cadastro de novas contas bancárias e carteiras (RF3.1, RN02, RN03, RN04).
 */
public class TelaCadastrarContaBancaria {

	private ControladorContaBancaria controlador;
	private String mensagemErro;
	private boolean confirmado = false;

	// Injeções FXML
	@FXML
	private TextField campoNome;

	@FXML
	private ComboBox<String> comboTipo;

	@FXML
	private TextField campoSaldo;

	@FXML
	private DatePicker pickerData;

	@FXML
	private VBox boxDadosBancarios;

	@FXML
	private TextField campoBanco;

	@FXML
	private TextField campoAgencia;

	@FXML
	private TextField campoNumeroConta;

	@FXML
	private Label labelErro;

	// Campos de entrada para compatibilidade headless com os testes unitários
	private String nomeDaContaBancaria;
	private String tipo;
	private Float saldo;
	private java.util.Date dataDeReferencia;
	private String nomeDoBanco;
	private int numeroDaAgencia;
	private int numeroDaConta;

	public TelaCadastrarContaBancaria() {
		this.controlador = new ControladorContaBancaria();
		this.controlador.setTelaCadastrarContaBancaria(this);
	}

	public TelaCadastrarContaBancaria(ControladorContaBancaria controlador) {
		this.controlador = controlador;
		if (this.controlador != null) {
			this.controlador.setTelaCadastrarContaBancaria(this);
		}
	}

	@FXML
	public void initialize() {
		if (comboTipo != null) {
			comboTipo.setItems(FXCollections.observableArrayList(
					"Conta Corrente",
					"Poupança",
					"Carteira de Dinheiro"
			));
			comboTipo.getSelectionModel().selectFirst();

			// Alterna visibilidade dos dados bancários (RN02/RN03)
			comboTipo.valueProperty().addListener((obs, antigo, novo) -> {
				if (boxDadosBancarios != null) {
					boolean ehCarteira = "Carteira de Dinheiro".equalsIgnoreCase(novo);
					boxDadosBancarios.setVisible(!ehCarteira);
					boxDadosBancarios.setManaged(!ehCarteira);
				}
			});
		}

		if (pickerData != null) {
			pickerData.setValue(LocalDate.now());
		}
	}

	public void preencherCampos(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		this.tipo = tipo;
		this.saldo = saldo;
		this.dataDeReferencia = dataDeReferencia;
		this.nomeDoBanco = nomeDoBanco;
		this.numeroDaAgencia = numeroDaAgencia;
		this.numeroDaConta = numeroDaConta;
	}

	public void preencherCampos(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		this.tipo = tipo;
		this.saldo = saldo;
		this.dataDeReferencia = dataDeReferencia;
		this.nomeDoBanco = null;
		this.numeroDaAgencia = 0;
		this.numeroDaConta = 0;
	}

	public String getMensagemErro() {
		return labelErro != null ? labelErro.getText() : mensagemErro;
	}

	public boolean isConfirmado() {
		return confirmado;
	}

	/**
	 * Ação de cadastro disparada pelo Usuário na tela ou teste.
	 * Delega para o ControladorContaBancaria conforme o tipo da conta (RF3.1).
	 */
	@FXML
	public void cadastrarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaCadastrarContaBancaria(this);
		}

		// Se executado em interface gráfica com FXML conectado
		if (campoNome != null && comboTipo != null) {
			extrairValoresInterface();
		}

		if (tipo != null && tipo.equalsIgnoreCase("carteira de dinheiro")) {
			controlador.cadastrarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);
		} else {
			controlador.cadastrarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
		}

		if (confirmado && campoNome != null && campoNome.getScene() != null && campoNome.getScene().getWindow() != null) {
			((Stage) campoNome.getScene().getWindow()).close();
		}
	}

	private void extrairValoresInterface() {
		this.nomeDaContaBancaria = campoNome.getText() != null ? campoNome.getText().trim() : "";
		String tipoSelecionado = comboTipo.getValue();
		if ("Conta Corrente".equalsIgnoreCase(tipoSelecionado)) {
			this.tipo = "conta corrente";
		} else if ("Poupança".equalsIgnoreCase(tipoSelecionado)) {
			this.tipo = "poupanca";
		} else {
			this.tipo = "carteira de dinheiro";
		}

		try {
			String saldoTexto = campoSaldo != null && campoSaldo.getText() != null ? campoSaldo.getText().trim() : "";
			this.saldo = !saldoTexto.isEmpty() ? Float.parseFloat(saldoTexto.replace(",", ".")) : 0.0f;
		} catch (Exception e) {
			this.saldo = 0.0f;
		}

		if (pickerData != null && pickerData.getValue() != null) {
			this.dataDeReferencia = Date.from(pickerData.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		} else {
			this.dataDeReferencia = new Date();
		}

		if ("carteira de dinheiro".equalsIgnoreCase(this.tipo)) {
			this.nomeDoBanco = null;
			this.numeroDaAgencia = 0;
			this.numeroDaConta = 0;
		} else {
			this.nomeDoBanco = campoBanco != null && campoBanco.getText() != null ? campoBanco.getText().trim() : "";
			try {
				String agTexto = campoAgencia != null && campoAgencia.getText() != null ? campoAgencia.getText().trim() : "";
				String agLimpo = agTexto.replaceAll("[^0-9]", "");
				this.numeroDaAgencia = !agLimpo.isEmpty() ? Integer.parseInt(agLimpo) : -1;
			} catch (Exception e) {
				this.numeroDaAgencia = -1;
			}
			try {
				String ccTexto = campoNumeroConta != null && campoNumeroConta.getText() != null ? campoNumeroConta.getText().trim() : "";
				String ccLimpo = ccTexto.replaceAll("[^0-9]", "");
				this.numeroDaConta = !ccLimpo.isEmpty() ? Integer.parseInt(ccLimpo) : -1;
			} catch (Exception e) {
				this.numeroDaConta = -1;
			}
		}
	}

	@FXML
	public void botaoCancelar() {
		if (campoNome != null && campoNome.getScene() != null && campoNome.getScene().getWindow() != null) {
			((Stage) campoNome.getScene().getWindow()).close();
		}
	}

	/**
	 * Exibe mensagem de erro na tela quando os dados informados forem inválidos.
	 */
	public void exibirMensagemErro(String mensagem) {
		this.mensagemErro = mensagem;
		this.confirmado = false;
		if (labelErro != null) {
			labelErro.getStyleClass().removeAll("banner-success");
			if (!labelErro.getStyleClass().contains("banner-error")) {
				labelErro.getStyleClass().add("banner-error");
			}
			labelErro.setText(mensagem);
		}
	}

	/**
	 * Exibe mensagem de confirmação após cadastro da conta com sucesso.
	 */
	public void exibirMensagemConfirmacao() {
		this.confirmado = true;
		this.mensagemErro = null;
		if (labelErro != null) {
			labelErro.getStyleClass().removeAll("banner-error");
			if (!labelErro.getStyleClass().contains("banner-success")) {
				labelErro.getStyleClass().add("banner-success");
			}
			labelErro.setText("Conta cadastrada com sucesso!");
		}
	}

}
