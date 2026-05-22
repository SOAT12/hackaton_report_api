# report-api

Microsserviço responsável pela geração, armazenamento e gestão de relatórios de arquitetura produzidos a partir da análise de diagramas.

Faz parte do MVP do HACKATHON INTEGRADO - IA PARA DEVS (IADT) +
SOFTWARE ARCHITECTURE (SOAT) da FIAP, composto por três microsserviços:

| Serviço | Responsabilidade |
|---|---|
| **diagram-api** | Orquestração, persistência e exposição de status |
| **diagram-processor** | Consumo da fila SQS e análise do diagrama com IA |
| **report-api** *(este)* | Geração do relatório PDF e armazenamento no S3 |

---

## Descrição do Problema

Em sistemas de análise de arquitetura de software, após o processamento de um diagrama por um modelo de IA, é necessário consolidar os resultados em um documento estruturado e disponibilizá-lo de forma persistente para consulta e auditoria.

O desafio consiste em:

- Receber o resultado da análise de um diagrama — contendo **componentes identificados**, **riscos arquiteturais** e **recomendações** — proveniente do `diagram-processor`
- Gerar automaticamente um relatório em **PDF bem formatado** com essas informações
- Armazenar o PDF em um bucket **Amazon S3** e retornar o link de acesso
- Persistir os metadados do relatório em banco de dados para consultas futuras
- Notificar outros serviços sobre a conclusão do relatório via fila **Amazon SQS**, para que a `diagram-api` atualize o status do diagrama correspondente

---

## Arquitetura Proposta

O serviço adota os princípios de **Clean Architecture**, com separação clara entre camadas de domínio, aplicação e infraestrutura, garantindo baixo acoplamento e alta testabilidade.

```
┌─────────────────────────────────────────────────────┐
│               Cliente / diagram-api                 │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP REST
┌──────────────────────▼──────────────────────────────┐
│                  ReportController                   │
│              (Infrastructure — Web)                 │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                    Use Cases                        │
│   CreateReport │ GetReport │ ListReports │ Update   │
│              (Application Layer)                    │
└────────┬──────────────────────────┬─────────────────┘
         │                          │
┌────────▼────────┐     ┌───────────▼─────────────────┐
│    MongoDB      │     │        AWS Services          │
│  (metadados)    │     │  S3 (PDF) │ SQS (eventos)   │
└─────────────────┘     └─────────────────────────────┘
```

### Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0 |
| Persistência | MongoDB (Spring Data) |
| Armazenamento de PDFs | Amazon S3 (AWS SDK v2) |
| Mensageria | Amazon SQS (AWS SDK v2) |
| Geração de PDF | Apache PDFBox 3.0 |
| Documentação | SpringDoc OpenAPI (Swagger UI) |
| Containerização | Docker |
| CI/CD | GitHub Actions → Amazon ECR → EC2 |
| Testes | JUnit 5 + Mockito + AssertJ |
| Cobertura | JaCoCo (mínimo 85%) |

### Estrutura de Pacotes

```
src/main/java/com/hackaton/reportapi/
├── application/
│   ├── dto/            # Objetos de entrada e saída da API
│   └── usecase/        # Casos de uso (orquestração de regras de negócio)
├── config/             # Configurações Spring: AWS, ObjectMapper
├── domain/
│   ├── entity/         # Entidades de domínio (Report, ReportContent, ReportStatus)
│   ├── event/          # Eventos publicados (ReportStatusEvent)
│   └── gateway/        # Interfaces de acesso a repositório e serviços externos
├── exceptions/         # Tratamento global de erros (GlobalExceptionHandler)
└── infrastructure/
    ├── controller/     # Endpoints REST
    ├── db/             # Implementação MongoDB (entidades, mapper, repositório)
    ├── messaging/      # Publicação de eventos no SQS
    ├── pdf/            # Geração de relatório em PDF
    └── storage/        # Upload de arquivos no S3
```

### Estratégias de Qualidade e Segurança

- **Tratamento global de exceções:** `GlobalExceptionHandler` garante que erros retornem JSON padronizado sem expor stacktraces
- **Validação de entrada:** Bean Validation (`@NotNull`, `@NotBlank`) nos DTOs de request
- **Acesso seguro à AWS:** Credenciais via variáveis de ambiente, gerenciadas pela cadeia padrão do AWS SDK
- **Logs estruturados:** Logback com Logstash Encoder em JSON, compatível com CloudWatch e ELK

---

## Fluxo da Solução

### Criação de Relatório — `POST /api/reports`

```
diagram-processor
      │
      │  POST /api/reports
      │  { diagramId, title, report: { components, risks, recommendations } }
      ▼
ReportController  ──►  CreateReportUseCase
                               │
                   ┌───────────▼────────────┐
                   │  1. Monta entidade     │
                   │     Report com UUID    │
                   └───────────┬────────────┘
                               │
                   ┌───────────▼────────────┐
                   │  2. Gera PDF           │
                   │     (PDFBox)           │
                   └───────────┬────────────┘
                               │
                   ┌───────────▼────────────┐
                   │  3. Upload S3          │
                   │  reports/{id}.pdf      │
                   │  ← retorna URL pública │
                   └───────────┬────────────┘
                               │
                   ┌───────────▼────────────┐
                   │  4. Persiste no        │
                   │     MongoDB c/ URL     │
                   └───────────┬────────────┘
                               │
                   ┌───────────▼────────────┐
                   │  5. Publica evento SQS │
                   │  { diagramId, status,  │
                   │    reportLink }        │
                   └───────────┬────────────┘
                               │
                      201 Created
                   { id, reportUrl, status: COMPLETED, ... }
```

