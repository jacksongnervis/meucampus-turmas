# MeuCampus - Serviço de Turmas

Microsserviço responsável pela oferta de turmas acadêmicas do sistema integrado **MeuCampus**, desenvolvido para a disciplina de **Desenvolvimento de Sistemas Computacionais** no **Instituto Federal Sul-rio-grandense (IFSUL) - Campus Sapucaia do Sul**.

O serviço responde na porta **`8082`** e conversa por HTTP com os serviços de **Cadastro** (Grupo 1, porta `8081`) e **Matrículas** (Grupo 3, porta `8083`).

---

## 1. Como Executar o Serviço

### Pré-requisitos
- **Java 17** (ou superior) instalado e configurado no PATH
- **Docker e Docker Compose** instalados

### Execução
1. Na raiz do projeto, inicie o container PostgreSQL:
   ```bash
   docker compose up -d
   ```
   *O banco sobe na porta `5432` com usuário `postgres`, senha `postgres` e database `meucampus_turmas`.*

2. Execute o serviço Spring Boot com o Maven Wrapper:
   ```bash
   # Windows (PowerShell ou CMD)
   .\mvnw.cmd spring-boot:run

   # Linux ou macOS
   ./mvnw spring-boot:run
   ```
   *Se aparecer "Permissão negada" no Linux/macOS, rode `chmod +x mvnw` uma vez.*

3. O serviço fica disponível em `http://localhost:8082`.

### Como subir os três serviços juntos para testar
Cada serviço tem o seu próprio projeto e o seu próprio banco. Para testar a integração:

1. Suba o banco de cada serviço (no caso de Turmas, `docker compose up -d` na raiz deste projeto).
2. Em terminais separados, suba cada serviço na sua porta:

   | Serviço | Grupo | Porta | Comando (na raiz de cada projeto) |
   | :--- | :--- | :--- | :--- |
   | Cadastro | 1 | `8081` | `./mvnw spring-boot:run` |
   | Turmas | 2 | `8082` | `./mvnw spring-boot:run` |
   | Matrículas | 3 | `8083` | `./mvnw spring-boot:run` |

3. A ordem de inicialização não importa: as chamadas entre serviços só acontecem quando uma requisição chega. Se um serviço dependente ainda não estiver no ar, Turmas devolve **`503`**.
4. A carga inicial de Turmas usa os `professorId` **`1`** e **`2`**, que devem existir na carga inicial do serviço de Cadastro.

### Documentação OpenAPI (Swagger) e Collection do Postman
- **Swagger UI:** `http://localhost:8082/swagger-ui/index.html` (especificação JSON em `/v3/api-docs`). Lista todos os endpoints, os filtros consumidos pelos outros grupos e as respostas `400`, `404`, `409` e `503`.
- **Collection do Postman:** `data/Meu Campus - Turmas.postman_collection.json`, organizada em três pastas (listagem e filtros, CRUD e consulta composta, casos de erro). A URL base fica na variável `baseUrl` da collection.

### Carga Inicial de Dados (`CargaInicial`)
Ao inicializar a aplicação, a classe [`CargaInicial`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/config/CargaInicial.java) verifica se a tabela `turma` está vazia (`count() == 0`). Caso esteja, são cadastradas 3 turmas de exemplo, evitando duplicação a cada reinício:

| Código | Disciplina | Professor | Sala | Dias | Horário | Vagas |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `DSC01` | Desenvolvimento de Sistemas Computacionais | `1` | `lab 03` | SEGUNDA, QUARTA | 19:00–22:15 | 30 |
| `BD01` | Banco de Dados | `2` | `sala 12` | TERCA | 19:00–22:15 | 25 |
| `ES01` | Engenharia de Software | `1` | `sala 08` | QUINTA | 19:00–20:40 | 2 |

---

## 2. Banco de Dados

### Banco utilizado e como acessá-lo
O serviço utiliza **PostgreSQL 16**, executado via Docker Compose, acessado com **Spring Data JPA** (Hibernate). O schema é criado e atualizado automaticamente (`spring.jpa.hibernate.ddl-auto: update`).

| Propriedade | Valor |
| :--- | :--- |
| Host / porta | `localhost:5432` |
| Database | `meucampus_turmas` |
| Usuário / senha | `postgres` / `postgres` |
| URL JDBC | `jdbc:postgresql://localhost:5432/meucampus_turmas` |

Para consultar o banco pelo terminal:
```bash
docker compose exec db psql -U postgres -d meucampus_turmas
```
```sql
\dt                  -- lista as tabelas
\d turma             -- mostra as colunas da tabela turma
SELECT * FROM turma; -- consulta os registros
\q                   -- sai do psql
```

As configurações do banco e dos outros serviços ficam em [`application.yml`](src/main/resources/application.yml) (equivalente ao `application.properties` citado no enunciado, no formato YAML).

### Entidade e Tabela (`turma`)
A entidade [`Turma`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/domain/Turma.java) é mapeada com `@Entity`, `@Id` e `@GeneratedValue(strategy = IDENTITY)` para a tabela **`turma`**:

| Coluna | Tipo Java / Banco | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | `Long` / `bigint` | PK, auto-incremento (`IDENTITY`) | Identificador único da turma |
| `codigo` | `String` / `varchar` | `NOT NULL`, `UNIQUE` | Código da turma (ex: `DSC01`) |
| `nome_disciplina` | `String` / `varchar` | `NOT NULL` | Nome da disciplina |
| `professor_id` | `Long` / `bigint` | `NOT NULL` | ID do professor no serviço de Cadastro. Apenas o identificador numérico, sem `@ManyToOne`, porque o professor mora em outro banco |
| `semestre` | `String` / `varchar` | `NOT NULL` | Semestre letivo (ex: `2026/2`) |
| `sala` | `String` / `varchar` | `NOT NULL` | Sala ou laboratório, gravada sem espaços nas pontas e em minúsculo (ex: `lab 03`) para a verificação de conflito de horário |
| `dias_semana` | `List<DiaSemana>` / `smallint[]` | `NOT NULL` | Dias da semana da aula, gravados como array |
| `horario_inicial` | `LocalTime` / `time` | `NOT NULL` | Horário de início da aula |
| `horario_final` | `LocalTime` / `time` | `NOT NULL` | Horário de término da aula |
| `carga_horaria` | `int` / `integer` | `NOT NULL` | Carga horária total da turma |
| `numero_vagas` | `int` / `integer` | `NOT NULL` | Quantidade máxima de vagas |

### Repositório e consultas
O [`TurmaRepository`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/repository/TurmaRepository.java) é uma interface que estende `JpaRepository<Turma, Long>`. É a única camada que acessa o banco.

| Método | Tipo | Uso |
| :--- | :--- | :--- |
| `findAll(Pageable)` | herdado do `JpaRepository` | Listagem sem filtros |
| `findAllByProfessorId(Long, Pageable)` | consulta derivada | Filtro `?professorId=` |
| `findAllBySemestre(String, Pageable)` | consulta derivada | Filtro `?semestre=` |
| `findAllByProfessorIdAndSemestre(Long, String, Pageable)` | consulta derivada | Os dois filtros juntos |
| `existsByCodigo` / `existsByCodigoAndIdNot` | consulta derivada | Unicidade do código |
| `existsConflitoPorHorario(...)` | consulta nativa (`@NativeQuery`) | Conflito de horário na sala |

---

## 3. Integração com Outros Serviços

As chamadas aos outros serviços ficam no pacote [`client`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/client), feitas com `RestClient`. Somente a camada de service usa os clients.

### Propriedades com os endereços dos outros serviços (`application.yml`)
Os endereços e os tempos limite nunca ficam escritos no código. São lidos pelo [`RestClientConfig`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/config/RestClientConfig.java), que cria um `RestClient` para cada serviço:

```yaml
servicos:
  cadastro:
    url: http://localhost:8081
  matriculas:
    url: http://localhost:8083
  timeout:
    conexao-ms: 2000   # tempo máximo para abrir a conexão
    leitura-ms: 3000   # tempo máximo esperando a resposta
```

### Endpoints consumidos de outros grupos

| Serviço (grupo) | Chamada | Client | Para quê |
| :--- | :--- | :--- | :--- |
| Cadastro (Grupo 1) | `GET /professores/{id}` | [`CadastroClient`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/client/CadastroClient.java) | Conferir se o `professorId` existe no `POST` e no `PUT`; buscar `nome` e `email` do professor em `GET /turmas/{id}/detalhes` |
| Matrículas (Grupo 3) | `GET /matriculas?turmaId={id}&situacao=ATIVA&size=1` | [`MatriculasClient`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/client/MatriculasClient.java) | Impedir a remoção de turma com matrículas ativas |

- Do Cadastro são lidos apenas os campos `id`, `nome` e `email` (`ProfessorDTO`); os demais são ignorados.
- A resposta de Matrículas é uma página (`Page`). Turmas lê apenas a lista `content` (`PaginaMatriculasDTO`) e verifica se ela está vazia. O `size=1` evita trazer mais registros do que o necessário.
- A situação da matrícula é escrita como **`ATIVA`** ou **`TRANCADA`**, conforme combinado com o Grupo 3.

### Endpoints de Turmas consumidos por outros grupos

| Quem chama | Chamada | Para quê |
| :--- | :--- | :--- |
| Cadastro (Grupo 1) | `GET /turmas?professorId={id}` | Impedir a remoção de professor responsável por turma |
| Matrículas (Grupo 3) | `GET /turmas/{id}` | Conferir se a turma existe e consultar `numeroVagas` |

A listagem `GET /turmas` é **paginada**. O formato da resposta (veja a seção 6.1) faz parte do contrato: quem consome deve ler a lista em `content` ou o total em `page.totalElements`.

### Tratamento de falhas na comunicação
Todos os erros são tratados de forma centralizada no [`ApiExceptionHandler`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/config/ApiExceptionHandler.java) (`@RestControllerAdvice`):

| Situação | Exceção | Resposta de Turmas |
| :--- | :--- | :--- |
| Serviço dependente fora do ar | `ResourceAccessException` | **`503 Service Unavailable`** |
| Tempo limite de conexão ou de leitura excedido | `ResourceAccessException` | **`503 Service Unavailable`** |
| Serviço dependente respondeu `5xx` ou outro erro inesperado | `RestClientResponseException` | **`503 Service Unavailable`** |
| Cadastro respondeu `404` para o professor | tratado no `CadastroClient` | **`400 Bad Request`** no `POST`/`PUT` |
| Matrículas respondeu `404` | tratado no `MatriculasClient` | considerado "sem matrículas ativas" |

Nenhum erro de outro serviço chega ao cliente como `500`, e nenhuma operação é concluída sem a conferência.

---

## 4. Regras de Negócio e de Integridade

1. **Unicidade do código da turma:** não é permitido cadastrar duas turmas com o mesmo código. No `POST` verifica-se se o código já existe; no `PUT`, se o novo código não pertence a outra turma. Violação → `400`.
2. **Existência do professor (integração com Cadastro):** no `POST` e no `PUT` que informa `professorId`, o serviço consulta `GET /professores/{id}` no Cadastro. Professor inexistente → `400`. Cadastro indisponível → `503`.
3. **Remoção bloqueada por matrículas ativas (integração com Matrículas):** antes de remover, o serviço consulta `GET /matriculas?turmaId={id}&situacao=ATIVA`. Se houver matrícula ativa → `409 Conflict`. Matrículas indisponível → `503`.
4. **Conflito de horário na sala:** não podem existir duas turmas na **mesma sala**, no **mesmo semestre**, com **pelo menos um dia da semana em comum** e **horários sobrepostos**. Dois intervalos se sobrepõem quando `inicioExistente < fimNovo` **e** `fimExistente > inicioNovo`. Turmas encostadas (uma termina às 15:00 e a outra começa às 15:00) são permitidas. A verificação vale no `POST` e no `PUT` (no `PUT`, com os valores finais da turma, ignorando a própria turma). Violação → `409 Conflict`.
5. **Consistência dos horários:** o `horarioFinal` deve ser estritamente posterior ao `horarioInicial`. Violação → `400`.
6. **Identificadores inexistentes:** toda operação sobre um `id` (`GET`, `PUT`, `DELETE` e `/detalhes`) passa pelo `ValidaIdTurmaService`. Turma inexistente → `404`.
7. **Atualização parcial no PUT:** o `PUT` altera apenas os campos enviados e preserva os demais. Decisão do grupo para facilitar a manutenção dos dados; as validações acima são aplicadas sobre o resultado final.
8. **Consulta composta sem persistência local:** em `GET /turmas/{id}/detalhes`, o nome e o e-mail do professor são buscados no Cadastro no momento da requisição e nunca são gravados no banco de Turmas. Se o professor não existir mais no Cadastro, a turma é devolvida com `professorNome` e `professorEmail` nulos.
9. **Validação de entrada:** os DTOs de entrada usam Bean Validation (`@NotBlank`, `@NotNull`, `@NotEmpty`, `@Positive`, `@Size`). Parâmetros com tipo inválido (ex.: `?professorId=abc`) e ordenação por campo inexistente (ex.: `?sort=xyz`) também devolvem `400`.

