package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import br.unesp.fct.sgfp.model.FabricaConexao;
import br.unesp.fct.sgfp.model.RepositorioDeCredenciais;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaAutenticacaoPerfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Modulares - Diagrama 2: Autenticar Perfil (RF1.3.2, RF1.5)")
public class AutenticarPerfilTest {

    private ControladorPerfil controlador;
    private Sessao sessao;
    private TelaAutenticacaoPerfil tela;

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
        tela = new TelaAutenticacaoPerfil(controlador);

        // Pré-cadastra usuário para testes de autenticação
        controlador.cadastrarPerfil("Caroline", "senha123");
        sessao.desconectar();
    }

    @Test
    @DisplayName("Mensagens 1, 2 e 4: Autenticação com credenciais corretas pela TelaAutenticacaoPerfil")
    void testAutenticacaoSucessoPelaTela() {
        tela.preencherCampos("Caroline", "senha123");
        tela.botaoAutenticar();

        assertEquals("Autenticado com sucesso!", tela.getMensagem());
        assertTrue(sessao.isAutenticado(), "Sessão deve estar autenticada após login com sucesso");
        assertEquals("Caroline", sessao.getPerfilAtivo().getNome());
    }

    @Test
    @DisplayName("Mensagens 1, 2 e 3: Senha incorreta dispara falhaDeAutenticacao com mensagem genérica (RF1.5)")
    void testAutenticacaoSenhaIncorreta() {
        tela.preencherCampos("Caroline", "senhaIncorreta");
        tela.botaoAutenticar();

        assertEquals("Usuário ou senha inválidos.", tela.getMensagem(), "RF1.5: Falha de autenticação genérica por segurança");
        assertFalse(sessao.isAutenticado(), "Sessão não deve estar ativa após erro de credenciais");
    }

    @Test
    @DisplayName("Mensagens 1, 2 e 3: Usuário inexistente dispara erro genérico sem revelar detalhes (RF1.5)")
    void testAutenticacaoUsuarioInexistente() {
        tela.preencherCampos("UsuarioInexistente", "senha123");
        tela.botaoAutenticar();

        assertEquals("Usuário ou senha inválidos.", tela.getMensagem(), "RF1.5: Não deve expor se o usuário existe ou não");
        assertFalse(sessao.isAutenticado());
    }
}
