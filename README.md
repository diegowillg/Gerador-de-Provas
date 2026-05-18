# Sistema Gerador de Provas

Projeto desenvolvido para a disciplina **Aplicações para Internet**.

**Aluno:** Diego William Gonçalves  
**RA:** 1180470

## Sobre o projeto

Eu desenvolvi um sistema web em Java para cadastrar questões e montar provas. A aplicação usa o padrão **MVC**, além das camadas **DAO** e **Service**, para deixar o código separado e mais fácil de entender.

O sistema possui CRUD de questões e também permite criar provas selecionando questões já cadastradas no banco.

## Funcionalidades

- Cadastrar, listar, editar e excluir questões.
- Filtrar questões por disciplina.
- Criar, listar, editar e excluir provas.
- Selecionar questões para montar uma prova.
- Visualizar e imprimir a prova.

## Tecnologias usadas

- Java
- Servlet e JSP
- Maven
- Tomcat 10
- MySQL
- HTML, CSS e JavaScript

## Organização do projeto

```text
src/main/java/br/edu/geradorprovas
├── config      Conexão com o banco e configuração UTF-8
├── controller  Controllers e rotas do sistema
├── dao         Classes que acessam o banco de dados
├── model       Classes que representam Questao e Prova
└── service     Regras de negócio e validações

src/main/webapp
├── WEB-INF/views  Telas JSP
├── css            Estilo da aplicação
└── js             JavaScript da interface
```

## Banco de dados

O projeto usa o **MySQL instalado no notebook**.

Configuração local:

```text
host: localhost
porta: 3308
banco: gerador_provas
usuario: root
senha: configurada localmente em src/main/resources/db.properties
```

O script do banco está em:

```text
database/schema.sql
```

Para criar o banco manualmente no PowerShell:

```powershell
Get-Content .\database\schema.sql -Encoding UTF8 | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -h 127.0.0.1 -P 3308 -u root -p --default-character-set=utf8mb4
```

As configurações usadas pela aplicação ficam em:

```text
src/main/resources/db.properties
```

## Como rodar

Neste notebook, o Maven e o Tomcat ficam na pasta local `.tools`.
O MySQL usado é o serviço instalado no Windows, na porta `3308`.

### Abrir com dois cliques

Na pasta do projeto, execute o arquivo:

```text
abrir-projeto.bat
```

Ele verifica o MySQL, compila o projeto, inicia o Tomcat e abre o sistema no navegador.

### Abrir pelo terminal

Antes de iniciar, confira se o serviço `MySQL80` está rodando. Se precisar iniciar:

```powershell
Start-Service MySQL80
```

Para configurar o banco, compilar o projeto, publicar o `.war` no Tomcat e abrir o servidor local:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\start-local.ps1
```

Depois acesse:

```text
http://localhost:8080/gerador-provas/home
```

Para parar o Tomcat:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\stop-local.ps1
```

O comando acima não para o MySQL, porque ele é um serviço instalado no notebook.

### Rodar manualmente

Gerar o arquivo `.war` com o Maven:

```powershell
.\.tools\apache-maven-3.9.11\bin\mvn.cmd clean package
```

Depois copie o arquivo abaixo para a pasta `webapps` do Tomcat:

```text
target/gerador-provas.war
```

Inicie o Tomcat e acesse:

```powershell
.\.tools\apache-tomcat-10.1.44\bin\startup.bat
```

```text
http://localhost:8080/gerador-provas/home
```

## O que mostrar na apresentação

1. Abrir a tela inicial.
2. Mostrar o CRUD de questões.
3. Criar uma questão nova.
4. Editar e excluir uma questão.
5. Criar uma prova selecionando questões.
6. Visualizar a prova pronta para impressão.

## Estrutura MVC

No projeto, o `FrontController` recebe as requisições e envia para o controller correto.

Exemplo:

```text
/questoes -> QuestaoController -> QuestaoService -> QuestaoDAO -> MySQL
```

As telas ficam nos arquivos JSP dentro de `WEB-INF/views`.

## Comentários no código

O código foi comentado por camadas para facilitar a manutenção:

- `controller`: explica como as rotas recebem ações e escolhem as telas.
- `service`: explica as regras de negócio e validações.
- `dao`: explica o acesso JDBC ao MySQL.
- `model`: explica os atributos que representam as tabelas.
- `webapp`: explica JSPs, estilos, impressão e interações em JavaScript.
- `scripts`: explica como o ambiente local prepara Maven, Tomcat, banco e execução.
