# Tarefas: Plataforma de Uso Consciente

**Entrada**: artefatos em `specs/001-uso-consciente-smartphone/`

**Pré-requisitos**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/` e `quickstart.md`

**Organização**: tarefas agrupadas por história de usuário, em entregas verticais demonstráveis.
Cada descrição informa prioridade, dependências, área e resultado observável.

## Formato: `[ID] [P?] [Story?] Descrição`

- **[P]**: pode executar em paralelo por atuar em arquivos distintos e não depender de tarefa aberta.
- **[USn]**: história correspondente na especificação.
- **Prioridade P0**: obrigatória para o fluxo central do hackathon.
- **Prioridade P1**: importante para demonstração.
- **Prioridade P2**: desejável após P0/P1.
- **Prioridade P3**: futura e não bloqueante.

## Fase 1: Setup Android

**Objetivo**: criar somente a base necessária para iniciar o primeiro incremento Android.

- [X] T001 Prioridade P0 — Criar projeto Android de módulo único com Gradle Kotlin DSL, Kotlin 2.x, Java 17, `minSdk 29`, `compileSdk 36` e `targetSdk 36` em `settings.gradle.kts`, `build.gradle.kts` e `app/build.gradle.kts`; concluído quando `assembleDebug` gera APK vazio.
- [X] T002 Prioridade P0 — Configurar Compose, Material 3, Navigation Compose, Lifecycle/ViewModel, Coroutines, Hilt, Room, DataStore e WorkManager no catálogo `gradle/libs.versions.toml` e plugins/dependências de `app/build.gradle.kts` (depende de T001); concluído quando Gradle resolve o grafo P0 sem dependências exclusivas do ciclo remoto.
- [X] T003 [P] Prioridade P0 — Criar `ConsciousUseApplication` anotada para Hilt e registrar no `app/src/main/AndroidManifest.xml` e `app/src/main/java/com/hackatudo/conscious/app/ConsciousUseApplication.kt` (depende de T002); concluído quando a aplicação inicializa.
- [X] T004 [P] Prioridade P0 — Criar `MainActivity` Compose e raiz de navegação vazia em `app/src/main/java/com/hackatudo/conscious/app/MainActivity.kt` e `app/src/main/java/com/hackatudo/conscious/app/AppNavHost.kt` (depende de T002); concluído quando a Activity renderiza.
- [X] T005 [P] Prioridade P0 — Criar estrutura pragmática `core/`, `data/`, `domain/` e `feature/` com arquivos `package-info.md` em `app/src/main/java/com/hackatudo/conscious/`; concluído quando responsabilidades e idioma de nomes estão documentados.
- [X] T006 [P] Prioridade P0 — Criar tema Material 3 mínimo, tipografia e cores de baixo estímulo em `app/src/main/java/com/hackatudo/conscious/core/designsystem/theme/`; concluído quando previews claro/escuro renderizam sem componentes prematuros.
- [X] T007 [P] Prioridade P0 — Configurar JUnit, kotlinx-coroutines-test, MockK, Room testing e Compose UI Test em `app/build.gradle.kts` e `app/src/test/java/com/hackatudo/conscious/TestDispatcherRule.kt` (depende de T002); concluído quando um teste de fumaça passa.

**Checkpoint**: aplicativo vazio compila e possui base de UI, DI e testes sem backend.

---

## Fase 2: Fundação local bloqueante

**Objetivo**: fornecer relógio, persistência, preferências, erros e navegação usados pelas histórias P0.

**⚠️ CRÍTICO**: nenhuma história deve começar antes de T008–T015.

- [X] T008 [P] Prioridade P0 — Criar `AppResult` e erros recuperáveis sem conteúdo privado em `app/src/main/java/com/hackatudo/conscious/core/common/AppResult.kt`; concluído quando sucesso e falha são representáveis sem exceção na UI.
- [X] T009 [P] Prioridade P0 — Criar abstrações `Clock` e `MonotonicClock` e implementações Android em `app/src/main/java/com/hackatudo/conscious/core/time/`; concluído quando testes podem controlar instante e tempo decorrido.
- [X] T010 [P] Prioridade P0 — Configurar `AppDatabase` Room v1 e conversores de UUID/instante em `app/src/main/java/com/hackatudo/conscious/core/database/` (depende de T002); concluído quando banco em memória abre em teste.
- [X] T011 [P] Prioridade P0 — Criar `PrivacyPreferencesDataStore` com onboarding e divulgações de dados em `app/src/main/java/com/hackatudo/conscious/core/datastore/PrivacyPreferencesDataStore.kt` (depende de T002); concluído quando valores sobrevivem à recriação.
- [X] T012 [P] Prioridade P0 — Criar destinos tipados das telas aprovadas em `app/src/main/java/com/hackatudo/conscious/app/Destinations.kt`; concluído quando nenhum destino extra é exposto.
- [X] T013 Prioridade P0 — Conectar tema, Hilt e `AppNavHost` em `app/src/main/java/com/hackatudo/conscious/app/MainActivity.kt` (depende de T003, T004, T006, T012); concluído quando a rota inicial abre.
- [X] T014 [P] Prioridade P0 — Criar módulos Hilt para banco, DataStore e relógio em `app/src/main/java/com/hackatudo/conscious/app/di/LocalDataModule.kt` (depende de T009–T011); concluído quando bindings resolvem em teste.
- [X] T015 Prioridade P0 — Criar tratamento de erro de tela com mensagem neutra e retry em `app/src/main/java/com/hackatudo/conscious/core/designsystem/component/ErrorState.kt` (depende de T006, T008); concluído quando preview apresenta erro sem dados sensíveis.

**Checkpoint**: fundação local pronta; histórias podem avançar na ordem indicada.

---

## Fase 3: História 1 — Launcher consciente básico (P1 da spec, entrega P0) 🎯

**Objetivo**: atuar como Home, listar e abrir aplicativos iniciáveis sem sessão.

**Teste independente**: selecionar o app como Home, visualizar apps, abrir um e retornar pelo botão Home;
recusar o papel Home mantém a Activity utilizável.

### Testes da História 1

- [X] T016 [P] [US1] Prioridade P0 — Escrever teste do mapeamento e ordenação de apps em `app/src/test/java/com/hackatudo/conscious/data/apps/InstalledAppsRepositoryTest.kt` (depende de T008); concluído quando falha antes da implementação e cobre app sem Activity.
- [X] T017 [P] [US1] Prioridade P0 — Escrever teste do estado de `LauncherViewModel` em `app/src/test/java/com/hackatudo/conscious/feature/launcher/LauncherViewModelTest.kt` (depende de T007); concluído quando cobre loading, lista e erro.
- [X] T018 [P] [US1] Prioridade P0 — Escrever teste Compose do grid e estado vazio em `app/src/androidTest/java/com/hackatudo/conscious/feature/launcher/HomeLauncherScreenTest.kt` (depende de T007); concluído quando falha antes da tela existir.

### Implementação da História 1

- [X] T019 [P] [US1] Prioridade P0 — Criar `InstalledApp(packageName, displayName, iconKey, launchable, systemApp)` sem tipos Android em `app/src/main/java/com/hackatudo/conscious/domain/model/InstalledApp.kt`; concluído quando `packageName` identifica o app e `iconKey` permite carregamento dinâmico sem persistência.
- [X] T020 [P] [US1] Prioridade P0 — Declarar contrato `InstalledAppsRepository.observeLaunchableApps()` em `app/src/main/java/com/hackatudo/conscious/domain/repository/InstalledAppsRepository.kt`; concluído quando não expõe `PackageManager` à apresentação.
- [X] T021 [P] [US1] Prioridade P0 — Criar contratos `AppLauncher` e `HomeRoleManager` em `app/src/main/java/com/hackatudo/conscious/core/launcher/`; concluído quando abertura e estados Home são independentes de Compose.
- [X] T022 [US1] Prioridade P0 — Implementar descoberta `ACTION_MAIN`/`CATEGORY_LAUNCHER`, rótulo, ícone sob demanda e exclusão de Activities inválidas em `app/src/main/java/com/hackatudo/conscious/data/apps/AndroidInstalledAppsRepository.kt` (depende de T019, T020); concluído quando T016 passa sem `QUERY_ALL_PACKAGES`.
- [X] T023 [P] [US1] Prioridade P0 — Implementar abertura segura por componente/packageName em `app/src/main/java/com/hackatudo/conscious/core/launcher/AndroidAppLauncher.kt` (depende de T021); concluído quando retorna `AppUnavailable` em vez de falhar.
- [X] T024 [P] [US1] Prioridade P0 — Implementar observação e solicitação de `ROLE_HOME` em `app/src/main/java/com/hackatudo/conscious/core/launcher/AndroidHomeRoleManager.kt` (depende de T021); concluído quando distingue não selecionado e selecionado.
- [X] T025 [US1] Prioridade P0 — Registrar Activity com `MAIN`, `HOME` e `DEFAULT` e queries mínimas de launcher em `app/src/main/AndroidManifest.xml` (depende de T024); concluído quando aparece no seletor Home sem permissões invasivas.
- [X] T026 [US1] Prioridade P0 — Implementar `LauncherUiState` imutável e `LauncherViewModel` com StateFlow em `app/src/main/java/com/hackatudo/conscious/feature/launcher/LauncherViewModel.kt` (depende de T022–T024); concluído quando T017 passa.
- [X] T027 [US1] Prioridade P0 — Implementar grid/lista acessível, loading, vazio, erro e aviso de Home padrão em `app/src/main/java/com/hackatudo/conscious/feature/launcher/HomeLauncherScreen.kt` (depende de T015, T026); concluído quando T018 passa.
- [X] T028 [US1] Prioridade P0 — Integrar rota inicial, solicitação explicada de Home e abertura direta sem sessão em `app/src/main/java/com/hackatudo/conscious/app/AppNavHost.kt` (depende de T013, T023, T027); concluído quando o teste independente da US1 passa em aparelho.

**Checkpoint**: launcher Android mínimo e demonstrável, sem sessão ou servidor.

---

## Fase 4: História 2 — Intenção, sessão e apps relacionados (P1 da spec, entrega P0)

**Objetivo**: criar, persistir, restaurar e conduzir uma sessão que reorganiza os aplicativos.

**Teste independente**: criar sessão com intenção/duração/apps, pausar, retomar, alterar intenção,
reiniciar o processo e concluir com estado coerente, inclusive offline.

### Testes da História 2

- [X] T029 [P] [US2] Prioridade P0 — Escrever testes de `FocusSessionStatus` para `PLANNED → ACTIVE ↔ PAUSED → COMPLETED/CANCELLED` e bloqueio de estado final em `app/src/test/java/com/hackatudo/conscious/domain/model/FocusSessionTest.kt`; concluído quando todas as transições inválidas falham.
- [X] T030 [P] [US2] Prioridade P0 — Escrever testes dos oito casos de uso de sessão, incluindo mudança consciente de intenção durante sessão ativa ou pausada, em `app/src/test/java/com/hackatudo/conscious/domain/usecase/FocusSessionUseCasesTest.kt`; concluído quando valida intenção não vazia, duração positiva, uma sessão ativa e histórico temporal da intenção.
- [X] T031 [P] [US2] Prioridade P0 — Escrever testes instrumentados de DAO, associação por packageName e restauração em `app/src/androidTest/java/com/hackatudo/conscious/data/local/FocusSessionDaoTest.kt`; concluído quando cascata e consulta atual são verificadas.
- [X] T032 [P] [US2] Prioridade P0 — Escrever teste Compose de criação e seleção de apps em `app/src/androidTest/java/com/hackatudo/conscious/feature/session/CreateSessionFlowTest.kt`; concluído quando cobre marcar/desmarcar e validação.

### Implementação da História 2

- [X] T033 [P] [US2] Prioridade P0 — Modelar `FocusSession`, `FocusSessionStatus`, `UsageIntent` e `FocusContext(name, suggestedIntention, relatedPackageNames, source)` em `app/src/main/java/com/hackatudo/conscious/domain/model/`; concluído quando contexto é opcional/editável, título/intenção são obrigatórios, duração é positiva e `groupId` é opcional.
- [X] T034 [P] [US2] Prioridade P0 — Criar `FocusSessionEntity`, `UsageIntentEntity`, `SessionAppEntity` e `FocusContextEntity` em `app/src/main/java/com/hackatudo/conscious/data/local/entity/`; concluído quando SessionApp usa `sessionId + packageName`, contexto guarda nome/intenção/apps/origem e nenhum ícone é persistido.
- [X] T035 [US2] Prioridade P0 — Criar `FocusSessionDao` e `FocusContextDao` para transições, sessão atual e CRUD de contextos reutilizáveis em `app/src/main/java/com/hackatudo/conscious/data/local/dao/` (depende de T034); concluído quando T031 passa e presets podem ser copiados sem alteração global.
- [X] T036 [US2] Prioridade P0 — Registrar entidades e DAO no `AppDatabase` v1 em `app/src/main/java/com/hackatudo/conscious/core/database/AppDatabase.kt` (depende de T010, T034, T035); concluído quando schema exportado corresponde ao modelo.
- [X] T037 [P] [US2] Prioridade P0 — Declarar `FocusSessionRepository` e `FocusContextRepository` em `app/src/main/java/com/hackatudo/conscious/domain/repository/` (depende de T033); concluído quando sessão expõe Flow atual/comandos atômicos e contexto expõe observação e CRUD sem tipos Room.
- [X] T038 [US2] Prioridade P0 — Implementar mapeadores, `RoomFocusSessionRepository` e `RoomFocusContextRepository` em `app/src/main/java/com/hackatudo/conscious/data/repository/` (depende de T035–T037); concluído quando sessão, seleção e contextos próprios sobrevivem à recriação.
- [X] T039 [P] [US2] Prioridade P0 — Implementar `CreateFocusSessionUseCase` e `StartFocusSessionUseCase` em `app/src/main/java/com/hackatudo/conscious/domain/usecase/session/` (depende de T037); concluído quando impedem dados inválidos e segunda sessão ativa.
- [X] T040 [P] [US2] Prioridade P0 — Implementar `PauseFocusSessionUseCase` e `ResumeFocusSessionUseCase` em `app/src/main/java/com/hackatudo/conscious/domain/usecase/session/` (depende de T037); concluído quando acumulam pausa sem duração negativa.
- [X] T041 [P] [US2] Prioridade P0 — Implementar `CompleteFocusSessionUseCase`, `CancelFocusSessionUseCase`, `GetCurrentSessionUseCase` e `ChangeSessionIntentionUseCase` em `app/src/main/java/com/hackatudo/conscious/domain/usecase/session/` (depende de T037); concluído quando estados finais/restauração são coerentes e a mudança encerra a intenção anterior e registra a nova sem classificá-la como falha.
- [X] T042 [US2] Prioridade P0 — Ligar casos de uso, `FocusSessionRepository` e `FocusContextRepository` às implementações Room em `app/src/main/java/com/hackatudo/conscious/app/di/SessionModule.kt` (depende de T038–T041); concluído quando T030 passa e nenhum ViewModel conhece DAO.
- [X] T043 [P] [US2] Prioridade P0 — Implementar `CreateSessionUiState` e `CreateSessionViewModel` com observação, criação, edição, exclusão e aplicação editável de `FocusContext` em `app/src/main/java/com/hackatudo/conscious/feature/session/create/` (depende de T026, T038, T039); concluído quando contexto, intenção, duração e seleção são StateFlow imutável e contextos próprios podem ser reutilizados.
- [X] T044 [P] [US2] Prioridade P0 — Implementar `CreateSessionScreen` com seleção opcional, criação e edição de contexto, intenção e duração em `app/src/main/java/com/hackatudo/conscious/feature/session/create/CreateSessionScreen.kt` (depende de T006, T043); concluído quando Aula/Estudo/Leitura preenchem valores editáveis, modelos próprios podem ser mantidos e erros aparecem antes da confirmação.
- [X] T045 [P] [US2] Prioridade P0 — Implementar `SelectAppsScreen` com marcação por packageName em `app/src/main/java/com/hackatudo/conscious/feature/session/selectapps/SelectAppsScreen.kt` (depende de T022, T043); concluído quando seleção pode ser restaurada.
- [X] T046 [US2] Prioridade P0 — Adaptar `HomeLauncherScreen` para área destacada e secundária durante sessão em `app/src/main/java/com/hackatudo/conscious/feature/launcher/HomeLauncherScreen.kt` (depende de T026, T038); concluído quando a mesma app pode mudar de contexto entre sessões.
- [X] T047 [US2] Prioridade P0 — Implementar `ActiveSessionScreen`, ação de mudança consciente de intenção e temporizador derivado do relógio em `app/src/main/java/com/hackatudo/conscious/feature/session/active/` (depende de T009, T040, T041); concluído quando mostra intenção, estado e tempo restante, permite alterá-la e registra a mudança sem gravar por segundo.
- [X] T048 [US2] Prioridade P0 — Integrar criação, seleção, sessão ativa e retorno ao launcher em `app/src/main/java/com/hackatudo/conscious/app/AppNavHost.kt` (depende de T044–T047); concluído quando T032 e o teste independente passam offline.

**Checkpoint**: intenção, sessão e organização contextual funcionam localmente de ponta a ponta.

---

## Fase 5: História 3 — Intervenção consciente (P1 da spec, entrega P0)

**Objetivo**: mediar somente toques fora do contexto iniciados no launcher, preservando decisão.

**Teste independente**: tocar em app relacionado abre diretamente; tocar em não relacionado oferece
continuar focado ou abrir, registra a escolha e nunca bloqueia ou pune.

### Testes da História 3

- [X] T049 [P] [US3] Prioridade P0 — Escrever matriz de testes `sem sessão/relacionado → ALLOW` e `não relacionado → INTERVENE` em `app/src/test/java/com/hackatudo/conscious/domain/usecase/EvaluateAppLaunchUseCaseTest.kt`; concluído quando casos falham antes da implementação.
- [X] T050 [P] [US3] Prioridade P0 — Escrever teste Room de persistência privada de decisão e motivo opcional em `app/src/androidTest/java/com/hackatudo/conscious/data/local/InterventionDaoTest.kt`; concluído quando nenhum evento coletivo é criado.
- [X] T051 [P] [US3] Prioridade P0 — Escrever teste Compose das duas ações e linguagem neutra em `app/src/androidTest/java/com/hackatudo/conscious/feature/intervention/InterventionScreenTest.kt`; concluído quando intenção, app e tempo estão presentes.

### Implementação da História 3

- [X] T052 [P] [US3] Prioridade P0 — Modelar `InterventionDecision`, `ReflectionReason`, `SessionEvent` e `AppLaunchEvaluation` em `app/src/main/java/com/hackatudo/conscious/domain/model/`; concluído quando decisão aceita somente `STAY_FOCUSED` ou `OPEN_ANYWAY` e motivo é opcional.
- [X] T053 [P] [US3] Prioridade P0 — Declarar `SessionEventRepository` no domínio e criar `SessionEventEntity` e `SessionEventDao` em `app/src/main/java/com/hackatudo/conscious/domain/repository/SessionEventRepository.kt` e `app/src/main/java/com/hackatudo/conscious/data/local/` (depende de T052); concluído quando o contrato não expõe Room e targetPackage existe apenas para tentativa do launcher.
- [X] T054 [US3] Prioridade P0 — Adicionar `SessionEventEntity` e cascata por sessão ao banco em `app/src/main/java/com/hackatudo/conscious/core/database/AppDatabase.kt` (depende de T036, T053); concluído quando T050 passa.
- [X] T055 [P] [US3] Prioridade P0 — Implementar `EvaluateAppLaunchUseCase` em `app/src/main/java/com/hackatudo/conscious/domain/usecase/intervention/EvaluateAppLaunchUseCase.kt` (depende de T037, T052); concluído quando T049 passa.
- [X] T056 [P] [US3] Prioridade P0 — Implementar `RoomSessionEventRepository` e `RecordInterventionDecisionUseCase` em `app/src/main/java/com/hackatudo/conscious/data/repository/RoomSessionEventRepository.kt` e `app/src/main/java/com/hackatudo/conscious/domain/usecase/intervention/RecordInterventionDecisionUseCase.kt` (depende de T053, T054); concluído quando o caso de uso depende somente do contrato de domínio e grava a escolha antes da abertura.
- [X] T057 [US3] Prioridade P0 — Criar `InterventionUiState` e `InterventionViewModel` em `app/src/main/java/com/hackatudo/conscious/feature/intervention/` (depende de T047, T055, T056); concluído quando expõe intenção, app, tempo e ações por StateFlow.
- [X] T058 [US3] Prioridade P0 — Implementar `InterventionScreen` com “Continuar focado”, “Abrir mesmo assim” e motivo opcional em `app/src/main/java/com/hackatudo/conscious/feature/intervention/InterventionScreen.kt` (depende de T057); concluído quando T051 passa sem texto punitivo.
- [X] T059 [US3] Prioridade P0 — Encaminhar cliques do launcher por `EvaluateAppLaunchUseCase` e abrir app somente após decisão em `app/src/main/java/com/hackatudo/conscious/feature/launcher/LauncherViewModel.kt` e `app/src/main/java/com/hackatudo/conscious/app/AppNavHost.kt` (depende de T023, T055–T058); concluído quando o teste independente passa.
- [X] T060 [US3] Prioridade P0 — Documentar na tela de privacidade provisória que notificações/deep links externos não são interceptados em `app/src/main/java/com/hackatudo/conscious/feature/intervention/InterventionLimitationsText.kt` (depende de T058); concluído quando limitação aparece acessivelmente.

**Checkpoint**: diferencial central demonstrável sem AccessibilityService, UsageStats ou servidor.

---

## Fase 6: História 4 — Resumo, indicadores e exclusão (P2 da spec, P0/P1)

**Objetivo**: fechar a sessão com resumo local neutro e dar ao estudante controle do histórico.

**Teste independente**: concluir sessões com eventos, conferir métricas e excluir uma ou todas sem
alterar onboarding/grupos nem expor dados fora do aparelho.

- [X] T061 [P] [US4] Prioridade P0 — Escrever testes de duração realizada sem pausas e contagens de eventos em `app/src/test/java/com/hackatudo/conscious/domain/usecase/BuildSessionSummaryUseCaseTest.kt`; concluído quando cobre relógio alterado e duração não negativa.
- [X] T062 [P] [US4] Prioridade P1 — Escrever testes de agregação diária pessoal em `app/src/test/java/com/hackatudo/conscious/domain/usecase/GetPersonalInsightsUseCaseTest.kt`; concluído quando sessões excluídas não são contadas.
- [X] T063 [P] [US4] Prioridade P0 — Escrever teste transacional de exclusão por sessão e total em `app/src/androidTest/java/com/hackatudo/conscious/data/local/DeleteHistoryTest.kt`; concluído quando dependentes somem e grupo/onboarding permanecem.
- [X] T064 [US4] Prioridade P0 — Criar consultas Room para resumo e histórico em `app/src/main/java/com/hackatudo/conscious/data/local/dao/SessionSummaryDao.kt` (depende de T054); concluído quando produz duração e contagens sem tabela duplicada.
- [X] T065 [P] [US4] Prioridade P0 — Implementar `BuildSessionSummaryUseCase` em `app/src/main/java/com/hackatudo/conscious/domain/usecase/summary/BuildSessionSummaryUseCase.kt` (depende de T009, T064); concluído quando T061 passa.
- [X] T066 [P] [US4] Prioridade P1 — Implementar `GetPersonalInsightsUseCase` em `app/src/main/java/com/hackatudo/conscious/domain/usecase/insights/GetPersonalInsightsUseCase.kt` (depende de T064); concluído quando T062 passa.
- [X] T067 [US4] Prioridade P0 — Implementar exclusão transacional por sessão e de todo histórico no `RoomFocusSessionRepository` em `app/src/main/java/com/hackatudo/conscious/data/repository/RoomFocusSessionRepository.kt` (depende de T063, T064); concluído quando T063 passa.
- [X] T068 [P] [US4] Prioridade P0 — Criar `SessionSummaryViewModel` e tela neutra em `app/src/main/java/com/hackatudo/conscious/feature/summary/` (depende de T065); concluído quando exibe duração, intervenções, foco e mudanças conscientes.
- [X] T069 [P] [US4] Prioridade P1 — Criar `PersonalInsightsViewModel` e indicadores básicos em `app/src/main/java/com/hackatudo/conscious/feature/insights/` (depende de T066); concluído quando mostra sessões, duração média e consistência local.
- [X] T070 [US4] Prioridade P0 — Integrar conclusão → resumo e confirmações de exclusão em `app/src/main/java/com/hackatudo/conscious/app/AppNavHost.kt` (depende de T067, T068); concluído quando o corte P0 passa offline; indicadores T069 integram sua própria rota P1 depois.

**Checkpoint P0**: launcher, sessão, apps, intervenção, decisão, persistência e resumo completos.

---

## Fase 7: História 5 — Grupo, mascote, meta e sincronização mínima (P3 da spec, entrega P1)

**Objetivo**: demonstrar colaboração local positiva e, somente depois, sincronização mínima real.

**Teste independente**: criar “Missão Matemática”, confirmar OWNER, meta de sessões e três estágios do
mascote sem ranking/regressão; com rede, repetir contribuição sem duplicar XP.

### Núcleo local da História 5

- [X] T071 [P] [US5] Prioridade P1 — Escrever testes de OWNER único, transferência e saída em `app/src/test/java/com/hackatudo/conscious/domain/usecase/GroupAdministrationUseCaseTest.kt`; concluído quando saída sem membro elegível tem resultado explícito.
- [X] T072 [P] [US5] Prioridade P1 — Escrever testes de thresholds `0–99`, `100–249`, `250+` e XP monotônico em `app/src/test/java/com/hackatudo/conscious/domain/usecase/MascotProgressCalculatorTest.kt`; concluído quando XP nunca diminui.
- [X] T073 [P] [US5] Prioridade P1 — Escrever testes de meta `NUMBER_OF_SESSIONS`, idempotência e XP de conclusão em `app/src/test/java/com/hackatudo/conscious/domain/usecase/GroupGoalProgressUseCaseTest.kt`; concluído quando a meta conclui uma vez.
- [X] T074 [P] [US5] Prioridade P1 — Modelar `StudyGroup`, `GroupMember`, `GroupRole`, `Mascot`, `MascotStage`, `GroupGoal` e `GroupContribution` em `app/src/main/java/com/hackatudo/conscious/domain/model/group/`; concluído quando grupo tem OWNER, XP não negativo e meta positiva.
- [X] T075 [P] [US5] Prioridade P1 — Criar entidades e DAOs Room de grupo, membro, mascote, meta e contribuição em `app/src/main/java/com/hackatudo/conscious/data/local/group/` (depende de T074); concluído quando chaves e cascatas preservam exatamente um OWNER.
- [X] T076 [US5] Prioridade P1 — Registrar tabelas colaborativas no banco e exportar schema em `app/src/main/java/com/hackatudo/conscious/core/database/AppDatabase.kt` (depende de T075); concluído quando teste Room abre a versão atual.
- [X] T077 [P] [US5] Prioridade P1 — Criar `StudyGroupRepository` e implementação Room em `app/src/main/java/com/hackatudo/conscious/domain/repository/StudyGroupRepository.kt` e `app/src/main/java/com/hackatudo/conscious/data/repository/RoomStudyGroupRepository.kt` (depende de T075); concluído quando criar/listar/editar, adicionar/remover membro, sair e transferir são atômicos.
- [X] T078 [P] [US5] Prioridade P1 — Implementar administração e transferência em `app/src/main/java/com/hackatudo/conscious/domain/usecase/group/GroupAdministrationUseCase.kt` (depende de T077); concluído quando T071 passa.
- [X] T079 [P] [US5] Prioridade P1 — Implementar `MascotProgressCalculator` com três estágios em `app/src/main/java/com/hackatudo/conscious/domain/usecase/mascot/MascotProgressCalculator.kt` (depende de T074); concluído quando T072 passa.
- [X] T080 [P] [US5] Prioridade P1 — Implementar meta `NUMBER_OF_SESSIONS` e XP transparente em `app/src/main/java/com/hackatudo/conscious/domain/usecase/group/GroupGoalProgressUseCase.kt` (depende de T074, T079); concluído quando T073 passa.
- [X] T081 [P] [US5] Prioridade P1 — Criar três assets simples de mascote com descrições acessíveis em `app/src/main/res/drawable/` e `app/src/main/res/values/strings.xml`; concluído quando estágios são distinguíveis sem punição visual.
- [X] T082 [US5] Prioridade P1 — Criar `GroupsScreen`, `CreateGroupScreen`, entrada demonstrativa por código e ViewModels em `app/src/main/java/com/hackatudo/conscious/feature/groups/` (depende de T077–T081); concluído quando criar/listar/entrar e escolher mascote funcionam localmente.
- [X] T083 [US5] Prioridade P1 — Criar `GroupDetailsScreen` com participantes, quantidade de sessões, atividades coletivas, meta, progresso, mascote, edição e transferência em `app/src/main/java/com/hackatudo/conscious/feature/groups/detail/` (depende de T082); concluído quando RF-024 passa sem ranking ou detalhe individual.

### Backend e sincronização mínima da História 5

- [ ] T084 [US5] Prioridade P2 — Criar solução ASP.NET Core 10 com um projeto de produção `backend/src/Hackatudo.Api.csproj`, pastas `Api/Application/Domain/Infrastructure` e um projeto `backend/tests/Hackatudo.Api.Tests/` (depende de T083); concluído quando `dotnet test backend/tests` executa.
- [ ] T085 [P] [US5] Prioridade P2 — Configurar PostgreSQL, EF Core, migração inicial e configuração externa em `backend/src/Infrastructure/Persistence/` (depende de T084); concluído quando banco sobe sem segredo versionado.
- [ ] T086 [P] [US5] Prioridade P2 — Modelar `User`, `School`, `Classroom`, `ClassroomMember`, `StudyGroup`, `GroupMember`, `GroupInvite`, `GroupGoal`, `MascotProgress` e `SharedSessionSummary` sem dados privados em `backend/src/Domain/` (depende de T084); concluído quando invariantes OWNER, convite, turma, XP e idempotência têm testes.
- [ ] T087 [US5] Prioridade P2 — Implementar hashing com `PasswordHasher<User>`, login, emissão/validação de token e teste que proíbe credencial em texto simples em `backend/src/Api/Endpoints/AuthEndpoints.cs` e `backend/tests/Hackatudo.Api.Tests/AuthTests.cs` (depende de T085, T086); concluído quando contratos 200/401 passam, somente o hash é persistido e senha não aparece em logs.
- [ ] T088 [P] [US5] Prioridade P2 — Implementar autorização OWNER e endpoints de criar/listar grupos e adicionar/remover membros em `backend/src/Api/Endpoints/GroupEndpoints.cs` (depende de T087); concluído quando contratos 201/403/409 e OWNER único passam.
- [ ] T089 [P] [US5] Prioridade P2 — Implementar criação, revogação e consumo de convite/código em `backend/src/Api/Endpoints/GroupInviteEndpoints.cs` (depende de T087); concluído quando código válido cria MEMBER uma vez e código inválido retorna 404.
- [ ] T090 [P] [US5] Prioridade P2 — Implementar endpoints de meta e mascote monotônico em `backend/src/Api/Endpoints/GroupProgressEndpoints.cs` (depende de T087); concluído quando meta `NUMBER_OF_SESSIONS`, XP idempotente e conflitos 409 passam.
- [ ] T091 [P] [US5] Prioridade P2 — Implementar ownership e `session-summaries` allowlist em `backend/src/Api/Endpoints/GroupSyncEndpoints.cs` (depende de T087); concluído quando transferência é atômica e resumo não aceita app, intenção, UsageStats ou motivo.
- [ ] T092 [P] [US5] Prioridade P2 — Criar `SyncOutboxEntity`, DAO e escrita na mesma transação da contribuição em `app/src/main/java/com/hackatudo/conscious/data/local/sync/` (depende de T076); concluído quando ação offline gera item PENDING sem dado privado.
- [ ] T093 [P] [US5] Prioridade P2 — Adicionar Retrofit/OkHttp ao catálogo Gradle e criar cliente somente HTTPS, DTO allowlist, sessão autenticada, armazenamento privado do token, interceptor de autorização e configuração externa de URL em `gradle/libs.versions.toml` e `app/src/main/java/com/hackatudo/conscious/core/network/` (depende de T087–T091); concluído quando login autentica chamadas protegidas, token não aparece em logs e DTO não possui packageName, intenção, UsageStats ou motivo.
- [ ] T094 [US5] Prioridade P2 — Implementar `SyncWorker` Hilt/WorkManager com rede, backoff e Idempotency-Key em `app/src/main/java/com/hackatudo/conscious/data/sync/SyncWorker.kt` (depende de T092, T093); concluído quando retry não duplica contribuição.
- [ ] T095 [US5] Prioridade P2 — Implementar conflito por versão e expor `PENDING/SYNCED/FAILED` em `app/src/main/java/com/hackatudo/conscious/data/repository/SyncRepositoryImpl.kt` e `app/src/main/java/com/hackatudo/conscious/feature/groups/SyncStatusUiState.kt` (depende de T094); concluído quando a UI mostra estado/retry e conflito administrativo pede nova decisão sem bloquear uso local.

**Checkpoint**: P1 termina em T083; T084–T095 são P2 e não bloqueiam a demonstração local.

---

## Fase 8: História 6 — Sugestão pedagógica opcional (P4 da spec, entrega P2)

**Objetivo**: demonstrar sugestão identificada que o estudante aceita, adapta ou ignora.

**Teste independente**: abrir sugestão local e exercer as três escolhas sem conceder controle externo.

- [ ] T096 [P] [US6] Prioridade P2 — Modelar `PedagogicalSuggestion` e estados `PENDING`, `ACCEPTED`, `ADAPTED`, `IGNORED` em `app/src/main/java/com/hackatudo/conscious/domain/model/PedagogicalSuggestion.kt`; concluído quando origem é obrigatória.
- [ ] T097 [P] [US6] Prioridade P2 — Criar fixture local de sugestão e repositório somente leitura em `app/src/main/java/com/hackatudo/conscious/data/repository/DemoSuggestionRepository.kt`; concluído quando não depende de servidor.
- [ ] T098 [US6] Prioridade P2 — Criar tela/ViewModel de sugestão com aceitar, adaptar e ignorar em `app/src/main/java/com/hackatudo/conscious/feature/session/suggestion/` (depende de T096, T097, T043); concluído quando aceitar/adaptar abre sessão editável e ignorar encerra.
- [ ] T099 [US6] Prioridade P2 — Adicionar teste Compose das três decisões e origem visível em `app/src/androidTest/java/com/hackatudo/conscious/feature/session/SuggestionScreenTest.kt` (depende de T098); concluído quando nenhuma escolha exige autorização.

---

## Fase 9: História 7 — Indicadores institucionais (P5 da spec, entrega P1/P2)

**Objetivo**: demonstrar totais úteis sem filtros, exportação ou detalhamento individual.

**Teste independente**: abrir painel com dados representativos, interpretar três tendências e tentar
sem sucesso isolar estudante ou acessar dados privados.

- [ ] T100 [P] [US7] Prioridade P1 — Definir fixture `InstitutionalAggregate` com totais sem dimensão individual em `app/src/main/java/com/hackatudo/conscious/data/demo/InstitutionalAggregateFixtures.kt`; concluído quando contém sessões, média, conclusão, intervenções e tendência.
- [ ] T101 [US7] Prioridade P1 — Criar tela Android demonstrativa de agregados em `app/src/main/java/com/hackatudo/conscious/feature/institution/InstitutionOverviewScreen.kt` (depende de T100); concluído quando não há filtro, exportação ou drill-down.
- [ ] T102 [P] [US7] Prioridade P2 — Implementar criação/listagem mínima de `School`, `Classroom`, `ClassroomMember` e endpoint agregado em `backend/src/Domain/` e `backend/src/Api/Endpoints/InstitutionEndpoints.cs` (depende de T084–T087); concluído quando organiza turmas e responde apenas ao schema agregado sem histórico individual.
- [ ] T103 [P] [US7] Prioridade P2 — Criar projeto Angular e configuração externa da API em `dashboard/` (depende de T087); concluído quando build e teste padrão passam.
- [ ] T104 [P] [US7] Prioridade P2 — Implementar login mock restrito ao build de demonstração em `dashboard/src/app/core/auth/` (depende de T103); concluído quando produção não contém credencial hardcoded.
- [ ] T105 [US7] Prioridade P2 — Implementar cliente de agregados, loading e erro em `dashboard/src/app/features/aggregates/aggregate.service.ts` (depende de T102, T103); concluído quando consome somente `/aggregates/classrooms/{id}`.
- [ ] T106 [US7] Prioridade P2 — Implementar seleção/criação mínima de turma e overview com total, média, conclusão, intervenções e tendência em `dashboard/src/app/features/aggregates/` (depende de T105); concluído quando organiza turmas sem filtro ou detalhe individual.
- [ ] T107 [US7] Prioridade P2 — Criar testes Angular de renderização, erro e ausência de drill-down em `dashboard/src/app/features/aggregates/aggregate-overview.spec.ts` (depende de T106); concluído quando todos passam.

**Checkpoint**: T100–T101 satisfazem a demonstração P1; backend/dashboard completos permanecem P2.

---

## Fase 10: UsageStats opcional e isolado (P3)

**Objetivo**: preparar indicador pessoal futuro sem criar dependência ou vigilância no MVP.

- [ ] T108 [P] Prioridade P3 — Criar contrato opcional `PersonalUsageStatsRepository` sem binding no fluxo principal em `app/src/main/java/com/hackatudo/conscious/domain/repository/PersonalUsageStatsRepository.kt`; concluído quando nenhuma feature P0 depende dele.
- [ ] T109 [P] Prioridade P3 — Criar detector de disponibilidade/permissão e Intent de configurações em `app/src/main/java/com/hackatudo/conscious/data/apps/AndroidUsageStatsRepository.kt`; concluído quando ausência de `PACKAGE_USAGE_STATS` retorna indisponível sem erro.
- [ ] T110 Prioridade P3 — Criar tela de explicação e opt-in separado em `app/src/main/java/com/hackatudo/conscious/feature/settings/UsageAccessSettingsScreen.kt` (depende de T108, T109); concluído quando recusa mantém launcher e sessões íntegros.
- [ ] T111 Prioridade P3 — Criar consulta local mínima e teste de não serialização em `app/src/test/java/com/hackatudo/conscious/data/apps/UsageStatsPrivacyTest.kt` (depende de T109, T110); concluído quando nenhum DTO remoto aceita detalhes de UsageStats.

---

## Fase 11: Onboarding, privacidade, qualidade e demonstração

**Objetivo**: preparar uma execução verificável e segura sem adicionar funcionalidades.

- [ ] T112 [P] Prioridade P0 — Criar onboarding sobre propósito, launcher, intenção, sessões, intervenções, privacidade, grupos, mascotes, categorias compartilháveis e papel Home em `app/src/main/java/com/hackatudo/conscious/feature/onboarding/` (depende de T027, T060); concluído quando todo RF-016 é explicado antes de solicitar ROLE_HOME.
- [ ] T113 [P] Prioridade P1 — Criar `SettingsScreen` com categorias locais/remotas previstas e exclusão de histórico em `app/src/main/java/com/hackatudo/conscious/feature/settings/SettingsScreen.kt` (depende de T011, T067, T083); concluído quando estudante identifica o que permanece local e o que poderá sair do aparelho.
- [ ] T114 [P] Prioridade P1 — Criar dados seed “Estudar Matemática”, “Missão Matemática”, meta e três estágios em `app/src/main/java/com/hackatudo/conscious/data/demo/DemoSeed.kt` (depende de T070, T083, T100); concluído quando reset de demo é determinístico.
- [ ] T115 [P] Prioridade P0 — Criar teste instrumentado do fluxo launcher → sessão → intervenção → resumo offline em `app/src/androidTest/java/com/hackatudo/conscious/EndToEndOfflineTest.kt` (depende de T070); concluído quando passa em modo avião.
- [ ] T116 [P] Prioridade P2 — Criar teste de serialização allowlist e rejeição de campos privados em `app/src/test/java/com/hackatudo/conscious/core/network/SharedPayloadPrivacyTest.kt` e `backend/tests/PrivacyPayloadTests.cs` (depende de T093); concluído quando packageName, intenção e motivo são impossíveis no payload.
- [ ] T117 [P] Prioridade P1 — Revisar semântica, fonte ampliada e independência de cor nas telas em `app/src/androidTest/java/com/hackatudo/conscious/AccessibilitySmokeTest.kt` (depende de T112–T114); concluído quando fluxos essenciais são navegáveis.
- [ ] T118 Prioridade P0 — Auditar manifesto e logs em `app/src/main/AndroidManifest.xml` e `app/src/main/java/com/hackatudo/conscious/core/common/`; concluído quando não há câmera, microfone, localização, AccessibilityService, Device Owner, UsageStats no P0 ou dado privado em log.
- [ ] T119 [P] Prioridade P2 — Documentar dados locais, sincronizáveis, institucionais e limitações do Android em `docs/privacy-and-platform-limitations.md` (depende de T095, T113); concluído quando aberturas externas e partes simuladas/reais estão explícitas.
- [ ] T120 Prioridade P0 — Executar testes/build e medir abertura fria ≤2 s e ações locais ≤100 ms no aparelho de demonstração conforme `specs/001-uso-consciente-smartphone/quickstart.md` (depende de T112, T115, T118); concluído quando P0 passa, metas são registradas e APK final é gerado.
- [ ] T121 Prioridade P1 — Executar os oito cenários e protocolo com participantes em `specs/001-uso-consciente-smartphone/quickstart.md`, registrando tempos, compreensão e neutralidade em `docs/demo-validation.md` (depende de T114, T117, T120); concluído quando CS-001, CS-004, CS-005 e CS-009 têm amostra e percentuais documentados.
- [ ] T122 Prioridade P2 — Executar testes/build do backend e dashboard e registrar limites em `docs/remote-demo-validation.md` (depende de T095, T107, T116); concluído quando retry, idempotência, conflito e agregados passam sem dados privados.

---

## Dependências e Ordem de Execução

### Dependências entre fases

- Setup T001–T007 precede a fundação T008–T015.
- Fundação completa bloqueia todas as histórias.
- US1 T016–T028 entrega o launcher e precede US2.
- US2 T029–T048 entrega sessão/contexto e precede US3.
- US3 T049–T060 entrega intervenção e precede US4.
- US4 T061–T070 fecha o corte P0.
- US5 local T071–T083 depende do P0 e entrega colaboração P1.
- Backend/sync T084–T095 depende do grupo local e é P2.
- US6 T096–T099 depende da criação de sessão, mas pode ocorrer após o P0.
- US7 local T100–T101 independe do backend; T102–T107 depende do ciclo remoto.
- UsageStats T108–T111 é P3 e não bloqueia nenhuma entrega.
- Preparação final seleciona T112–T122 conforme o corte P0, P1 ou P2 escolhido.

### Grafo das histórias

```text
Setup -> Fundação -> US1 -> US2 -> US3 -> US4 -> P0 completo
                                                  ├-> US5 local -> P1 colaboração
                                                  ├-> US6 sugestão (P2)
                                                  └-> US7 local -> P1 institucional
