# Modelo de Dados: Plataforma de Uso Consciente

## Convenções

- Identificadores são UUIDs gerados localmente, salvo identificadores de pacotes fornecidos pelo
  Android.
- Instantes históricos são armazenados em UTC; durações são armazenadas em milissegundos.
- Dados pessoais detalhados pertencem ao perfil local e não integram projeções institucionais.
- Exclusões de sessão e histórico são transacionais e removem dados pessoais derivados.
- O MVP admite exatamente uma sessão `ACTIVE` ou `PAUSED` por perfil local.

## Entidades locais pessoais

### StudentProfile

Representa o proprietário local dos dados, sem exigir identidade institucional no MVP.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único e imutável. |
| displayName | Texto opcional | Não necessário para o fluxo individual. |
| onboardingCompleted | Booleano | Padrão falso. |
| createdAt | Instante | Definido na criação. |

### UsageIntent

Declara o objetivo consciente de uma sessão.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único. |
| sessionId | UUID | Referencia exatamente uma Session. |
| text | Texto | Obrigatório, não vazio. |
| source | `STUDENT`, `TEACHER`, `INSTITUTION` | Origem sempre visível. |
| createdAt | Instante | Ordem das mudanças de intenção. |
| activeUntil | Instante opcional | Nulo enquanto for a intenção atual. |

Uma Session possui uma ou mais UsageIntent em ordem temporal; somente a última pode não ter
`activeUntil`.

### Session

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único. |
| studentProfileId | UUID | Proprietário local. |
| title | Texto | Obrigatório; pode repetir a intenção inicial. |
| plannedDurationMs | Inteiro longo | Maior que zero. |
| startedAt | Instante opcional | Obrigatório a partir de `ACTIVE`. |
| completedAt | Instante opcional | Obrigatório em `COMPLETED` ou `CANCELLED`. |
| accumulatedPauseMs | Inteiro longo | Não negativo. |
| state | FocusSessionStatus | Estado atual. |
| createdAt | Instante | Imutável. |
| groupId | UUID opcional | Associação coletiva sem transferir controle. |

### FocusSessionStatus

Valores: `PLANNED`, `ACTIVE`, `PAUSED`, `COMPLETED`, `CANCELLED`.

Transições válidas:

```text
PLANNED ──start──> ACTIVE
PLANNED ──cancel─> CANCELLED
ACTIVE ───pause──> PAUSED
PAUSED ───resume─> ACTIVE
ACTIVE ─complete─> COMPLETED
PAUSED ─complete─> COMPLETED
ACTIVE ───cancel─> CANCELLED
PAUSED ───cancel─> CANCELLED
```

Estados finais não aceitam novas transições. Criar outra sessão ativa exige finalizar ou cancelar a
atual.

### FocusContext

Modelo opcional e reutilizável que preenche a criação de sessão sem impor configuração.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único. |
| name | Texto | Obrigatório; exemplos não exclusivos incluem Aula, Estudo e Leitura. |
| suggestedIntention | Texto | Obrigatório e editável ao aplicar. |
| relatedPackageNames | Lista de texto | Pode ser vazia e é editável por sessão. |
| source | `STUDENT`, `TEACHER`, `INSTITUTION` | Sempre visível. |

Aplicar um FocusContext copia seus valores para uma nova sessão; alterações posteriores na sessão ou
no contexto não modificam retroativamente o outro.

FocusContext é persistido localmente para reutilização. Seu repositório permite observar, criar,
editar e excluir modelos próprios; presets fornecidos pelo produto podem ser copiados e adaptados,
mas não alterados globalmente.

### RelatedApp

Associação contextual entre uma sessão e um aplicativo iniciável.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| sessionId | UUID | Parte da chave composta. |
| packageName | Texto | Parte da chave composta; identificador do pacote. |
| componentName | Texto | Activity iniciável selecionada. |
| labelSnapshot | Texto | Nome apresentado no momento da seleção. |

A classificação termina com a sessão e não altera futuras sessões.

`packageName` é o identificador técnico persistido. Nome, ícone, indicador de app de sistema e
capacidade de abertura são obtidos dinamicamente do catálogo `InstalledApp` e não são duplicados no
banco, exceto o rótulo opcional usado no histórico quando necessário para compreensão.

### SessionEvent

Registro cronológico privado do que ocorreu durante uma sessão.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único. |
| sessionId | UUID | Sessão proprietária. |
| type | SessionEventType | Categoria do evento. |
| occurredAt | Instante | Obrigatório. |
| targetPackage | Texto opcional | Somente para tentativa iniciada pelo launcher. |
| decision | InterventionDecision opcional | Somente para intervenção respondida. |
| reflectionReason | ReflectionReason opcional | Sempre privado e opcional. |
| intentId | UUID opcional | Intenção associada ao evento. |

