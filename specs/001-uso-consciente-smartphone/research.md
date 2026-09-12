# Pesquisa Técnica: Plataforma de Uso Consciente

## 1. Experiência de launcher

**Decisão**: Declarar uma Activity elegível para `MAIN`, `HOME` e `DEFAULT`; solicitar ao usuário o
papel de app Home quando a API permitir e orientar a seleção nas configurações nos demais casos.

**Justificativa**: O Android define oficialmente `ROLE_HOME` para o aplicativo usado como tela
inicial. A escolha continua explícita e reversível pelo usuário.

**Alternativas consideradas**: Sobrepor o launcher existente foi rejeitado por não entregar a tela
inicial; gestão corporativa do aparelho foi rejeitada por violar escopo e autonomia.

**Fonte**: [RoleManager — Android Developers](https://developer.android.com/reference/android/app/role/RoleManager)

## 2. Catálogo e abertura de aplicativos

**Decisão**: Consultar Activities iniciáveis com intenção `MAIN`/`LAUNCHER`, exibir apenas resultados
legitimamente visíveis e abrir o componente escolhido por Intent explícita.

**Justificativa**: É suficiente para um launcher funcional e limita a visibilidade de pacotes. A
necessidade de ampliar visibilidade deve ser reavaliada somente se testes reais demonstrarem lacunas.

**Alternativas consideradas**: `QUERY_ALL_PACKAGES` foi rejeitado no MVP por ampliar o acesso à lista
de aplicativos; lista fixa foi rejeitada por não representar o aparelho do estudante.

**Fonte**: [Visibilidade de pacotes — Android Developers][package-visibility]

[package-visibility]: https://developer.android.com/training/package-visibility/declaring

## 3. Limite da intervenção consciente

**Decisão**: Intervir apenas quando o estudante toca em um aplicativo fora do contexto dentro do
launcher. Aberturas por notificação, link ou outro aplicativo não serão interceptadas nem inferidas.

**Justificativa**: Um launcher controla o fluxo que ele inicia, mas não possui um gancho geral e
preventivo para toda abertura. `UsageStatsManager` fornece eventos históricos mediante acesso
especial, não uma interceptação prévia; seu uso ampliaria vigilância e permissões.

**Alternativas consideradas**: Serviço de acessibilidade foi rejeitado por finalidade incompatível e
alto risco de vigilância; estatísticas de uso foram rejeitadas por serem posteriores à abertura;
bloqueio por gestão do dispositivo foi rejeitado pelo produto.

**Fonte**: [UsageStatsManager — Android Developers][usage-stats]

[usage-stats]: https://developer.android.com/reference/android/app/usage/UsageStatsManager

## 4. Persistência local-first

**Decisão**: Usar Room como fonte de verdade para sessões, eventos, grupos e projeções demonstrativas;
usar DataStore somente para preferências simples. Armazenar ambos na área interna privada.

**Justificativa**: Room oferece verificação de consultas, migrações e suporte adequado a dados
estruturados offline. O armazenamento interno não é acessível diretamente por outros aplicativos.

**Alternativas consideradas**: Arquivos JSON foram rejeitados por fragilidade em consultas e
exclusões relacionais; somente DataStore foi rejeitado pelo volume e relações; banco remoto como
fonte principal foi rejeitado pelo requisito offline.

**Fontes**: [Room][room] e [armazenamento de dados][data-storage]

[room]: https://developer.android.com/training/data-storage/room
[data-storage]: https://developer.android.com/training/data-storage

## 5. Arquitetura e estado

**Decisão**: Adotar fluxo unidirecional, ViewModel por destino, `StateFlow` imutável e repositórios
como fronteira de dados. Criar casos de uso apenas para regras compartilhadas ou complexas, como
transições de sessão, exclusão, agregação e evolução do mascote.

**Justificativa**: Atende à constituição e mantém as telas livres de regras sem criar uma camada de
domínio para operações triviais.

**Alternativas consideradas**: Regras em Composables foram rejeitadas por baixa testabilidade;
Clean Architecture com módulos por camada foi rejeitada como abstração prematura no hackathon.

**Fontes**: [Recomendações de arquitetura](https://developer.android.com/topic/architecture/recommendations)
e [camada de domínio](https://developer.android.com/topic/architecture/domain-layer)

## 6. Temporização e recuperação

**Decisão**: Persistir instantes civis para histórico e uma referência monotônica para medir duração
enquanto o processo está vivo; ao restaurar, derivar o estado do último evento persistido e limitar
resultados inválidos após mudança manual de relógio.

**Justificativa**: Evita duração negativa, reduz deriva e permite restaurar a sessão após recriação de
Activity ou processo sem gravar a cada segundo.

**Alternativas consideradas**: Contador em memória foi rejeitado por perder estado; gravação por
segundo foi rejeitada por custo e complexidade; alarme contínuo foi rejeitado por não ser necessário.

## 7. Sincronização colaborativa futura

**Decisão**: Não implementar backend no MVP. Isolar modelos de contribuição coletiva e estado de
sincronização; quando houver serviço remoto, enviar trabalho persistente condicionado à rede por
WorkManager, com operações identificadas e idempotentes.

**Justificativa**: Mantém o MVP demonstrável e não cria dependência de internet. WorkManager é a
opção oficial para trabalho persistente após as condições de rede serem satisfeitas.

**Alternativas consideradas**: Sincronização em tempo real no MVP foi rejeitada pela prioridade;
tentativas de rede ligadas à tela foram rejeitadas por perda após encerramento do processo.

**Fonte**: [WorkManager — Android Developers](https://developer.android.com/reference/androidx/work/WorkManager)

## 8. Testes

**Decisão**: Cobrir regras e ViewModels com testes locais; persistência e integração Android com
testes instrumentados; jornadas, semântica e acessibilidade principais com Compose UI Test.

**Justificativa**: Mantém a maior parte da suíte rápida, reservando dispositivo/emulador para APIs
Android e UI. O contrato de launcher exige ainda validação manual em aparelho ou emulador configurado
com o app como Home.

**Alternativas consideradas**: Apenas testes manuais foram rejeitados para estados e privacidade
críticos; testes instrumentados para toda regra foram rejeitados por lentidão.

**Fonte**: [Testes no Compose — Android Developers](https://developer.android.com/develop/ui/compose/testing)

## 9. Injeção de dependências

**Decisão**: Usar Hilt no aplicativo Android, com componentes no escopo da aplicação e ViewModels;
evitar escopos ou qualifiers sem necessidade concreta.

**Justificativa**: É uma decisão técnica explícita do projeto e integra ViewModel, WorkManager e
substituição de bindings em testes, mantendo composição centralizada.

**Alternativas consideradas**: Composição manual seria menor, mas conflita com a decisão atual;
singletons globais foram rejeitados por dificultar isolamento de testes.

## 10. Demonstrações social e institucional

**Decisão**: Começar grupo, mascote, meta e painel com dados locais rotulados como demonstrativos.
Após o núcleo, conectar o mesmo fluxo ao backend mínimo e substituir apenas os dados compartilháveis.
O painel mostra totais do grupo ou turma, sem filtros ou drill-down.

**Justificativa**: Permite demonstrar cedo sem fingir coleta real e acrescentar sincronização depois,
sem alterar o fluxo local ou criar risco de reidentificação.

**Alternativas consideradas**: Integração remota fictícia foi rejeitada por induzir entendimento
incorreto; dados reais foram rejeitados por risco de privacidade e ausência de consentimento definido.

## 11. UsageStats

**Decisão**: Não solicitar `PACKAGE_USAGE_STATS` nem usar `UsageStatsManager` no MVP. Indicadores
pessoais derivam das sessões e decisões registradas pelo próprio launcher. Preparar apenas uma
capacidade opcional futura, separada do funcionamento principal.

**Justificativa**: O acesso não oferece interceptação preventiva e amplia o conjunto de dados
sensíveis. Nenhum requisito obrigatório depende dele, portanto a minimização prevalece.

**Alternativas consideradas**: Solicitar no onboarding foi rejeitado por falta de necessidade;
consultar eventos em segundo plano foi rejeitado por aproximar o produto de vigilância.

**Fonte**: [UsageStatsManager — Android Developers][usage-stats]

## 12. Backend e persistência remota

**Decisão**: Usar ASP.NET Core Minimal APIs e PostgreSQL somente para autenticação, vínculo coletivo,
grupos, metas, mascote, contribuições idempotentes e agregados. Separar dados remotos por allowlist;
não criar entidades remotas para aplicativos ou reflexões privadas.

**Justificativa**: Minimal APIs reduzem boilerplate no hackathon; PostgreSQL garante transações e
restrições para administração, metas e deduplicação da outbox.

**Alternativas consideradas**: Backend monolítico com controllers foi rejeitado pelo volume de
cerimônia; backend como fonte primária foi rejeitado pelo local-first; banco documental foi rejeitado
porque o domínio compartilhado é relacional e requer integridade.

## 13. Contrato REST e autenticação

**Decisão**: API REST versionada em `/v1`, HTTPS, JSON e `Idempotency-Key` nos comandos sincronizados.
Usar autenticação simples por e-mail/senha apenas quando o ciclo remoto iniciar; o launcher opera com
perfil local antes disso. No backend, aplicar `PasswordHasher<User>` do ASP.NET Core e persistir
somente o hash; nunca armazenar ou registrar senha em texto simples. Build de demonstração pode usar
identidade mock claramente sinalizada.

**Justificativa**: Evita que autenticação atrase o núcleo e permite trocar o mecanismo sem alterar
entidades privadas locais. Idempotência impede XP duplicado após retry.

**Alternativas consideradas**: Autenticação obrigatória no primeiro acesso foi rejeitada; enviar
credenciais a cada chamada foi rejeitado; API sem versão foi rejeitada pela evolução prevista.

### Cliente HTTP Android

**Decisão**: Usar Retrofit sobre OkHttp no ciclo remoto, com URL fornecida por configuração de build,
HTTPS obrigatório e DTOs definidos por allowlist conforme o contrato OpenAPI.

**Justificativa**: A combinação mantém o cliente REST pequeno, tipado e testável durante o hackathon,
além de centralizar transporte, timeouts e interceptação sem espalhar chamadas HTTP pelas features.
Embora sejam dependências externas, foram explicitamente escolhidas no plano técnico e substituem
boilerplate de serialização e conexão que elevaria o risco do ciclo remoto.

**Alternativas consideradas**: `HttpURLConnection` foi rejeitado pelo boilerplate e maior risco de
divergência do contrato; Ktor Client não foi adotado porque não oferece vantagem necessária para o
escopo Android-only do MVP.

## 14. Sincronização e conflitos

**Decisão**: Persistir uma outbox Room na mesma transação da ação compartilhável e executá-la por
WorkManager com exigência de rede e backoff exponencial. O servidor deduplica por ID. Recursos
administrativos usam versão otimista; conflitos retornam o estado atual para decisão explícita.

**Justificativa**: A gravação local nunca depende da rede e retries não duplicam contribuição. Soma de
XP é comutativa quando identificada; transferência de dono não é e não deve ser mesclada cegamente.

**Alternativas consideradas**: Last-write-wins foi rejeitado para administração; fila apenas em
memória foi rejeitada por perda após encerramento; sincronização síncrona foi rejeitada pelo offline.

## 15. Dashboard institucional

**Decisão**: Usar Angular em uma única feature de agregados para o MVP. Mostrar totais e uma tendência
simples de turma/grupo, sem filtros, exportação ou navegação individual.

**Justificativa**: Atende à decisão técnica mantendo a fronteira de privacidade e um escopo que não
compete com o launcher.

**Alternativas consideradas**: Painel administrativo completo foi rejeitado; dashboard embutido no
Android foi rejeitado por misturar públicos e responsabilidades.

## 16. Níveis de SDK e publicação

**Decisão**: Definir `minSdk 29`, `compileSdk 36` e `targetSdk 36` no primeiro scaffold.

**Justificativa**: `minSdk 29` é requisito do projeto e permite `RoleManager.ROLE_HOME`; desde 31 de
agosto de 2026, novos aplicativos e atualizações para celulares devem mirar Android 16/API 36 para
submissão ao Google Play. O requisito deve ser revalidado a cada publicação.

**Alternativas consideradas**: `targetSdk 35` foi rejeitado por não atender nova submissão na data do
plano; alvo dinâmico sem registro foi rejeitado por tornar o build irreproduzível.

**Fonte**: [Requisitos de nível da API — Google Play][target-api]

[target-api]: https://support.google.com/googleplay/android-developer/answer/11926878

## 17. Versões de backend e testes do dashboard

**Decisão**: Planejar ASP.NET Core 10 e usar o conjunto de testes padrão do scaffold Angular atual.

**Justificativa**: O plano parte da versão atual documentada das Minimal APIs. No Angular, o scaffold
deve determinar o runner suportado, sem adicionar uma segunda infraestrutura de testes.

**Alternativas consideradas**: Fixar uma versão Angular no documento foi rejeitado porque o projeto
ainda não foi criado; framework de testes adicional foi rejeitado pela simplicidade do MVP.

**Fontes**: [Minimal APIs no ASP.NET Core 10][minimal-api] e [testes Angular][angular-tests]

[minimal-api]: https://learn.microsoft.com/aspnet/core/fundamentals/minimal-apis?view=aspnetcore-10.0
[angular-tests]: https://angular.dev/guide/testing

## 18. Hilt

**Decisão**: Aplicar Hilt ao `Application`, Activity, ViewModels, repositórios e integração do Worker,
sem criar componentes personalizados no MVP.

**Justificativa**: Hilt é a solução recomendada oficialmente para injeção Android e reduz composição
manual. Restringir escopos evita complexidade desnecessária.

**Alternativas consideradas**: Componentes Dagger manuais e service locator foram rejeitados.

**Fonte**: [Injeção com Hilt — Android Developers][hilt]

[hilt]: https://developer.android.com/training/dependency-injection/hilt-android