US5 local -> backend/sync -> US7 remoto/dashboard (P2)
UsageStats (P3) permanece isolado
```

### Oportunidades de paralelismo

- Setup: T003–T007 após T002.
- Fundação: T008–T012; T014 após suas entradas.
- US1: testes T016–T018 e contratos/modelos T019–T021.
- US2: testes T029–T032; modelos T033–T034; casos de uso T039–T041; telas T044–T045.
- US3: testes T049–T051; modelos/persistência T052–T053; casos de uso T055–T056.
- US4: testes T061–T063; resumo e insights T065–T066; telas T068–T069.
- US5: testes T071–T073; modelo T074; regras T078–T080; fundação backend T085–T086 após T084; endpoints T088–T091 em paralelo após T087.
- US7: fixture/tela Android podem avançar sem T102–T107; dashboard T103 pode avançar em paralelo a T102.

## Exemplos de Execução Paralela

### US1

```text
T016 + T017 + T018
T019 + T020 + T021
T023 + T024
```

### US2

```text
T029 + T030 + T031 + T032
T039 + T040 + T041
T044 + T045
```

### US5

```text
T071 + T072 + T073
T078 + T079 + T080
T085 + T086
```

## Estratégia de Implementação

### Corte P0 obrigatório

1. Concluir Setup e Fundação.
2. Entregar US1, US2, US3 e o resumo/exclusão de US4.
3. Executar T112, T115, T118 e T120.
4. Parar e demonstrar o fluxo individual integralmente offline.

### Corte P1 para o hackathon

1. Adicionar indicadores pessoais T062, T066 e T069.
2. Entregar grupo/mascote/meta local T071–T083.
3. Entregar painel Android representativo T100–T101.
4. Preparar seed, acessibilidade e roteiro T114, T117, T121.

### Corte P2 remoto

1. Somente após P0 e colaboração local estáveis, executar T084–T095.
2. Acrescentar sugestão pedagógica T096–T099.
3. Executar API agregada e dashboard T102–T107.
4. Validar privacidade e demonstração remota T116, T119 e T122.

### Corte P3 futuro

T108–T111 formam backlog P3 fora da execução padrão. Movê-las para uma entrega ativa somente se surgir
requisito claro para UsageStats; sua ausência não reduz nenhum critério do MVP.

## Notas

- Testes de regra devem ser escritos e falhar antes da implementação correspondente.
- `[P]` indica arquivos distintos e ausência de dependência aberta naquele ponto.
- Não iniciar backend/dashboard para compensar atraso do fluxo Android P0.
- Código, tipos e arquivos ficam em inglês; documentação permanece em português do Brasil.
- Nenhuma tarefa autoriza MDM, root, AccessibilityService, vigilância ou bloqueio irreversível.
