package dto;

public class AgendaDTO {
    private int idAtendimento;
    private String dataHora;
    private String procedimento;
    private String status;
    private String nomePaciente;
    private int gravidade;
    private String nomeDentista;
    private String observacao;

    public int getIdAtendimento() { return idAtendimento; }
    public void setIdAtendimento(int idAtendimento) { this.idAtendimento = idAtendimento; }
    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }
    public String getProcedimento() { return procedimento; }
    public void setProcedimento(String procedimento) { this.procedimento = procedimento; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNomePaciente() { return nomePaciente; }
    public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }
    public int getGravidade() { return gravidade; }
    public void setGravidade(int gravidade) { this.gravidade = gravidade; }
    public String getNomeDentista() {
        return nomeDentista;
    }
    public void setNomeDentista(String nomeDentista) {
        this.nomeDentista = nomeDentista;
    }
    public String getObservacao() {
        return observacao;
    }
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}