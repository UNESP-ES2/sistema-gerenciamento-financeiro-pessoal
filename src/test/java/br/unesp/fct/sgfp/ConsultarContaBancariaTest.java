package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaConsultaContaBancaria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Diagrama de Colaboração 'consultarContaBancaria' (Seção 3.1.3).
 * Valida a recuperação da conta do PerfilDeUsuario e envio à TelaConsultaContaBancaria (RF3.2).
 */
public class ConsultarContaBancariaTest {

	private PerfilDeUsuario perfil;
	private ControladorContaBancaria controlador;
	private TelaConsultaContaBancaria tela;

	@BeforeEach
	public void setUp() {
		perfil = new PerfilDeUsuario("Gabriel", "senha123");
		Sessao.getInstancia().autenticar("Gabriel", "senha123");

		controlador = new ControladorContaBancaria(perfil);
		tela = new TelaConsultaContaBancaria(controlador);

		// Cria e adiciona uma conta de teste no perfil
		ContaCorrente conta = ContaCorrente.criar("Conta Salario", "conta corrente", 2500.0f, new Date(), "Banco Inter", 1, 12345);
		perfil.adicionarConta(conta);
	}

	@Test
	@DisplayName("Mensagens 1, 1.1 e 1.2: Deve consultar conta existente e repassar para exibição na tela (RF3.2)")
	public void testConsultarContaExistenteComSucesso() {
		tela.preencherCampoBusca("Conta Salario");
		tela.iniciarConsultaDeContaBancaria();

		ContaBancaria contaExibida = tela.getContaExibida();
		assertNotNull(contaExibida, "A conta deve ter sido localizada e exibida na tela.");
		assertEquals("Conta Salario", contaExibida.getNomeDaContaBancaria());
		assertEquals("conta corrente", contaExibida.getTipo());
		assertEquals(2500.0f, contaExibida.getSaldo());
		assertInstanceOf(ContaCorrente.class, contaExibida);
	}

	@Test
	@DisplayName("Deve consultar conta de forma case-insensitive")
	public void testConsultarContaCaseInsensitive() {
		tela.preencherCampoBusca("conta salario");
		tela.iniciarConsultaDeContaBancaria();

		ContaBancaria contaExibida = tela.getContaExibida();
		assertNotNull(contaExibida);
		assertEquals("Conta Salario", contaExibida.getNomeDaContaBancaria());
	}

	@Test
	@DisplayName("Deve retornar nulo para exibição quando a conta não existir no perfil")
	public void testConsultarContaInexistente() {
		tela.preencherCampoBusca("Conta Inexistente");
		tela.iniciarConsultaDeContaBancaria();

		assertNull(tela.getContaExibida(), "Não deve exibir nenhuma conta para nome inexistente.");
	}

}
