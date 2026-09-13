# Validação do ciclo remoto

## Escopo verificável

- API: autenticação 200/401, senha com hash, autorização OWNER, convite revogável, versão otimista,
  contribuição idempotente e agregado sem dimensão individual.
- Android: DTO allowlist, HTTPS obrigatório, token privado, outbox transacional, retry exponencial e
  estado `PENDING/SYNCED/FAILED`.
- Dashboard: configuração externa da URL, login mock somente no build `demo`, loading, erro e visão
  geral sem filtro, exportação ou drill-down.

## Limites

O endpoint agregado não recebe aplicativos, intenções, UsageStats ou motivos. Conflitos
administrativos não são mesclados automaticamente; a interface pede nova decisão. A contribuição
usa o próprio identificador como chave idempotente e nunca reduz o XP do mascote.

## Resultado da execução

Preencher após `dotnet test backend/tests` e `npm test --prefix dashboard`. Neste ambiente, o SDK
.NET e as dependências Angular podem exigir instalação externa antes da execução.
