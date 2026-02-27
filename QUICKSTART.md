# 🚀 Guia Rápido de Início

## Pré-requisitos

- ✅ Java 21 instalado
- ✅ PostgreSQL 12+ instalado e rodando
- ✅ Maven 3.8+ instalado

## Passos de Configuração

### 1️⃣ Preparar o Banco de Dados

Abra o PostgreSQL e execute:

```bash
psql -U postgres
```

Dentro do PostgreSQL:

```sql
CREATE DATABASE board_db;
```

Se quiser usar um usuário específico (opcional):

```sql
CREATE USER board_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE board_db TO board_user;
```

### 2️⃣ Clonar/Baixar o Projeto

```bash
cd /caminho/para/Board-Project
```

### 3️⃣ Compilar o Projeto

```bash
mvn clean compile
```

### 4️⃣ Executar a Aplicação

#### Opção A: Executar diretamente com Maven

```bash
mvn exec:java@main
```

#### Opção B: Criar um JAR e executar

```bash
# Gera um JAR executável com todas as dependências
mvn clean package -DskipTests

# Executar o JAR
java -jar target/BoardManager-all.jar
```

## ✨ Primeiro Uso

1. A aplicação criará automaticamente as tabelas no banco de dados
2. No menu principal, escolha "1. Criar um novo board"
3. Digite um nome para seu primeiro board (ex: "Meu Primeiro Board")
4. Escolha "1. Usar configuração padrão"
5. Seu board será criado com as colunas padrão:
   - A Fazer (Inicial)
   - Em Progresso (Pendente)
   - Revisão (Pendente)
   - Concluído (Final)
   - Cancelado (Cancelamento)

6. Selecione o board e comece a criar cards!

## 📝 Exemplo de Uso Básico

```
Criar novo card:
- Título: "Implementar login"
- Descrição: "Criar tela de autenticação"

Mover card:
- Selecione "2. Mover card para próxima coluna"
- Digite o ID do card
- Card será movido de "A Fazer" → "Em Progresso"

Bloquear card:
- Selecione "4. Bloquear card"
- Digite ID do card
- Motivo: "Aguardando feedback do cliente"

Gerar relatório:
- Selecione "6. Gerar relatórios"
- Escolha entre:
  - Tempo de conclusão
  - Bloqueios
  - Relatório completo
```

## 🔧 Configuração de Credenciais

Se seus dados PostgreSQL forem diferentes, edite:

```
src/main/java/br/usuario/clinica/config/DatabaseConnection.java
```

Altere estas linhas:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/board_db";
private static final String USER = "postgres";
private static final String PASSWORD = "postgres";
```

## ❌ Troubleshooting

| Erro | Solução |
|------|---------|
| "Database connection failed" | Verifique se PostgreSQL está rodando |
| "Database board_db not found" | Execute `CREATE DATABASE board_db;` |
| "Compilation failed" | Verifique se tem Java 21+: `java -version` |
| "Maven not found" | Verifique instalação Maven: `mvn -version` |

## 📚 Próximas Etapas

- Leia o [README.md](README.md) completo para documentação detalhada
- Consulte a seção de Regras para entender melhor o funcionamento
- Explore os diferentes tipos de relatórios disponíveis

## 💡 Dicas

- Use nomes descritivos para seus boards e cards
- Aproveite o bloqueio para cards que têm dependências
- Gere relatórios regularmente para acompanhar o progresso
- Customize as colunas conforme sua metodologia

---

**Pronto para começar? Execute:** `mvn exec:java@main` 🚀
