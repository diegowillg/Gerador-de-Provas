-- Corrige erros de acentuação (acentos em UTF-8 via UNHEX, sem depender do encoding do terminal)
USE gerador_provas;

SET NAMES utf8mb4;

DELETE FROM prova_questao;
DELETE FROM provas;
DELETE FROM questoes;

ALTER TABLE questoes AUTO_INCREMENT = 1;
ALTER TABLE provas AUTO_INCREMENT = 1;

INSERT INTO questoes (enunciado, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta, disciplina, dificuldade) VALUES
(
  CONCAT('Qual padr', UNHEX('C3A3'), 'o de arquitetura separa Model, View e Controller?'),
  'Singleton', 'MVC', 'Factory', 'Observer', 'B',
  CONCAT('Programa', UNHEX('C3A7'), UNHEX('C3A3'), 'o'),
  'FACIL'
),
(
  CONCAT('Em qual camada ficam as regras de neg', UNHEX('C3B3'), 'cio neste projeto?'),
  'View', 'DAO', 'Service', 'JSP', 'C',
  CONCAT('Programa', UNHEX('C3A7'), UNHEX('C3A3'), 'o'),
  'MEDIA'
),
(
  'Qual comando SQL remove registros de uma tabela?',
  'SELECT', 'INSERT', 'UPDATE', 'DELETE', 'D',
  'Banco de Dados',
  'FACIL'
);

INSERT INTO provas (titulo, descricao, instrucoes) VALUES
(
  CONCAT('Avalia', UNHEX('C3A7'), UNHEX('C3A3'), 'o Bimestral - Exemplo'),
  CONCAT('Prova de demonstra', UNHEX('C3A7'), UNHEX('C3A3'), 'o gerada pelo sistema'),
  CONCAT('Leia atentamente cada quest', UNHEX('C3A3'), 'o. Marque apenas uma alternativa.')
);

INSERT INTO prova_questao (prova_id, questao_id, ordem) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3);
