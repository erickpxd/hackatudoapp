# Plano de Implementação: Plataforma de Uso Consciente

**Branch**: `001-uso-consciente-smartphone` | **Data**: 2026-09-12 | **Spec**: [spec.md](spec.md)

**Entrada**: Especificação em `specs/001-uso-consciente-smartphone/spec.md`

## Resumo

Construir um aplicativo Android que possa atuar como tela inicial, liste aplicativos iniciáveis e
adapte sua apresentação durante sessões definidas pelo estudante. O núcleo será local-first: sessão,
temporização, intervenções, decisões, resumos e indicadores serão persistidos no aparelho. O MVP
intervirá somente nas aberturas iniciadas pelo próprio launcher; aberturas externas não serão
monitoradas. Grupos, mascote e painel institucional usarão dados locais representativos no hackathon,
com contratos preparados para sincronização posterior e sem expor histórico individual.

## Contexto Técnico

**Linguagem/versão**: Kotlin 2.x, toolchain Java 17

**Dependências principais**: AndroidX Activity e Lifecycle, Jetpack Compose, Material Design 3,
Navigation Compose, Room, DataStore, Kotlin Coroutines, Hilt, WorkManager, Retrofit e OkHttp;
ASP.NET Core Minimal APIs com PostgreSQL no backend; Angular no dashboard institucional

**Armazenamento**: Room em armazenamento interno privado; DataStore para preferências pequenas;
PostgreSQL somente para identidades e dados compartilháveis; dados demonstrativos locais enquanto o
backend não estiver disponível

**Testes**: JUnit, MockK quando necessário, kotlinx-coroutines-test, testes de Room e Compose UI Test;
testes de integração do ASP.NET Core e testes de componentes/serviços Angular nos incrementos remotos

**Plataforma alvo**: Aplicativo Android com `minSdk 29`, `compileSdk 36` e `targetSdk 36`, atendendo à
exigência de novos apps em 2026; ASP.NET Core 10 e dashboard Angular responsivo

**Tipo de projeto**: Plataforma greenfield com app Android, API web e dashboard; cada produto inicia
com um único projeto/módulo e separação interna por responsabilidade

**Metas de desempenho**: Tela inicial utilizável em até 2 segundos após abertura a frio no aparelho
de demonstração; ações locais refletidas em até 100 ms em condições normais; temporizador sem
atualizações persistentes por segundo

**Restrições**: Funcionalidade individual offline; sem câmera, microfone, localização,
AccessibilityService ou leitura de conteúdo; sem `QUERY_ALL_PACKAGES` no primeiro recorte; UsageStats
fora do núcleo e somente com opt-in futuro; HTTPS; nenhum dado privado em logs ou payloads remotos

**Escala/escopo**: Um perfil local por aparelho, uma sessão ativa por vez, centenas de sessões locais;
uma modalidade inicial de meta; sincronização mínima de grupo; painel com totais sem filtros

## Verificação da Constituição

*GATE: deve passar antes da pesquisa da Fase 0 e ser verificado novamente após o design da Fase 1.*

| Gate constitucional | Resultado pré-design | Evidência no plano |
|---------------------|----------------------|-------------------|
| Autonomia e reflexão | APROVADO | Há escolha; não há bloqueio ou controle remoto. |
| Privacidade desde a concepção | APROVADO | Dados privados locais e agregados sem filtros. |
| Experiência local-first | APROVADO | O fluxo individual usa persistência e relógio locais. |
| Colaboração sem punição | APROVADO | Mascote monotônico, sem ranking individual. |
| Prioridade do MVP | APROVADO | Launcher, intenção e sessão precedem grupos e painel demonstrativo. |
| Kotlin, Compose, Material 3 e MVVM | APROVADO | Stack e estrutura respeitam as escolhas obrigatórias. |
| Estado reativo e assincronismo | APROVADO | ViewModels usam StateFlow; repositórios usam coroutines. |
| Simplicidade | APROVADO | Um módulo Android e Hilt restrito ao grafo necessário. |
| Regras fora das telas | APROVADO | Domínio e repositórios concentram regras. |
| Requisitos e testes | APROVADO | Contratos e quickstart cobrem riscos críticos. |

