# 📂 Estrutura do Projeto

## Visão Geral

O projeto está organizado em pacotes bem definidos seguindo padrões de arquitetura Java:

```
Board-Project/
├── src/
│   ├── main/
│   │   ├── java/br/usuario/clinica/
│   │   │   ├── Main.java                          # Classe principal
│   │   │   ├── config/
│   │   │   │   └── DatabaseConnection.java        # Gerenciamento de conexão
│   │   │   ├── dao/                               # Data Access Objects
│   │   │   │   ├── BoardDAO.java
│   │   │   │   ├── BoardColumnDAO.java
│   │   │   │   ├── CardDAO.java
│   │   │   │   └── CardBlockHistoryDAO.java
│   │   │   ├── enums/
│   │   │   │   └── ColumnType.java                # Tipos de coluna
│   │   │   ├── model/                             # Entidades
│   │   │   │   ├── Board.java
│   │   │   │   ├── BoardColumn.java
│   │   │   │   ├── Card.java
│   │   │   │   └── CardBlockHistory.java
│   │   │   ├── service/                           # Lógica de negócio
│   │   │   │   ├── BoardService.java
│   │   │   │   ├── CardService.java
│   │   │   │   └── ReportService.java
│   │   │   ├── ui/                                # Interface com usuário
│   │   │   │   ├── MainMenu.java
│   │   │   │   └── BoardManipulationMenu.java
│   │   │   └── util/
│   │   │       └── InputUtil.java                 # Utilitários
│   │   └── resources/
│   └── test/
│       └── java/
├── target/                                        # Artefatos compilados
│   ├── BoardManager-all.jar                       # JAR executável
│   └── classes/
├── pom.xml                                        # Configuração Maven
├── README.md                                      # Documentação completa
├── QUICKSTART.md                                  # Guia rápido
├── database-setup.sql                             # Scripts SQL
└── STRUCTURE.md                                   # Este arquivo
```

## 📦 Descrição dos Pacotes

### `config`
Gerenciamento de recursos globais:
- **DatabaseConnection.java** - Singleton para conexão com PostgreSQL, inicialização de tabelas

### `dao` (Data Access Object)
Camada de acesso a dados:
- **BoardDAO.java** - CRUD e queries de boards
- **BoardColumnDAO.java** - CRUD e queries de colunas
- **CardDAO.java** - CRUD e queries de cards
- **CardBlockHistoryDAO.java** - Queries de histórico de bloqueios

### `enums`
Enumerações do sistema:
- **ColumnType.java** - Tipos de coluna (INICIAL, PENDENTE, FINAL, CANCELAMENTO)

### `model` (Entidades/POJOs)
Representações de dados:
- **Board.java** - Representa um board com validações
- **BoardColumn.java** - Representa uma coluna com lista de cards
- **Card.java** - Representa um card com informações de tempo
- **CardBlockHistory.java** - Registro de bloqueios com datas

### `service` (Camada de Negócio)
Implementação de regras de negócio:
- **BoardService.java** - Operações e validações de boards
- **CardService.java** - Movimentação de cards, bloqueio/desbloqueio
- **ReportService.java** - Geração de relatórios

### `ui` (User Interface)
Menus interativos:
- **MainMenu.java** - Menu principal com opções de CRUD de boards
- **BoardManipulationMenu.java** - Menu para gerenciar cards dentro de um board

### `util` (Utilitários)
Funções auxiliares:
- **InputUtil.java** - Leitura padronizada de entrada do usuário

## 🔄 Fluxo de Dados

```
Main.java
    ↓
MainMenu (UI)
    ├→ BoardService (Criar/Selecionar/Deletar boards)
    │   ├→ BoardDAO (Persistência)
    │   └→ BoardColumnDAO (Persistência de colunas)
    │
    └→ BoardManipulationMenu (UI)
        ├→ CardService (Movimentação, bloqueio)
        │   ├→ CardDAO (Persistência)
        │   └→ CardBlockHistoryDAO (Histórico)
        │
        └→ ReportService (Relatórios)
            ├→ CardDAO (Leitura)
            └→ CardBlockHistoryDAO (Leitura)

DatabaseConnection
    ↑
    └─ Utilizado por todos os DAOs
```

