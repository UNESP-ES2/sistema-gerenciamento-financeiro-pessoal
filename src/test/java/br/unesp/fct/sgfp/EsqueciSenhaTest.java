package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import br.unesp.fct.sgfp.model.FabricaConexao;
import br.unesp.fct.sgfp.model.RepositorioDeCredenciais;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.InputEdicaoSenha;
import br.unesp.fct.sgfp.view.TelaAutenticacaoPerfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Modulares - Diagrama 5: Esqueci Senha (RF1.8)")
public class EsqueciSenhaTest {

    private ControladorPerfil controlador;
    private Sessao sessao;
    private TelaAutenticacaoPerfil tela;
    private InputEdicaoSenha inputEdicaoSenha;

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
        inputEdicaoSenha = new InputEdicaoSenha();
        controlador.setInputEdicaoSenha(inputEdicaoSenha);

        tela = new TelaAutenticacaoPerfil(controlador);

        // Prepara usuário cadastrado
        controlador.cadastrarPerfil("Caroline", "senhaAntiga123");
        sessao.desconectar();
    }

    @Test
    @DisplayName("Mensagens 1 a 7: Recuperação de senha com nova senha válida redefine credenciais e ativa sessão")
    void testEsqueciSenhaSucessoPelaTela() {
        // Simula nova senha digitada na caixa de diálogo InputEdicaoSenha (Mensagem 3)
        inputEdicaoSenha.setNovaSenha("novaSenhaForte123");

        tela.preencherCampos("Caroline", "");
        tela.botaoEsqueceuSenha();

        assertEquals("Senha redefinida e autenticado com sucesso!", tela.getMensagem());
        assertTrue(sessao.isAutenticado(), "RF1.8 / Mensagem 7: Sessão deve ser ativada com as novas credenciais");
        assertEquals("Caroline", sessao.getPerfilAtivo().getNome());

        // Valida que o SQLite foi atualizado com a nova senha (Mensagem 6)
        RepositorioDeCredenciais repo = new RepositorioDeCredenciais();
        assertTrue(repo.verificarCredenciais("Caroline", "novaSenhaForte123"));
        assertFalse(repo.verificarCredenciais("Caroline", "senhaAntiga123"));
    }

    @Test
    @DisplayName("Mensagens 4 e 5: Nova senha inválida (curta) dispara falhaDeEdicao() na TelaAutenticacaoPerfil")
    void testEsqueciSenhaNovaSenhaInvalida() {
        // Senha com menos de 4 caracteres (RF1.1.2)
        inputEdicaoSenha.setNovaSenha("123");

        tela.preencherCampos("Caroline", "");
        tela.botaoEsqueceuSenha();

        assertEquals("Falha na edição da senha: senha inválida.", tela.getMensagem());
        assertFalse(sessao.isAutenticado(), "Sessão não deve ser ativada se a nova senha for inválida");

        // Senha antiga deve permanecer no SQLite
        RepositorioDeCredenciais repo = new RepositorioDeCredenciais();
        assertTrue(repo.verificarCredenciais("Caroline", "senhaAntiga123"));
    }

    @Test
    @DisplayName("Mensagens 1 e 2: Usuário inexistente não localiza nome no RepositorioDeCredenciais")
    void testEsqueciSenhaUsuarioInexistente() {
        tela.preencherCampos("UsuarioInexistente", "");
        tela.botaoEsqueceuSenha();

        assertEquals("Usuário não encontrado.", tela.getMensagem());
        assertFalse(sessao.isAutenticado());
    }
}
