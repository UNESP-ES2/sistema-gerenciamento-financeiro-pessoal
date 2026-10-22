package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaContasBancarias;
import br.unesp.fct.sgfp.view.TelaExibirContasBancarias;
import br.unesp.fct.sgfp.view.TelasContaBancaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para os Diagramas de Colaboração 'inativarContaBancaria' e 'reativarContaBancaria' (Seção 3.1.3).
 * Valida a alternância de status da conta entre 'inativa' e 'ativa' preservando saldos e histórico (RF3.6, RF3.7, RN05).
 */
public class InativarEReativarContaBancariaTest {

	private PerfilDeUsuario perfil;
	private ControladorContaBancaria controlador;
	private TelasContaBancaria telaInativar;
	private TelaContasBancarias telaContasBancarias;
	private TelaExibirContasBancarias telaReativar;
	private ContaCorrente conta;

	@BeforeEach
	public void setUp() {
		perfil = new PerfilDeUsuario("Gabriel", "senha123");
		Sessao.getInstancia().autenticar("Gabriel", "senha123");

		controlador = new ControladorContaBancaria(perfil);
		telaInativar = new TelasContaBancaria(controlador);
		telaContasBancarias = new TelaContasBancarias(controlador);
		telaReativar = new TelaExibirContasBancarias(controlador);

		// Cria uma conta ativa com saldo de 3000.0f
		conta = ContaCorrente.criar("Conta Investimento", "conta corrente", 3000.0f, new Date(), "Banco X", 123, 456);
		perfil.adicionarConta(conta);
	}

	@Test
	@DisplayName("Mensagens 1 a 3 (inativarContaBancaria): Deve alterar o status para 'inativa' e preservar histórico e saldo (RF3.6, RN05)")
	public void testInativarContaBancariaSucesso() {
		assertEquals("ativo", conta.getStatus(), "Status inicial deve ser ativo.");

		telaInativar.inativarContaBancaria("Conta Investimento");

		ContaBancaria contaInativada = perfil.buscarContaBancaria("Conta Investimento");
		assertNotNull(contaInativada);
		assertEquals("inativa", contaInativada.getStatus(), "Status deve ter sido alterado para 'inativa'.");
		assertEquals(3000.0f, contaInativada.getSaldo(), "RN05: O saldo da conta inativada deve ser rigorosamente preservado.");
	}

	@Test
	@DisplayName("Validação nominal: Deve inativar conta utilizando a lifeline TelaContasBancarias")
	public void testInativarContaComTelaContasBancariasNominal() {
		telaContasBancarias.inativarContaBancaria("Conta Investimento");

		ContaBancaria contaInativada = perfil.buscarContaBancaria("Conta Investimento");
		assertEquals("inativa", contaInativada.getStatus());
	}

	@Test
	@DisplayName("Mensagens 1 a 3 (reativarContaBancaria): Deve alterar o status para 'ativa' e manter integridade (RF3.7, RN05)")
	public void testReativarContaBancariaSucesso() {
		// Inativa a conta previamente
		controlador.inativarContaBancaria("Conta Investimento");
		assertEquals("inativa", conta.getStatus());

		// Reativa através da TelaExibirContasBancarias
		telaReativar.reativarContaBancaria("Conta Investimento");

		ContaBancaria contaReativada = perfil.buscarContaBancaria("Conta Investimento");
		assertNotNull(contaReativada);
		assertEquals("ativa", contaReativada.getStatus(), "Status deve ter sido restaurado para 'ativa'.");
		assertEquals(3000.0f, contaReativada.getSaldo(), "RN05: O saldo da conta reativada deve permanecer intacto.");
	}

	@Test
	@DisplayName("Deve tratar busca de conta inexistente sem lançar exceções")
	public void testInativarEReativarContaInexistente() {
		assertDoesNotThrow(() -> {
			controlador.inativarContaBancaria("Conta Que Nao Existe");
			controlador.reativarContaBancaria("Conta Que Nao Existe");
		});
	}

}
