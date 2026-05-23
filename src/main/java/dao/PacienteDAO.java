package dao;

import dto.PacientePainelDTO;
import entities.Genero;
import entities.Paciente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PacienteDAO {

    @Inject
    DataSource dataSource;

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }




    public void cadastrar(Paciente paciente) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int idPacienteGerado = 1;
            try (PreparedStatement psIdPac = conn.prepareStatement("SELECT seq_paciente.NEXTVAL FROM DUAL");
                 ResultSet rsPac = psIdPac.executeQuery()) {
                if (rsPac.next()) idPacienteGerado = rsPac.getInt(1);
            }

            String sqlPaciente = "INSERT INTO T_TDB_PACIENTE (id_paciente, nm_paciente, ds_idade, email_paciente, " +
                    "telefone_paciente, genero, renda_bruta_total, renda_media, score, gravidade_dentaria, ds_escola, ds_status, ds_programa) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstm = conn.prepareStatement(sqlPaciente)) {
                pstm.setInt(1, idPacienteGerado);
                pstm.setString(2, paciente.getNome());
                pstm.setInt(3, paciente.getIdade());
                pstm.setString(4, paciente.getEmail());
                pstm.setString(5, paciente.getTelefone());
                pstm.setString(6, paciente.getGenero() != null ? paciente.getGenero().name() : "FEMININO");
                pstm.setDouble(7, paciente.getRendaBrutaTotal());
                pstm.setDouble(8, paciente.getRendaMedia());
                pstm.setDouble(9, paciente.getScore());
                pstm.setInt(10, paciente.getGravidadeDentaria());
                pstm.setString(11, paciente.getEscola());
                pstm.setString(12, paciente.getStatus() != null ? paciente.getStatus() : "Sem dentista");
                pstm.setString(13, paciente.getPrograma());
                pstm.executeUpdate();
            }

            int idAtendimento = 1;
            try (PreparedStatement psIdAtend = conn.prepareStatement("SELECT NVL(MAX(id_atendimento), 0) + 1 FROM T_TDB_ATENDIMENTO");
                 ResultSet rsAtend = psIdAtend.executeQuery()) {
                if (rsAtend.next()) idAtendimento = rsAtend.getInt(1);
            }

            String sqlAtendimento = "INSERT INTO T_TDB_ATENDIMENTO (id_atendimento, dt_hr_atendimento, ds_tipo_procedimento, ds_descricao_procedimento, st_status_atendimento) " +
                    "VALUES (?, SYSDATE, 'Triagem Inicial', 'Cadastro inicial gerado via sistema. Aguardando avaliação.', 'Aguardando')";
            try (PreparedStatement psAtend = conn.prepareStatement(sqlAtendimento)) {
                psAtend.setInt(1, idAtendimento);
                psAtend.executeUpdate();
            }

            String sqlHist = "INSERT INTO T_TDB_TRIAGEM_HISTORICO (id_historico, id_atendimento, ds_tipo_procedimento, ds_procedimento, st_status_procedimento, id_funcionario, id_consultorio, id_paciente) " +
                    "VALUES ((SELECT NVL(MAX(id_historico), 0) + 1 FROM T_TDB_TRIAGEM_HISTORICO), ?, 'Triagem', 'Aguardando triagem', 'PENDENTE', 1, 999, ?)";
            try (PreparedStatement psHist = conn.prepareStatement(sqlHist)) {
                psHist.setInt(1, idAtendimento);
                psHist.setInt(2, idPacienteGerado);
                psHist.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erro ao cadastrar paciente: " + e.getMessage(), e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }
    public void atualizar(Paciente paciente) {
        String sql = "UPDATE T_TDB_PACIENTE SET nm_paciente = ?, ds_idade = ?, telefone_paciente = ?, genero = ?, renda_bruta_total = ?, renda_media = ?, score = ?, gravidade_dentaria = ?, ds_escola = ?, ds_status = ?, ds_programa = ? WHERE id_paciente = ?";
        try (Connection conn = getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, paciente.getNome());
            pstm.setInt(2, paciente.getIdade());
            pstm.setString(3, paciente.getTelefone());
            pstm.setString(4, paciente.getGenero().name());
            pstm.setDouble(5, paciente.getRendaBrutaTotal());
            pstm.setDouble(6, paciente.getRendaMedia());
            pstm.setDouble(7, paciente.getScore());
            pstm.setInt(8, paciente.getGravidadeDentaria());
            pstm.setString(9, paciente.getEscola());
            pstm.setString(10, paciente.getStatus());
            pstm.setString(11, paciente.getPrograma());
            pstm.setInt(12, paciente.getId());
            pstm.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o paciente", e);
        }
    }
    public void criarNovoAgendamento(int idPaciente, dto.NovoAgendamentoRequest req) throws Exception {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            int idAtendimento = 1;
            try (PreparedStatement psId = conn.prepareStatement("SELECT NVL(MAX(id_atendimento), 0) + 1 FROM T_TDB_ATENDIMENTO"); ResultSet rs = psId.executeQuery()) {
                if (rs.next()) idAtendimento = rs.getInt(1);
            }
            String sqlAtend = "INSERT INTO T_TDB_ATENDIMENTO (id_atendimento, dt_hr_atendimento, ds_tipo_procedimento, ds_descricao_procedimento, st_status_atendimento, id_medico) VALUES (?, TO_DATE(?, 'YYYY-MM-DD HH24:MI'), ?, 'Agendamento de retorno gerado via painel.', 'AGENDADO', ?)";
            try (PreparedStatement stmtAtend = conn.prepareStatement(sqlAtend)) {
                stmtAtend.setInt(1, idAtendimento);
                stmtAtend.setString(2, req.dataHora);
                stmtAtend.setString(3, req.procedimento);
                stmtAtend.setInt(4, req.idMedico);
                stmtAtend.executeUpdate();
            }
            String sqlHist = "INSERT INTO T_TDB_TRIAGEM_HISTORICO (id_historico, id_atendimento, ds_tipo_procedimento, ds_procedimento, st_status_procedimento, id_funcionario, id_consultorio, id_paciente) VALUES ((SELECT NVL(MAX(id_historico), 0) + 1 FROM T_TDB_TRIAGEM_HISTORICO), ?, ?, ?, 'AGENDADO', 1, 999, ?)";
            try (PreparedStatement stmtHist = conn.prepareStatement(sqlHist)) {
                stmtHist.setInt(1, idAtendimento);
                stmtHist.setString(2, req.procedimento);
                stmtHist.setString(3, "Consulta de retorno agendada.");
                stmtHist.setInt(4, idPaciente);
                stmtHist.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            throw new Exception("Falha no agendamento: " + e.getMessage());
        }
    }
    public List<PacientePainelDTO> listarPacientesPainelAdmin() {
        List<PacientePainelDTO> lista = new ArrayList<>();
        String sql = "SELECT id_paciente, nm_paciente, ds_idade, ds_escola, ds_programa, gravidade_dentaria, nm_dentista, st_status_atendimento, ds_descricao_procedimento, id_atendimento " +
                "FROM (SELECT p.id_paciente, p.nm_paciente, p.ds_idade, p.ds_escola, p.ds_programa, p.gravidade_dentaria, d.nm_dentista, a.st_status_atendimento, a.ds_descricao_procedimento, a.id_atendimento, ROW_NUMBER() OVER (PARTITION BY p.id_paciente ORDER BY a.dt_hr_atendimento DESC NULLS LAST) as rn " +
                "FROM T_TDB_PACIENTE p LEFT JOIN T_TDB_TRIAGEM_HISTORICO h ON h.id_paciente = p.id_paciente LEFT JOIN T_TDB_ATENDIMENTO a ON h.id_atendimento = a.id_atendimento LEFT JOIN T_TDB_DENTISTA_VOLUNTARIO d ON a.id_medico = d.id_medico " +
                "WHERE p.ds_status != 'Aguardando análise') WHERE rn = 1 ORDER BY id_paciente DESC";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PacientePainelDTO dto = new PacientePainelDTO();
                dto.setId(rs.getInt("id_paciente"));
                dto.setNome(rs.getString("nm_paciente"));
                dto.setGravidade(rs.getInt("gravidade_dentaria"));
                dto.setDentista(rs.getString("nm_dentista"));
                dto.setStatus(rs.getString("st_status_atendimento"));
                dto.setObservacao(rs.getString("ds_descricao_procedimento"));
                dto.setIdAtendimento(rs.getInt("id_atendimento"));
                dto.setIdade(rs.getInt("ds_idade"));
                dto.setEscola(rs.getString("ds_escola"));
                dto.setPrograma(rs.getString("ds_programa"));
                lista.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public List<Paciente> listarTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM T_TDB_PACIENTE";
        try (Connection conn = getConnection(); PreparedStatement pstm = conn.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                Paciente p = new Paciente(rs.getInt("id_paciente"), rs.getString("nm_paciente"), rs.getInt("ds_idade"), Genero.valueOf(rs.getString("genero")), rs.getString("telefone_paciente"));
                p.setEmail(rs.getString("email_paciente"));
                p.setRendaMedia(rs.getDouble("renda_media"));
                p.setGravidadeDentaria(rs.getInt("gravidade_dentaria"));
                p.setScore(rs.getDouble("score"));
                p.setEscola(rs.getString("ds_escola"));
                p.setStatus(rs.getString("ds_status"));
                p.setPrograma(rs.getString("ds_programa"));
                pacientes.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pacientes", e);
        }
        return pacientes;
    }
    public Paciente buscarPorId(int id) {
        String sql = "SELECT * FROM T_TDB_PACIENTE WHERE id_paciente = ?";
        try (Connection conn = getConnection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    Paciente p = new Paciente(rs.getString("nm_paciente"), rs.getInt("ds_idade"), Genero.valueOf(rs.getString("genero")), rs.getString("telefone_paciente"));
                    p.setId(rs.getInt("id_paciente"));
                    p.setEmail(rs.getString("email_paciente"));
                    p.setRendaBrutaTotal(rs.getDouble("renda_bruta_total"));
                    p.setRendaMedia(rs.getDouble("renda_media"));
                    p.setScore(rs.getDouble("score"));
                    p.setGravidadeDentaria(rs.getInt("gravidade_dentaria"));
                    p.setEscola(rs.getString("ds_escola"));
                    p.setStatus(rs.getString("ds_status"));
                    p.setPrograma(rs.getString("ds_programa"));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void delete(int id) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM T_TDB_TRIAGEM_HISTORICO WHERE id_paciente = ?");
                 PreparedStatement ps2 = conn.prepareStatement("DELETE FROM T_TDB_PACIENTE WHERE id_paciente = ?")) {
                ps1.setInt(1, id); ps1.executeUpdate();
                ps2.setInt(1, id); ps2.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar", e);
        }
    }
    public void atribuirMedico(int idAtendimento, int idMedico) throws Exception {
        String sql = "UPDATE T_TDB_ATENDIMENTO SET id_medico = ?, st_status_atendimento = 'AGENDADO' WHERE id_atendimento = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setInt(2, idAtendimento);
            if (stmt.executeUpdate() == 0) throw new RuntimeException("Atendimento não encontrado.");
        }
    }
    public void marcarConsulta(int idAtendimento, String dataHora) throws Exception {
        String sql = "UPDATE T_TDB_ATENDIMENTO SET dt_hr_atendimento = TO_DATE(?, 'YYYY-MM-DD HH24:MI'), st_status_atendimento = 'AGENDADO' WHERE id_atendimento = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dataHora);
            stmt.setInt(2, idAtendimento);
            if (stmt.executeUpdate() == 0) throw new RuntimeException("Atendimento não encontrado.");
        }
    }
    public void aprovarPaciente(int id) throws Exception {
        String sql = "UPDATE T_TDB_PACIENTE SET ds_status = 'Sem dentista' WHERE id_paciente = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new Exception("Paciente não encontrado.");
        }
    }
}