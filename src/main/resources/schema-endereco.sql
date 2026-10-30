-- ====================================================================
-- NXT DRIVE - MIGRATION DDL: ENTIDADE ENDEREÇO & RESTRIÇÕES DE PEDIDO
-- ====================================================================

-- 1. Cria ou ajusta a tabela de endereço com a constraint NOT NULL no CEP
CREATE TABLE IF NOT EXISTS endereco (
    id_endereco UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cep VARCHAR(10) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    bairro VARCHAR(100) NOT NULL,
    localidade VARCHAR(100) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    complemento VARCHAR(255)
);

-- Garante que o CEP e campos obrigatórios possuam constraint NOT NULL se a tabela já existia
ALTER TABLE endereco ALTER COLUMN cep SET NOT NULL;
ALTER TABLE endereco ALTER COLUMN logradouro SET NOT NULL;
ALTER TABLE endereco ALTER COLUMN numero SET NOT NULL;
ALTER TABLE endereco ALTER COLUMN bairro SET NOT NULL;
ALTER TABLE endereco ALTER COLUMN localidade SET NOT NULL;
ALTER TABLE endereco ALTER COLUMN uf SET NOT NULL;

-- 2. Vincula a chave estrangeira na tabela de usuários
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS endereco_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_usuario_endereco'
    ) THEN
        ALTER TABLE usuario 
        ADD CONSTRAINT fk_usuario_endereco 
        FOREIGN KEY (endereco_id) REFERENCES endereco(id_endereco) ON DELETE SET NULL;
    END IF;
END $$;

-- 3. Atualiza ou remove constraint CHECK legada de StatusPedido aceitando o novo valor 'PAGO'
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'pedido_status_check') THEN
        ALTER TABLE pedido DROP CONSTRAINT pedido_status_check;
        ALTER TABLE pedido ADD CONSTRAINT pedido_status_check 
            CHECK (status IN ('PENDENTE', 'AGUARDANDO_PAGAMENTO', 'PAGO', 'REJEITADO', 'CANCELADO', 'EM_PROCESSAMENTO'));
    END IF;
END $$;
