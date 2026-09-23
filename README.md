# MeuCampus - Serviço de Turmas (Grupo 2)

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

### Collection do Postman
Para facilitar a execução dos testes pela professora, o projeto já inclui uma collection completa e pronta para importação no arquivo:
📁 `data/Meu Campus - Turmas.postman_collection.json`

---

## 2. Anotações do Jackson uUilizadas

Foram aplicadas anotações da biblioteca **Jackson** que alteram diretamente a representação e o tratamento dos dados em JSON:

1. **`@JsonFormat(pattern = "HH:mm")`**
   - **Onde foi aplicada:** Nos campos `horarioInicial` e `horarioFinal` em `TurmaRequestDTO`, `TurmaResponseDTO`, `AtualizarTurmaRequestDTO` e `BuscarTurmaResponse`.
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

---

## 3. Regras de Negócio

1. **Unicidade do Código da Turma:**
   - Não é permitido cadastrar duas turmas com o mesmo código. Ao cadastrar (`POST`), verifica-se se o código já existe. Ao atualizar (`PUT`), verifica-se se o novo código já não pertence a outra turma diferente da atual.
2. **Consistência Temporal dos Horários:**
   - O `horarioFinal` deve ser estritamente posterior ao `horarioInicial`. Caso o usuário envie um horário final anterior ou igual ao inicial, a requisição é rejeitada com `400 Bad Request`.
3. **Validação Estrita de Identificadores (404 Not Found):**
   - Todas as operações direcionadas a um identificador específico (`GET /turma/{id}`, `PUT /turma/{id}` e `DELETE /turma/{id}`) passam pelo serviço centralizador `ValidaIdTurmaService`. Se o `id` não existir no banco de dados, é retornado imediatamente o código **`404 Not Found`** com mensagem em JSON.
4. **Atualização Flexível no PUT:**
   - O endpoint de substituição permite alterar todos os dados ou atualizar campos específicos (preservando os valores existentes no banco para atributos omitidos no payload), garantindo usabilidade sem quebrar a consistência.
5. **Tratamento Centralizado com `@RestControllerAdvice`:**
   - Todos os erros retornam no formato JSON padrão da aplicação com `timestamp`, `status`, `error`, `message` e `path`.

---

## 4. Endpoints

Recurso base: **`/turma`**

### 4.1. Listar Todas as Turmas (com Filtro Opcional por Query String)
- **Método:** `GET`
- **URL:** `/turma` ou `/turma/semestre?semestre=2026/2`
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

### 4.2. Recuperar Turma por ID
- **Método:** `GET`
- **URL:** `/turma/{id}`
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

### 4.3. Cadastrar Nova Turma
- **Método:** `POST`
- **URL:** `/turma`
- **Status de Sucesso:** `201 Created`
- **Status de Erro:** `400 Bad Request` (validação de campos obrigatórios ou regras de negócio)
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

### 4.4. Atualizar Turma Existente
- **Método:** `PUT`
- **URL:** `/turma/{id}`
- **Status de Sucesso:** `200 OK`
- **Status de Erro:** `404 Not Found` (ID inexistente) ou `400 Bad Request` (regras violadas)
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

### 4.5. Remover Turma
- **Método:** `DELETE`
- **URL:** `/turma/{id}`
- **Status de Sucesso:** `204 No Content` (corpo vazio)
- **Status de Erro:** `404 Not Found` (ID inexistente)

---

### 4.6. Exemplos de Respostas de Erro Centralizadas 

#### Erro 404 - Registro Não Encontrado:
```json
{
  "timestamp": "2026-09-22",
  "status": 404,
  "error": "Not Found",
  "message": "Id de turma inexistente",
  "path": "/turma/999"
}
```

#### Erro 400 - Violação de Bean Validation:
```json
{
  "timestamp": "2026-09-22",
  "status": 400,
  "error": "Bad Request",
  "message": "Campo codigo O código da turma é obrigatório",
  "path": "/turma"
}
```

#### Erro 400 - Valor de Enum Inválido com Descrição:
```json
{
  "timestamp": "2026-09-22",
  "status": 400,
  "error": "Bad Request",
  "message": "Dia da semana 'DOMINGO' inválido. Valores aceitos: [SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA]",
  "path": "/turma"
}
```

---

## 5. Divisão de Atividades entre os Integrantes

As atividades foram distribuídas entre os dois integrantes:

- **Nathan Doile:**
  - Modelagem inicial da entidade de domínio `Turma` e enum `DiaSemana`.
  - Implementação dos serviços de consulta: `BuscarTurmasService`, `BuscarTurmasPorSemestreService` e `BuscarTurmaPorIdService`.
  - Criação do validador de existência de registros (`ValidaIdTurmaService`).
  - Estruturação inicial do manipulador centralizado de exceções (`ApiExceptionHandler`).
  - Elaboração da collection de requisições no Postman (`Meu Campus - Turmas.postman_collection.json`).

- **Jackson Gonçalves:**
  - Definição e implementação dos DTOs de transferência: `TurmaRequestDTO`, `TurmaResponseDTO` e `AtualizarTurmaRequestDTO`.
  - Configuração das anotações do Jackson (`@JsonFormat`, `@JsonProperty`, `@JsonCreator`) e validações de entrada (Bean Validation).
  - Implementação dos serviços de mutação: `CadastrarTurmaService`, `AtualizarTurmaService` e `DeletarTurmaService`.
  - Implementação das regras de unicidade de código, consistência de horários e atualização seletiva no `PUT`.
  - Mapeamento JPA para persistência de coleções no banco de dados e elaboração da documentação técnica no `README.md`.
