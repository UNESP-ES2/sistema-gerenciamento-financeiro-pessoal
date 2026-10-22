package br.unesp.fct.sgfp.view;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.CarteiraDeDinheiro;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.ObservadorContaAtiva;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.model.Poupanca;
import br.unesp.fct.sgfp.model.Sessao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Tela e Controlador FXML de exibição, seleção e gestão de contas bancárias (Dashboard).
 * Atua como Observador de Sessao para atualização visual automática (RF3.5, RF3.6, RF3.7, RN10).
 */
public class TelaExibirContasBancarias implements ObservadorContaAtiva {

	private ControladorContaBancaria controlador;
	private String nomeDaContaBancaria;
	private ContaBancaria contaSelecionada;
	private ContaBancaria contaVisualizada;
	private ContaCorrente contaCorrenteRef;

	// Injeções FXML para o Dashboard
	@FXML
	private Label labelNomeUsuario;

	@FXML
	private Label labelSaldoConsolidado;

	@FXML
	private Label labelNomeContaAtiva;

	@FXML
	private Label labelSaldoContaAtiva;

	@FXML
	private Label labelFeedback;

	@FXML
	private TextField campoBuscaConta;

	@FXML
	private TableView<ContaBancaria> tabelaContas;

	@FXML
	private TableColumn<ContaBancaria, String> colNome;

	@FXML
	private TableColumn<ContaBancaria, String> colTipo;

	@FXML
	private TableColumn<ContaBancaria, String> colDetalhes;

	@FXML
	private TableColumn<ContaBancaria, String> colSaldo;

	@FXML
	private TableColumn<ContaBancaria, String> colDataRef;

	@FXML
	private TableColumn<ContaBancaria, String> colStatus;

	private final NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
	private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

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

