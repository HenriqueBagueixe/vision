package dao;

import dto.AgendaDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AgendaDAO {

    @Inject
    DataSource dataSource;

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public List<AgendaDTO> buscarAgendaDoDentista(int idMedico) throws Exception {
        List<AgendaDTO> agenda = new ArrayList<>();
        String sql = "SELECT a.id_atendimento, TO_CHAR(a.dt_hr_atendimento, 'DD/MM/YYYY HH24:MI') as data_formatada, " +
                "a.ds_tipo_procedimento, a.st_status_atendimento, a.ds_descricao_procedimento, " +
                "p.nm_paciente, p.gravidade_dentaria, d.nm_dentista " +
                "FROM T_TDB_ATENDIMENTO a " +
                "INNER JOIN T_TDB_TRIAGEM_HISTORICO h ON h.id_atendimento = a.id_atendimento " +
                "INNER JOIN T_TDB_PACIENTE p ON p.id_paciente = h.id_paciente " +
                "LEFT JOIN T_TDB_DENTISTA_VOLUNTARIO d ON d.id_medico = a.id_medico " +
                "WHERE a.id_medico = ? " +
                "ORDER BY a.dt_hr_atendimento ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMedico);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AgendaDTO dto = new AgendaDTO();
                    dto.setIdAtendimento(rs.getInt("id_atendimento"));
                    dto.setDataHora(rs.getString("data_formatada"));
                    dto.setProcedimento(rs.getString("ds_tipo_procedimento"));
                    dto.setStatus(rs.getString("st_status_atendimento"));
                    dto.setNomePaciente(rs.getString("nm_paciente"));
                    dto.setGravidade(rs.getInt("gravidade_dentaria"));
                    dto.setNomeDentista(rs.getString("nm_dentista"));
                    dto.setObservacao(rs.getString("ds_descricao_procedimento"));

                    agenda.add(dto);
                }
            }
        }
        return agenda;
    }
    public List<AgendaDTO> buscarAgendaGeralAdmin() throws Exception {
        List<AgendaDTO> agenda = new ArrayList<>();
        String sql = "SELECT a.id_atendimento, TO_CHAR(a.dt_hr_atendimento, 'DD/MM/YYYY HH24:MI') as data_formatada, " +
                "a.ds_tipo_procedimento, a.st_status_atendimento, a.ds_descricao_procedimento, " +
                "p.nm_paciente, p.gravidade_dentaria, d.nm_dentista " +
                "FROM T_TDB_ATENDIMENTO a " +
                "INNER JOIN T_TDB_TRIAGEM_HISTORICO h ON h.id_atendimento = a.id_atendimento " +
                "INNER JOIN T_TDB_PACIENTE p ON p.id_paciente = h.id_paciente " +
                "LEFT JOIN T_TDB_DENTISTA_VOLUNTARIO d ON d.id_medico = a.id_medico " +
                "ORDER BY a.dt_hr_atendimento ASC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AgendaDTO dto = new AgendaDTO();
                dto.setIdAtendimento(rs.getInt("id_atendimento"));
                dto.setDataHora(rs.getString("data_formatada"));
                dto.setProcedimento(rs.getString("ds_tipo_procedimento"));
                dto.setStatus(rs.getString("st_status_atendimento"));
                dto.setNomePaciente(rs.getString("nm_paciente"));
                dto.setGravidade(rs.getInt("gravidade_dentaria"));
                dto.setObservacao(rs.getString("ds_descricao_procedimento"));
                dto.setNomeDentista(rs.getString("nm_dentista"));
                agenda.add(dto);
            }
        }
        return agenda;
    }
    public void atualizarStatusAtendimento(int idAtendimento, String novoStatus, String descricao) throws Exception {
        String sql = "UPDATE T_TDB_ATENDIMENTO SET st_status_atendimento = ?, ds_descricao_procedimento = ? WHERE id_atendimento = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setString(2, descricao);
            stmt.setInt(3, idAtendimento);
            stmt.executeUpdate();
        }
    }
}