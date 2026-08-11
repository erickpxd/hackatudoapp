# Privacidade e limitações da plataforma

## Dados por fronteira

| Categoria | Local no aparelho | Sincronizável | Institucional |
|---|---|---|---|
| Intenção, apps relacionados, motivos e decisões | Sim | Nunca | Nunca |
| Histórico e indicadores pessoais | Sim | Nunca | Nunca |
| Identificador da contribuição, grupo, duração, conclusão e horário | Projeção em outbox | Sim, após login e via HTTPS | Apenas como total agregado |
| UsageStats opcional | Sim, após opt-in nas configurações Android | Nunca | Nunca |
| Grupo, meta, mascote e participantes | Sim | Sim no ciclo remoto | Totais coletivos |

O payload remoto é uma allowlist explícita. Não existem campos remotos para `packageName`, intenção,
UsageStats ou motivo de reflexão. Tokens ficam no armazenamento privado do aplicativo e não são
incluídos em logs.

## Limitações do Android

O aplicativo intervém somente quando a abertura começa no próprio launcher. Aberturas por
notificação, deep link, outro launcher ou outro aplicativo não podem ser mediadas. Essa limitação é
intencional: o produto não usa AccessibilityService, Device Owner, root, câmera, microfone ou
localização. O papel Home é solicitado pelo mecanismo oficial e pode ser recusado ou alterado.

O acesso a UsageStats é um recurso P3 separado. Sem autorização, ele devolve uma lista vazia e não
afeta launcher, sessões, intervenções ou grupos.

## Real e demonstrativo

- Reais no Android: Room, DataStore, catálogo de apps, papel Home, sessões, intervenções e outbox.
- Demonstrativos locais: sugestão pedagógica e agregado institucional representativo.
- Ciclo remoto: API ASP.NET/PostgreSQL, autenticação JWT, grupos, contribuições idempotentes e painel
  Angular. A URL vem de configuração externa e o valor padrão não aponta para um servidor real.

Integrações reais incluem o launcher Android/Home role, PackageManager, Room, DataStore, WorkManager
e o acesso opcional ao UsageStats após consentimento. A sugestão pedagógica, os dados seed e o
agregado institucional usado na demonstração são fixtures locais; o dashboard remoto depende de
API, PostgreSQL e dependências instaladas. Aberturas iniciadas por notificações, links ou outros
aplicativos não passam pelo fluxo de intervenção do launcher.
