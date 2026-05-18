package dao;
import dto.PacientePainelDTO;
import entities.Genero;
import entities.Paciente;
import factory.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public void cadastrar(Paciente paciente) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            int idAtendimento = 1;
            try (PreparedStatement psIdAtend = conn.prepareStatement("SELECT NVL(MAX(id_atendimento), 0) + 1 FROM T_TDB_ATENDIMENTO");
                 ResultSet rsAtend = psIdAtend.executeQuery()) {
                if (rsAtend.next()) idAtendimento = rsAtend.getInt(1);
            }
            try (PreparedStatement psAtend = conn.prepareStatement("INSERT INTO T_TDB_ATENDIMENTO (id_atendimento, st_status_atendimento, ds_tipo_procedimento) VALUES (?, 'Aguardando', 'Triagem Inicial')")) {
                psAtend.setInt(1, idAtendimento);
                psAtend.executeUpdate();
            }
            int idHistorico = 1;
            try (PreparedStatement psIdHist = conn.prepareStatement("SELECT NVL(MAX(id_historico), 0) + 1 FROM T_TDB_TRIAGEM_HISTORICO");
                 ResultSet rsHist = psIdHist.executeQuery()) {
                if (rsHist.next()) idHistorico = rsHist.getInt(1);
            }
            try (PreparedStatement psHist = conn.prepareStatement("INSERT INTO T_TDB_TRIAGEM_HISTORICO (id_historico, id_atendimento) VALUES (?, ?)")) {
                psHist.setInt(1, idHistorico);
                psHist.setInt(2, idAtendimento);
                psHist.executeUpdate();
            }
            String sql = "INSERT INTO T_TDB_PACIENTE (id_paciente, nm_paciente, ds_idade, telefone_paciente, " +
                    "genero, renda_bruta_total, renda_media, score, gravidade_dentaria, id_historico, ds_escola, ds_status, ds_programa) " +
                    "VALUES (seq_paciente.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstm = conn.prepareStatement(sql)) {
                pstm.setString(1, paciente.getNome());
                pstm.setInt(2, paciente.getIdade());
                pstm.setString(3, paciente.getTelefone());
                pstm.setString(4, paciente.getGenero() != null ? paciente.getGenero().name() : "FEMININO");
                pstm.setDouble(5, paciente.getRendaBrutaTotal());
                pstm.setDouble(6, paciente.getRendaMedia());
                pstm.setDouble(7, paciente.getScore());
                pstm.setInt(8, paciente.getGravidadeDentaria());
                pstm.setInt(9, idHistorico); // O histórico verdadeiro amarrado aqui
                pstm.setString(10, paciente.getEscola());
                pstm.setString(11, "Aguardando");
                pstm.setString(12, paciente.getPrograma());

                pstm.executeUpdate();
            }

            conn.commit();
            System.out.println("Cadastro efetuado com sucesso! Estrutura do paciente gerada.");

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Erro ao gerar paciente: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public List<Paciente> listarTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM T_TDB_PACIENTE";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                Paciente p = new Paciente(
                        rs.getInt("id_paciente"),
                        rs.getString("nm_paciente"),
                        rs.getInt("ds_idade"),
                        Genero.valueOf(rs.getString("genero")),
                        rs.getString("telefone_paciente")
                );

                p.setRendaMedia(rs.getDouble("renda_media"));
                p.setGravidadeDentaria(rs.getInt("gravidade_dentaria"));
                p.setScore(rs.getDouble("score"));
                p.setEscola(rs.getString("ds_escola"));
                p.setStatus(rs.getString("ds_status"));
                p.setPrograma(rs.getString("ds_programa"));
                pacientes.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar a lista de pacientes", e);
        }
        return pacientes;
    }

    public java.util.List<dto.PacientePainelDTO> listarPacientesPainelAdmin() {
        java.util.List<dto.PacientePainelDTO> lista = new java.util.ArrayList<>();

        String sql = "SELECT p.id_paciente, p.nm_paciente, p.ds_idade, p.ds_escola, p.ds_programa, p.gravidade_dentaria, " +
                "d.nm_dentista, a.st_status_atendimento, a.ds_descricao_procedimento, " +
                "a.id_atendimento " +
                "FROM T_TDB_PACIENTE p " +
                "LEFT JOIN T_TDB_TRIAGEM_HISTORICO h ON p.id_historico = h.id_historico " +
                "LEFT JOIN T_TDB_ATENDIMENTO a ON h.id_atendimento = a.id_atendimento " +
                "LEFT JOIN T_TDB_DENTISTA_VOLUNTARIO d ON a.id_medico = d.id_medico " +
                "ORDER BY p.id_paciente DESC";

        try (java.sql.Connection conn = factory.ConnectionFactory.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dto.PacientePainelDTO dto = new dto.PacientePainelDTO();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void atualizar(Paciente paciente){
        String sql = "UPDATE T_TDB_PACIENTE SET nm_paciente = ?, ds_idade = ?, telefone_paciente = ?, " +
                "genero = ?, renda_bruta_total = ?, renda_media = ?, score = ?, gravidade_dentaria = ?, id_historico = ?, " +
                "ds_escola = ?, ds_status = ?, ds_programa = ? WHERE id_paciente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)){

            pstm.setString(1, paciente.getNome());
            pstm.setInt(2, paciente.getIdade());
            pstm.setString(3, paciente.getTelefone());
            pstm.setString(4, paciente.getGenero().name());
            pstm.setDouble(5, paciente.getRendaBrutaTotal());
            pstm.setDouble(6, paciente.getRendaMedia());
            pstm.setDouble(7, paciente.getScore());
            pstm.setInt(8, paciente.getGravidadeDentaria());
            pstm.setInt(9, paciente.getIdHistorico());
            pstm.setString(10, paciente.getEscola());
            pstm.setString(11, paciente.getStatus());
            pstm.setString(12, paciente.getPrograma());
            pstm.setInt(13, paciente.getId());

            pstm.execute();
            System.out.println("Dados do paciente atualizados com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o paciente.", e);
        }
    }

    public void delete(int id){
        String sql = "DELETE FROM T_TDB_PACIENTE WHERE id_paciente = ?";

        try (Connection conn =  ConnectionFactory.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)){

            pstm.setInt(1, id);
            pstm.execute();
            System.out.println("Paciente deletado com sucesso.");
        }catch (SQLException e){
            throw new RuntimeException("Erro ao deletar paciente");
        }
    }

    public Paciente buscarPorId(int id) {
        String sql = "SELECT * FROM T_TDB_PACIENTE WHERE id_paciente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, id);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    Paciente p = new Paciente(
                            rs.getString("nm_paciente"),
                            rs.getInt("ds_idade"),
                            Genero.valueOf(rs.getString("genero")),
                            rs.getString("telefone_paciente")
                    );
                    p.setId(rs.getInt("id_paciente"));
                    p.setRendaBrutaTotal(rs.getDouble("renda_bruta_total"));
                    p.setRendaMedia(rs.getDouble("renda_media"));
                    p.setScore(rs.getDouble("score"));
                    p.setGravidadeDentaria(rs.getInt("gravidade_dentaria"));
                    p.setIdHistorico(rs.getInt("id_historico"));
                    p.setEscola(rs.getString("ds_escola"));
                    p.setStatus(rs.getString("ds_status"));
                    p.setPrograma(rs.getString("ds_programa"));

                    return p;
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar paciente: " + e.getMessage());
        }
        return null;
    }

    public void atribuirMedico(int idAtendimento, int idMedico) throws Exception {
        String sql = "UPDATE T_TDB_ATENDIMENTO SET id_medico = ?, st_status_atendimento = 'AGENDADO' WHERE id_atendimento = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMedico);
            stmt.setInt(2, idAtendimento);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Nenhum atendimento encontrado com o ID fornecido.");
            }
            System.out.println("Dentista atribuído com sucesso ao atendimento " + idAtendimento);
        }
    }

    public void marcarConsulta(int idAtendimento, String dataHora) throws Exception {
        String sql = "UPDATE T_TDB_ATENDIMENTO SET dt_hr_atendimento = TO_DATE(?, 'YYYY-MM-DD HH24:MI'), st_status_atendimento = 'AGENDADO' WHERE id_atendimento = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dataHora);
            stmt.setInt(2, idAtendimento);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Nenhum atendimento encontrado com o ID fornecido.");
            }
            System.out.println("Data e hora agendadas com sucesso no atendimento " + idAtendimento);
        }
    }
}