Nenhuma violação ou exceção constitucional foi identificada.

## Estrutura do Projeto

### Documentação desta funcionalidade

```text
specs/001-uso-consciente-smartphone/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── launcher-session-contract.md
│   └── collaboration-privacy-contract.md
└── tasks.md
```

### Código-fonte na raiz do repositório

```text
app/
├── src/main/
│   ├── AndroidManifest.xml
│   └── java/.../
│       ├── app/                 # Application, Activity, navegação e composição manual
│       ├── core/
│       │   ├── database/        # Room e configuração do banco
│       │   ├── datastore/       # Preferências pequenas
│       │   ├── network/         # Cliente remoto e DTOs sincronizáveis
│       │   ├── launcher/        # PackageManager, RoleManager e AppLauncher
│       │   ├── model/           # Modelos compartilhados e estados
│       │   ├── time/            # Relógio e cálculo monotônico da sessão
│       │   ├── designsystem/    # Tema Material 3 e componentes usados
│       │   └── common/          # Resultados e tratamento de erros
│       ├── data/
│       │   ├── local/           # Room, DataStore, entidades e mapeadores
│       │   ├── apps/            # Catálogo de aplicativos iniciáveis
│       │   └── repository/      # Implementações dos repositórios
│       ├── domain/
│       │   ├── repository/      # Contratos necessários entre domínio e dados
│       │   └── usecase/         # Regras de sessão, intervenção, resumo e progresso
│       └── feature/
│           ├── onboarding/
│           ├── launcher/
│           ├── session/
│           ├── summary/
│           ├── insights/
│           ├── groups/
│           ├── mascot/
│           ├── intervention/
│           └── institution/
├── src/test/java/.../           # Domínio, ViewModels, repositórios falsos e mapeadores
└── src/androidTest/java/.../    # Room, launcher e jornadas Compose

backend/
├── src/
│   ├── Hackatudo.Api.csproj     # Único projeto de produção
│   ├── Api/                     # Minimal APIs, autenticação e composição
│   ├── Application/             # Casos de uso e contratos
│   ├── Domain/                  # Entidades e regras compartilháveis
│   └── Infrastructure/          # PostgreSQL e integrações
└── tests/Hackatudo.Api.Tests/   # Único projeto de testes

dashboard/
└── src/app/
    ├── core/                    # Autenticação, erros e cliente HTTP
    ├── features/aggregates/     # Totais e tendências institucionais
    └── shared/                  # Componentes efetivamente reutilizados
```

**Decisão estrutural**: Android, backend e dashboard são produtos separados, mas internamente começam
com um único módulo/projeto. Presentation, Domain e Data são pacotes ou projetos leves; novas divisões
exigem uma necessidade demonstrada. Hilt compõe apenas dependências Android.

## Componentes e Responsabilidades

- `InstalledAppsRepository`: consulta `PackageManager`, mapeia apps iniciáveis e fornece metadados
  leves; ícones são carregados dinamicamente e não persistidos no Room.
- `AppLauncher`: resolve e inicia a Activity do `packageName`; falhas viram erros recuperáveis.
- `HomeRoleManager`: observa `ROLE_HOME`, explica e dispara a solicitação oficial ao usuário.
- `FocusSessionRepository`: persiste sessão, apps relacionados, intenção e eventos atomicamente.
- `FocusContextRepository`: observa e mantém contextos reutilizáveis próprios, preservando presets
  como modelos copiáveis e não mutáveis globalmente.
- `SessionEventRepository`: expõe ao domínio o registro privado de eventos e decisões sem revelar
  Room ou DAOs aos casos de uso.
