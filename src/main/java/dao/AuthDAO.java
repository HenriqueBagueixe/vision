package dao;

import factory.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

public class AuthDAO {

    public void criarSenha(String emailToken, String senha) throws Exception {
        Integer idFuncionario = null;
        Integer idDentista = null;

        String sqlBuscaFunc = "SELECT id_funcionario FROM T_TDB_CADASTRO_FUNCIONARIO WHERE email_funcionario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmtBuscaFunc = conn.prepareStatement(sqlBuscaFunc)) {
            stmtBuscaFunc.setString(1, emailToken);
            try (ResultSet rs = stmtBuscaFunc.executeQuery()) {
                if (rs.next()) idFuncionario = rs.getInt("id_funcionario");
            }
        }

        if (idFuncionario == null) {
            String sqlBuscaDentista = "SELECT id_medico FROM T_TDB_DENTISTA_VOLUNTARIO WHERE ds_email = ?";
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmtBuscaDentista = conn.prepareStatement(sqlBuscaDentista)) {
                stmtBuscaDentista.setString(1, emailToken);
                try (ResultSet rs = stmtBuscaDentista.executeQuery()) {
                    if (rs.next()) idDentista = rs.getInt("id_medico");
                }
            }
        }

        if (idFuncionario == null && idDentista == null) {
            throw new Exception("Usuário não encontrado com o e-mail fornecido. O token é inválido.");
        }

        boolean loginExiste = false;
        String sqlVerifica = "SELECT id_usuario FROM T_TDB_LOGIN WHERE id_funcionario = ? OR id_dentista = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmtVerifica = conn.prepareStatement(sqlVerifica)) {

            if (idFuncionario != null) stmtVerifica.setInt(1, idFuncionario);
            else stmtVerifica.setNull(1, Types.INTEGER);

            if (idDentista != null) stmtVerifica.setInt(2, idDentista);
            else stmtVerifica.setNull(2, Types.INTEGER);

            try (ResultSet rsVerifica = stmtVerifica.executeQuery()) {
                if (rsVerifica.next()) loginExiste = true;
            }
        }

        if (loginExiste) {
            String sqlUpdate = "UPDATE T_TDB_LOGIN SET ds_senha = ? WHERE id_funcionario = ? OR id_dentista = ?";
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setString(1, senha);
                if (idFuncionario != null) stmtUpdate.setInt(2, idFuncionario); else stmtUpdate.setNull(2, Types.INTEGER);
                if (idDentista != null) stmtUpdate.setInt(3, idDentista); else stmtUpdate.setNull(3, Types.INTEGER);
                stmtUpdate.executeUpdate();
            }
        } else {
            String sqlInsert = "INSERT INTO T_TDB_LOGIN (id_usuario, ds_login, ds_senha, id_funcionario, id_dentista) " +
                    "VALUES (seq_login.NEXTVAL, ?, ?, ?, ?)";
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                stmtInsert.setString(1, emailToken);
                stmtInsert.setString(2, senha);
                if (idFuncionario != null) stmtInsert.setInt(3, idFuncionario); else stmtInsert.setNull(3, Types.INTEGER);
                if (idDentista != null) stmtInsert.setInt(4, idDentista); else stmtInsert.setNull(4, Types.INTEGER);
                stmtInsert.executeUpdate();
            }
        }
    }

    public String fazerLogin(String email, String senha) throws Exception {
        String sql = "SELECT id_funcionario, id_dentista FROM T_TDB_LOGIN WHERE ds_login = ? AND ds_senha = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getObject("id_dentista") != null) {
                        return "{\"tipoAcesso\": \"DENTISTA\", \"id\": " + rs.getInt("id_dentista") + "}";
                    }
                    if (rs.getObject("id_funcionario") != null) {
                        return "{\"tipoAcesso\": \"ADMIN\", \"id\": " + rs.getInt("id_funcionario") + "}";
                    }
                }
            }
        }
        return "INVALIDO";
    }
}