---

## 5. Anotações do Jackson Utilizadas

1. **`@JsonFormat(pattern = "HH:mm")`**
   - **Onde:** campos `horarioInicial` e `horarioFinal` de `TurmaRequestDTO`, `AtualizarTurmaRequestDTO` e `TurmaResponseDTO`.
   - **Por quê:** define que os horários trafegam como texto no formato 24 horas (`"19:45"`), tanto na leitura quanto na escrita, em vez da representação padrão do `LocalTime`, que inclui segundos e frações.

2. **`@JsonCreator`**
   - **Onde:** método fábrica `from(String valor)` do enum `DiaSemana`.
   - **Por quê:** trata variações na entrada. Aceita minúsculas e acentuação (`"terça"` vira `TERCA`) e, para um dia inválido (ex.: `"DOMINGO"`), devolve `400` com a lista de valores aceitos.

3. **`@JsonUnwrapped`**
   - **Onde:** campo `turma` de `TurmaDetalhesResponseDTO`.
   - **Por quê:** coloca os atributos da turma no mesmo nível do JSON que `professorNome` e `professorEmail` em `GET /turmas/{id}/detalhes`, em vez de um objeto `turma` aninhado.

4. **`@JsonIgnoreProperties(ignoreUnknown = true)`**
   - **Onde:** DTOs do pacote `client` (`ProfessorDTO`, `MatriculaDTO`, `PaginaMatriculasDTO`).
   - **Por quê:** Turmas lê apenas os campos de que precisa nas respostas dos outros serviços. Campos a mais, ou adicionados no futuro por outro grupo, são ignorados em vez de quebrar a integração.

5. **`@JsonProperty(access = JsonProperty.Access.READ_ONLY)`**
   - **Onde:** campo `id` de `TurmaResponseDTO`.
   - **Por quê:** marca o `id` como somente leitura no contrato: ele é gerado pelo banco e devolvido ao cliente, nunca informado por ele.

6. **`@JsonProperty("professorId")`**
   - **Onde:** atributo `professorId` dos DTOs de entrada e saída.
   - **Por quê:** fixa explicitamente o nome do campo que os outros serviços usam como referência ao professor, protegendo o contrato contra renomeações do atributo Java.

---

## 6. Endpoints

Recurso base: **`/turmas`**. Todos os detalhes também estão no Swagger UI.

| Método | URL | Sucesso | Erros |
| :--- | :--- | :--- | :--- |
| `GET` | `/turmas` | `200` | `400` |
| `GET` | `/turmas/{id}` | `200` | `400`, `404` |
| `GET` | `/turmas/{id}/detalhes` | `200` | `404`, `503` |
| `POST` | `/turmas` | `201` | `400`, `409`, `503` |
| `PUT` | `/turmas/{id}` | `200` | `400`, `404`, `409`, `503` |
| `DELETE` | `/turmas/{id}` | `204` | `404`, `409`, `503` |

### 6.1. Listar Turmas (paginado, com filtros)
- **Método:** `GET`
- **URL:** `/turmas`
- **Parâmetros de query (todos opcionais):**

  | Parâmetro | Exemplo | Descrição |
  | :--- | :--- | :--- |
  | `professorId` | `1` | Turmas do professor (contrato com o Cadastro) |
  | `semestre` | `2026/2` | Turmas do semestre |
  | `page` | `0` | Número da página (começa em 0). Padrão: `0` |
  | `size` | `10` | Itens por página. Padrão: `10` |
  | `sort` | `codigo,desc` | Campo e direção da ordenação. Padrão: `nomeDisciplina,asc` |