## 💾 Padrões Arquiteturais Utilizados

### 1. **DAO Pattern**
Isolamento da lógica de acesso a dados através de DAOs específicos para cada entidade.

### 2. **Service Layer Pattern**
Lógica de negócio encapsulada em serviços que orquestram DAOs e aplicam validações.

### 3. **Model Objects**
Entidades com lógica de validação e referências entre objetos.

### 4. **Singleton Pattern**
DatabaseConnection como singleton garantendo uma única instância.

### 5. **MVC-like Pattern**
Separação entre:
- **Model** (model/) - Dados
- **View** (ui/) - Interface
- **Controller** (service/) - Lógica

## 🔐 Camadas de Validação

### Validação na Model
- Board valida estrutura de colunas
- Card valida estado de bloqueio
- BoardColumn valida tipo e ordem

### Validação no Service
- CardService valida movimentação sequencial
- CardService valida bloqueios
- BoardService valida estrutura completa

### Validação na UI
- InputUtil garante entrada válida
- MainMenu valida seleções
- BoardManipulationMenu valida operações

## 📊 Modelo de Dados

### Relacionamentos

```
Boards (1) ──→ (N) Columns ──→ (N) Cards
              │
              └─→ (N) CardBlockHistory
```

### Integridade Referencial
- Cards devem ter um board_id válido
- Cards devem ter um column_id válido
- CardBlockHistory deve ter um card_id válido
- Colunas devem estar ordenadas sequencialmente

## 🔄 Ciclo de Vida de um Card

```
1. CRIADO
   └─→ Card inserido na coluna INICIAL
       └─→ enteredAt = agora

2. MOVIMENTAÇÃO
   └─→ Card se move sequencialmente por colunas
       ├─→ leftColumnAt = agora
       └─→ enteredColumnAt = agora (na nova coluna)

3. BLOQUEIO (opcional)
   └─→ CardBlockHistory criado
       └─→ blockedAt = agora
           └─→ DESBLOQUEIO
               ├─→ unblockedAt = agora
               └─→ active = false

4. CONCLUSÃO
   └─→ Chega à coluna FINAL
       └─→ leftColumnAt = agora

5. OU CANCELAMENTO
   └─→ Movido para coluna CANCELAMENTO
       └─→ leftColumnAt = agora
           └─→ enteredColumnAt = agora (em Cancelado)
```

## 🎯 Pontos de Extensão

Adicionar novas funcionalidades:

1. **Novo tipo de relatório**: Estender `ReportService`
2. **Novas operações de board**: Estender `BoardService`
3. **Novos tipos de coluna**: Adicionar ao `ColumnType.enum` e atualizar `BoardService`
4. **Autenticação**: Adicionar `UserDAO` e `AuthService`
5. **Notificações**: Adicionar `NotificationService`
6. **Histórico de movimentos**: Adicionar `CardMovementHistoryDAO`

## 📈 Crescimento do Projeto

### Fase 1 (Atual) ✅
- [x] CRUD de Boards
- [x] CRUD de Cards
- [x] Movimentação de cards
- [x] Bloqueio/desbloqueio
- [x] Relatórios básicos
- [x] Interface CLI

### Fase 2 (Futuro)
- [ ] Interface Web (Spring Boot)
- [ ] API REST
- [ ] Autenticação de usuários
- [ ] Permissões por usuário
- [ ] Notificações em tempo real
- [ ] Histórico completo de movimentos
- [ ] Attachments em cards
- [ ] Comentários em cards

## 📚 Dependências Externas

```
├── JDBC (Java nativo)
│   └─ Acesso a PostgreSQL
│
├── PostgreSQL Driver (Maven)
│   └─ org.postgresql:postgresql:42.7.1
│
└── JUnit (Testes)
    └─ junit:junit:4.13.2
```

## 🚀 Build e Deploy

### Compilação
```bash
mvn clean compile
```

### Testes
```bash
mvn test
```

### Empacotamento
```bash
mvn clean package
```

### Execução
```bash
java -jar target/BoardManager-all.jar
```

---

**Versão:** 1.0  
**Data:** 27 de Fevereiro de 2026  
**Status:** Completo e Funcional
