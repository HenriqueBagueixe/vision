package controllers;

import dao.DentistaDAO;
import dao.AgendaDAO;
import entities.Dentista;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

@Path("/dentistas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DentistaResource {

    @Inject
    DentistaDAO dao;

    @Inject
    AgendaDAO agendaDao;

    @Inject
    Mailer mailer;

    @POST
    public Response cadastrarDentista(Dentista novoDentista) {
        try {
            String link = "http://localhost:5173/definir-senha?token=" + novoDentista.getEmail();
            String html = "<h3>Olá Dr(a). " + novoDentista.getNome() + ",</h3>" +
                    "<p>Seu cadastro na plataforma Vision foi realizado com sucesso!</p>" +
                    "<p>Para acessar o sistema e visualizar sua agenda, você precisa definir uma senha de segurança.</p>" +
                    "<br><a href='" + link + "' style='padding: 10px 20px; background-color: #f58200; color: white; text-decoration: none; border-radius: 5px;'>Definir minha senha</a>";

            mailer.send(Mail.withHtml(novoDentista.getEmail(), "Bem-vindo à Vision - Defina sua senha", html));
            dao.salvar(novoDentista);

            return Response.status(Response.Status.CREATED).entity(novoDentista).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao processar cadastro: " + e.getMessage()).build();
        }
    }

    @GET
    public Response listarDentistas() {
        try {
            return Response.ok(dao.listar()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar dentistas no banco: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/agenda")
    public Response getAgenda(@PathParam("id") int idMedico) {
        try {
            return Response.ok(agendaDao.buscarAgendaDoDentista(idMedico)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar agenda: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/atendimento/{idAtendimento}")
    public Response atualizarAtendimento(@PathParam("idAtendimento") int idAtendimento, dto.AtualizarAtendimentoRequest req) {
        try {
            agendaDao.atualizarStatusAtendimento(idAtendimento, req.status, req.descricao);
            return Response.ok().entity("Atendimento atualizado com sucesso!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao salvar atendimento: " + e.getMessage()).build();
        }
    }
}