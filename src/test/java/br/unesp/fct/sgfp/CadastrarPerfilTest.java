package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import br.unesp.fct.sgfp.model.FabricaConexao;
import br.unesp.fct.sgfp.model.RepositorioDeCredenciais;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaCadastrarPerfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Modulares - Diagrama 1: Cadastrar Perfil (RF1.1, RF1.2, RF1.3.4)")
public class CadastrarPerfilTest {

    private ControladorPerfil controlador;
    private Sessao sessao;
    private TelaCadastrarPerfil tela;

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
        tela = new TelaCadastrarPerfil(controlador);
    }

    @Test
    @DisplayName("Mensagens 1 a 8: Cadastro com dados válidos realizado pela TelaCadastrarPerfil")
    void testCadastroPerfilSucessoPelaTela() {
        tela.preencherCampos("Gabriel", "senha123", "senha123");
        tela.botaoCadastrar();

        assertEquals("Perfil cadastrado com sucesso!", tela.getMensagem());
        assertTrue(sessao.isAutenticado(), "RF1.3.4: A sessão deve ser ativada automaticamente pós-cadastro");
        assertEquals("Gabriel", sessao.getPerfilAtivo().getNome());
    }

    @Test
    @DisplayName("Mensagem 4: Confirmação de senha divergente dispara falhaDeCadastro()")
    void testCadastroPerfilConfirmacaoSenhaInvalida() {
        tela.preencherCampos("Gabriel", "senha123", "senhaOutra");
        tela.botaoCadastrar();

        assertEquals("Falha no cadastro: entradas inválidas.", tela.getMensagem());
        assertFalse(sessao.isAutenticado(), "Sessão não deve ser ativada em caso de falha de validação");
    }

    @Test
    @DisplayName("Mensagens 2 e 4: Validação sintática inválida (nome curto) dispara falhaDeCadastro()")
    void testCadastroPerfilNomeInvalido() {
        tela.preencherCampos("Ana", "senha123", "senha123");
        tela.botaoCadastrar();

        assertEquals("Falha no cadastro: entradas inválidas.", tela.getMensagem());
        assertFalse(sessao.isAutenticado());
    }

    @Test
    @DisplayName("Mensagens 5 e 6: Tentativa de cadastrar segundo perfil na mesma base é bloqueada (RF1.2)")
    void testBloqueioSegundoUsuario() {
        // Primeiro perfil
        assertTrue(controlador.cadastrarPerfil("Gabriel", "senha123"));
        sessao.desconectar();

        // Segundo perfil aciona fluxo de perfil já existente
        boolean segundoCadastro = controlador.cadastrarPerfil("Caroline", "senha456");
        assertFalse(segundoCadastro, "RF1.2: Deve impedir segundo usuário na mesma base local");
    }
}
