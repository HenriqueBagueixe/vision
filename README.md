# Backend Vision - Turma do Bem

## Sobre o projeto
O Vision é uma API desenvolvida para gerenciar e automatizar a triagem de pacientes da ONG Turma do Bem. Construído em Java utilizando o framework Quarkus, o back-end orquestra as operações de três perfis: Pacientes, Dentistas Voluntários e Funcionários.

A principal regra de negócio do sistema é a substituição da fila de espera por ordem de chegada por um sistema automatizado de Score. O algoritmo cruza dados de renda per capita, idade, gênero e gravidade clínica para calcular um peso e definir a prioridade real de atendimento. O sistema também barra automaticamente na origem cadastros que violam as regras dos programas sociais parceiros (como o limite de idade do programa Dentistas do Bem).

---
(Ou você pode acessar [ https://vision-tech-platform.vercel.app ]. O projeto já está in live com o render.
## Como rodar o projeto localmente

### Pré-requisitos
* Java 17 ou superior
* Maven
* Acesso à rede ou VPN da FIAP (necessário para o banco Oracle responder)

### Passo a Passo

**1. Clone o repositório**
Baixe o código-fonte e abra a pasta raiz na sua IDE. Aguarde o Maven sincronizar e baixar as dependências.

**2. Configure o acesso ao Oracle**
Vá no arquivo `src/main/resources/application.properties` e insira as credenciais do banco. Sem isso, a aplicação aborta a inicialização por falha no Agroal (pool de conexões).

```properties
quarkus.datasource.jdbc.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
quarkus.datasource.username=SEU_RM_AQUI
quarkus.datasource.password=SUA_SENHA_AQUI

**3. Teste de E-mail (Mock)**
Para evitar crashes por bloqueio de porta SMTP na rede da faculdade ou local, a aplicação está com o serviço de e-mail mockado (quarkus.mailer.mock=true). As requisições de cadastro que disparam convites e senhas não vão travar, mas o link gerado pelo Quarkus Mailer será impresso apenas nos logs do console, em vez de enviado para uma caixa real.

**4. Inicie o servidor**
Abra o terminal na pasta raiz do projeto e execute:
