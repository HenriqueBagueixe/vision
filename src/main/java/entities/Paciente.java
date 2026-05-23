package entities;

public class Paciente extends Pessoa{
    private int idade;
    private Genero genero;
    private double rendaBrutaTotal;
    private int gravidadeDentaria;
    private double score;
    private String notacoes;
    private int totalMembrosRenda;
    private double rendaMedia;
    private int idHistorico;
    private String escola;
    private String status;
    private String programa;
    private String email;

    public Paciente() {
    }

    public String getPrioridade() {
        if (this.score >= 70) return "Alta";
        if (this.score >= 40) return "Média";
        return "Baixa";
    }

    public Paciente(String nome, int idade, Genero genero, String telefone) {
        super(nome, telefone);
        this.idade = idade;
        this.genero = genero;
    }
    public Paciente(int id, String nome, int idade, Genero genero, String telefone){
        super(id, nome, telefone);
        this.idade = idade;
        this.genero = genero;
    }
    public int getIdade() {
        return idade;
    }
    public void setIdade(int idade) {
        this.idade = idade;
    }
    public Genero getGenero() {
        return genero;
    }
    public void setGenero(Genero genero) {
        this.genero = genero;
    }
    public double getRendaBrutaTotal() {
        return rendaBrutaTotal;
    }
    public void setRendaBrutaTotal(double rendaBrutaTotal) {
        this.rendaBrutaTotal = rendaBrutaTotal;
    }
    public int getGravidadeDentaria() {
        return gravidadeDentaria;
    }
    public void setGravidadeDentaria(int gravidadeDentaria) {
        this.gravidadeDentaria = gravidadeDentaria;
    }
    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }
    public String getNotacoes() {
        return notacoes;
    }
    public void setNotacoes(String notacoes) {
        this.notacoes = notacoes;
    }
    public int getTotalMembrosRenda() {
        return totalMembrosRenda;
    }
    public void setTotalMembrosRenda(int totalMembrosRenda) {
        this.totalMembrosRenda = totalMembrosRenda;
    }
    public double getRendaMedia() {
        return rendaMedia;
    }
    public void setRendaMedia(double rendaMedia) {
        this.rendaMedia = rendaMedia;
    }
    public int getIdHistorico() {
        return idHistorico;
    }
    public void setIdHistorico(int idHistorico) {
        this.idHistorico = idHistorico;
    }
    public String getEscola() {
        return escola;
    }
    public void setEscola(String escola) {
        this.escola = escola;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPrograma() {
        return programa;
    }
    public void setPrograma(String programa) {
        this.programa = programa;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
