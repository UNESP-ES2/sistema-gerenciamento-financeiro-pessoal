package br.unesp.fct.sgfp.model;

import br.unesp.fct.sgfp.view.TelaAutenticacaoPerfil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repositório de credenciais e persistência do perfil local no banco SQLite.
 */
public class RepositorioDeCredenciais {

	public RepositorioDeCredenciais() {
		FabricaConexao.inicializarBanco();
	}

	/**
	 * Cria o perfil local inicial no SQLite caso não exista outro (RF1.1 e RF1.2).
	 */
	public boolean criarPerfil(String nome, String senha) {
		boolean jaExistePerfil = verificarExistenciaPerfil();

		if (jaExistePerfil) {
			new TelaAutenticacaoPerfil().iniciarAutenticacao();
			return false;
		}

		boolean verificar = salvarCredenciais(nome, senha);
		return verificar;
	}

	/**
	 * Verifica se já existe um perfil cadastrado nesta base SQLite (RF1.2).
	 */
	public boolean verificarExistenciaPerfil() {
		String sql = "SELECT COUNT(*) FROM perfil";
		try (Connection conn = FabricaConexao.getConexao();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			// Em caso de falha de leitura, assume falso para não mascarar problemas
		}
		return false;
	}

	/**
	 * Persiste credenciais do perfil único (RF1.1, RF1.6).
	 */
	public boolean salvarCredenciais(String nome, String senha) {
		boolean jaExistePerfil = verificarExistenciaPerfil();
		String sql = jaExistePerfil
				? "UPDATE perfil SET nome = ?, senha = ?"
				: "INSERT INTO perfil (nome, senha) VALUES (?, ?)";

		try (Connection conn = FabricaConexao.getConexao();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, nome);
			stmt.setString(2, senha);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Verifica se o nome informado corresponde ao do perfil cadastrado (RF1.8 - Esqueci Senha).
	 */
	public boolean verificarNome(String nome) {
		String sql = "SELECT COUNT(*) FROM perfil WHERE nome = ?";
		try (Connection conn = FabricaConexao.getConexao();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, nome);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}

	/**
	 * Atualiza apenas a senha do perfil cadastrado (RF1.8).
	 */
	public void salvarSenha(String senha) {
		String sql = "UPDATE perfil SET senha = ?";
		try (Connection conn = FabricaConexao.getConexao();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, senha);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao atualizar senha: " + e.getMessage(), e);
		}
	}

	/**
	 * Valida o par de credenciais para autenticação (RF1.3.2 e RF1.5).
	 */
	public boolean verificarCredenciais(String nome, String senha) {
		String sql = "SELECT COUNT(*) FROM perfil WHERE nome = ? AND senha = ?";
		try (Connection conn = FabricaConexao.getConexao();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, nome);
			stmt.setString(2, senha);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}

}
