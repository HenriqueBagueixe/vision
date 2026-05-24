package dao;

import dto.FuncionarioDTO;
import entities.Funcionario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ApplicationScoped
public class FuncionarioDAO {

    @Inject
    DataSource dataSource;

    public void salvar(Funcionario funcionario) throws SQLException {
        String sqlId = "SELECT NVL(MAX(id_funcionario), 0) + 1 FROM T_TDB_CADASTRO_FUNCIONARIO";
        String sqlInsert = "INSERT INTO T_TDB_CADASTRO_FUNCIONARIO " +
                "(id_funcionario, nm_funcionario, email_funcionario, st_status_funcionario, ds_cargo_funcao, id_projeto) " +
                "VALUES (?, ?, ?, 'ATIVO', ?, 1)";

        try (Connection conn = dataSource.getConnection()) {
            int proximoId = 1;
            try (PreparedStatement stmtId = conn.prepareStatement(sqlId);
                 ResultSet rsId = stmtId.executeQuery()) {
                if (rsId.next()) {
                    proximoId = rsId.getInt(1);
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
                stmt.setInt(1, proximoId);
                stmt.setString(2, funcionario.getNome() + " " + funcionario.getSobrenome());
                stmt.setString(3, funcionario.getEmail());
                stmt.setString(4, funcionario.getCargo());
                stmt.executeUpdate();
            }
        }
    }
    public FuncionarioDTO buscarPorId(int id) throws Exception {
        String sql = "SELECT id_funcionario, nm_funcionario, email_funcionario FROM T_TDB_CADASTRO_FUNCIONARIO WHERE id_funcionario = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    FuncionarioDTO dto = new FuncionarioDTO();
                    dto.setId(rs.getInt("id_funcionario"));
                    dto.setNome(rs.getString("nm_funcionario"));
                    dto.setEmail(rs.getString("email_funcionario"));
                    return dto;
                }
            }
        }
        return null;
    }
}