`SessionEventType`: `STARTED`, `PAUSED`, `RESUMED`, `INTERVENTION_SHOWN`,
`INTERVENTION_DECIDED`, `INTENT_CHANGED`, `COMPLETED`, `CANCELLED`.

`InterventionDecision`: `STAY_FOCUSED`, `OPEN_ANYWAY`.

`ReflectionReason`: `NEEDED_FOR_ACTIVITY`, `CONTACT_SOMEONE`, `TAKE_BREAK`, `OPENED_BY_HABIT`,
`INTENT_CHANGED`, `OTHER`; a ausência representa pergunta ignorada.

### PersonalSummary

Projeção calculada a partir de Session e SessionEvent, não uma fonte de verdade independente.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| sessionId | UUID | Identifica a sessão resumida. |
| actualDurationMs | Inteiro longo | Nunca negativo; exclui pausas. |
| interventionCount | Inteiro | Não negativo. |
| stayFocusedCount | Inteiro | Não excede intervenções decididas. |
| openAnywayCount | Inteiro | Não excede intervenções decididas. |
| consciousIntentChangeCount | Inteiro | Não negativo. |

Ao excluir uma Session, UsageIntent, RelatedApp e SessionEvent correspondentes são removidos; resumos
e indicadores pessoais são recalculados. A exclusão total aplica essa regra a todas as sessões.

## Entidades colaborativas

### StudyGroup

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único. |
| name | Texto | Obrigatório. |
| description | Texto | Pode ser vazio. |
| objective | Texto | Obrigatório. |
| administratorMembershipId | UUID | Deve apontar para membro elegível ativo. |
| mascotId | UUID | Mascote escolhido pelo grupo. |
| createdAt | Instante | Imutável. |

### GroupMembership

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único. |
| groupId | UUID | Grupo associado. |
| participantAlias | Texto | Identificação necessária no grupo. |
| role | `ADMIN`, `MEMBER` | Exatamente um `ADMIN` no MVP. |
| status | `ACTIVE`, `LEFT` | Membro que saiu não contribui novamente. |
| joinedAt | Instante | Obrigatório. |

O administrador gerencia membros, metas, configurações e mascote. Para sair, transfere a função; se
não o fizer, outro membro ativo elegível é escolhido de forma determinística antes da saída.

### GroupGoal

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único. |
| groupId | UUID | Grupo proprietário. |
| title | Texto | Obrigatório. |
| metric | `NUMBER_OF_SESSIONS`, `TOTAL_FOCUS_MINUTES` | O MVP prioriza a primeira. |
| target | Inteiro longo | Maior que zero. |
| currentValue | Inteiro longo | Entre zero e target para exibição. |
| startsAt | Instante | Início do período. |
| endsAt | Instante | Posterior ao início. |
| status | `ACTIVE`, `ACHIEVED`, `EXPIRED` | Nunca implica punição. |

### GroupContribution

Evento mínimo para progresso coletivo, sem aplicativo, reflexão ou histórico detalhado.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único e idempotente para sincronização futura. |
| groupId | UUID | Grupo beneficiado. |
| goalId | UUID opcional | Meta beneficiada. |
| kind | Tipo de contribuição | Sessão concluída, coletiva, dia ativo ou duração elegível. |
| amount | Inteiro longo | Positivo. |
| occurredAt | Instante | Obrigatório. |

### GroupInvite

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| code | Texto | Único, não vazio e revogável. |
| groupId | UUID | Grupo de destino. |
| createdByMembershipId | UUID | Deve ser o OWNER ativo. |
| expiresAt | Instante | Posterior à criação. |
| status | `ACTIVE`, `USED`, `REVOKED`, `EXPIRED` | Somente ACTIVE permite entrada. |

Usar um convite cria participação `MEMBER`, nunca `OWNER`.

### Mascot

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Identifica opção visual. |
| name | Texto | Obrigatório. |
| stage | `INITIAL`, `GROWING`, `EVOLVED` | Pelo menos três estágios no MVP. |
| progressPoints | Inteiro longo | Monotônico e não negativo. |

O estágio deriva dos pontos e nunca regride por falta de uso, saída de membro ou expiração de meta.

## Entidades pedagógicas e institucionais

