# Contrato de Interface: Launcher e Sessão

## Objetivo

Definir comportamentos observáveis entre UI, domínio e plataforma Android para o fluxo individual.

## Catálogo de aplicativos

### `ObserveLaunchableApps`

**Entrada**: nenhuma.

**Saída**: fluxo de `LaunchableApp(packageName, componentName, label, iconReference)` ordenado por
rótulo e contendo apenas aplicativos legitimamente iniciáveis e visíveis.

**Falhas**: componente removido ou indisponível é omitido na próxima emissão; o launcher continua
utilizável.

## Papel Home

### `ObserveHomeRole`

Estados: `NOT_ELIGIBLE`, `AVAILABLE_NOT_SELECTED`, `SELECTED`.

A UI explica a finalidade antes de disparar qualquer solicitação do sistema. Recusa ou troca do app
Home não impede que o aplicativo seja aberto normalmente para demonstração.

## Comandos de sessão

```text
CreateSession(title, intention, plannedDuration, relatedApps)
StartSession(sessionId)
PauseSession(sessionId)
ResumeSession(sessionId)
CompleteSession(sessionId)
CancelSession(sessionId)
ChangeIntent(sessionId, newIntent)
DeleteSession(sessionId)
DeleteAllPersonalHistory()
```

Regras:

- Título/intenção não podem ser vazios e duração deve ser positiva.
- Somente uma sessão pode estar `ACTIVE` ou `PAUSED`.
- Transições devem seguir `data-model.md`.
- Exclusões removem dependentes na mesma transação e atualizam indicadores.
- Todo comando retorna sucesso ou erro de domínio compreensível, nunca uma falha silenciosa.

## Abertura de aplicativo

### `RequestAppLaunch(app)`

```text
sem sessão ativa                  -> LaunchApproved
app relacionado                  -> LaunchApproved
app fora do contexto              -> InterventionRequired
app não mais disponível           -> AppUnavailable
```

### `ResolveIntervention`

Entradas válidas:

- `STAY_FOCUSED`: registra decisão e retorna ao launcher.
- `OPEN_ANYWAY`: registra decisão e abre o app; pode oferecer reflexão opcional.
- `CHANGE_INTENT`: registra a nova intenção, reavalia o contexto e permite nova escolha.
- `END_SESSION`: finaliza ou cancela conforme escolha explícita e devolve controle ao estudante.

Não há timeout que escolha pelo estudante. A intervenção nunca comunica a decisão à escola ou grupo.

## Estado reativo de tela

Cada destino expõe um estado imutável com `loading`, conteúdo e erro recuperável. Eventos únicos de
navegação ou abertura de app não são persistidos como estado repetível. A tela Compose envia ações e
renderiza o estado; não executa regras de sessão.

## Relógio

O tempo restante exibido deriva do estado persistido e do relógio, não de gravações por segundo. A
recriação da UI deve produzir o mesmo estado lógico. Mudança de relógio não pode resultar em duração
negativa ou sessão artificialmente concluída sem registro coerente.
