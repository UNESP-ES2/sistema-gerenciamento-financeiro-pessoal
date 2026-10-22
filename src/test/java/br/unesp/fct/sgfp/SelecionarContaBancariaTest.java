package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.model.CarteiraDeDinheiro;
import br.unesp.fct.sgfp.model.ContaBancaria;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.PerfilDeUsuario;
import br.unesp.fct.sgfp.model.Poupanca;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaExibirContasBancarias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o Diagrama de Colaboração 'selecionarContaBancaria' (Seção 3.1.2).
 * Valida a seleção da conta bancária ativa no perfil, atualização na Sessão e notificação visual (RF3.5).
 */
public class SelecionarContaBancariaTest {

	private PerfilDeUsuario perfil;
	private ContaCorrente cc;
	private Poupanca cp;
	private CarteiraDeDinheiro cd;
	private TelaExibirContasBancarias tela;

	@BeforeEach
	public void setUp() {
		Sessao.getInstancia().desconectar();
		perfil = new PerfilDeUsuario("Gabriel Ciriaco", "senha123");
		Sessao.getInstancia().autenticar("Gabriel Ciriaco", "senha123");

		PerfilDeUsuario perfilAtivo = Sessao.getInstancia().getPerfilAtivo();
		cc = ContaCorrente.criar("Inter Principal", "conta corrente", 2500.0f, new Date(), "Inter", 1, 1001);
		cp = Poupanca.criar("Reserva Emergencia", "poupanca", 10000.0f, new Date(), "Nubank", 1, 2002);
		cd = CarteiraDeDinheiro.criar("Carteira Fisica", "carteira de dinheiro", 350.0f, new Date());

		perfilAtivo.adicionarConta(cc);
		perfilAtivo.adicionarConta(cp);
		perfilAtivo.adicionarConta(cd);

		tela = new TelaExibirContasBancarias();
	}

	@Test
	@DisplayName("Cenário 1: Mensagens 1, 1.1, 1.2 e 1.2.1: Deve selecionar conta ativa via TelaExibirContasBancarias e notificar via Observer")
	public void testSelecionarContaBancariaFluxoCompletoTela() {
		assertNull(Sessao.getInstancia().getContaAtiva(), "Inicialmente nenhuma conta deve estar ativa na sessão.");

		// Usuário aciona a seleção na tela
		tela.setContaSelecionada(cc);
		tela.acaoSelecionarContaBancaria();

		// Valida que a Sessão definiu a conta ativa
		ContaBancaria ativaNaSessao = Sessao.getInstancia().getContaAtiva();
		assertNotNull(ativaNaSessao, "A conta ativa na sessão não pode ser nula após a seleção.");
		assertEquals("Inter Principal", ativaNaSessao.getNomeDaContaBancaria());
		assertEquals(2500.0f, ativaNaSessao.getSaldo());

		// Valida que a mensagem 1.2.1 (atualizarVisualizacao) foi disparada pela Sessão na tela
		ContaBancaria visualizada = tela.getContaVisualizada();
		assertNotNull(visualizada, "A tela deve ter sido notificada com a conta ativa atualizada.");
		assertEquals("Inter Principal", visualizada.getNomeDaContaBancaria());
	}

	@Test
	@DisplayName("Cenário 2: Seleção polimórfica de diferentes modalidades de conta (Poupança e Carteira de Dinheiro)")
	public void testSelecionarContaPolimorfica() {
		// Seleciona Poupança
		tela.setContaSelecionada(cp);
		tela.acaoSelecionarContaBancaria();

		assertEquals("Reserva Emergencia", Sessao.getInstancia().getContaAtiva().getNomeDaContaBancaria());
		assertEquals("Reserva Emergencia", tela.getContaVisualizada().getNomeDaContaBancaria());
		assertInstanceOf(Poupanca.class, Sessao.getInstancia().getContaAtiva());

		// Alterna para Carteira de Dinheiro
		tela.setContaSelecionada(cd);
		tela.acaoSelecionarContaBancaria();

		assertEquals("Carteira Fisica", Sessao.getInstancia().getContaAtiva().getNomeDaContaBancaria());
		assertEquals("Carteira Fisica", tela.getContaVisualizada().getNomeDaContaBancaria());
		assertInstanceOf(CarteiraDeDinheiro.class, Sessao.getInstancia().getContaAtiva());
	}

	@Test
	@DisplayName("Cenário 3: Seleção direta a partir de ContaCorrente garantindo atualização da Sessão e do Observer")
	public void testSelecionarContaDiretoContaCorrente() {
		ContaCorrente instanciaCC = new ContaCorrente();
		instanciaCC.selecionarContaBancaria(cp);

		assertNotNull(Sessao.getInstancia().getContaAtiva());
		assertEquals("Reserva Emergencia", Sessao.getInstancia().getContaAtiva().getNomeDaContaBancaria());
		assertEquals("Reserva Emergencia", tela.getContaVisualizada().getNomeDaContaBancaria());
	}

}
