package dao;

import entities.Dentista;
import factory.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DentistaDAO {

    public void salvar(Dentista dentista) throws SQLException {
        String sql = "INSERT INTO T_TDB_DENTISTA_VOLUNTARIO " +
                "(id_medico, dt_hr_disponivel, nm_dentista, ds_especialidade, nr_cro, ds_consultorio_vinculado, st_status_dentista, id_consultorio, ds_email) " +
                "VALUES (seq_dentista.NEXTVAL, SYSDATE, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentista.getNome());
            stmt.setString(2, dentista.getEspecialidade());
            stmt.setString(3, dentista.getCro());
            stmt.setString(4, dentista.getConsultorioVinculado() != null ? dentista.getConsultorioVinculado() : "Matriz");
            stmt.setString(5, dentista.getStatusDentista() != null ? dentista.getStatusDentista() : "ATIVO");
            stmt.setInt(6, dentista.getIdConsultorio() > 0 ? dentista.getIdConsultorio() : 1);

            stmt.setString(7, dentista.getEmail());

            stmt.executeUpdate();
        }
    }

    public List<Dentista> listar() throws SQLException {
        List<Dentista> dentistas = new ArrayList<>();
        String sql = "SELECT id_medico, nm_dentista FROM T_TDB_DENTISTA_VOLUNTARIO WHERE st_status_dentista = 'ATIVO'";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Dentista d = new Dentista();
                d.setId(rs.getInt("id_medico"));
                d.setNome(rs.getString("nm_dentista"));
                dentistas.add(d);
            }
        }
        return dentistas;
    }
}