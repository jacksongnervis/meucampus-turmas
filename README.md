# MeuCampus - Serviço de Turmas

Microsserviço responsável pela oferta de turmas acadêmicas do sistema integrado **MeuCampus**, desenvolvido para a disciplina de **Desenvolvimento de Sistemas Computacionais** no **Instituto Federal Sul-rio-grandense (IFSUL) - Campus Sapucaia do Sul**.

---

## 1. Como Executar o Serviço

### Pré-requisitos
- **Java 17** (ou superior) instalado e configurado no PATH
- **Docker e Docker Compose** instalados

### Execução do Docker 
1. Na raiz do projeto, inicie o container PostgreSQL:
   ```bash
   docker compose up -d
   ```
   *O banco subirá na porta `5432` com usuário `postgres`, senha `postgres` e database `meucampus_turmas`.*

2. Execute o serviço Spring Boot com o Maven Wrapper:
   ```bash
   # Windows (PowerShell ou CMD)
   .\mvnw.cmd spring-boot:run

   # Linux ou macOS
   ./mvnw spring-boot:run
   ```

### Collection do Postman e Documentação OpenAPI (Swagger)
- **Swagger UI:** Com o serviço em execução, a documentação interativa OpenAPI está disponível em:
  `http://localhost:8082/swagger-ui/index.html` (especificação JSON em `/v3/api-docs`).
- **Collection do Postman:** Para facilitar a execução dos testes pela professora, o projeto também inclui uma collection no arquivo:
  📁 `data/Meu Campus - Turmas.postman_collection.json`

### Carga Inicial de Dados (`CargaInicial`)
Ao inicializar a aplicação, a classe [`CargaInicial`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/config/CargaInicial.java) verifica se a tabela `turma` está vazia (`count() == 0`). Caso esteja, são cadastradas 3 turmas de exemplo (`DSC01`, `BD01` e `ES01`) vinculadas aos `professorId` `1` e `2`, evitando duplicação a cada reinício.

### Para consultas no banco via terminal:
1. Se conecte ao container do PostgreSQL:
   ```bash
   docker exec -it <nome-do-container> psql -U postgres -d meucampus_turmas
   ```

2. Selecione o banco de dados:
   ```sql
   \c meucampus_turmas
   ```
   
3. Liste todas as tabelas:
   ```sql
   \dt
   ```
   
4. Liste todas as colunas de uma tabela:
   ```sql
   \d nome-da-tabela
   ```

5. Realize as consultas.


6. Sair do terminal:
   ```sql
   \q
   ```

---

## 2. Banco de Dados, Entidade e Integração com Serviços

### Entidade e Tabela (`turma`)
O serviço utiliza banco relacional **PostgreSQL 16** (executado via Docker Compose na porta `5432`, database `meucampus_turmas`, usuário/senha `postgres`), acessado com Spring Data JPA.

A entidade principal [`Turma`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/domain/Turma.java) é mapeada para a tabela **`turma`** com as seguintes colunas:

| Coluna | Tipo Java / Banco | Restrições | Descrição |
| :--- | :--- | :--- | :--- |
| `id` | `Long` / `bigint` | PK, auto-incremento (`IDENTITY`) | Identificador único da turma |
| `codigo` | `String` / `varchar` | `NOT NULL`, `UNIQUE` | Código identificador da turma (ex: `DSC01`) |
| `nome_disciplina` | `String` / `varchar` | `NOT NULL` | Nome da disciplina |
| `professor_id` | `Long` / `bigint` | `NOT NULL` | ID do professor responsável (referência ao serviço de Cadastro; sem `@ManyToOne`) |
| `semestre` | `String` / `varchar` | `NOT NULL` | Semestre letivo (ex: `2026/2`) |
| `sala` | `String` / `varchar` | `NOT NULL` | Sala ou laboratório |
| `dias_semana` | `List<DiaSemana>` / array | `NOT NULL` | Dias da semana da aula |
| `horario_inicial` | `LocalTime` / `time` | `NOT NULL` | Horário de início da aula |
| `horario_final` | `LocalTime` / `time` | `NOT NULL` | Horário de término da aula |
| `carga_horaria` | `int` / `integer` | `NOT NULL` | Carga horária total da turma |
| `numero_vagas` | `int` / `integer` | `NOT NULL` | Quantidade máxima de vagas |

