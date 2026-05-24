package controllers;

import dao.PacienteDAO;
import dao.AgendaDAO;
import entities.Paciente;
import jakarta.inject.Inject;
import services.PacienteService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteResource {

    @Inject
    PacienteDAO dao;

    @Inject
    PacienteService service;
    @Inject
    AgendaDAO agendaDao;

    @GET
    public Response listarPacientes() {
        List<Paciente> lista = dao.listarTodos();
        return Response.ok(lista).build();
    }

    @POST
    public Response cadastrarPaciente(Paciente novoPaciente) {
        try {
            if (novoPaciente.getStatus() == null || novoPaciente.getStatus().trim().isEmpty()) {
                novoPaciente.setStatus("Sem dentista");
            }

            if (novoPaciente.getPrograma() == null) novoPaciente.setPrograma("apolonias");
            if (novoPaciente.getEscola() == null || novoPaciente.getEscola().isEmpty())
                novoPaciente.setEscola("Cadastro Externo");
            if (novoPaciente.getGenero() == null) novoPaciente.setGenero(entities.Genero.FEMININO);

            double rendaBruta = novoPaciente.getRendaBrutaTotal() > 0 ? novoPaciente.getRendaBrutaTotal() : 3000.00;
            int membrosFamilia = 1;
            int gravidade = 5;

            service.cadastrarEProcessarPaciente(novoPaciente, rendaBruta, membrosFamilia, gravidade);
            return Response.status(Response.Status.CREATED).entity(novoPaciente).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro no servidor: " + e.getMessage()).build();
        }
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
            dao.atualizar(pacienteAtualizado);
            return Response.ok(pacienteAtualizado).build();
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/agenda-geral")
    public Response getAgendaGeral() {
        try {
            return Response.ok(agendaDao.buscarAgendaGeralAdmin()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/novo-agendamento")
    public Response criarNovoAgendamento(@PathParam("id") int idPaciente, dto.NovoAgendamentoRequest req) {
        try {
            dao.criarNovoAgendamento(idPaciente, req);
            return Response.status(Response.Status.CREATED).entity("Novo agendamento salvo com sucesso.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar agendamento no histórico: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/painel-admin")
    @Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response buscarPacientesPainel() {
        try {
            java.util.List<dto.PacientePainelDTO> pacientes = dao.listarPacientesPainelAdmin();
            return Response.ok(pacientes).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao processar dados do painel: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{idAtendimento}/atribuir-medico/{idMedico}")
    public Response atribuirDentista(@PathParam("idAtendimento") int idAtendimento, @PathParam("idMedico") int idMedico) {
        try {
            dao.atribuirMedico(idAtendimento, idMedico);
            return Response.ok("Dentista atribuído com sucesso.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atribuir dentista: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{idAtendimento}/marcar-consulta")
    public Response marcarConsulta(@PathParam("idAtendimento") int idAtendimento, java.util.Map<String, String> payload) {
        try {
            String dataHora = payload.get("dataHora");
            if (dataHora == null || dataHora.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Data e hora não fornecidas.").build();
            }

            dao.marcarConsulta(idAtendimento, dataHora);
            return Response.ok("Consulta agendada com sucesso.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao agendar consulta: " + e.getMessage())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "PUT, OPTIONS")
                    .build();
        }
    }

    @PUT
    @Path("/{id}/aprovar")
    public Response aprovarPaciente(@PathParam("id") int id) {
        try {
            dao.aprovarPaciente(id);
            return Response.ok("Paciente aprovado para o fluxo principal.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao aprovar: " + e.getMessage()).build();
        }
    }
}