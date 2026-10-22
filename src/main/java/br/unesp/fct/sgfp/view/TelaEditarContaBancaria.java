package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.Poupanca;
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
import java.util.Locale;

/**
 * Tela de Edição de Conta Bancária derivada do Astah (Seção 3.1.3).
 */
public class TelaEditarContaBancaria {

	private ControladorContaBancaria controlador;
	private ContaBancaria contaCarregada;
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

	// Campos de formulário para edição (e compatibilidade headless com testes unitários)
	private String nomeDaContaBancaria;
	private String tipo;
	private Float saldo;
	private java.util.Date dataDeReferencia;
	private String nomeDoBanco;
	private int numeroDaAgencia;
	private int numeroDaConta;

	public TelaEditarContaBancaria() {
		this.controlador = new ControladorContaBancaria();
		this.controlador.setTelaEditarContaBancaria(this);
	}

	public TelaEditarContaBancaria(ControladorContaBancaria controlador) {
		this.controlador = controlador;
		if (this.controlador != null) {
			this.controlador.setTelaEditarContaBancaria(this);
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

			// Alterna visibilidade dos dados bancários (RN02/RN03)
			comboTipo.valueProperty().addListener((obs, antigo, novo) -> {
				if (boxDadosBancarios != null) {
					boolean ehCarteira = "Carteira de Dinheiro".equalsIgnoreCase(novo);
					boxDadosBancarios.setVisible(!ehCarteira);
					boxDadosBancarios.setManaged(!ehCarteira);
				}
			});
		}

		if (pickerData != null && pickerData.getValue() == null) {
			pickerData.setValue(LocalDate.now());
		}
	}

	public void preencherCamposParaEdicao(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia,
			String nomeDoBanco, int numeroDaAgencia, int numeroDaConta) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		this.tipo = tipo;
		this.saldo = saldo;
		this.dataDeReferencia = dataDeReferencia;
		this.nomeDoBanco = nomeDoBanco;
		this.numeroDaAgencia = numeroDaAgencia;
		this.numeroDaConta = numeroDaConta;
	}

	public void preencherCamposParaEdicao(String nomeDaContaBancaria, String tipo, Float saldo, java.util.Date dataDeReferencia) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;
		this.tipo = tipo;
		this.saldo = saldo;
		this.dataDeReferencia = dataDeReferencia;
		this.nomeDoBanco = null;
		this.numeroDaAgencia = 0;
		this.numeroDaConta = 0;
	}

	public ContaBancaria getContaCarregada() {
		return contaCarregada;
	}

	public String getMensagemErro() {
		return labelErro != null ? labelErro.getText() : mensagemErro;
	}

	public boolean isConfirmado() {
		return confirmado;
	}


	/**
	 * Solicita os dados atuais da conta para pré-carregar na tela de edição (RF3.3).
	 */
	public void editarContaBancaria(String nomeDaContaBancaria) {
		this.nomeDaContaBancaria = nomeDaContaBancaria;	
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaEditarContaBancaria(this);
		}
		controlador.obterConta(nomeDaContaBancaria);
	}

	/**
	 * Carrega os dados da conta na interface.
	 */
	public void exibirContaBancaria(ContaBancaria conta) {
		this.contaCarregada = conta;

		if (campoNome != null && conta != null) {
			campoNome.setText(conta.getNomeDaContaBancaria());
			this.nomeDaContaBancaria = conta.getNomeDaContaBancaria();

			if (comboTipo != null) {
				String t = conta.getTipo();
				if ("conta corrente".equalsIgnoreCase(t)) {
					comboTipo.setValue("Conta Corrente");
				} else if ("poupanca".equalsIgnoreCase(t)) {
					comboTipo.setValue("Poupança");
				} else {
					comboTipo.setValue("Carteira de Dinheiro");
				}
			}

			if (campoSaldo != null) {
				Float s = conta.getSaldo();
				campoSaldo.setText(s != null ? String.format(Locale.US, "%.2f", s) : "0.00");
			}

			if (pickerData != null && conta.getDataDeReferencia() != null) {
				pickerData.setValue(conta.getDataDeReferencia().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
			}

			if (conta instanceof ContaCorrente cc) {
				if (campoBanco != null) campoBanco.setText(cc.getNomeDoBanco());
				if (campoAgencia != null) campoAgencia.setText(String.valueOf(cc.getNumeroDaAgencia()));
				if (campoNumeroConta != null) campoNumeroConta.setText(String.valueOf(cc.getNumeroDaConta()));
			} else if (conta instanceof Poupanca cp) {
				if (campoBanco != null) campoBanco.setText(cp.getNomeDoBanco());
				if (campoAgencia != null) campoAgencia.setText(String.valueOf(cp.getNumeroDaAgencia()));
				if (campoNumeroConta != null) campoNumeroConta.setText(String.valueOf(cp.getNumeroDaConta()));
			}
		}
	}

	@FXML
	public void botaoSalvar() {
		editarContaBancaria();
	}

	/**
	 * Ação acionada pelo Usuário para submeter as alterações cadastrais da conta (RF3.3).
	 */
	@FXML
	public void editarContaBancaria() {
		if (controlador == null) {
			controlador = new ControladorContaBancaria();
			controlador.setTelaEditarContaBancaria(this);
		}

		if (campoNome != null && comboTipo != null) {
			extrairValoresInterface();
		}

		if (tipo != null && tipo.equalsIgnoreCase("carteira de dinheiro")) {
			controlador.atualizarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia);
		} else {
			controlador.atualizarContaBancaria(nomeDaContaBancaria, tipo, saldo, dataDeReferencia, nomeDoBanco, numeroDaAgencia, numeroDaConta);
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
	 * Exibe mensagem de confirmação após atualização da conta com sucesso.
	 */
	public void exibirMensagemConfirmacao() {
		this.confirmado = true;
		this.mensagemErro = null;
		if (labelErro != null) {
			labelErro.getStyleClass().removeAll("banner-error");
			if (!labelErro.getStyleClass().contains("banner-success")) {
				labelErro.getStyleClass().add("banner-success");
			}
			labelErro.setText("Conta atualizada com sucesso!");
		}
	}

}
