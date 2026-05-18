package entities;

public class Dentista extends Pessoa {
    private String nome;
    private String cro;
    private String especialidade;
    private String email;
    private String sobrenome;
    private String consultorioVinculado;
    private String statusDentista;
    private int idConsultorio;

    public Dentista() {
    }

    public Dentista(String nome, String telefone, String cro, String especialidade) {
        super(nome, telefone);
        this.especialidade = especialidade;
        this.cro = cro;
    }
    public Dentista(int id, String nome, String telefone, String cro, String especialidade) {
        super(id, nome, telefone);
        this.cro = cro;
        this.especialidade = especialidade;
    }
    public String getCro() {
        return cro;
    }
    public void setCro(String cro) {
        this.cro = cro;
    }
    public String getEspecialidade() {
        return especialidade;
    }
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSobrenome() {
        return sobrenome;
    }
    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getConsultorioVinculado() {
        return consultorioVinculado;
    }
    public void setConsultorioVinculado(String consultorioVinculado) {
        this.consultorioVinculado = consultorioVinculado;
    }
    public String getStatusDentista() {
        return statusDentista;
    }
    public void setStatusDentista(String statusDentista) {
        this.statusDentista = statusDentista;
    }
    public int getIdConsultorio() {
        return idConsultorio;
    }
    public void setIdConsultorio(int idConsultorio) {
        this.idConsultorio = idConsultorio;
    }
}