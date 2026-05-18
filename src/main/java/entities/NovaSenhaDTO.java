package entities;

public class NovaSenhaDTO {
    private String token;
    private String password;

    public NovaSenhaDTO() {}
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}