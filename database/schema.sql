-- Banco de dados do Sistema Gerador de Provas
-- PowerShell (UTF-8):
-- Get-Content .\database\schema.sql -Encoding UTF8 | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p --default-character-set=utf8mb4

CREATE DATABASE IF NOT EXISTS gerador_provas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gerador_provas;

CREATE TABLE IF NOT EXISTS questoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enunciado TEXT NOT NULL,
    alternativa_a VARCHAR(500) NOT NULL,
    alternativa_b VARCHAR(500) NOT NULL,
    alternativa_c VARCHAR(500) NOT NULL,
    alternativa_d VARCHAR(500) NOT NULL,
    alternativa_e VARCHAR(500) DEFAULT NULL,
    resposta_correta CHAR(1) NOT NULL CHECK (resposta_correta IN ('A', 'B', 'C', 'D', 'E')),
    disciplina VARCHAR(100) NOT NULL,
    dificuldade ENUM('FACIL', 'MEDIA', 'DIFICIL') NOT NULL DEFAULT 'MEDIA',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS provas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao TEXT,
    instrucoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prova_questao (
    prova_id INT NOT NULL,
    questao_id INT NOT NULL,
    ordem INT NOT NULL DEFAULT 1,
    PRIMARY KEY (prova_id, questao_id),
    FOREIGN KEY (prova_id) REFERENCES provas(id) ON DELETE CASCADE,
    FOREIGN KEY (questao_id) REFERENCES questoes(id) ON DELETE CASCADE
);

INSERT INTO questoes (enunciado, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta, disciplina, dificuldade) VALUES
('Qual padrão de arquitetura separa Model, View e Controller?', 'Singleton', 'MVC', 'Factory', 'Observer', 'B', 'Programação', 'FACIL'),
('Em qual camada ficam as regras de negócio neste projeto?', 'View', 'DAO', 'Service', 'JSP', 'C', 'Programação', 'MEDIA'),
('Qual comando SQL remove registros de uma tabela?', 'SELECT', 'INSERT', 'UPDATE', 'DELETE', 'D', 'Banco de Dados', 'FACIL');

INSERT INTO provas (titulo, descricao, instrucoes) VALUES
('Avaliação Bimestral - Exemplo', 'Prova de demonstração gerada pelo sistema', 'Leia atentamente cada questão. Marque apenas uma alternativa.');

INSERT INTO prova_questao (prova_id, questao_id, ordem) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3);
