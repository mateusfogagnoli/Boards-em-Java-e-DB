-- Script de Configuração do Banco de Dados PostgreSQL
-- Para o Gerenciador de Boards Kanban

-- 1. Criar o banco de dados
CREATE DATABASE board_db;

-- 2. Conectar ao banco de dados recém-criado
\c board_db


CREATE TABLE IF NOT EXISTS boards (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS columns (
    id SERIAL PRIMARY KEY,
    board_id INTEGER NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    order_num INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL,
    UNIQUE(board_id, order_num)
);

CREATE TABLE IF NOT EXISTS cards (
    id SERIAL PRIMARY KEY,
    board_id INTEGER NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
    column_id INTEGER NOT NULL REFERENCES columns(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    blocked BOOLEAN DEFAULT FALSE,
    entered_column_at TIMESTAMP,
    left_column_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS card_block_history (
    id SERIAL PRIMARY KEY,
    card_id INTEGER NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unblocked_at TIMESTAMP,
    block_reason VARCHAR(500),
    unblock_reason VARCHAR(500),
    active BOOLEAN DEFAULT TRUE
);

-- 4. (OPCIONAL) Criar um usuário específico para a aplicação
-- CREATE USER board_user WITH PASSWORD 'sua_senha_aqui';
-- GRANT ALL PRIVILEGES ON DATABASE board_db TO board_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO board_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO board_user;

-- 5. (OPCIONAL) Dados de exemplo para teste
-- Descomente as linhas abaixo para inserir um board de exemplo

/*
INSERT INTO boards (name) VALUES ('Board de Exemplo');

INSERT INTO columns (board_id, name, order_num, type) VALUES
(1, 'A Fazer', 1, 'INICIAL'),
(1, 'Em Progresso', 2, 'PENDENTE'),
(1, 'Revisão', 3, 'PENDENTE'),
(1, 'Concluído', 4, 'FINAL'),
(1, 'Cancelado', 5, 'CANCELAMENTO');

INSERT INTO cards (board_id, column_id, title, description, created_at, blocked, entered_column_at) VALUES
(1, 1, 'Tarefa de Exemplo', 'Descrição da tarefa de exemplo', NOW(), FALSE, NOW());
*/

-- Fim do script