- Casos de uso `Create`, `Start`, `Pause`, `Resume`, `Complete`, `Cancel` e `GetCurrentFocusSession`
  validam transições sem depender da UI; `ChangeSessionIntentionUseCase` encerra a intenção anterior,
  registra a nova e mantém o histórico consciente durante uma sessão.
- `EvaluateAppLaunchUseCase`: retorna `ALLOW` ou `INTERVENE`; não abre apps nem escreve UI.
- `RecordInterventionDecisionUseCase`: grava decisão e motivo opcional antes de `AppLauncher`.
- `MascotProgressCalculator`: aplica XP e thresholds monotônicos; o MVP usa três estágios.
- `GroupGoalProgressUseCase`: suporta inicialmente `NUMBER_OF_SESSIONS`; minutos ficam como segunda
  modalidade somente se o núcleo estiver estável.
- `SyncRepository` e `SyncWorker`: enviam somente outbox permitida, com retry e idempotência.
- API: autenticação mínima, grupos, membros, metas, mascote e resumos compartilháveis.
- Dashboard: lê somente agregados da turma/grupo, sem filtros ou drill-down individual.

## Fluxos de Dados

### Abertura e intervenção

```text
Compose -> LauncherViewModel -> EvaluateAppLaunchUseCase
  -> ALLOW -> AppLauncher -> Intent do aplicativo
  -> INTERVENE -> InterventionScreen -> decisão persistida -> AppLauncher opcional
```

Aberturas por notificação, deep link ou outro app não passam por esse fluxo. O MVP não tenta
detectá-las ou bloqueá-las.

### Persistência e sincronização

```text
ação local -> transação Room -> UI atualizada por Flow
           -> OutboxEntry (somente dado permitido) -> WorkManager com rede
           -> HTTPS API -> confirmação idempotente -> estado SYNCED
```

Falha remota mantém `PENDING` e aplica backoff. A UI nunca aguarda a rede para concluir uma ação
individual. Conflitos usam versão do recurso: o servidor rejeita versão obsoleta; o app busca o estado
atual e reaplica apenas operações com semântica segura. Contribuições de XP usam ID único e soma
idempotente; mudanças administrativas usam validação de versão e não fazem merge automático.

## APIs Android e Permissões

- Manifesto Home: `ACTION_MAIN`, `CATEGORY_HOME` e `CATEGORY_DEFAULT`.
- `RoleManager.ROLE_HOME`: solicitação explícita após explicação; se não selecionado, o app funciona
  como Activity comum, mas não recebe o botão Home.
- `PackageManager`: descoberta por `ACTION_MAIN` + `CATEGORY_LAUNCHER`, metadados e Intent de abertura.
- Package visibility: declarar apenas consultas por Intent necessárias; não pedir `QUERY_ALL_PACKAGES`
  sem evidência de que o papel Home e consultas compatíveis são insuficientes.
- UsageStats: `PACKAGE_USAGE_STATS` não será solicitado no MVP. Se indicadores futuros exigirem,
  haverá opt-in, tela de justificativa, atalho às configurações e degradação total sem permissão.

## Backend, API e Dashboard

O backend usa um único projeto ASP.NET Core Minimal APIs, separando handlers, aplicação, domínio e
infraestrutura por pastas. PostgreSQL armazena `User`, `School`, `Classroom`, associações, grupos,
metas, progresso e resumos compartilháveis; não armazena lista de apps, motivos ou histórico pessoal.

Endpoints do primeiro ciclo remoto: autenticação demonstrativa, grupos, convite/código, membros, meta,
mascote, resumo compartilhável, escola, turma e vínculo mínimo. O contrato está em
`contracts/openapi.yaml`.

O dashboard Angular limita-se a autenticação demonstrativa e uma tela de totais/tendência. Não há
administração completa, filtros, exportação ou detalhes individuais.

