# 🎯 Gerenciador de Boards Kanban

Um sistema completo de gestão de boards (Kanban) desenvolvido em Java 21 com persistência em PostgreSQL.

## 📋 Requisitos

- **Java 21** ou superior
- **PostgreSQL 12** ou superior
- **Maven 3.8** ou superior

## 🚀 Instalação e Configuração

### 1. Preparar o Banco de Dados PostgreSQL

Antes de executar a aplicação, você precisa criar o banco de dados:

```sql
-- Conecte ao PostgreSQL como administrador
-- Execute os comandos abaixo:

CREATE DATABASE board_db;

-- Se quiser usar um usuário específico (opcional):
CREATE USER board_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE board_db TO board_user;
```

### 2. Configurar Credenciais do Banco

Edite o arquivo `src/main/java/br/usuario/clinica/config/DatabaseConnection.java` e ajuste as seguintes variáveis se necessário:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/board_db";
private static final String USER = "postgres";  // Seu usuário PostgreSQL
private static final String PASSWORD = "postgres";  // Sua senha
```

### 3. Compilar o Projeto

```bash
cd Board-Project
mvn clean compile
```

### 4. Executar a Aplicação

```bash
mvn exec:java@main
```

## 📚 Guia de Uso

### Menu Principal

Ao iniciar a aplicação, você verá o seguinte menu:

```
1. Criar um novo board
2. Selecionar board
3. Excluir boards
4. Sair
```

### Criar um Novo Board

1. Escolha a opção "1" no menu principal
2. Digite um nome para o board
3. Escolha entre configuração padrão ou personalizada
4. As colunas padrão são:
   - **A Fazer** (Inicial)
   - **Em Progresso** (Pendente)
   - **Revisão** (Pendente)
   - **Concluído** (Final)
   - **Cancelado** (Cancelamento)

### Regras de Estrutura do Board

Um board válido deve ter:

- **Exatamente 3 tipos obrigatórios de colunas:**
  - 1 coluna do tipo **Inicial** (primeira coluna)
  - 1 coluna do tipo **Final** (penúltima coluna)
  - 1 coluna do tipo **Cancelamento** (última coluna)

- **Colunas opcionais:**
  - Quantas colunas do tipo **Pendente** forem necessárias

### Gerenciar um Board Selecionado

Após selecionar um board, você tem as seguintes opções:

1. **Criar novo card** - Adiciona um novo card na coluna inicial
2. **Mover card para próxima coluna** - Move o card sequencialmente
3. **Cancelar card** - Move o card para a coluna de cancelamento
4. **Bloquear card** - Bloqueia o card com um motivo (impede movimentação)
5. **Desbloquear card** - Desbloqueia o card com um motivo
6. **Gerar relatórios** - Exibe relatórios de andamento
7. **Fechar board** - Retorna ao menu principal

### Regras de Movimentação de Cards

- **Cards não bloqueados** podem ser movidos para a próxima coluna sequencialmente
- **Cards bloqueados** não podem ser movidos até serem desbloqueados
- **Cards na coluna Final** não podem ser movidos
- **Cards podem ser cancelados** a qualquer momento (exceto da coluna Final), sendo movidos para a coluna de Cancelamento
- **Ao bloquear/desbloquear**, você deve informar um motivo

### Relatórios

#### Relatório de Tempo de Conclusão
Mostra quanto tempo cada card passou em cada coluna e o tempo total desde sua criação.

#### Relatório de Bloqueios
Exibe informações sobre:
- Quantas vezes cada card foi bloqueado
- Motivos dos bloqueios
- Tempo de bloqueio
- Motivos de desbloqueio

#### Relatório Completo
Combina todas as informações incluindo resumo geral, cards por coluna e ambos os relatórios acima.

## 🗄️ Estrutura do Banco de Dados

### Tabela: boards
```sql
- id (SERIAL PRIMARY KEY)
- name (VARCHAR(255))
- created_at (TIMESTAMP)
```

### Tabela: columns
```sql
- id (SERIAL PRIMARY KEY)
- board_id (INTEGER FOREIGN KEY)
- name (VARCHAR(255))
- order_num (INTEGER)
- type (VARCHAR(50))
```

### Tabela: cards
```sql
- id (SERIAL PRIMARY KEY)
- board_id (INTEGER FOREIGN KEY)
- column_id (INTEGER FOREIGN KEY)
- title (VARCHAR(255))
- description (TEXT)
- created_at (TIMESTAMP)
- blocked (BOOLEAN)
- entered_column_at (TIMESTAMP)
- left_column_at (TIMESTAMP)
```

### Tabela: card_block_history
```sql
- id (SERIAL PRIMARY KEY)
- card_id (INTEGER FOREIGN KEY)
- blocked_at (TIMESTAMP)
- unblocked_at (TIMESTAMP)
- block_reason (VARCHAR(500))
- unblock_reason (VARCHAR(500))
- active (BOOLEAN)
```

## 📦 Estrutura de Pacotes

```
br.usuario.clinica/
├── Main.java                           # Classe principal
├── config/
│   └── DatabaseConnection.java         # Gerenciamento de conexão
├── dao/
│   ├── BoardDAO.java                   # Operações de Boards
│   ├── BoardColumnDAO.java             # Operações de Colunas
│   ├── CardDAO.java                    # Operações de Cards
│   └── CardBlockHistoryDAO.java        # Operações de Bloqueios
├── enums/
│   └── ColumnType.java                 # Tipos de coluna
├── model/
│   ├── Board.java                      # Modelo de Board
│   ├── BoardColumn.java                # Modelo de Coluna
│   ├── Card.java                       # Modelo de Card
│   └── CardBlockHistory.java           # Modelo de Histórico
├── service/
│   ├── BoardService.java               # Lógica de Boards
│   ├── CardService.java                # Lógica de Cards
│   └── ReportService.java              # Geração de Relatórios
├── ui/
│   ├── MainMenu.java                   # Menu principal
│   └── BoardManipulationMenu.java      # Menu de manipulação
└── util/
    └── InputUtil.java                  # Utilitários de entrada
