# report-api

Microsserviço responsável pela geração de relatórios técnicos em PDF com base na análise arquitetural gerada pelo `diagram-processor`, armazenamento seguro na nuvem (Amazon S3) e atualização de status via Amazon SQS.

Faz parte do MVP da FIAP Secure Systems composto por três microsserviços:

| Serviço | Responsabilidade |
|---|---|
| **diagram-api** | Orquestração, persistência e exposição de status |
| **diagram-processor** | Consumo da fila SQS e análise do diagrama com IA |
| **report-api** *(este)* | Geração do relatório PDF e armazenamento no S3 |

---

## Descrição

O `report-api` atua no final do fluxo assíncrono. Ele é acionado após a análise da IA estar concluída, recebendo um payload com os componentes identificados, riscos arquiteturais e recomendações estruturadas.

Suas principais funções são:
1. Validar os dados de análise técnica.
2. Gerar um arquivo PDF bem formatado contendo o relatório técnico da arquitetura avaliada.
3. Fazer o upload do PDF gerado para um bucket protegido no Amazon S3.
4. Enviar a URL pública (ou assinada) do PDF de volta para a fila SQS de atualização de status para que a API principal (`diagram-api`) atualize o banco de dados.

---

## Stack

- **Runtime:** Java 21
- **Framework:** Spring Boot 3
- **Build:** Maven
- **Banco de Dados:** MongoDB
- **Armazenamento:** Amazon S3
- **Mensageria:** Amazon SQS
- **Geração de PDF:** Apache PDFBox

---

## Estratégias de Segurança e Validação

Para assegurar o correto funcionamento da plataforma e prevenir vulnerabilidades, este serviço adota:
- **Tratamento Global de Exceções:** Classes de exceção genéricas e bad requests não retornam stacktraces. Retornam JSON padronizado (`GlobalExceptionHandler`).
- **Comunicação Restrita e Segura:** Utilização de roles e tokens (IAM) para acesso aos serviços S3 e SQS da AWS, limitando privilégios.
- **Validação de Entrada:** Os endpoints e métodos de fila realizam a validação estruturada do JSON para evitar falhas ou ataques (ex. nulos, XSS).

---

## Executando o Projeto

### Pré-requisitos
- Docker e Docker Compose
- Credenciais da AWS configuradas ou uso do LocalStack

### Configuração
Crie um arquivo `.env` na raiz do projeto com as variáveis necessárias:
```env
MONGODB_URI=mongodb://mongodb:27017/report_db
AWS_REGION=us-east-1
AWS_S3_BUCKET_NAME=nome_do_seu_bucket
AWS_SQS_STATUS_UPDATE_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/000000000000/diagram-status-update
```

### Inicialização
Para subir a aplicação e o MongoDB local, execute:
```bash
docker compose up --build
```
A API estará acessível em `http://localhost:8081`.

---

## Qualidade e Observabilidade
- **Testes Automatizados:** O projeto conta com testes unitários que devem passar obrigatoriamente no pipeline de CI antes de qualquer deploy.
- **Logs Estruturados:** Logs padronizados em JSON (utilizando Logback e Logstash Encoder) para facilitar ingestão em ferramentas como CloudWatch, Datadog ou ELK.

---

## Relacionados
- [diagram-api](https://github.com/SOAT12/hackaton_diagram_api)
- [diagram-processor](https://github.com/SOAT12/hackaton_processor_api)
