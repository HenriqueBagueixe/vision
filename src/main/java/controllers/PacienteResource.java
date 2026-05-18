package controllers;

import dao.PacienteDAO;
import entities.Genero;
import entities.Paciente;
import services.PacienteService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteResource {

    private PacienteDAO dao = new PacienteDAO();
    private PacienteService service = new PacienteService();

    @GET
    public Response listarPacientes() {
        List<Paciente> lista = dao.listarTodos();
        return Response.ok(lista).build();
    }

    @POST
    public Response cadastrarPaciente(Paciente novoPaciente) {
        try {
            //segurança preenchimento automatico
            if (novoPaciente.getStatus() == null) novoPaciente.setStatus("Sem dentista");
            if (novoPaciente.getPrograma() == null) novoPaciente.setPrograma("apolonias");
            if (novoPaciente.getEscola() == null || novoPaciente.getEscola().isEmpty())
                novoPaciente.setEscola("Cadastro Externo");
            if (novoPaciente.getGenero() == null) novoPaciente.setGenero(entities.Genero.FEMININO);
            double rendaBruta = novoPaciente.getRendaBrutaTotal() > 0 ? novoPaciente.getRendaBrutaTotal() : 3000.00;
            int membrosFamilia = 1;
            int gravidade = 5;
            novoPaciente.setIdHistorico(1);

            service.cadastrarEProcessarPaciente(novoPaciente, rendaBruta, membrosFamilia, gravidade);
            return Response.status(Response.Status.CREATED).entity(novoPaciente).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro no servidor: " + e.getMessage()).build();
        }
    }

    @OPTIONS
    public Response preflight() {
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizarPaciente(@PathParam("id") int id, Paciente pacienteAtualizado) {
        try {
            Paciente pacienteExistente = dao.buscarPorId(id);
            if (pacienteExistente == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Paciente não encontrado para atualização.").build();
            }

            pacienteAtualizado.setId(id);
            pacienteAtualizado.setIdHistorico(1);

            dao.atualizar(pacienteAtualizado);
            return Response.ok(pacienteAtualizado).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletarPaciente(@PathParam("id") int id) {
        try {
            Paciente pacienteExistente = dao.buscarPorId(id);
            if (pacienteExistente == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("O paciente já não existe no banco.").build();
            }

            dao.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar: " + e.getMessage()).build();
        }
    }

    //agenda
    @GET
    @Path("/agenda-geral")
    public Response getAgendaGeral() {
        try {
            dao.AgendaDAO dao = new dao.AgendaDAO();
            return Response.ok(dao.buscarAgendaGeralAdmin()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    //

    @GET
    @Path("/painel-admin")
    @Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response buscarPacientesPainel() {
        try {
            java.util.List<dto.PacientePainelDTO> pacientes = dao.listarPacientesPainelAdmin();
            return Response.ok(pacientes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao processar dados do painel: " + e.getMessage()).build();
        }
    }
}