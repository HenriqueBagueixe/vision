package controllers;

import dao.AuthDAO;
import entities.LoginDTO;
import entities.NovaSenhaDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)


public class AuthResource {

    private AuthDAO dao = new AuthDAO();

    @POST
    @Path("/create-password")
    public Response criarSenha(NovaSenhaDTO dados) {
        try {
            dao.criarSenha(dados.getToken(), dados.getPassword());
            return Response.status(Response.Status.CREATED).entity("Senha salva com sucesso.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro: " + e.getMessage()).build();
        }
    }


    @POST
    @Path("/login")
    public Response logar(LoginDTO dados) {
        try {
            String resultado = dao.fazerLogin(dados.getEmail(), dados.getSenha());

            if (resultado.equals("INVALIDO")) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("E-mail ou senha incorretos.").build();
            }
            return Response.ok(resultado).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}