	@FXML
	public void initialize() {
		if (tabelaContas == null) {
			return; // Proteção contra modo headless
		}

		configurarColunas();

		// Duplo clique na linha seleciona a conta ativa imediatamente (RF3.5, CUE 3.1.2)
		tabelaContas.setRowFactory(tv -> {
			TableRow<ContaBancaria> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					ContaBancaria rowData = row.getItem();
					this.contaSelecionada = rowData;
					acaoSelecionarContaBancaria();
					exibirFeedback("Conta '" + rowData.getNomeDaContaBancaria() + "' selecionada como conta ativa da sessão!", "banner-success");
				}
			});
			return row;
		});

		atualizarTabelaEValores();
	}

	private void configurarColunas() {
		if (colNome != null) {
			colNome.setCellValueFactory(cell -> new SimpleStringProperty(
					cell.getValue() != null ? cell.getValue().getNomeDaContaBancaria() : ""));
		}

		if (colTipo != null) {
			colTipo.setCellValueFactory(cell -> new SimpleStringProperty(
					cell.getValue() != null ? formatarTipo(cell.getValue().getTipo()) : ""));
		}

		if (colDetalhes != null) {
			colDetalhes.setCellValueFactory(cell -> new SimpleStringProperty(
					formatarDetalhesBancarios(cell.getValue())));
		}

		if (colSaldo != null) {
			colSaldo.setCellValueFactory(cell -> {
				Float saldo = cell.getValue() != null ? cell.getValue().getSaldo() : 0.0f;
				return new SimpleStringProperty(formatoMoeda.format(saldo != null ? saldo : 0.0f));
			});
		}

		if (colDataRef != null) {
			colDataRef.setCellValueFactory(cell -> {
				java.util.Date data = cell.getValue() != null ? cell.getValue().getDataDeReferencia() : null;
				return new SimpleStringProperty(data != null ? formatoData.format(data) : "-");
			});
		}

		if (colStatus != null) {
			colStatus.setCellValueFactory(cell -> new SimpleStringProperty(
					cell.getValue() != null && cell.getValue().getStatus() != null ? cell.getValue().getStatus().toUpperCase() : "ATIVA"));

			colStatus.setCellFactory(column -> new TableCell<ContaBancaria, String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
						setStyle("");
					} else {
						setText(item);
						if ("ATIVA".equalsIgnoreCase(item) || "ATIVO".equalsIgnoreCase(item)) {
							setStyle("-fx-text-fill: #15803D; -fx-font-weight: bold;");
						} else {
							setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
						}
					}
				}
			});
		}
	}

	public void atualizarTabelaEValores() {
		PerfilDeUsuario perfil = Sessao.getInstancia().getPerfilAtivo();
		if (labelNomeUsuario != null && perfil != null) {
			labelNomeUsuario.setText(perfil.getNome());
		}

		double saldoTotal = 0.0;
		ObservableList<ContaBancaria> lista = FXCollections.observableArrayList();

		if (perfil != null && perfil.obterContas() != null) {
			for (ContaBancaria c : perfil.obterContas()) {
				lista.add(c);
				if (c.getSaldo() != null) {
					saldoTotal += c.getSaldo(); // Soma saldos de contas ativas e inativas (RN10)
				}
			}
		}

		if (tabelaContas != null) {
			tabelaContas.setItems(lista);
			tabelaContas.refresh();
		}

		if (labelSaldoConsolidado != null) {
			labelSaldoConsolidado.setText(formatoMoeda.format(saldoTotal));
		}

		// Atualiza card da Conta Ativa
		ContaBancaria ativa = Sessao.getInstancia().getContaAtiva();
		if (labelNomeContaAtiva != null) {
			if (ativa != null) {
				labelNomeContaAtiva.setText(ativa.getNomeDaContaBancaria() + " (" + formatarTipo(ativa.getTipo()) + ")");
			} else {
				labelNomeContaAtiva.setText("Nenhuma conta selecionada");
			}
		}

		if (labelSaldoContaAtiva != null) {
			if (ativa != null && ativa.getSaldo() != null) {
				labelSaldoContaAtiva.setText(formatoMoeda.format(ativa.getSaldo()));
			} else {
				labelSaldoContaAtiva.setText("-");
			}
		}

		if (lista.isEmpty()) {
			exibirFeedback("Nenhuma conta bancária cadastrada ainda. Clique em '+ Nova Conta Bancária' para cadastrar sua primeira conta ou carteira.", "banner-info");
		} else if (ativa == null && (labelFeedback == null || labelFeedback.getText() == null || labelFeedback.getText().isEmpty())) {
			exibirFeedback("Dica: Dê um duplo clique em uma conta da tabela para torná-la a conta ativa da sessão.", "banner-info");
		}
	}

	@FXML
	public void botaoEditarPerfil() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/unesp/fct/sgfp/modal-editar-perfil.fxml"));
			Parent raiz = loader.load();
			Stage modalStage = new Stage();
			modalStage.setTitle("SGFP - Editar Perfil do Titular");
			modalStage.initModality(Modality.WINDOW_MODAL);
			if (tabelaContas != null && tabelaContas.getScene() != null) {
				modalStage.initOwner(tabelaContas.getScene().getWindow());
			}
			modalStage.setScene(new Scene(raiz));
			modalStage.centerOnScreen();
			modalStage.showAndWait();

			atualizarTabelaEValores();
		} catch (IOException e) {
			System.err.println("Erro ao abrir modal de editar perfil: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Consulta a conta pelo nome informado, executando o fluxo do Astah (Seção 3.1.3 - consultarContaBancaria).
	 * Localiza a conta e a seleciona na tabela (RF3.2).
	 */
	@FXML
	public void botaoBuscarConta() {
		String nomeBusca = campoBuscaConta != null && campoBuscaConta.getText() != null ? campoBuscaConta.getText().trim() : "";
		if (nomeBusca.isEmpty()) {
			exibirFeedback("Digite o nome da conta que deseja consultar.", "banner-warning");
			return;
		}

		if (controlador == null) {
			controlador = new ControladorContaBancaria();
		}

		// Rastreabilidade com o diagrama do Astah 3.1.3 (consultarContaBancaria)
		TelaConsultaContaBancaria telaConsulta = new TelaConsultaContaBancaria(controlador);
		telaConsulta.preencherCampoBusca(nomeBusca);
		telaConsulta.iniciarConsultaDeContaBancaria();
		ContaBancaria contaLocalizada = telaConsulta.getContaExibida();

		if (contaLocalizada != null && tabelaContas != null) {
			for (ContaBancaria c : tabelaContas.getItems()) {
				if (c.getNomeDaContaBancaria().equalsIgnoreCase(contaLocalizada.getNomeDaContaBancaria())) {
					tabelaContas.getSelectionModel().select(c);
					tabelaContas.scrollTo(c);
					this.contaSelecionada = c;
					exibirFeedback("Conta '" + c.getNomeDaContaBancaria() + "' localizada com sucesso! Dê duplo clique ou use 'Selecionar' para ativá-la.", "banner-success");
					return;
				}
			}
		}

		exibirFeedback("Nenhuma conta encontrada com o nome '" + nomeBusca + "' no perfil atual.", "banner-warning");
	}

	/**
	 * Limpa o campo de busca de consulta e remove a seleção na tabela.
	 */
	@FXML
	public void botaoLimparBusca() {
		if (campoBuscaConta != null) {
			campoBuscaConta.clear();
		}
		if (tabelaContas != null) {
			tabelaContas.getSelectionModel().clearSelection();
		}
		this.contaSelecionada = null;
		if (labelFeedback != null) {
			labelFeedback.setText("");
			labelFeedback.getStyleClass().removeAll("banner-success", "banner-error", "banner-warning", "banner-info");
		}
	}

	@FXML
	public void botaoConsultarConta() {
		botaoBuscarConta();
	}

	@FXML
	public void botaoEditarConta() {
		ContaBancaria selecionada = tabelaContas != null ? tabelaContas.getSelectionModel().getSelectedItem() : null;
		if (selecionada == null) {
			exibirFeedback("Selecione uma conta na tabela para editar.", "banner-warning");
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/unesp/fct/sgfp/modal-editar-conta.fxml"));
			Parent raiz = loader.load();
			TelaEditarContaBancaria telaEditar = loader.getController();
			if (telaEditar != null) {
				telaEditar.editarContaBancaria(selecionada.getNomeDaContaBancaria());
			}

			Stage modalStage = new Stage();
			modalStage.setTitle("SGFP - Editar Conta: " + selecionada.getNomeDaContaBancaria());
			modalStage.initModality(Modality.WINDOW_MODAL);
			if (tabelaContas != null && tabelaContas.getScene() != null) {
				modalStage.initOwner(tabelaContas.getScene().getWindow());
			}
			modalStage.setScene(new Scene(raiz));
			modalStage.centerOnScreen();
			modalStage.showAndWait();

			atualizarTabelaEValores();
		} catch (IOException e) {
			System.err.println("Erro ao abrir modal de edição: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String formatarTipo(String tipo) {
		if (tipo == null) return "Conta";
		if (tipo.equalsIgnoreCase("conta corrente")) return "Conta Corrente";
		if (tipo.equalsIgnoreCase("poupanca")) return "Poupança";
		if (tipo.equalsIgnoreCase("carteira de dinheiro")) return "Carteira de Dinheiro";
		return tipo;
	}

	private String formatarDetalhesBancarios(ContaBancaria conta) {
		if (conta instanceof ContaCorrente cc) {
			return cc.getNomeDoBanco() + " | Ag: " + cc.getNumeroDaAgencia() + " | C/C: " + cc.getNumeroDaConta();
		} else if (conta instanceof Poupanca cp) {
			return cp.getNomeDoBanco() + " | Ag: " + cp.getNumeroDaAgencia() + " | C/P: " + cp.getNumeroDaConta();
		} else if (conta instanceof CarteiraDeDinheiro) {
			return "Dinheiro em Espécie";
		}
		return "-";
	}

	@FXML
	public void botaoDefinirComoAtiva() {
		ContaBancaria selecionada = tabelaContas != null ? tabelaContas.getSelectionModel().getSelectedItem() : null;
		if (selecionada != null) {
			this.contaSelecionada = selecionada;
			acaoSelecionarContaBancaria();
			exibirFeedback("Conta '" + selecionada.getNomeDaContaBancaria() + "' selecionada como conta ativa da sessão!", "banner-success");
		} else {
			exibirFeedback("Selecione uma conta na tabela para ativá-la na sessão.", "banner-warning");
		}
	}

	@FXML
	public void botaoInativarReativar() {
		ContaBancaria selecionada = tabelaContas != null ? tabelaContas.getSelectionModel().getSelectedItem() : null;
		if (selecionada != null) {
			String status = selecionada.getStatus();
			if ("inativa".equalsIgnoreCase(status) || "inativo".equalsIgnoreCase(status)) {
				reativarContaBancaria(selecionada.getNomeDaContaBancaria());
				exibirFeedback("Conta '" + selecionada.getNomeDaContaBancaria() + "' reativada com sucesso!", "banner-success");
			} else {
				if (controlador == null) {
					controlador = new ControladorContaBancaria();
				}
				controlador.inativarContaBancaria(selecionada.getNomeDaContaBancaria());
				exibirFeedback("Conta '" + selecionada.getNomeDaContaBancaria() + "' inativada com sucesso. Saldo e histórico foram preservados.", "banner-info");
			}
			atualizarTabelaEValores();
		} else {
			exibirFeedback("Selecione uma conta na tabela para alterar seu status.", "banner-warning");
		}
	}

	@FXML
	public void botaoNovaConta() {
		abrirModalCadastroConta();
	}

	private void abrirModalCadastroConta() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/unesp/fct/sgfp/modal-cadastrar-conta.fxml"));
			Parent raiz = loader.load();
			Stage modalStage = new Stage();
			modalStage.setTitle("SGFP - Cadastrar Nova Conta Bancária");
			modalStage.initModality(Modality.WINDOW_MODAL);
			if (tabelaContas != null && tabelaContas.getScene() != null) {
				modalStage.initOwner(tabelaContas.getScene().getWindow());
			}
			modalStage.setScene(new Scene(raiz));
			modalStage.centerOnScreen();
			modalStage.showAndWait();

			atualizarTabelaEValores();
		} catch (IOException e) {
			System.err.println("Erro ao abrir modal de cadastro: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	public void botaoDesconectar() {
		Sessao.getInstancia().desconectar();
		if (NavegadorCenas.getPalcoPrincipal() != null) {
			NavegadorCenas.irParaLogin();
		}
	}

	private void exibirFeedback(String mensagem, String estilo) {
		if (labelFeedback != null) {
			labelFeedback.getStyleClass().removeAll("banner-success", "banner-error", "banner-warning", "banner-info");
			labelFeedback.getStyleClass().add(estilo);
			labelFeedback.setText(mensagem);
		}
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
		if (tabelaContas != null) {
			atualizarTabelaEValores();
		}
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
