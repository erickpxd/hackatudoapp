# Contrato de Interface: Colaboração e Privacidade

## Fronteiras de dados

| Consumidor | Dados permitidos | Dados proibidos |
|------------|------------------|-----------------|
| Estudante | Seu histórico e indicadores | Dados privados de terceiros |
| Grupo | Meta, totais, mascote e participantes | Apps, reflexões e mensagens individuais |
| Professor | Sugestão criada e totais autorizados | Histórico individual e controle do aparelho |
| Escola | Totais da turma ou grupo | Filtros, conteúdo e identificação individual |

## Administração do grupo

```text
CreateGroup(creator, name, objective, mascot) -> grupo com creator como ADMIN
AddMember(admin, invitee)                     -> participação ACTIVE
CreateInvite(admin)                           -> código de uso único ou revogável
JoinByInvite(student, code)                   -> participação ACTIVE sem privilégio de OWNER
RemoveMember(admin, member)                   -> participação LEFT
UpdateGoal(admin, goal)                       -> meta validada
ChangeMascot(admin, mascot)                   -> opção disponibilizada
TransferAdministration(admin, member)         -> troca atômica de papel
LeaveGroup(admin)                             -> transfere explicitamente ou elege membro elegível
LeaveGroup(member)                            -> participação LEFT
```

Somente o administrador altera membros, metas, configurações e mascote. A saída do último integrante
encerra o grupo local; ela não produz punição nem reduz progresso já apresentado.

## Progresso coletivo

Cada contribuição tem identificador único e só pode ser contabilizada uma vez. A soma altera meta e
pontos do mascote; pontos nunca diminuem. O contrato não expõe contribuição por participante nas
visões coletivas.

## Sugestão pedagógica em aparelho pessoal

Estados: `PENDING`, `ACCEPTED`, `ADAPTED`, `IGNORED`.

O estudante pode escolher qualquer transição a partir de `PENDING`. Aceitar cria uma sessão editável;
adaptar cria uma sessão com alterações; ignorar encerra o fluxo. Nenhuma opção concede controle ao
emissor nem impede o uso do aparelho.

## Painel institucional do MVP

Entrada: um conjunto local de `InstitutionalAggregate` representativo.

Saída: totais e tendências do grupo ou turma inteira. Não oferece busca, filtro, ordenação por pessoa,
exportação ou navegação para detalhes individuais.

## Exclusão pessoal

`DeleteSession` remove a sessão, intenções, relações com apps e eventos correspondentes. Indicadores
pessoais são recalculados. Contribuições coletivas já agregadas não revelam o histórico apagado e não
regridem, pois representam progresso conquistado pelo grupo.

`DeleteAllPersonalHistory` aplica o mesmo comportamento a todas as sessões do estudante. Preferências
de onboarding e participação em grupos só são removidas se o estudante solicitar a exclusão desses
dados separadamente; a interface deve explicar essa distinção.