### PedagogicalSuggestion

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Único. |
| sourceType | `TEACHER`, `INSTITUTION` | Sempre exibido. |
| sourceLabel | Texto | Identificação compreensível da origem. |
| objective | Texto | Obrigatório. |
| suggestedDurationMs | Inteiro longo | Maior que zero. |
| suggestedApps | Lista de identificadores | Pode ser adaptada pelo estudante. |
| studentDecision | `PENDING`, `ACCEPTED`, `ADAPTED`, `IGNORED` | Decisão sempre livre. |

### InstitutionalAggregate

Projeção demonstrativa de totais de uma turma ou grupo, sem filtros ou dimensão individual.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| cohortId | UUID | Turma ou grupo inteiro. |
| periodStart | Data | Início da janela apresentada. |
| periodEnd | Data | Fim da janela apresentada. |
| sessionCount | Inteiro | Total agregado. |
| totalDurationMs | Inteiro longo | Total agregado. |
| completedSessionCount | Inteiro | Total agregado. |
| interventionCount | Inteiro | Total agregado. |
| stayFocusedDecisionCount | Inteiro | Total agregado. |

Não contém estudante, pacote, conteúdo, reflexão ou chave que permita detalhamento. No MVP, os dados
são representativos e locais; não existe exportação nem consulta filtrada.

## Entidades de sincronização local

### SyncOutboxEntry

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Identificador idempotente enviado ao servidor. |
| aggregateType | Tipo | `GROUP`, `GOAL`, `CONTRIBUTION`, `SHARED_SESSION_SUMMARY`. |
| aggregateId | UUID | Recurso compartilhável afetado. |
| operation | Tipo | Ação permitida pela allowlist. |
| payload | JSON mínimo | Nunca contém pacote, intenção ou motivo de intervenção. |
| localVersion | Inteiro longo | Versão esperada para conflitos. |
| status | `PENDING`, `IN_FLIGHT`, `SYNCED`, `FAILED` | Estado de entrega. |
| attemptCount | Inteiro | Não negativo. |
| nextAttemptAt | Instante opcional | Controla retry. |
| createdAt | Instante | Ordem estável da fila. |

Estados `IN_FLIGHT` abandonados retornam a `PENDING`. `FAILED` representa erro não transitório e exige
ação compreensível; rede indisponível não é falha final.

### SharedSessionSummary

Projeção mínima sincronizável somente quando a sessão está relacionada a um grupo.

| Campo | Tipo conceitual | Regra |
|-------|-----------------|-------|
| id | UUID | Também funciona como chave idempotente. |
| groupId | UUID | Grupo beneficiado. |
| durationMinutes | Inteiro | Minutos elegíveis, não negativos. |
| completed | Booleano | Indica conclusão sem expor estado detalhado. |
| occurredAt | Instante | Necessário para meta/período. |

Não contém intenção, aplicativos, intervenções, motivos ou histórico de navegação.

## Entidades do servidor

- **User**: identidade remota mínima; credencial é armazenada somente como hash seguro.
- **School**: instituição identificada por nome e estado ativo.
- **Classroom**: agrupamento pertencente a uma School.
- **ClassroomMember**: vínculo entre User e Classroom, com papel institucional mínimo.
- **StudyGroup**: projeção compartilhada do grupo com `ownerId` e versão de concorrência.
- **StudyGroupMember**: vínculo `OWNER` ou `MEMBER`; exatamente um OWNER ativo.
- **GroupInvite**: código temporário e revogável que cria somente participação MEMBER.
- **GroupGoal**: meta compartilhada, inicialmente por número de sessões, com versão.
- **MascotProgress**: tipo, XP monotônico e estágio derivado.
- **SharedSessionSummary**: contribuição mínima e idempotente definida acima.
- **ClassroomAggregateMetric**: totais por turma e período, sem dimensão de estudante.

O servidor não possui tabela para aplicativos instalados, UsageStats, motivos de intervenção ou
histórico individual detalhado.

## Regras de concorrência

- Criação de contribuição e resumo compartilhável é idempotente pelo UUID do cliente.
- XP e progresso de meta são derivados de contribuições únicas, nunca incrementos cegos repetíveis.
- StudyGroup e GroupGoal possuem versão incrementada a cada alteração administrativa.
- Versão divergente rejeita a escrita; o cliente atualiza o estado e pede nova decisão do usuário.
- Transferência de OWNER ocorre em transação que mantém exatamente um proprietário ativo.

## Preferências

### PrivacyPreferences

- `onboardingCompleted`: indica conclusão da explicação inicial.
- `disclosedLocalCategories`: registra categorias locais explicadas.
- `disclosedSharedCategories`: registra categorias compartilháveis explicadas.
- `homeRoleEducationShown`: evita solicitar o papel Home sem contexto.

Preferências não armazenam histórico de aplicativos ou respostas de reflexão.
