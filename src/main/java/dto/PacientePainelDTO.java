package dto;

public class PacientePainelDTO {
    private int id;
    private String nome;
    private int gravidade;
    private String dentista;
    private String status;
    private String observacao;
    private int idAtendimento;
    private int idade;
    private String escola;
    private String programa;

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getGravidade() { return gravidade; }
    public void setGravidade(int gravidade) { this.gravidade = gravidade; }
    public String getDentista() { return dentista; }
    public void setDentista(String dentista) { this.dentista = dentista; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public int getIdAtendimento() { return idAtendimento; }
    public void setIdAtendimento(int idAtendimento) { this.idAtendimento = idAtendimento; }
    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }
    public String getEscola() { return escola; }
    public void setEscola(String escola) { this.escola = escola; }
    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }
}