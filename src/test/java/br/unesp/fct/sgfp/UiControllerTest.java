package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.controller.ControladorPerfil;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaAutenticacaoPerfil;
import br.unesp.fct.sgfp.view.TelaCadastrarContaBancaria;
import br.unesp.fct.sgfp.view.TelaCadastrarPerfil;
import br.unesp.fct.sgfp.view.TelaExibirContasBancarias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suíte de testes unitários para a Camada de Apresentação (View Controllers).
 * Valida blindagem contra NullPointerException, entradas vazias, espaços indevidos e fluxos de erro.
 */
public class UiControllerTest {

	@BeforeEach
	public void setUp() {
		Sessao.getInstancia().desconectar();
	}

	@Test
	@DisplayName("TelaCadastrarPerfil: Deve bloquear entradas vazias sem lançar NullPointerException")
	public void testCadastrarPerfilComCamposVazios() {
		ControladorPerfil controlador = new ControladorPerfil();
		TelaCadastrarPerfil tela = new TelaCadastrarPerfil(controlador);

		// Disparo com campos vazios (simula clique em Salvar sem preencher nada)
		tela.preencherCampos("", "", "");
		assertDoesNotThrow(() -> tela.botaoCadastrar(), "O cadastro com campos vazios não deve lançar exceção.");
		assertEquals("Falha no cadastro: entradas inválidas.", tela.getMensagem());
	}

	@Test
	@DisplayName("TelaCadastrarPerfil: Deve rejeitar nome de usuário contendo espaços (RF1.1.1)")
	public void testCadastrarPerfilComEspacosNoNome() {
		ControladorPerfil controlador = new ControladorPerfil();
		TelaCadastrarPerfil tela = new TelaCadastrarPerfil(controlador);

		tela.preencherCampos("Nome Com Espaco", "senha123", "senha123");
		tela.botaoCadastrar();
		assertEquals("Falha no cadastro: entradas inválidas.", tela.getMensagem());
	}

	@Test
	@DisplayName("TelaCadastrarPerfil: Deve rejeitar senhas divergentes na confirmação (RF1.1)")
	public void testCadastrarPerfilComSenhasDivergentes() {
		ControladorPerfil controlador = new ControladorPerfil();
		TelaCadastrarPerfil tela = new TelaCadastrarPerfil(controlador);

		tela.preencherCampos("usuarioValido", "senha123", "outraSenha456");
		tela.botaoCadastrar();
		assertEquals("Falha no cadastro: entradas inválidas.", tela.getMensagem());
	}

	@Test
	@DisplayName("TelaAutenticacaoPerfil: Deve bloquear login com campos vazios sem lançar NullPointerException")
	public void testAutenticacaoComCamposVazios() {
		ControladorPerfil controlador = new ControladorPerfil();
		TelaAutenticacaoPerfil tela = new TelaAutenticacaoPerfil(controlador);

		tela.preencherCampos("", "");
		assertDoesNotThrow(() -> tela.botaoAutenticar(), "Autenticação vazia não deve lançar exceção.");
		assertEquals("Usuário ou senha inválidos.", tela.getMensagem());
	}

	@Test
	@DisplayName("TelaAutenticacaoPerfil: Deve alertar quando Esqueci Senha for acionado sem nome informado")
	public void testEsqueciSenhaSemNomeInformado() {
		ControladorPerfil controlador = new ControladorPerfil();
		TelaAutenticacaoPerfil tela = new TelaAutenticacaoPerfil(controlador);

		tela.preencherCampos("", "");
		assertDoesNotThrow(() -> tela.botaoEsqueceuSenha());
		assertEquals("Informe seu nome de usuário no campo acima para redefinir a senha.", tela.getMensagem());
	}

	@Test
	@DisplayName("TelaCadastrarContaBancaria: Deve rejeitar dados inválidos/vazios sem lançar NullPointerException")
	public void testCadastrarContaComCamposVazios() {
		ControladorContaBancaria controlador = new ControladorContaBancaria();
		TelaCadastrarContaBancaria tela = new TelaCadastrarContaBancaria(controlador);

		// Disparo com campos vazios (simula clique em Salvar sem preenchimento)
		assertDoesNotThrow(() -> tela.cadastrarContaBancaria());
		assertFalse(tela.isConfirmado());
		assertNotNull(tela.getMensagemErro());
	}

	@Test
	@DisplayName("TelaExibirContasBancarias: Deve tratar ativação/inativação sem seleção de linha de forma segura")
	public void testExibirContasAcoesSemSelecao() {
		TelaExibirContasBancarias tela = new TelaExibirContasBancarias();

		// Sem selecionar conta
		assertDoesNotThrow(() -> tela.botaoDefinirComoAtiva());
		assertDoesNotThrow(() -> tela.botaoInativarReativar());
		assertNull(tela.getContaSelecionada());
	}

	@Test
	@DisplayName("TelaExibirContasBancarias: Deve inativar e reativar conta alternando status preservando saldo (RN05)")
	public void testExibirContasInativarEReativar() {
		PerfilDeUsuario perfil = new PerfilDeUsuario("titular", "senha123");
		Sessao.getInstancia().autenticar("titular", "senha123");
		PerfilDeUsuario perfilAtivo = Sessao.getInstancia().getPerfilAtivo();

		ContaCorrente cc = ContaCorrente.criar("Inter", "conta corrente", 1500.0f, new Date(), "Inter", 1, 100);
		perfilAtivo.adicionarConta(cc);

		TelaExibirContasBancarias tela = new TelaExibirContasBancarias();
		tela.setNomeDaContaBancaria("Inter");

		// Inativa
		ControladorContaBancaria ctrl = new ControladorContaBancaria(perfilAtivo);
		ctrl.inativarContaBancaria("Inter");
		assertEquals("inativa", cc.getStatus());
		assertEquals(1500.0f, cc.getSaldo(), "Saldo deve ser preservado ao inativar (RN05).");

		// Reativa
		tela.reativarContaBancaria("Inter");
		assertEquals("ativa", cc.getStatus());
		assertEquals(1500.0f, cc.getSaldo(), "Saldo deve ser preservado ao reativar (RN05).");
	}

}
