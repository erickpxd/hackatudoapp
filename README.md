# GEDU — uso consciente do smartphone

O GEDU é uma plataforma educacional para ajudar estudantes a usar o celular com intenção, sem
bloqueios punitivos e sem vigilância individual. O produto combina um launcher Android, sessões de
foco, reflexões privadas, grupos com metas coletivas e um painel escolar que trabalha somente com
dados agregados.

> Status: MVP demonstrável. O núcleo Android funciona offline; API e dashboard representam o ciclo
> remoto de colaboração e indicadores institucionais.

## Links do projeto

| Conteúdo | Link |
| --- | --- |
| 🎨 Protótipo no Figma | [Adicionar link do Figma](https://www.figma.com/design/3QVFFuHEc2hdYNLh1gi6N7/hackatudo?node-id=0-1&t=v0cH1MJNuwmLtCnS-1) |
| 🎬 Vídeo de apresentação | [Adicionar link do vídeo](https://youtu.be/fLE96bj5zQo?is=pL2MYuxdvDU1rPFs) |
| 📊 Lean Canvas | [Adicionar link do Lean Canvas](https://canva.link/xtr3hod3166pc6w) |
| 📱 Baixar APK pelo GitHub | [Abrir a versão mais recente](https://github.com/erickpxd/hackatudoapp/releases/latest) |
| ☁️ APK no Google Drive (backup) | [Adicionar link do APK no Drive](https://drive.google.com/drive/folders/1M9H3OVPBfSVAmiPzEuu7URWysK4RF7JU) |
| 📊 Requisitos iniciais | [Requisitos](https://docs.google.com/document/d/1UsA7lfxwbf3GIW7dtRGDXXWjXc9_Ur30_TIYJZaIYvo/edit?usp=sharing) |

## Como desinstalar o GEDU

>  Caso escolha o GEDU como aplicativo de tela inicial (launcher), para desinstalá-lo abra o aplicativo Configurações do Android e siga:
>  Configurações → Aplicativos → GEDU → Desinstalar
>  Se necessário, antes da desinstalação, altere o aplicativo de tela inicial em:
>  Configurações → Aplicativos → Aplicativos padrão → App de início/Tela inicial
>  Selecione o launcher original do aparelho e depois desinstale o GEDU normalmente. Os nomes dessas opções podem variar conforme a marca e a versão do Android.


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

Primeiro, envie o README, o script e o workflow para a branch principal:

```bash
git add README.md scripts/build-apk.sh .github/workflows/android-apk.yml
git commit -m "docs: adiciona instruções e publicação do APK"
git push origin main
```

Depois disso, o workflow **Gerar APK Android** faz o build sem exigir Android Studio local.

Para testar manualmente:

1. Abra a aba **Actions** do repositório e selecione **Gerar APK Android**.
2. Clique em **Run workflow**.
3. Ao terminar, baixe o artefato `gedu-debug-apk` na execução.

O artefato da aba Actions é útil para a equipe, mas pode exigir login no GitHub. Para disponibilizar
um download simples ao público, crie uma **Release** publicando uma tag de versão:

```bash
git tag v0.1.0
git push origin v0.1.0
```

O workflow cria a Release e anexa `gedu-v0.1.0.apk` automaticamente. O download ficará disponível
em [Releases](https://github.com/erickpxd/hackatudoapp/releases), e o botão da seção **Links do
projeto** sempre apontará para a versão mais recente.

Para publicar uma atualização, altere `versionCode` e `versionName` em `app/build.gradle.kts`, faça
commit e use uma nova tag, por exemplo `v0.2.0`. Uma tag já publicada não deve ser reutilizada.

### Backup do APK no Google Drive

Também é possível guardar `dist/gedu-debug.apk` no Drive como alternativa:

1. Faça upload do arquivo para o Google Drive.
2. Abra **Compartilhar** e permita acesso a **Qualquer pessoa com o link**.
3. Copie o endereço e substitua o link de backup na tabela **Links do projeto**.

O APK de debug é adequado para avaliação e demonstração. Para Play Store ou distribuição em
produção, gere um build `release` assinado com uma chave protegida.

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