### Propriedades de Endereços e Timeouts (`application.yml`)
Os endereços dos demais serviços e os limites de tempo das chamadas HTTP são configurados em [`application.yml`](src/main/resources/application.yml) e utilizados pelo [`RestClientConfig`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/config/RestClientConfig.java):

```yaml
servicos:
  cadastro:
    url: http://localhost:8081
  matriculas:
    url: http://localhost:8083
  timeout:
    conexao-ms: 2000
    leitura-ms: 3000
```

### Endpoints Consumidos do Serviço de Cadastro (Grupo 1)
A comunicação com o serviço de **Cadastro** (porta `8081`) é feita pela classe [`CadastroClient`](src/main/java/br/edu/ifsul/sapucaia/meucampus_turmas/client/CadastroClient.java) usando `RestClient`:
- **`GET /professores/{id}`**:
  - **Na criação e atualização (`POST /turmas` e `PUT /turmas/{id}`):** valida se o `professorId` informado existe no Cadastro. Caso o Cadastro responda `404 Not Found`, a operação é recusada em Turmas com **`400 Bad Request`**.
  - **Na consulta composta (`GET /turmas/{id}/detalhes`):** busca em tempo de execução o `nome` e o `email` do professor responsável para compor a resposta sem gravar dados do professor no banco local.

---

## 3. Anotações do Jackson Utilizadas

Foram aplicadas anotações da biblioteca **Jackson** que alteram diretamente a representação e o tratamento dos dados em JSON:

1. **`@JsonFormat(pattern = "HH:mm")`**
   - **Onde foi aplicada:** Nos campos `horarioInicial` e `horarioFinal` em `TurmaRequestDTO`, `TurmaResponseDTO` e `AtualizarTurmaRequestDTO`.
   - **Justificativa:** Por padrão, o módulo JSR-310 do Jackson serializa objetos `java.time.LocalTime` como arrays de números inteiros (ex: `[19, 45]`). A anotação `@JsonFormat(pattern = "HH:mm")` instrui o serializador e o desserializador a manipular os horários no formato String de 24 horas (`"19:45"`), garantindo legibilidade e conformidade com o padrão JSON da API.

2. **`@JsonProperty("professorId")`**
   - **Onde foi aplicada:** No atributo `professorId` dos DTOs de entrada e saída.
   - **Justificativa:** O uso explícito de `@JsonProperty("professorId")` padroniza o contrato de dados JSON trafegado entre os microsserviços.

3. **`@JsonCreator`**
   - **Onde foi aplicada:** No método fábrica `from(String valor)` do enum `DiaSemana`.
   - **Justificativa:** Permite tratar variações na entrada do usuário. Converte automaticamente caracteres com acentuação (como `"TERÇA"` para `"TERCA"`) e textos em minúsculo, além de interceptar dias inválidos (ex: `"DOMINGO"`) e disparar uma mensagem de erro indicando exatamente quais são os dias aceitos pela instituição.

4. **`@JsonProperty(access = JsonProperty.Access.READ_ONLY)`**
   - **Onde foi aplicada:** No campo `id` em `TurmaResponseDTO`.
   - **Justificativa:** Garante que o identificador numérico gerado pelo banco seja retornado ao cliente, mas impede que clientes forcem um `id` arbitrário no corpo de requisições de criação.

5. **`@JsonUnwrapped`**
   - **Onde foi aplicada:** No campo `turma` em `TurmaDetalhesResponseDTO`.
   - **Justificativa:** Faz com que os atributos da turma apareçam no mesmo nível JSON dos campos `professorNome` e `professorEmail` na resposta de `GET /turmas/{id}/detalhes`.

---

## 4. Regras de Negócio

1. **Unicidade do Código da Turma:**
   - Não é permitido cadastrar duas turmas com o mesmo código. Ao cadastrar (`POST`), verifica-se se o código já existe. Ao atualizar (`PUT`), verifica-se se o novo código já não pertence a outra turma diferente da atual.
2. **Validação de Existência do Professor (Integração Cadastro):**
   - Ao cadastrar (`POST /turmas`) ou atualizar informando um `professorId` (`PUT /turmas/{id}`), o serviço consulta `GET /professores/{id}` no Cadastro. Caso não exista, retorna `400 Bad Request`.
