package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import br.unesp.fct.sgfp.model.ContaCorrente;
import br.unesp.fct.sgfp.model.FabricaConexao;
import br.unesp.fct.sgfp.model.RepositorioDeCredenciais;
import br.unesp.fct.sgfp.model.Sessao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Modulares - Diagrama 4: Desconectar Perfil (RF1.7)")
public class DesconectarPerfilTest {

    private ControladorPerfil controlador;
    private Sessao sessao;

    @BeforeEach
    void setUp() {
        FabricaConexao.setUrlBanco("jdbc:sqlite:sgfp_teste.db");
        FabricaConexao.inicializarBanco();

        try (Connection conn = FabricaConexao.getConexao();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM perfil");
            stmt.execute("DELETE FROM conta");
        } catch (Exception e) {
            fail("Falha ao inicializar base de dados: " + e.getMessage());
        }

        sessao = Sessao.getInstancia();
        sessao.desconectar();

        controlador = new ControladorPerfil(new RepositorioDeCredenciais(), sessao);

        // Prepara usuário e ativa a sessão
        controlador.cadastrarPerfil("Gabriel", "senha123");
    }

    @Test
    @DisplayName("Mensagem 1: botaoDesconectar() invoca sessao.desconectar(), desabilita acesso e limpa dados da memória (RF1.7)")
    void testDesconectarPerfilComSessaoAtiva() {
        assertTrue(sessao.isAutenticado(), "Sessão deve estar autenticada antes de desconectar");
        assertNotNull(sessao.getPerfilAtivo(), "Perfil ativo deve existir na sessão antes de desconectar");

        // Simula uma conta ativa vinculada à sessão
        ContaCorrente conta = new ContaCorrente();
        conta.setNomeDaContaBancaria("Banco do Brasil");
        conta.setSaldo(1000.0f);
        sessao.setContaAtiva(conta);
        assertNotNull(sessao.getContaAtiva());

        // Ação do Ator: botaoDesconectar()
        controlador.botaoDesconectar();

        // Verificações pós-desconexão
        assertFalse(sessao.isAutenticado(), "RF1.7: Sessão deve ser desativada");
        assertNull(sessao.getPerfilAtivo(), "RF1.7: Dados do usuário devem ser descartados da memória");
        assertNull(sessao.getContaAtiva(), "RF1.7: Conta ativa deve ser limpa da sessão");
    }
}
