# GEDU — uso consciente do smartphone

O GEDU é uma plataforma educacional para ajudar estudantes a usar o celular com intenção, sem
bloqueios punitivos e sem vigilância individual. O produto combina um launcher Android, sessões de
foco, reflexões privadas, grupos com metas coletivas e um painel escolar que trabalha somente com
dados agregados.

> Status: MVP demonstrável. O núcleo Android funciona offline; API e dashboard representam o ciclo
> remoto de colaboração e indicadores institucionais.

## O que o projeto oferece

- Launcher opcional que lista e abre os aplicativos instalados.
- Sessões com intenção, duração e aplicativos relacionados ao contexto.
- Intervenção neutra antes de abrir um app fora do contexto, mantendo a decisão com o estudante.
- Pausa, retomada, alteração de intenção, conclusão e resumo da sessão.
- Indicadores pessoais e exclusão do histórico pelo próprio estudante.
- Grupos, metas coletivas e mascotes evolutivos, sem ranking individual.
- Sugestões pedagógicas que podem ser aceitas, adaptadas ou ignoradas.
- Sincronização resiliente e painel institucional apenas com agregados.

## Arquitetura

| Parte | Tecnologias | Responsabilidade |
| --- | --- | --- |
| `app/` | Kotlin, Jetpack Compose, Room, Hilt, WorkManager e Retrofit | Launcher, sessões, intervenções, dados privados locais e sincronização |
| `backend/` | ASP.NET Core 8, Entity Framework Core e PostgreSQL | Autenticação, grupos, convites, progresso e agregados |
| `dashboard/` | Angular 20 e TypeScript | Visualização institucional de tendências agregadas |
| `specs/` | Spec Kit e OpenAPI | Requisitos, modelo de dados, contrato e plano do produto |
| `docs/` | Markdown | Roteiros de validação e limites de privacidade/plataforma |

O aplicativo segue separação entre interface (`feature`), regras de negócio (`domain`), acesso a
dados (`data`) e infraestrutura compartilhada (`core`). Dados sensíveis, como intenção, aplicativos
usados e respostas de reflexão, permanecem no aparelho. Apenas resumos explicitamente permitidos
entram no fluxo compartilhado.

## Gerar um APK em um comando

### Linux ou macOS

Com Java 17 e o Android SDK 36 instalados, execute na raiz:

```bash
./scripts/build-apk.sh
```

O APK instalável será criado em:

```text
dist/gedu-debug.apk
```

Esse arquivo pode ser enviado diretamente para quem vai testar. No Android, o usuário deve permitir
a instalação de apps da fonte usada para abrir o arquivo. O APK de debug é indicado para testes e
demonstrações, não para publicação na Play Store.

No Windows, use:

```powershell
.\gradlew.bat assembleDebug
```

e compartilhe `app\build\outputs\apk\debug\app-debug.apk`.

### Publicar para download pelo GitHub

O workflow `Gerar APK Android` também faz o build sem exigir Android Studio local:

1. Abra a aba **Actions** do repositório e selecione **Gerar APK Android**.
2. Clique em **Run workflow**.
3. Ao terminar, baixe o artefato `gedu-debug-apk` na execução.

Para criar um link público e permanente em **Releases**, publique uma tag de versão:

```bash
git tag v0.1.0
git push origin v0.1.0
```

O workflow cria a release e anexa `gedu-v0.1.0.apk` automaticamente. A versão atual do app deve ser
atualizada em `app/build.gradle.kts` antes de uma nova distribuição.

## Executar o app Android

Pré-requisitos:

- JDK 17;
- Android Studio ou Android SDK com Platform 36 e Build Tools 36.0.0;
- aparelho ou emulador Android 10 (API 29) ou superior.

Abra a raiz no Android Studio e execute a configuração `app`, ou use:

```bash
./gradlew installDebug
```

Na primeira abertura, o app apresenta o onboarding. Torná-lo o app de tela inicial é opcional e pode
ser revertido nas configurações do Android. O acesso às estatísticas de uso também é opcional e deve
ser concedido explicitamente pelo usuário.

Por padrão, o build aponta para uma URL HTTPS de exemplo. Os recursos locais e de demonstração
continuam disponíveis sem a API; a sincronização remota exige configurar uma URL real no campo
`API_BASE_URL` de `app/build.gradle.kts`.

## Subir API e banco localmente

Pré-requisitos: Docker e .NET SDK 8.

```bash
export HACKATUDO_DB_PASSWORD='uma-senha-local-segura'
docker compose -f backend/compose.yaml up -d

export HACKATUDO_ConnectionStrings__Hackatudo="Host=localhost;Port=5432;Database=hackatudo;Username=hackatudo;Password=$HACKATUDO_DB_PASSWORD"
export HACKATUDO_Jwt__SigningKey='troque-por-uma-chave-local-com-pelo-menos-32-caracteres'
dotnet run --project backend/src/Hackatudo.Api.csproj
```

A API expõe `GET /health` e os endpoints definidos em
[`specs/001-uso-consciente-smartphone/contracts/openapi.yaml`](specs/001-uso-consciente-smartphone/contracts/openapi.yaml).
O usuário demonstrativo inicial é `demo@hackatudo.local`, com senha `Demonstracao!2026`; não use
essas credenciais em produção.

## Executar o dashboard

Pré-requisitos: Node.js 20+ e npm.

```bash
cd dashboard
npm ci
npm start
```

O modo de demonstração usa autenticação simulada. Para integrar uma implantação real, configure
`globalThis.HACKATUDO_API_URL` antes da inicialização do Angular e use o build de produção.

## Testes e verificações

```bash
# Android — testes unitários e build
./gradlew test assembleDebug

# Android — requer emulador ou aparelho conectado
./gradlew connectedDebugAndroidTest

# API
dotnet test backend/tests/Hackatudo.Api.Tests/Hackatudo.Api.Tests.csproj

# Dashboard
cd dashboard
npm ci
npm test
npm run build
```

Os cenários completos de demonstração estão em
[`specs/001-uso-consciente-smartphone/quickstart.md`](specs/001-uso-consciente-smartphone/quickstart.md).

## Privacidade e limitações

- O app não usa câmera, microfone, localização, acessibilidade ou administração do dispositivo.
- A intervenção é confiável para aberturas iniciadas dentro do launcher; o Android pode não permitir
  identificar aberturas por notificações, links ou outros aplicativos.
- Escolher o GEDU como launcher e conceder acesso ao uso são decisões separadas e reversíveis.
- O painel institucional não oferece detalhamento, exportação ou filtros por estudante.

Leia a análise completa em
[`docs/privacy-and-platform-limitations.md`](docs/privacy-and-platform-limitations.md).

## Documentação adicional

- [Especificação funcional](specs/001-uso-consciente-smartphone/spec.md)
- [Modelo de dados](specs/001-uso-consciente-smartphone/data-model.md)
- [Contrato de privacidade](specs/001-uso-consciente-smartphone/contracts/collaboration-privacy-contract.md)
- [Validação do ciclo remoto](docs/remote-demo-validation.md)
