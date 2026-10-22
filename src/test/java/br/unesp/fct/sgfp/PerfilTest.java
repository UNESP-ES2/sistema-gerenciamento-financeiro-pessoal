package br.unesp.fct.sgfp;

import br.unesp.fct.sgfp.controller.ControladorPerfil;
import br.unesp.fct.sgfp.model.FabricaConexao;
import br.unesp.fct.sgfp.model.RepositorioDeCredenciais;
import br.unesp.fct.sgfp.model.Sessao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suíte de Testes Automatizados - Fluxo de Perfil e Governança (RF1)")
public class PerfilTest {

    private ControladorPerfil controlador;
    private Sessao sessao;

    @BeforeEach
    void setUp() {
        // Usa banco SQLite de teste local e reinicia as tabelas para isolamento
        FabricaConexao.setUrlBanco("jdbc:sqlite:sgfp_teste.db");
        FabricaConexao.inicializarBanco();

        // Limpa registros anteriores da tabela perfil
        try (Connection conn = FabricaConexao.getConexao();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM perfil");
            stmt.execute("DELETE FROM conta");
        } catch (Exception e) {
            fail("Falha ao limpar banco de teste: " + e.getMessage());
        }

        sessao = Sessao.getInstancia();
        sessao.desconectar();
        controlador = new ControladorPerfil(new RepositorioDeCredenciais(), sessao);
    }

    @Test
    @DisplayName("RF1.1.1: Validação do campo Nome (apenas letras, 4 a 18 caracteres, sem números ou especiais)")
    void testValidacaoNome() {
        // Nomes válidos
        assertTrue(controlador.validarEntradas("Gabriel", "senha123"));
        assertTrue(controlador.validarEntradas("Caroline", "1234"));

        // Nomes inválidos
        assertFalse(controlador.validarEntradas(null, "senha123"), "Nome nulo deve ser rejeitado");
        assertFalse(controlador.validarEntradas("Ana", "senha123"), "Nome com menos de 4 letras deve ser rejeitado");
        assertFalse(controlador.validarEntradas("NomeMuitoLongoComMaisDeDezoitoCaracteres", "senha123"), "Nome com mais de 18 letras deve ser rejeitado");
        assertFalse(controlador.validarEntradas("Gabriel123", "senha123"), "Nome com números deve ser rejeitado");
        assertFalse(controlador.validarEntradas("Carol@", "senha123"), "Nome com caracteres especiais deve ser rejeitado");
    }

    @Test
    @DisplayName("RF1.1.2: Validação do campo Senha (4 a 18 caracteres, sem espaços)")
    void testValidacaoSenha() {
        // Senhas válidas
        assertTrue(controlador.validarSenha("1234"));
        assertTrue(controlador.validarSenha("senhaForte123"));

        // Senhas inválidas
        assertFalse(controlador.validarSenha(null), "Senha nula deve ser rejeitada");
        assertFalse(controlador.validarSenha("123"), "Senha com menos de 4 caracteres deve ser rejeitada");
        assertFalse(controlador.validarSenha("senha com espacos"), "Senha com espaços deve ser rejeitada");
        assertFalse(controlador.validarSenha("senhaGiganteComMaisDeDezoitoCaracteres"), "Senha > 18 caracteres deve ser rejeitada");
    }

    @Test
    @DisplayName("RF1.1 e RF1.2: Cadastro de Perfil e Bloqueio de Segundo Usuário na mesma base")
    void testCadastroPerfilUsuarioUnico() {
        // Primeiro cadastro deve suceder (RF1.1)
        boolean cadastrouPrimeiro = controlador.cadastrarPerfil("Gabriel", "senha123");
        assertTrue(cadastrouPrimeiro, "Primeiro perfil deve ser cadastrado com sucesso");
        assertTrue(sessao.isAutenticado(), "RF1.3.4: Sessão deve ser estabelecida automaticamente pós-cadastro");
        assertEquals("Gabriel", sessao.getPerfilAtivo().getNome());

        // Desconecta e tenta cadastrar um segundo perfil na mesma base
        sessao.desconectar();
        boolean cadastrouSegundo = controlador.cadastrarPerfil("Caroline", "senha456");
        assertFalse(cadastrouSegundo, "RF1.2: Criação de um segundo usuário na mesma base deve ser bloqueada");
    }

    @Test
    @DisplayName("RF1.3.2 e RF1.5: Autenticação de Perfil e Falha Genérica")
    void testAutenticacaoPerfil() {
        controlador.cadastrarPerfil("Caroline", "senha123");
        sessao.desconectar();

        // Autenticação com credenciais corretas
        boolean loginValido = controlador.autenticarPerfil("Caroline", "senha123");
        assertTrue(loginValido, "Autenticação válida deve retornar verdadeiro");
        assertTrue(sessao.isAutenticado(), "Sessão deve estar autenticada");

        // Autenticação com senha errada (RF1.5: falha genérica)
        sessao.desconectar();
        boolean loginInvalido = controlador.autenticarPerfil("Caroline", "senhaErrada");
        assertFalse(loginInvalido, "Autenticação incorreta deve falhar");
        assertFalse(sessao.isAutenticado(), "Sessão não deve estar ativa");
    }

    @Test
    @DisplayName("RF1.7: Desconectar encerra a sessão ativa e limpa os dados")
    void testDesconectarSessao() {
        controlador.cadastrarPerfil("Gabriel", "senha123");
        assertTrue(sessao.isAutenticado());

        controlador.botaoDesconectar();
        assertFalse(sessao.isAutenticado(), "RF1.7: Sessão deve ser finalizada");
        assertNull(sessao.getPerfilAtivo(), "RF1.7: Dados do usuário devem ser limpos da sessão");
    }

    @Test
    @DisplayName("RF1.6: Editar Perfil mediante validação")
    void testEditarPerfil() {
        controlador.cadastrarPerfil("Gabriel", "senha123");

        boolean editou = controlador.editarPerfil("NovoGabriel", "novaSenha123");
        assertTrue(editou, "Edição com dados válidos deve suceder");
        assertEquals("NovoGabriel", sessao.getPerfilAtivo().getNome());

        // Edição com nome inválido deve falhar
        boolean editouInvalido = controlador.editarPerfil("Nom3Invalido", "novaSenha123");
        assertFalse(editouInvalido, "Edição com nome inválido deve ser rejeitada");
    }

    @Test
    @DisplayName("RF1.8: Esqueci Senha identifica a existência do nome")
    void testEsqueciSenha() {
        controlador.cadastrarPerfil("Caroline", "senha123");

        assertTrue(controlador.esqueceuSenha("Caroline"), "Usuário existente deve ser reconhecido");
        assertFalse(controlador.esqueceuSenha("UsuarioInexistente"), "Usuário inexistente deve retornar falso");
    }
}
