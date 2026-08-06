# Guia de Validação: Plataforma de Uso Consciente

## Objetivo

Validar o MVP de ponta a ponta contra [spec.md](spec.md), [data-model.md](data-model.md) e os contratos
em [contracts/](contracts/), sem depender de internet ou dados privados reais.

## Pré-requisitos

- Android Studio com SDK compatível com o projeto e Java 17.
- SDK do .NET e Node.js compatíveis com os projetos gerados, além de PostgreSQL para o ciclo remoto.
- Emulador ou aparelho Android com pelo menos um aplicativo iniciável além do sistema.
- Capacidade de escolher temporariamente o aplicativo como tela inicial.
- Modo avião disponível para o cenário offline.

## Comandos de verificação

Após o scaffold do aplicativo existir:

```bash
./gradlew test
./gradlew connectedDebugAndroidTest
./gradlew assembleDebug
```

Resultados esperados: testes locais e instrumentados aprovados e APK de depuração gerado. Se não
houver dispositivo conectado, registrar os testes instrumentados como pendentes e executar os
cenários manuais abaixo no aparelho de demonstração.

Após os projetos remotos existirem e o núcleo Android estar aprovado:

```bash
dotnet test backend/tests
dotnet run --project backend/src/Api
cd dashboard && npm test -- --watch=false
cd dashboard && npm run build
```

Validar também `contracts/openapi.yaml` com o validador escolhido no scaffold. A API deve iniciar com
configuração externa para conexão e segredos; nenhuma credencial real pertence ao repositório.

## Cenário 1 — Launcher básico

1. Instalar e abrir o aplicativo.
2. Ler a explicação do papel de tela inicial e aceitar a solicitação do sistema.
3. Confirmar que aplicativos iniciáveis aparecem com rótulo e ícone.
4. Abrir um aplicativo e pressionar Home.

**Esperado**: o aplicativo escolhido abre e o botão Home retorna ao launcher consciente. Recusar o
papel Home mantém o app utilizável sem prender o usuário.

## Cenário 2 — Sessão e intervenção

1. Criar sessão “Estudar matemática” por 30 minutos.
2. Relacionar Calculadora e navegador, deixando outro aplicativo fora do contexto.
3. Confirmar a sessão e observar intenção, estado, tempo restante e apps priorizados.
4. Tocar no aplicativo fora do contexto e escolher “Continuar focado”.
5. Repetir e escolher “Abrir mesmo assim”; ignorar a reflexão opcional.
6. Alterar a intenção, pausar, retomar e concluir.

**Esperado**: nenhuma escolha é bloqueada ou julgada; todas as decisões aparecem corretamente no
resumo e permanecem privadas.

## Cenário 3 — Continuidade offline e recuperação

1. Ativar modo avião antes de criar a sessão.
2. Criar e conduzir a sessão normalmente.
3. Durante a sessão, encerrar o processo ou reiniciar o aparelho.
4. Reabrir o launcher e concluir a sessão.

**Esperado**: catálogo, intenção, sessão, intervenção, registro, resumo e indicadores funcionam sem
rede; a sessão é restaurada sem duração negativa ou eventos duplicados.

## Cenário 4 — Exclusão controlada pelo estudante

1. Criar duas sessões concluídas e confirmar que ambas compõem os indicadores.
2. Excluir uma sessão e confirmar a ação.
3. Verificar histórico, resumo e indicadores.
4. Acionar exclusão de todo o histórico e confirmar separadamente.

**Esperado**: a primeira exclusão remove apenas a sessão e derivados correspondentes; a segunda
remove todas as sessões e recalcula indicadores vazios. Onboarding e grupo não são apagados sem
solicitação distinta.

## Cenário 5 — Grupo, meta e mascote

1. Criar o grupo “Missão Matemática” e escolher um mascote.
2. Confirmar que o criador é administrador.
3. Criar uma meta coletiva e aplicar contribuições demonstrativas.
4. Observar o mascote avançar por três estágios.
5. Simular ausência de uso e saída de membro.
6. Transferir a administração e sair como administrador anterior.

**Esperado**: meta e mascote progridem sem ranking, exposição ou regressão; a transferência mantém
exatamente um administrador elegível.

## Cenário 6 — Sugestão pedagógica

1. Abrir uma sugestão representativa de professor com objetivo, duração e apps.
2. Verificar a origem; repetir o fluxo escolhendo aceitar, adaptar e ignorar.

**Esperado**: as três escolhas estão disponíveis em aparelho pessoal e nenhuma concede controle ao
professor ou escola.

## Cenário 7 — Painel institucional

1. Carregar os agregados representativos da turma.
2. Abrir o painel e interpretar pelo menos três tendências.
3. Procurar filtros, detalhes individuais e exportação.

**Esperado**: o painel mostra somente totais de turma ou grupo e não oferece caminhos para isolar
estudantes ou consultar dados privados.

## Cenário 8 — Sincronização mínima

1. Concluir offline uma sessão relacionada a grupo e confirmar a outbox pendente.
2. Restaurar a rede e executar o trabalho de sincronização.
3. Repetir deliberadamente a mesma requisição com a mesma chave idempotente.
4. Simular conflito de versão ao editar uma meta ou transferir administração.

**Esperado**: a ação local nunca aguarda a rede; a contribuição é contabilizada uma única vez; erro
transitório volta para retry; conflito administrativo preserva o estado do servidor e pede nova ação.

## Inspeção de privacidade e acessibilidade

- Confirmar no manifesto a ausência de câmera, microfone, localização, acessibilidade,
  `PACKAGE_USAGE_STATS` e controle de dispositivo.
- Confirmar que permissões e papel Home são explicados antes da solicitação.
- Navegar pelos fluxos essenciais com leitor de tela e fonte ampliada.
- Confirmar que estado e ações não dependem apenas de cor, animação ou som.
- Confirmar que nenhum log de teste contém intenção, pacote ou resposta privada em texto aberto.
- Inspecionar payloads e banco PostgreSQL para confirmar ausência de aplicativos, UsageStats,
  intenções e motivos de intervenção.
- Confirmar que o dashboard não oferece filtros, exportação ou navegação individual.

## Protocolo de usabilidade e desempenho

- Medir abertura fria do launcher no aparelho de demonstração e registrar se ocorre em até 2 segundos.
- Medir criação/pausa/retomada local e registrar se a resposta observável ocorre em até 100 ms.
- Com participantes representativos, registrar tempo para criar sessão, compreensão do estado atual,
  percepção de neutralidade da intervenção e distinção da origem de sugestões.
- Calcular e registrar os percentuais de CS-001, CS-004, CS-005 e CS-009, incluindo tamanho da amostra
  e qualquer assistência necessária.

## Critério de saída

O MVP está pronto para demonstração quando todos os testes executáveis passam, os oito cenários são
concluídos e a inspeção não encontra violação de autonomia, privacidade ou funcionamento offline.
Limitações de detecção fora do launcher devem ser explicadas durante a demonstração.
