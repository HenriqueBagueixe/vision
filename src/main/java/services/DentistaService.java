package services;

import entities.Paciente;

public class DentistaService {
    private final PacienteService pacienteService = new PacienteService();

    public  void definirGravidade(Paciente paciente, int gravidadeDefinida){
        if (gravidadeDefinida >= 1 && gravidadeDefinida <= 10){
            paciente.setGravidadeDentaria(gravidadeDefinida);
            int idade = paciente.getIdade();
            String genero = paciente.getGenero().name();
            pacienteService.gerarScore(paciente, gravidadeDefinida, genero, idade);
            System.out.print("Gravidade dentária definido com sucesso.\n");
        } else{
            System.out.println("Score inválido.");
        }
    }
}
