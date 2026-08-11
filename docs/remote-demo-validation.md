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

## Execução neste ambiente

- `dotnet test backend/tests`: bloqueado porque o projeto requer .NET 10 e o SDK disponível é 8.0.
- `dotnet run --project backend/src/Api`: não executado; além da incompatibilidade de SDK, o caminho
  `backend/src/Api` não é um projeto executável separado.
- `npm test --prefix dashboard`: bloqueado; o executável `ng`/`node_modules` não está disponível.
- `npm run build --prefix dashboard`: bloqueado pela mesma ausência de dependências Angular.
- Testes Android de allowlist: executados anteriormente no conjunto unitário e aprovados.

Retry, idempotência, conflitos e agregados remotos exigem SDK .NET 10, dependências Node/Angular e
serviços configurados; não foram declarados como aprovados sem essa infraestrutura.
