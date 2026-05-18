package dao;

import dto.FuncionarioDTO;
import entities.Funcionario;
import factory.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FuncionarioDAO {

    public void salvar(Funcionario funcionario) throws SQLException {
        String sql = "INSERT INTO T_VISION_FUNCIONARIO (nm_nome, nm_sobrenome, ds_cargo, ds_email, ds_tipo_acesso) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getSobrenome());
            stmt.setString(3, funcionario.getCargo());
            stmt.setString(4, funcionario.getEmail());
            stmt.setString(5, funcionario.getTipoAcesso() != null ? funcionario.getTipoAcesso() : "Administrador");

            stmt.executeUpdate();
        }
    }

    public FuncionarioDTO buscarPorId(int id) throws Exception {
        String sql = "SELECT id_funcionario, nm_nome, nm_sobrenome, ds_email FROM T_VISION_FUNCIONARIO WHERE id_funcionario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    FuncionarioDTO dto = new FuncionarioDTO();
                    dto.setId(rs.getInt("id_funcionario"));
                    String nomeCompleto = rs.getString("nm_nome") + " " + rs.getString("nm_sobrenome");
                    dto.setNome(nomeCompleto);

                    dto.setEmail(rs.getString("ds_email"));
                    return dto;
                }
            }
        }
        return null;
    }
}