- **Exemplos:** `/turmas`, `/turmas?professorId=1`, `/turmas?semestre=2026/2&page=0&size=5&sort=codigo,desc`
- **Status:** `200 OK` (com `content` vazio quando nenhum registro atende ao filtro) ou `400 Bad Request` (parâmetro inválido)
- **Exemplo de Resposta (`GET /turmas?professorId=1`):**
```json
{
  "content": [
    {
      "id": 1,
      "codigo": "DSC01",
      "nomeDisciplina": "Desenvolvimento de Sistemas Computacionais",
      "professorId": 1,
      "semestre": "2026/2",
      "sala": "lab 03",
      "diasSemana": ["SEGUNDA", "QUARTA"],
      "horarioInicial": "19:00",
      "horarioFinal": "22:15",
      "cargaHoraria": 60,
      "numeroVagas": 30
    },
    {
      "id": 3,
      "codigo": "ES01",
      "nomeDisciplina": "Engenharia de Software",
      "professorId": 1,
      "semestre": "2026/2",
      "sala": "sala 08",
      "diasSemana": ["QUINTA"],
      "horarioInicial": "19:00",
      "horarioFinal": "20:40",
      "cargaHoraria": 30,
      "numeroVagas": 2
    }
  ],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

### 6.2. Recuperar Turma por ID
- **Método:** `GET`
- **URL:** `/turmas/{id}`
- **Status:** `200 OK`, `400 Bad Request` (id em formato inválido) ou `404 Not Found`
- **Exemplo de Resposta (200):**
```json
{
  "id": 1,
  "codigo": "DSC01",
  "nomeDisciplina": "Desenvolvimento de Sistemas Computacionais",
  "professorId": 1,
  "semestre": "2026/2",
  "sala": "lab 03",
  "diasSemana": ["SEGUNDA", "QUARTA"],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 30
}
```

---

### 6.3. Consulta Composta — Detalhes da Turma com Professor
- **Método:** `GET`
- **URL:** `/turmas/{id}/detalhes`
- **Status:** `200 OK`, `404 Not Found` (turma inexistente) ou `503 Service Unavailable` (Cadastro indisponível)
- **Exemplo de Resposta (200):**
```json
{
  "id": 1,
  "codigo": "DSC01",
  "nomeDisciplina": "Desenvolvimento de Sistemas Computacionais",
  "professorId": 1,
  "semestre": "2026/2",
  "sala": "lab 03",
  "diasSemana": ["SEGUNDA", "QUARTA"],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 30,
  "professorNome": "Manoella Jarces de Azevedo",
  "professorEmail": "manoella@ifsul.edu.br"
}
```

---

### 6.4. Cadastrar Nova Turma
- **Método:** `POST`
- **URL:** `/turmas`
- **Status:** `201 Created`, `400 Bad Request` (campos inválidos, código duplicado ou `professorId` inexistente no Cadastro), `409 Conflict` (conflito de horário na sala) ou `503 Service Unavailable` (Cadastro indisponível)
- **Exemplo de Requisição:**
```json
{
  "codigo": "POO01",
  "nomeDisciplina": "Programação Orientada a Objetos",
  "professorId": 1,
  "semestre": "2026/2",
  "sala": "Lab 05",
  "diasSemana": ["SEXTA"],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 30
}
```
- **Exemplo de Resposta (201):**
```json
{
  "id": 4,
  "codigo": "POO01",
  "nomeDisciplina": "Programação Orientada a Objetos",
  "professorId": 1,
  "semestre": "2026/2",
  "sala": "lab 05",
  "diasSemana": ["SEXTA"],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 30
}
```

---

### 6.5. Atualizar Turma Existente
- **Método:** `PUT`
- **URL:** `/turmas/{id}`
- **Status:** `200 OK`, `400 Bad Request` (regras violadas ou `professorId` inexistente), `404 Not Found`, `409 Conflict` (conflito de horário na sala) ou `503 Service Unavailable` (Cadastro indisponível)
- **Exemplo de Requisição (atualização parcial de sala e vagas):**
```json
{
  "sala": "Lab 07",
  "numeroVagas": 40
}
```
- **Exemplo de Resposta (200):**
```json
{
  "id": 1,
  "codigo": "DSC01",
  "nomeDisciplina": "Desenvolvimento de Sistemas Computacionais",
  "professorId": 1,
  "semestre": "2026/2",
  "sala": "lab 07",
  "diasSemana": ["SEGUNDA", "QUARTA"],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 40
}
```

---

### 6.6. Remover Turma
- **Método:** `DELETE`
- **URL:** `/turmas/{id}`
- **Status:** `204 No Content` (corpo vazio), `404 Not Found`, `409 Conflict` (turma com matrículas ativas) ou `503 Service Unavailable` (Matrículas indisponível)

---

### 6.7. Exemplos de Respostas de Erro
Todos os erros seguem o mesmo formato JSON, montado no `ApiExceptionHandler`.

#### 400 - Violação de Bean Validation
```json
{
  "timestamp": "2026-11-21T19:30:12.345",
  "status": 400,
  "error": "Bad Request",
  "message": "Campo codigo é obrigatório",
  "path": "/turmas"
}
```

#### 400 - Professor inexistente no Cadastro
```json
{
  "timestamp": "2026-11-21T19:30:12.345",
  "status": 400,
  "error": "Bad Request",
  "message": "Professor de id 999 não encontrado no serviço de Cadastro",
  "path": "/turmas"
}
```

#### 400 - Dia da semana inválido
```json
{
  "timestamp": "2026-11-21T19:30:12.345",
  "status": 400,
  "error": "Bad Request",
  "message": "Dia da semana 'DOMINGO' inválido. Valores aceitos: [SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA]",
  "path": "/turmas"
}
```

#### 404 - Turma não encontrada
```json
{
  "timestamp": "2026-11-21T19:30:12.345",
  "status": 404,
  "error": "Not Found",
  "message": "Id de turma inexistente",
  "path": "/turmas/999"
}
```

#### 409 - Conflito de horário
```json
{
  "timestamp": "2026-11-21T19:30:12.345",
  "status": 409,
  "error": "Conflict",
  "message": "Há turma cadastrada nessa sala que conflita com seu horário.",
  "path": "/turmas"
}
```

#### 409 - Turma com matrículas ativas
```json
{
  "timestamp": "2026-11-21T19:30:12.345",
  "status": 409,
  "error": "Conflict",
  "message": "Turma não pode ser deletada pois possui matrícula(s) ativa(s).",
  "path": "/turmas/1"
}
```

#### 503 - Serviço dependente indisponível
```json
{
  "timestamp": "2026-11-21T19:30:12.345",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Serviço dependente indisponível ou tempo limite excedido. Tente novamente mais tarde.",
  "path": "/turmas/1/detalhes"
}
```

---

## 7. Divisão de Atividades entre os Integrantes

- **Nathan Doile:**
  - **T1:** Modelagem inicial da entidade de domínio `Turma` e enum `DiaSemana`, serviços de consulta, `ValidaIdTurmaService`, `ApiExceptionHandler` inicial e collection no Postman.
  - **T2:** Regra de conflito de horário na sala (`ValidaConflitoHorarioTurmaService` e consulta nativa) no `POST` e no `PUT`; integração com Matrículas (`MatriculasClient`, `PaginaMatriculasDTO`, `MatriculaDTO`, `ValidaMatriculasService`) e recusa da remoção com `409`; listagem paginada com `Pageable` e filtros `professorId`/`semestre` por consultas derivadas; tratamento de falhas (`503` para indisponibilidade, timeout e erros do serviço dependente; `400` para parâmetros inválidos); documentação Swagger dos endpoints restantes; atualização da collection do Postman e do README.

- **Jackson Gonçalves:**
  - **T1:** DTOs de entrada/saída, anotações Jackson, Bean Validation, serviços de mutação (`Cadastrar`, `Atualizar`, `Deletar`), regras de unicidade/horários/PUT parcial e documentação inicial.
  - **T2:** Base comum (Springdoc OpenAPI, configuração de `RestClient` com timeouts no `application.yml`, padronização `/turmas`), cliente HTTP `CadastroClient` e `ProfessorDTO`, `ValidaProfessorService` (tradução 404 → 400), integração da validação no `POST` e `PUT`, consulta composta `GET /turmas/{id}/detalhes` (`TurmaDetalhesResponseDTO` e `BuscarDetalhesTurmaService`), carga inicial idempotente (`CargaInicial`), documentação Swagger nos endpoints de cadastro/atualização/detalhes e atualização do README (banco, entidade, Cadastro e consulta composta).
