# README.md — Catálogo de Produtos

## 📋 Sobre o projeto

Catálogo de Produtos é uma aplicação full stack para gerenciar produtos de um catálogo. Cada produto tem nome, descrição, preço, categoria, status e timestamps de criação/atualização.

A aplicação é composta por:

| Camada | Stack | Pasta |
|---|---|---|
| **Backend** | Java 21 · Spring Boot 4.1 · PostgreSQL 15 · Flyway · OpenAPI | `ms_product/` |
| **Frontend** | Angular 22 · Tailwind CSS 4 · Signal Forms | `product-frontend/` |
| **Infra local** | Docker Compose (Postgres, OTel Collector, Jaeger, Prometheus, Grafana) | — |

---

## 🚀 Como rodar

### Pré-requisitos

- Docker + Docker Compose
- Java 21
- Node 22 LTS

### 1. Subir infra local

```bash
docker compose up -d
```

Sobe Postgres (`5432`), OTel Collector (`4318`), Jaeger (`16686`), Prometheus (`9090`) e Grafana (`3000`).

### 2. Rodar o backend

```bash
cd ms_product
./mvnw spring-boot:run
```

API disponível em `http://localhost:8080`.
Swagger em `http://localhost:8080/swagger-ui.html`.

### 3. Rodar o frontend

```bash
cd product-frontend
npm install
ng serve
```

Aplicação disponível em `http://localhost:4200`. O proxy redireciona `/api` para o backend em `8080`.

---

## 🔌 API REST

Base URL: `http://localhost:8080/api/produtos`

| Método | Rota | Descrição | Sucesso | Erros |
|---|---|---|---|---|
| GET | `/api/produtos` | Lista paginada com filtros | 200 | 400 |
| GET | `/api/produtos/{id}` | Busca por ID | 200 | 404 |
| POST | `/api/produtos` | Cria produto | 201 | 400/422 |
| PUT | `/api/produtos/{id}` | Atualiza produto | 200 | 400/404/422 |
| DELETE | `/api/produtos/{id}` | Soft delete | 204 | 404 |

### Filtros de listagem

| Param | Tipo | Default |
|---|---|---|
| `category` | string | — |
| `status` | `ACTIVE` / `INACTIVE` / `DISCONTINUED` | — |
| `name` | string (LIKE, case-insensitive) | — |
| `page` | int | 0 |
| `size` | int | 20 |
| `sort` | string | `createdAt,desc` |

Documentação interativa: **Swagger UI** em `http://localhost:8080/swagger-ui.html`.

---

## 💻 Frontend

SPA com fluxo completo de CRUD:

- **Listagem** paginada com empty state e loading
- **Formulário** de criação/edição com validação em tempo real
- **Confirmação** antes de deletar (dialog)
- **Feedback** visual de erros via snackbar (interceptor global)

### Decisões técnicas principais

- **Standalone components** + **lazy loading** por feature
- **Signals** para estado local (sem NgRx — escopo pequeno)
- **Signal Forms** (`@angular/forms/signals`) em vez de Reactive Forms
- **Tailwind CSS 4** com design system em TS
- **Interceptors funcionais** para loading e erro

---

## 🧪 Testes

### Backend

```bash
cd ms_product

./mvnw test                              # todos
./mvnw test -Dtest=ProductServiceTest    # unitário (Mockito)
./mvnw test -Dtest=ProductControllerIT   # integração (Testcontainers + Postgres real)
```

**Cobertura:**
- `ProductServiceTest` — list, findById, create, update, delete (sucesso e falha)
- `ProductControllerIT` — CRUD completo + validações (422) + 404 + paginação
---

## 📐 Decisões arquiteturais e trade-offs

### Backend

| Decisão | Motivo | Trade-off |
|---|---|---|
| **Monolito modular** em vez de microsserviços | Escopo é um CRUD; microsserviços adicionam complexidade sem ganho | Menos "moderno", mas mais fácil de manter e testar |
| **DTOs separados da entidade** | Evita vazar `deleted`, versionamento e lazy loading | Mais código de mapeamento |
| **Soft delete** (`deleted = false`) | Preserva histórico, permite auditoria | Toda query precisa filtrar |

### Frontend

| Decisão | Motivo | Trade-off |
|---|---|---|
| **Signals + Services** em vez de NgRx | Escopo pequeno; boilerplate não se justifica | Se crescer, migrar para `NgRx SignalStore` |
| **Signal Forms** | Type-safe, reativo, sem `ReactiveFormsModule` | API ainda experimental no Angular 22 |
| **Tailwind CSS 4** | Utility-first, sem `tailwind.config.js` | Classes longas no HTML |

---

## 🛠️ Comandos rápidos

```bash
# Infra
docker compose up -d

# Backend
cd ms_product
./mvnw spring-boot:run
./mvnw test

# Frontend
cd product-frontend
npm install
ng serve
ng test
```

---

## 🔗 URLs úteis

| Serviço | URL | Credenciais |
|---|---|---|
| Frontend | http://localhost:4200 | — |
| API | http://localhost:8080/api/produtos | — |
| Swagger UI | http://localhost:8080/swagger-ui.html | — |
| Actuator health | http://localhost:8080/actuator/health | — |
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3000 | admin / admin |
| Jaeger UI | http://localhost:16686 | — |