```

## 🔧 Tecnologias Utilizadas

- **Java 21** - Linguagem principal
- **PostgreSQL** - Banco de dados relacional
- **JDBC** - Conectividade com banco de dados
- **Maven** - Gerenciador de dependências

## 📝 Exemplos de Uso

### Criar um Board com Columns Customizadas

```
1. Escolha "Criar um novo board" no menu principal
2. Nome: "Projeto XYZ"
3. Escolha "Configurar colunas customizadas"
4. Adicione:
   - "Backlog" (Inicial)
   - "Development" (Pendente)
   - "Testing" (Pendente)
   - "Ready" (Final)
   - "Rejected" (Cancelamento)
```

### Gerenciar Cards

```
1. Selecione um board
2. Crie um novo card: "Implementar autenticação"
3. Mova para "Em Progresso"
4. Se necessário, bloqueie com motivo "Aguardando library"
5. Desbloquie com motivo "Library instalada"
6. Mova sequencialmente até "Concluído"
```

## 🐛 Troubleshooting

### Erro: "Database connection failed"
- Verifique se PostgreSQL está rodando
- Confirme as credenciais em DatabaseConnection.java
- Verifique se o banco de dados "board_db" existe

### Erro: "Estrutura de board inválida"
- Certifique-se de ter criado colunas com tipos corretos
- Verifique se tem exatamente 1 coluna Inicial, 1 Final e 1 Cancelamento
- A ordem das colunas deve estar correta

### Compilação falha
- Verifique se está usando Java 21 ou superior
- Execute `mvn clean install`
- Verifique dependências no pom.xml

## 📄 Licença

Este projeto é fornecido como está para fins educacionais e comerciais.

## 👨‍💻 Autor
Mateus Silva
Desenvolvido como sistema de gestão de boards Kanban.

---

## 📈 Melhorias futuras

Criar uma interface gráfica com Swing.

---

**Última atualização:** 27 de Fevereiro de 2026