### Endpoints Disponíveis

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/reports` | Cria relatório, gera PDF, salva no S3 |
| `GET` | `/api/reports/{id}` | Busca metadados e link do PDF |
| `GET` | `/api/reports?status=COMPLETED` | Lista relatórios com filtro opcional de status |
| `PATCH` | `/api/reports/{id}/status` | Atualiza status e publica evento SQS |

### Statuses do Relatório

| Status | Descrição |
|---|---|
| `COMPLETED` | Relatório gerado e disponível no S3 |
| `FAILED` | Falha no processamento |
| `PROCESSED` | Processamento em andamento |

### Estrutura do PDF Gerado

Cada PDF contém as seguintes seções:

```
┌──────────────────────────────────────────┐
│  [Título do Relatório]                   │
│  Diagram ID: <uuid>                      │
│  Generated: YYYY-MM-DD HH:mm            │
│                                          │
│  Components                              │
│  • Componente 1                          │
│  • Componente 2                          │
│                                          │
│  Risks                                   │
│  • Risco identificado                    │
│                                          │
│  Recommendations                         │
│  • Ação recomendada                      │
└──────────────────────────────────────────┘
```

---

## Instruções de Execução

### Pré-requisitos

- Java 21+
- Maven 3.9+ (ou use o wrapper `./mvnw`)
- Docker e Docker Compose
- Credenciais AWS ativas com permissão em S3 e SQS

### Configuração do Arquivo `.env`

Crie um arquivo `.env` na raiz do projeto:

```env
AWS_ACCESS_KEY_ID=your-access-key-id
AWS_SECRET_ACCESS_KEY=your-secret-access-key
AWS_SESSION_TOKEN=your-session-token
AWS_REGION=us-east-1

# Opcional — sobrescreve os defaults do application-docker.properties
# AWS_S3_BUCKET_NAME=hackaton-reports-soat-6jneli
# AWS_SQS_STATUS_UPDATE_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/000000000000/diagram-status-update
```

### Executar com Docker Compose

```bash
docker compose up --build
```

A API estará disponível em `http://localhost:8080`.

> O Docker Compose sobe a aplicação e o MongoDB. O acesso ao S3 e SQS é feito diretamente na AWS usando as credenciais do `.env`.

### Executar Localmente (sem Docker)

```bash
# Compilar sem rodar testes
./mvnw clean package -DskipTests

# Executar o JAR
java -jar target/report-api-*.jar
```

### Executar os Testes

```bash
# Testes + relatório de cobertura JaCoCo
./mvnw test jacoco:report
```

O relatório HTML de cobertura estará disponível em:
```
target/site/jacoco/index.html
```

> O build falha automaticamente se a cobertura de instruções ficar abaixo de **85%**.

### Documentação Interativa

Com a aplicação no ar, acesse:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`
- **Health check**: `http://localhost:8080/actuator/health`

### Exemplos de Requisição

**Criar relatório:**
```bash
curl -X POST http://localhost:8080/api/reports \
  -H "Content-Type: application/json" \
  -d '{
    "diagramId": "3f1c2b6e-9d4a-4d8f-8c3b-1e7f6a9d2c55",
    "title": "Relatório de Arquitetura",
    "report": {
      "components": ["API Gateway", "Lambda", "DynamoDB", "S3"],
      "risks": ["Ausência de retry na integração", "Sem circuit breaker"],
      "recommendations": ["Implementar retry com exponential backoff", "Adicionar health checks"]
    }
  }'
```

**Buscar relatório por ID:**
```bash
curl http://localhost:8080/api/reports/{id}
```

**Listar relatórios por status:**
```bash
curl http://localhost:8080/api/reports?status=COMPLETED
```

**Atualizar status:**
```bash
curl -X PATCH http://localhost:8080/api/reports/{id}/status \
  -H "Content-Type: application/json" \
  -d '{ "status": "FAILED" }'
```

### Pipeline de Deploy (CI/CD)

O pipeline é acionado automaticamente a cada push em `main` que altere `src/**`, `pom.xml` ou `Dockerfile`:

1. **Build Maven** — compila o projeto e executa todos os testes
2. **Build e push ECR** — gera a imagem Docker e envia ao Amazon ECR com a tag da versão do `pom.xml`
3. **Deploy na EC2** — via SSH: remove imagens antigas (`docker system prune -af`) → pull da nova imagem → `docker run`

---

## Serviços Relacionados

- [diagram-api](https://github.com/SOAT12/hackaton_diagram_api)
- [diagram-processor](https://github.com/SOAT12/hackaton_processor_api)
