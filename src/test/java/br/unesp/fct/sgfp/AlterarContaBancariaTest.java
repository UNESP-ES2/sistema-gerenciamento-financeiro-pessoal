package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorContaBancaria;
import br.unesp.fct.sgfp.model.CarteiraDeDinheiro;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.model.Poupanca;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaConta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Diagrama de Colaboração 'alterarContaBancaria' (Seção 3.1.2).
 * Valida a listagem de contas bancárias do perfil para seleção e alteração da conta ativa (RF3.5).
 */
public class AlterarContaBancariaTest {

	private PerfilDeUsuario perfil;
	private ControladorContaBancaria controlador;
	private TelaConta telaConta;

	@BeforeEach
	public void setUp() {
		Sessao.getInstancia().desconectar();
		perfil = new PerfilDeUsuario("Gabriel Ciriaco", "senha123");
		Sessao.getInstancia().autenticar("Gabriel Ciriaco", "senha123");

		controlador = new ControladorContaBancaria(perfil);
		telaConta = new TelaConta(controlador);
	}

	@Test
	@DisplayName("Mensagens 1, 1.1, 1.2 e 1.3: Deve recuperar contas do perfil autenticado na Sessão e exibir na TelaConta")
	public void testAlterarContaBancariaComSucessoPerfilSessao() {
		// Cadastra 3 contas distintas no perfil da sessão ativa
		PerfilDeUsuario perfilAtivo = Sessao.getInstancia().getPerfilAtivo();
		ContaCorrente cc = ContaCorrente.criar("Inter Principal", "conta corrente", 1500.0f, new Date(), "Inter", 1, 12345);
		Poupanca cp = Poupanca.criar("Reserva Emergencia", "poupanca", 5000.0f, new Date(), "Nubank", 1, 67890);
		CarteiraDeDinheiro cd = CarteiraDeDinheiro.criar("Carteira Fisica", "carteira de dinheiro", 200.0f, new Date());

		perfilAtivo.adicionarConta(cc);
		perfilAtivo.adicionarConta(cp);
		perfilAtivo.adicionarConta(cd);

		// Disparo a partir da view (lifeline : TelaConta)
		telaConta.acaoAlterarContaBancaria();

		ArrayList<ContaBancaria> contasExibidas = telaConta.getContasExibidas();
		assertNotNull(contasExibidas, "A lista de contas exibidas não pode ser nula.");
		assertEquals(3, contasExibidas.size(), "Devem ser exibidas exatamente 3 contas cadastradas.");
		assertEquals("Inter Principal", contasExibidas.get(0).getNomeDaContaBancaria());
		assertEquals("Reserva Emergencia", contasExibidas.get(1).getNomeDaContaBancaria());
		assertEquals("Carteira Fisica", contasExibidas.get(2).getNomeDaContaBancaria());
	}

	@Test
	@DisplayName("Deve exibir lista vazia quando o perfil não possuir contas cadastradas")
	public void testAlterarContaBancariaPerfilSemContas() {
		// Perfil autenticado mas sem nenhuma conta cadastrada
		telaConta.acaoAlterarContaBancaria();

		ArrayList<ContaBancaria> contasExibidas = telaConta.getContasExibidas();
		assertNotNull(contasExibidas, "A lista de contas exibidas não deve ser nula.");
		assertTrue(contasExibidas.isEmpty(), "A lista de contas deve estar vazia quando não há contas cadastradas.");
	}

	@Test
	@DisplayName("Deve permitir listagem de contas com perfil injetado diretamente no controlador")
	public void testAlterarContaBancariaComPerfilInjetado() {
		Sessao.getInstancia().desconectar(); // Desconecta sessão global
		PerfilDeUsuario perfilIsolado = new PerfilDeUsuario("Caroline Nunes", "senha456");
		ContaCorrente cc = ContaCorrente.criar("Banco do Brasil", "conta corrente", 3000.0f, new Date(), "BB", 100, 200);
		perfilIsolado.adicionarConta(cc);

		ControladorContaBancaria ctrlIsolado = new ControladorContaBancaria(perfilIsolado);
		TelaConta telaIsolada = new TelaConta(ctrlIsolado);

		telaIsolada.acaoAlterarContaBancaria();

		ArrayList<ContaBancaria> contasExibidas = telaIsolada.getContasExibidas();
		assertNotNull(contasExibidas);
		assertEquals(1, contasExibidas.size());
		assertEquals("Banco do Brasil", contasExibidas.get(0).getNomeDaContaBancaria());
	}

}
