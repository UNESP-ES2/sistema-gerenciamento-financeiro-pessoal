package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.CarteiraDeDinheiro;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaEditarContaBancaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Diagrama de Colaboração 'editarContaBancaria' (Seção 3.1.3).
 * Valida a obtenção de dados atuais e a atualização in-place dos atributos (RF3.3, RN02, RN03, RN04).
 */
public class EditarContaBancariaTest {

	private PerfilDeUsuario perfil;
	private ControladorContaBancaria controlador;
	private TelaEditarContaBancaria tela;

	@BeforeEach
	public void setUp() {
		perfil = new PerfilDeUsuario("Gabriel", "senha123");
		Sessao.getInstancia().autenticar("Gabriel", "senha123");

		controlador = new ControladorContaBancaria(perfil);
		tela = new TelaEditarContaBancaria(controlador);

		// Cadastra uma Conta Corrente e uma Carteira de Dinheiro para os cenários de edição
		ContaCorrente cc = ContaCorrente.criar("Minha Conta", "conta corrente", 1000.0f, new Date(), "Banco A", 10, 20);
		perfil.adicionarConta(cc);

		CarteiraDeDinheiro cd = CarteiraDeDinheiro.criar("Minha Carteira", "carteira de dinheiro", 50.0f, new Date());
		perfil.adicionarConta(cd);
	}

	@Test
	@DisplayName("Mensagens 1, 1.1 e 1.2: Deve obter e exibir dados atuais da conta para edição")
	public void testObterContaParaEdicao() {
		tela.obterConta("Minha Conta");

		ContaBancaria carregada = tela.getContaCarregada();
		assertNotNull(carregada, "A conta deve ser carregada na tela.");
		assertEquals("Minha Conta", carregada.getNomeDaContaBancaria());
		assertEquals(1000.0f, carregada.getSaldo());
		assertInstanceOf(ContaCorrente.class, carregada);
	}

	@Test
	@DisplayName("Mensagens 2, 2.1, 2.2 e 5: Deve atualizar Conta Corrente com sucesso in-place (RF3.3, RN03, RN04)")
	public void testAtualizarContaCorrenteSucesso() {
		Date novaData = new Date();
		tela.preencherCamposParaEdicao("Minha Conta", "conta corrente", 2500.0f, novaData, "Banco B", 99, 888);

		tela.editarContaBancaria();

		assertTrue(tela.isConfirmado(), "A tela deve confirmar a edição.");
		assertNull(tela.getMensagemErro());

		ContaBancaria contaAtualizada = perfil.buscarContaBancaria("Minha Conta");
		assertNotNull(contaAtualizada);
		assertEquals(2500.0f, contaAtualizada.getSaldo(), "Saldo deve ter sido atualizado in-place.");
		assertEquals(novaData, contaAtualizada.getDataDeReferencia());

		ContaCorrente cc = (ContaCorrente) contaAtualizada;
		assertEquals("Banco B", cc.getNomeDoBanco());
		assertEquals(99, cc.getNumeroDaAgencia());
		assertEquals(888, cc.getNumeroDaConta());
	}

	@Test
	@DisplayName("Mensagens 3, 3.1, 3.2 e 5: Deve atualizar Carteira de Dinheiro com sucesso in-place (RF3.3, RN02, RN04)")
	public void testAtualizarCarteiraDeDinheiroSucesso() {
		Date novaData = new Date();
		tela.preencherCamposParaEdicao("Minha Carteira", "carteira de dinheiro", 120.0f, novaData);

		tela.editarContaBancaria();

		assertTrue(tela.isConfirmado());
		assertNull(tela.getMensagemErro());

		ContaBancaria contaAtualizada = perfil.buscarContaBancaria("Minha Carteira");
		assertEquals(120.0f, contaAtualizada.getSaldo());
	}

	@Test
	@DisplayName("Mensagens 3.1 e 4: Deve rejeitar atualização de carteira com saldo negativo (RN02)")
	public void testRejeitarAtualizacaoCarteiraSaldoNegativo() {
		Date novaData = new Date();
		tela.preencherCamposParaEdicao("Minha Carteira", "carteira de dinheiro", -10.0f, novaData);

		tela.editarContaBancaria();

		assertFalse(tela.isConfirmado(), "Edição não deve ser confirmada para saldo negativo.");
		assertNotNull(tela.getMensagemErro(), "Deve exibir mensagem de erro.");

		// Saldo original deve permanecer inalterado
		ContaBancaria conta = perfil.buscarContaBancaria("Minha Carteira");
		assertEquals(50.0f, conta.getSaldo(), "Saldo original deve permanecer inalterado.");
	}

	@Test
	@DisplayName("Mensagens 2.1 e 4: Deve rejeitar atualização de conta corrente com dados bancários inválidos (RN03)")
	public void testRejeitarAtualizacaoContaCorrenteDadosInvalidos() {
		tela.preencherCamposParaEdicao("Minha Conta", "conta corrente", 2500.0f, new Date(), "", 0, 0);

		tela.editarContaBancaria();

		assertFalse(tela.isConfirmado());
		assertNotNull(tela.getMensagemErro());

		// Dados originais devem permanecer inalterados
		ContaCorrente cc = (ContaCorrente) perfil.buscarContaBancaria("Minha Conta");
		assertEquals("Banco A", cc.getNomeDoBanco());
		assertEquals(10, cc.getNumeroDaAgencia());
	}

}
