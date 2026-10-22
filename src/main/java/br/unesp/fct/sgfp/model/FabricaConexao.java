package br.unesp.fct.sgfp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Fábrica de conexões para o banco de dados SQLite local.
 * Gerencia a conexão com o banco e inicializa as tabelas caso não existam.
 */
public class FabricaConexao {

    private static String urlBanco = "jdbc:sqlite:sgfp.db";

    /**
     * Define uma URL customizada de conexão (útil para testes unitários com banco em memória).
     * @param url URL JDBC (ex: "jdbc:sqlite::memory:")
     */
    public static void setUrlBanco(String url) {
        urlBanco = url;
    }

    /**
     * Retorna a URL atual de conexão.
     */
    public static String getUrlBanco() {
        return urlBanco;
    }

    /**
     * Obtém uma nova conexão com o banco de dados SQLite.
     * @return Conexão ativa
     * @throws SQLException Se ocorrer falha na conexão
     */
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(urlBanco);
    }

    /**
     * Inicializa a estrutura das tabelas da Sprint 1 (Perfil e Contas) caso ainda não existam.
     */
    public static void inicializarBanco() {
        String ddlPerfil = """
            CREATE TABLE IF NOT EXISTS perfil (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL
            );
        """;

        String ddlConta = """
            CREATE TABLE IF NOT EXISTS conta (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                perfil_id INTEGER,
                nome TEXT NOT NULL UNIQUE,
                tipo TEXT NOT NULL,
                saldo REAL NOT NULL,
                data_referencia TEXT NOT NULL,
                status TEXT NOT NULL,
                banco TEXT,
                agencia TEXT,
                numero_conta TEXT,
                FOREIGN KEY(perfil_id) REFERENCES perfil(id)
            );
        """;

        try (Connection conn = getConexao();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddlPerfil);
            stmt.execute(ddlConta);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar o banco de dados SQLite: " + e.getMessage(), e);
        }
    }
}