## Segurança, Privacidade e Observabilidade

- Credenciais nunca são persistidas em texto simples; tokens ficam em armazenamento privado e são
  enviados apenas por HTTPS. Autenticação mock só pode existir em build de demonstração.
- No Android, uma abstração de sessão remota autentica o perfil quando o ciclo P2 é habilitado,
  mantém o token em armazenamento privado e o injeta somente nas chamadas HTTPS autorizadas.
- URL remota é configuração de build; segredos não entram no repositório.
- Logs usam eventos técnicos e códigos de correlação, nunca intenção, packageName, UsageStats, motivo,
  mensagem ou conteúdo pessoal.
- Um tratador central converte falhas locais/remotas em mensagens compreensíveis e opções de retry.
- Payloads seguem allowlist explícita. Dados locais privados não possuem DTO remoto.
- Métricas operacionais do MVP limitam-se a saúde da API, erros por código e contagem de jobs, sem
  identificadores ou conteúdo estudantil.

## Riscos e Limitações

| Risco/limitação | Impacto | Mitigação |
|-----------------|---------|-----------|
| Fluxo Home varia por fabricante | Seleção pode divergir | Testar em dois alvos; usar fallback de configurações. |
| Visibilidade de pacotes omite apps | Catálogo incompleto | Validar como app Home; ampliar queries somente com evidência. |
| Abertura externa contorna intervenção | Cobertura parcial | Comunicar limite; não usar acessibilidade, root ou Device Owner. |
| Uso de ícones consome memória | Rolagem lenta | Carregamento sob demanda e cache limitado, sem persistência no banco. |
| Processo morre durante sessão | Estado aparente incorreto | Persistir transições e reconstruir tempo pelo relógio. |
| Rede intermitente duplica contribuição | XP incorreto | IDs idempotentes, outbox e confirmação do servidor. |
| Edição concorrente de grupo | Perda de atualização | Versão otimista e resolução explícita de conflito administrativo. |
| Backend/dashboard atrasam o núcleo | MVP sem fluxo central | Só iniciar milestone remoto após o grupo local demonstrável. |

## Decisões de Entrega por Incremento

1. **Launcher básico**: registro como Home, catálogo iniciável e abertura normal de aplicativos.
2. **Intenção e sessão**: criação, estados, relógio resiliente, priorização contextual e persistência.
3. **Intervenção consciente**: mediação apenas dos cliques originados no launcher e registro privado.
4. **Resumo e indicadores**: cálculos locais e exclusão total ou por sessão.
5. **Grupo e mascote**: grupo local, meta por número de sessões, XP monotônico e três estágios.
6. **Backend e sincronização mínima**: autenticação simples, grupo, outbox e contribuição idempotente.
7. **Dashboard institucional**: totais representativos ou sincronizados, sem filtros.

Cada incremento deve permanecer demonstrável e testável antes do início do seguinte.

## Verificação Pós-Design da Constituição

| Gate constitucional | Resultado pós-design | Evidência de design |
|---------------------|-----------------------|--------------------|
| Autonomia | APROVADO | Contrato de sessão exige `OPEN_ANYWAY`, alteração e encerramento sem autorização. |
| Privacidade | APROVADO | Modelo separa registros pessoais de projeções coletivas; exclusão é transacional. |
| Local-first | APROVADO | Room é fonte de verdade; rede não participa do fluxo individual. |
| Colaboração saudável | APROVADO | Sem ranking; o mascote nunca regride. |
| Simplicidade/MVP | APROVADO | Backend e painel vêm depois do núcleo e têm escopo mínimo. |
| Arquitetura e qualidade | APROVADO | Contratos e quickstart cobrem os riscos. |

O design mantém todos os gates aprovados. Não há item em Rastreamento de Complexidade.

## Rastreamento de Complexidade

Não aplicável: nenhuma violação constitucional requer justificativa.
