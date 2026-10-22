package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import br.unesp.fct.sgfp.model.FabricaConexao;
import br.unesp.fct.sgfp.model.RepositorioDeCredenciais;
import br.unesp.fct.sgfp.model.Sessao;
import br.unesp.fct.sgfp.view.TelaEditarPerfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Modulares - Diagrama 3: Editar Perfil (RF1.6)")
public class EditarPerfilTest {

    private ControladorPerfil controlador;
    private Sessao sessao;
    private TelaEditarPerfil tela;

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
        tela = new TelaEditarPerfil(controlador);

        // Pré-cadastra usuário e estabelece sessão ativa
        controlador.cadastrarPerfil("Gabriel", "senha123");
    }

    @Test
    @DisplayName("Mensagens 1 a 4: Edição de perfil com novos dados válidos pela TelaEditarPerfil")
    void testEdicaoPerfilSucessoPelaTela() {
        tela.preencherCampos("NovoGabriel", "novaSenha123", "novaSenha123");
        tela.botaoEditar();

        assertEquals("Perfil atualizado com sucesso!", tela.getMensagem());
        assertEquals("NovoGabriel", sessao.getPerfilAtivo().getNome(), "Perfil ativo na sessão deve refletir o novo nome");

        // Valida persistência das novas credenciais no SQLite
        RepositorioDeCredenciais repo = new RepositorioDeCredenciais();
        assertTrue(repo.verificarCredenciais("NovoGabriel", "novaSenha123"), "Novas credenciais devem estar salvas no SQLite");
        assertFalse(repo.verificarCredenciais("Gabriel", "senha123"), "Credenciais antigas não devem mais ser válidas");
    }

    @Test
    @DisplayName("Confirmação de senha divergente na TelaEditarPerfil dispara falhaDeEdicao()")
    void testEdicaoPerfilConfirmacaoDivergente() {
        tela.preencherCampos("NovoGabriel", "novaSenha123", "senhaInvalida");
        tela.botaoEditar();

        assertEquals("Falha na edição: dados inválidos.", tela.getMensagem());
        assertEquals("Gabriel", sessao.getPerfilAtivo().getNome(), "Dados da sessão não devem ser alterados");
    }

    @Test
    @DisplayName("Mensagens 2 e 4: Nome inválido (com números) dispara falhaDeEdicao()")
    void testEdicaoPerfilNomeInvalido() {
        tela.preencherCampos("Gabriel123", "novaSenha123", "novaSenha123");
        tela.botaoEditar();

        assertEquals("Falha na edição: dados inválidos.", tela.getMensagem());
        assertEquals("Gabriel", sessao.getPerfilAtivo().getNome());
    }

    @Test
    @DisplayName("Mensagens 2 e 4: Senha inválida (com espaços) dispara falhaDeEdicao()")
    void testEdicaoPerfilSenhaComEspacos() {
        tela.preencherCampos("NovoGabriel", "senha com espaco", "senha com espaco");
        tela.botaoEditar();

        assertEquals("Falha na edição: dados inválidos.", tela.getMensagem());
    }
}
