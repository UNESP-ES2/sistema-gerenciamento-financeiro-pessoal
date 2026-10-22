package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.CarteiraDeDinheiro;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.model.Poupanca;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaCadastrarContaBancaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Diagrama de Colaboração 'cadastrarContaBancaria' (Seção 3.1.3).
 * Valida os fluxos felizes e de exceção das modalidades polimórficas (RF3.1, RN02, RN03, RN04).
 */
public class CadastrarContaBancariaTest {

	private PerfilDeUsuario perfil;
	private ControladorContaBancaria controlador;
	private TelaCadastrarContaBancaria tela;

	@BeforeEach
	public void setUp() {
		perfil = new PerfilDeUsuario("Gabriel", "senha123");
		Sessao.getInstancia().autenticar("Gabriel", "senha123");

		controlador = new ControladorContaBancaria(perfil);
		tela = new TelaCadastrarContaBancaria(controlador);
	}

	@Test
	@DisplayName("Deve cadastrar Conta Corrente com sucesso (RF3.1, RN03, RN04)")
	public void testCadastrarContaCorrenteSucesso() {
		Date hoje = new Date();
		tela.preencherCampos("Conta Principal", "conta corrente", 1500.0f, hoje, "Banco do Brasil", 1234, 56789);

		tela.cadastrarContaBancaria();

		assertTrue(tela.isConfirmado(), "A tela deve confirmar o cadastro da conta corrente.");
		assertNull(tela.getMensagemErro(), "Não deve haver mensagem de erro.");
		assertEquals(1, perfil.obterContas().size(), "O perfil deve conter exatamente 1 conta.");

		ContaBancaria conta = perfil.obterContas().get(0);
		assertInstanceOf(ContaCorrente.class, conta, "A conta deve ser uma instância de ContaCorrente.");
		assertEquals("Conta Principal", conta.getNomeDaContaBancaria());
		assertEquals("conta corrente", conta.getTipo());
		assertEquals(1500.0f, conta.getSaldo());
		assertEquals(hoje, conta.getDataDeReferencia());
		assertEquals("ativo", conta.getStatus());

		ContaCorrente cc = (ContaCorrente) conta;
		assertEquals("Banco do Brasil", cc.getNomeDoBanco());
		assertEquals(1234, cc.getNumeroDaAgencia());
		assertEquals(56789, cc.getNumeroDaConta());
	}

	@Test
	@DisplayName("Deve cadastrar Poupança com sucesso (RF3.1, RN03, RN04)")
	public void testCadastrarPoupancaSucesso() {
		Date hoje = new Date();
		tela.preencherCampos("Reserva Emergencial", "poupanca", 3500.0f, hoje, "Caixa", 4321, 98765);

		tela.cadastrarContaBancaria();

		assertTrue(tela.isConfirmado(), "A tela deve confirmar o cadastro da poupança.");
		assertNull(tela.getMensagemErro());
		assertEquals(1, perfil.obterContas().size());

		ContaBancaria conta = perfil.obterContas().get(0);
		assertInstanceOf(Poupanca.class, conta, "A conta deve ser uma instância de Poupanca.");
		assertEquals("Reserva Emergencial", conta.getNomeDaContaBancaria());
		assertEquals("ativo", conta.getStatus());

		Poupanca poupanca = (Poupanca) conta;
		assertEquals("Caixa", poupanca.getNomeDoBanco());
		assertEquals(4321, poupanca.getNumeroDaAgencia());
		assertEquals(98765, poupanca.getNumeroDaConta());
	}

	@Test
	@DisplayName("Deve cadastrar Carteira de Dinheiro com sucesso sem dados bancários (RF3.1, RN02, RN04)")
	public void testCadastrarCarteiraDeDinheiroSucesso() {
		Date hoje = new Date();
		tela.preencherCampos("Carteira Física", "carteira de dinheiro", 120.0f, hoje);

		tela.cadastrarContaBancaria();

		assertTrue(tela.isConfirmado(), "A tela deve confirmar o cadastro da carteira.");
		assertNull(tela.getMensagemErro());
		assertEquals(1, perfil.obterContas().size());

		ContaBancaria conta = perfil.obterContas().get(0);
		assertInstanceOf(CarteiraDeDinheiro.class, conta, "A conta deve ser uma instância de CarteiraDeDinheiro.");
		assertEquals("Carteira Física", conta.getNomeDaContaBancaria());
		assertEquals("carteira de dinheiro", conta.getTipo());
		assertEquals(120.0f, conta.getSaldo());
		assertEquals("ativo", conta.getStatus());
	}

	@Test
	@DisplayName("Deve rejeitar Carteira de Dinheiro com saldo inicial negativo (RN02)")
	public void testRejeitarCarteiraDeDinheiroSaldoNegativo() {
		Date hoje = new Date();
		tela.preencherCampos("Carteira Devedora", "carteira de dinheiro", -50.0f, hoje);

		tela.cadastrarContaBancaria();

		assertFalse(tela.isConfirmado(), "Cadastro não deve ser confirmado para saldo negativo.");
		assertNotNull(tela.getMensagemErro(), "Deve exibir mensagem de erro.");
		assertEquals(0, perfil.obterContas().size(), "Nenhuma conta deve ser criada.");
	}

	@Test
	@DisplayName("Deve rejeitar Conta Corrente com campos bancários inválidos ou vazios (RN03)")
	public void testRejeitarContaBancariaCamposInvalidos() {
		Date hoje = new Date();
		// Banco vazio e agência zerada
		tela.preencherCampos("Conta Sem Banco", "conta corrente", 500.0f, hoje, "", 0, 123);

		tela.cadastrarContaBancaria();

		assertFalse(tela.isConfirmado());
		assertNotNull(tela.getMensagemErro());
		assertEquals(0, perfil.obterContas().size());
	}

	@Test
	@DisplayName("Deve rejeitar conta sem nome ou sem data de referência (RN04)")
	public void testRejeitarContaSemNomeOuSemData() {
		tela.preencherCampos("", "conta corrente", 500.0f, null, "Banco", 123, 456);

		tela.cadastrarContaBancaria();

		assertFalse(tela.isConfirmado());
		assertNotNull(tela.getMensagemErro());
		assertEquals(0, perfil.obterContas().size());
	}

}
