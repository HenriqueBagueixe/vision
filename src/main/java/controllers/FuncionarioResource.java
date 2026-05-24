package controllers;

import dao.FuncionarioDAO;
import dto.FuncionarioDTO;
import entities.Funcionario;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/funcionarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FuncionarioResource {

    @Inject
    FuncionarioDAO dao;

    @Inject
    Mailer mailer;

    @POST
    public Response cadastrarFuncionario(Funcionario novoFuncionario) {
        try {
            String link = "http://localhost:5173/definir-senha?token=" + novoFuncionario.getEmail();
            String texto = "Olá " + novoFuncionario.getNome() + ",\n\n" +
                    "Seu cadastro na plataforma Vision foi realizado com sucesso!\n" +
                    "Para acessar o sistema, você precisa definir uma senha de segurança.\n\n" +
                    "Clique no link abaixo para criar sua senha:\n" +
                    link;

            mailer.send(Mail.withText(novoFuncionario.getEmail(), "Bem-vindo à Vision - Defina sua senha", texto));
            dao.salvar(novoFuncionario);

            return Response.status(Response.Status.CREATED).entity(novoFuncionario).build();

        } catch (java.sql.SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro no banco de dados Oracle: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno (Falha de e-mail ou sistema): " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorId(@PathParam("id") int id) {
        try {
            FuncionarioDTO funcionario = dao.buscarPorId(id);
            if (funcionario != null) {
                return Response.ok(funcionario).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Funcionário não encontrado no banco.").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar funcionário: " + e.getMessage()).build();
        }
    }
}