3. **Consistência Temporal dos Horários:**
   - O `horarioFinal` deve ser estritamente posterior ao `horarioInicial`. Caso o usuário envie um horário final anterior ou igual ao inicial, a requisição é rejeitada com `400 Bad Request`.
4. **Validação Estrita de Identificadores (404 Not Found):**
   - Todas as operações direcionadas a um identificador específico (`GET /turmas/{id}`, `GET /turmas/{id}/detalhes`, `PUT /turmas/{id}` e `DELETE /turmas/{id}`) passam pelo serviço centralizador `ValidaIdTurmaService`. Se o `id` não existir no banco de dados, é retornado imediatamente o código **`404 Not Found`** com mensagem em JSON.
5. **Atualização Flexível no PUT:**
   - O endpoint de substituição permite alterar todos os dados ou atualizar campos específicos (preservando os valores existentes no banco para atributos omitidos no payload), garantindo usabilidade sem quebrar a consistência.
6. **Consulta Composta sem Persistência Local:**
   - Em `GET /turmas/{id}/detalhes`, os dados do professor (`professorNome` e `professorEmail`) são buscados no momento da requisição no serviço de Cadastro e não são gravados no banco local.
7. **Tratamento Centralizado com `@RestControllerAdvice`:**
   - Todos os erros retornam no formato JSON padrão da aplicação com `timestamp`, `status`, `error`, `message` e `path`.

---

## 5. Endpoints

Recurso base: **`/turmas`**

### 5.1. Listar Todas as Turmas (com Filtro Opcional por Query String)
- **Método:** `GET`
- **URL:** `/turmas` ou `/turmas/semestre?semestre=2026/2` *(a paginação com `Pageable` e o filtro `?professorId=` serão documentados aqui)*
- **Status:** `200 OK`
- **Exemplo de Resposta (Status 200):**
```json
[
  {
    "id": 1,
    "codigo": "DSC01",
    "nomeDisciplina": "Desenvolvimento de Sistemas",
    "professorId": 10,
    "semestre": "2026/2",
    "sala": "Lab 03",
    "diasSemana": [
      "SEGUNDA",
      "QUARTA"
    ],
    "horarioInicial": "19:00",
    "horarioFinal": "22:15",
    "cargaHoraria": 60,
    "numeroVagas": 35
  }
]
```

---

### 5.2. Recuperar Turma por ID
- **Método:** `GET`
- **URL:** `/turmas/{id}`
- **Status de Sucesso:** `200 OK`
- **Status de Erro:** `404 Not Found` (quando o ID não existe)
- **Exemplo de Resposta (Status 200):**
```json
{
  "id": 1,
  "codigo": "DSC01",
  "nomeDisciplina": "Desenvolvimento de Sistemas",
  "professorId": 10,
  "semestre": "2026/2",
  "sala": "Lab 03",
  "diasSemana": [
    "SEGUNDA",
    "QUARTA"
  ],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 35
}
```

---

### 5.3. Consulta Composta — Detalhes da Turma com Professor
- **Método:** `GET`
- **URL:** `/turmas/{id}/detalhes`
- **Status de Sucesso:** `200 OK`
- **Status de Erro:** `404 Not Found` (turma inexistente) ou `503 Service Unavailable` (serviço de Cadastro indisponível)
- **Exemplo de Resposta (Status 200):**
```json
{
  "id": 1,
  "codigo": "DSC01",
  "nomeDisciplina": "Desenvolvimento de Sistemas Computacionais",
  "professorId": 1,
  "semestre": "2026/2",
  "sala": "Lab 03",
  "diasSemana": [
    "SEGUNDA",
    "QUARTA"
  ],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 30,
  "professorNome": "Manoella Jarces de Azevedo",
  "professorEmail": "manoella@ifsul.edu.br"
}
```

---

