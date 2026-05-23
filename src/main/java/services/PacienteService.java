package services;

import dao.PacienteDAO;
import entities.Paciente;

public class PacienteService {

    private final PacienteDAO pacienteDAO;

    public PacienteService(){
        this.pacienteDAO = new PacienteDAO();
    }

    public void cadastrarEProcessarPaciente(Paciente paciente, double rendaBrutaTotal, int totalMembrosRenda, int gravidadeDentaria){
        if (paciente.getPrograma() != null && paciente.getPrograma().equalsIgnoreCase("Dentistas do Bem")) {
            if (paciente.getIdade() > 17) {
                throw new IllegalArgumentException("O programa Dentistas do Bem é exclusivo para menores de 18 anos.");
            }
        }
        paciente.setRendaBrutaTotal(rendaBrutaTotal);
        paciente.setGravidadeDentaria(gravidadeDentaria);
        calcularRegistrarRendaMedia(paciente, rendaBrutaTotal, totalMembrosRenda);
        gerarScore(paciente, gravidadeDentaria, paciente.getGenero().name(), paciente.getIdade());
        pacienteDAO.cadastrar(paciente);
    }
    public void calcularRegistrarRendaMedia(Paciente paciente, double rendaBrutaTotal, int totalMembrosRenda){
        double rendaPerCapta = (totalMembrosRenda > 0) ? (rendaBrutaTotal / totalMembrosRenda) : 0;
        paciente.setRendaMedia(rendaPerCapta);
    }

    public void gerarScore(Paciente paciente, int gravidadeDentaria, String genero, int idade){
        double idadeCalculo = 1.0;
        if(idade >= 17){
            idadeCalculo = 30;
        } else if(idade >= 14 && idade <= 16){
            idadeCalculo = 10;
        } else if(idade >= 11 && idade <= 13){
            idadeCalculo = 5;
        }

        double score = (gravidadeDentaria * idade / 10.0) * idadeCalculo;
        if(genero != null && genero.equalsIgnoreCase("FEMININO")){
            score = score * 2.0;
        }

        paciente.setScore(score);
    }
}