### 5.4. Cadastrar Nova Turma
- **Método:** `POST`
- **URL:** `/turmas`
- **Status de Sucesso:** `201 Created`
- **Status de Erro:** `400 Bad Request` (campos inválidos, código duplicado ou `professorId` inexistente no Cadastro) ou `503 Service Unavailable` (serviço de Cadastro indisponível)
- **Exemplo de Requisição:**
```json
{
  "codigo": "DSC01",
  "nomeDisciplina": "Desenvolvimento de Sistemas",
  "professorId": 10,
  "semestre": "2026/2",
  "sala": "Lab 03",
  "diasSemana": [
    "SEGUNDA",
    "QUARTA"
  ],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 35
}
```
- **Exemplo de Resposta (Status 201):**
```json
{
  "id": 1,
  "codigo": "DSC01",
  "nomeDisciplina": "Desenvolvimento de Sistemas",
  "professorId": 10,
  "semestre": "2026/2",
  "sala": "Lab 03",
  "diasSemana": [
    "SEGUNDA",
    "QUARTA"
  ],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 35
}
```

---

### 5.5. Atualizar Turma Existente
- **Método:** `PUT`
- **URL:** `/turmas/{id}`
- **Status de Sucesso:** `200 OK`
- **Status de Erro:** `404 Not Found` (ID inexistente), `400 Bad Request` (regras violadas ou `professorId` inexistente) ou `503 Service Unavailable` (serviço de Cadastro indisponível)
- **Exemplo de Requisição (atualização de sala e vagas):**
```json
{
  "sala": "Lab 05",
  "numeroVagas": 40
}
```
- **Exemplo de Resposta (Status 200):**
```json
{
  "id": 1,
  "codigo": "DSC01",
  "nomeDisciplina": "Desenvolvimento de Sistemas",
  "professorId": 10,
  "semestre": "2026/2",
  "sala": "Lab 05",
  "diasSemana": [
    "SEGUNDA",
    "QUARTA"
  ],
  "horarioInicial": "19:00",
  "horarioFinal": "22:15",
  "cargaHoraria": 60,
  "numeroVagas": 40
}
```

---

### 5.6. Remover Turma
- **Método:** `DELETE`
- **URL:** `/turmas/{id}`
- **Status de Sucesso:** `204 No Content` (corpo vazio)
- **Status de Erro:** `404 Not Found` (ID inexistente)

---

### 5.7. Exemplos de Respostas de Erro Centralizadas 

#### Erro 404 - Registro Não Encontrado:
```json
{
  "timestamp": "2026-09-22",
  "status": 404,
  "error": "Not Found",
  "message": "Id de turma inexistente",
  "path": "/turmas/999"
}
```

#### Erro 400 - Violação de Bean Validation:
```json
{
  "timestamp": "2026-09-22",
  "status": 400,
  "error": "Bad Request",
  "message": "Campo codigo é obrigatório",
  "path": "/turmas"
}
```

#### Erro 400 - Valor de Enum Inválido com Descrição:
```json
{
  "timestamp": "2026-09-22",
  "status": 400,
  "error": "Bad Request",
  "message": "Dia da semana 'DOMINGO' inválido. Valores aceitos: [SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA]",
  "path": "/turmas"
}
```

---

## 6. Divisão de Atividades entre os Integrantes

As atividades foram distribuídas entre os dois integrantes:

- **Nathan Doile:**
  - **T1:** Modelagem inicial da entidade de domínio `Turma` e enum `DiaSemana`, serviços de consulta, `ValidaIdTurmaService`, `ApiExceptionHandler` inicial e collection no Postman.

- **Jackson Gonçalves:**
  - **T1:** DTOs de entrada/saída, anotações Jackson, Bean Validation, serviços de mutação (`Cadastrar`, `Atualizar`, `Deletar`), regras de unicidade/horários/PUT parcial e documentação inicial.
  - **T2:** Base comum (Springdoc OpenAPI, configuração de `RestClient` com timeouts no `application.yml`, padronização `/turmas`), cliente HTTP `CadastroClient` e `ProfessorDTO`, `ValidaProfessorService` (tradução 404 → 400), integração da validação no `POST` e `PUT`, consulta composta `GET /turmas/{id}/detalhes` (`TurmaDetalhesResponseDTO` e `BuscarDetalhesTurmaService`), carga inicial idempotente (`CargaInicial`), documentação Swagger nos endpoints de cadastro/atualização/detalhes e atualização do README (banco, entidade, Cadastro e consulta composta).
