# Especificação da Funcionalidade: Plataforma de Uso Consciente

**Diretório da funcionalidade**: `001-uso-consciente-smartphone`

**Criada em**: 2026-09-12

**Status**: Pronta para planejamento

**Entrada**: Plataforma educacional que adapta a experiência de launcher à intenção do estudante,
oferece sessões de foco e reflexão, indicadores privados, colaboração por grupos e indicadores
institucionais agregados sem vigilância.

## Clarifications

### Session 2026-09-12

- Q: Por quanto tempo o histórico pessoal de sessões deve permanecer no aparelho do estudante?
  → A: Até o estudante excluir, com exclusão total ou por sessão.

## Cenários de Usuário e Testes *(obrigatório)*

### História de Usuário 1 — Usar um launcher consciente (Prioridade: P1)

Como estudante, quero encontrar meus aplicativos em uma tela inicial simples e contextual para
usar o smartphone de acordo com o que pretendo fazer, sem perder o controle do aparelho.

**Por que esta prioridade**: É a base da experiência do produto e precisa existir antes de qualquer
adaptação por sessão.

**Teste independente**: Pode ser demonstrada ao abrir a experiência principal, visualizar os
aplicativos disponíveis, abrir um deles e retornar à tela inicial sem iniciar uma sessão.

**Cenários de aceitação**:

1. **Dado** que o estudante acessou a plataforma, **quando** abrir a tela principal, **então** verá
   os aplicativos disponíveis e uma forma clara de iniciar uma sessão.
2. **Dado** que não há sessão ativa, **quando** o estudante escolher um aplicativo, **então** poderá
   abri-lo sem ser submetido a uma intervenção de foco.
3. **Dado** que o dispositivo está sem internet, **quando** o estudante acessar o launcher,
   **então** continuará podendo visualizar e abrir os aplicativos disponíveis.

---

### História de Usuário 2 — Realizar uma sessão com intenção (Prioridade: P1)

Como estudante, quero declarar uma intenção, definir uma duração e escolher aplicativos relacionados
para que a interface me ajude a manter o contexto da atividade.

**Por que esta prioridade**: Intenção e sessão constituem o núcleo do valor de autonomia digital.

**Teste independente**: Pode ser testada criando uma sessão, observando a adaptação da tela,
pausando, alterando a intenção e finalizando com um resumo.

**Cenários de aceitação**:

1. **Dado** que não há sessão ativa, **quando** o estudante informar intenção, duração e aplicativos
   relacionados e confirmar, **então** a sessão ficará ativa e o launcher priorizará esses
   aplicativos.
2. **Dado** que há sessão ativa, **quando** o estudante consultar a tela principal, **então** verá
   intenção, estado e tempo restante de forma evidente.
3. **Dado** que há sessão ativa, **quando** o estudante pausar, retomar, concluir ou cancelar a
   sessão, **então** a ação será permitida e registrada sem linguagem de fracasso.
4. **Dado** que o dispositivo está offline, **quando** a sessão for criada ou conduzida, **então**
   intenção, temporização, adaptação do launcher e registro continuarão funcionando.

---

### História de Usuário 3 — Refletir diante de um desvio (Prioridade: P1)

Como estudante, quero receber uma intervenção breve ao tentar acessar um aplicativo fora do
contexto para escolher conscientemente entre manter o foco, abrir o aplicativo ou mudar a intenção.

**Por que esta prioridade**: A intervenção sem bloqueio diferencia o produto de ferramentas de
controle e concretiza seu princípio central.

**Teste independente**: Pode ser testada iniciando uma sessão, tentando abrir um aplicativo não
relacionado e exercendo separadamente cada decisão disponível.

**Cenários de aceitação**:

1. **Dado** que um aplicativo não está relacionado à sessão ativa, **quando** o estudante tentar
   abri-lo por um fluxo que a plataforma consegue identificar, **então** verá sua intenção atual,
   uma explicação neutra e as opções de continuar focado ou abrir mesmo assim.
2. **Dado** que a intervenção está visível, **quando** o estudante escolher continuar focado,
   **então** retornará ao contexto da sessão e a decisão será registrada localmente.
3. **Dado** que a intervenção está visível, **quando** o estudante escolher abrir mesmo assim,
   **então** o acesso será permitido e uma reflexão breve poderá ser oferecida sem ser punitiva.
4. **Dado** que a intenção mudou, **quando** o estudante atualizar a intenção, **então** o novo
   contexto será aplicado e a alteração será registrada como decisão consciente, não como falha.

---

### História de Usuário 4 — Consultar progresso pessoal (Prioridade: P2)

Como estudante, quero consultar resumos e indicadores pessoais para compreender meus hábitos sem
julgamento e controlar meus próprios dados.

**Por que esta prioridade**: Fecha o ciclo de reflexão individual depois que o fluxo principal está
funcional.

**Teste independente**: Pode ser testada concluindo uma sessão com intervenções e verificando se o
resumo e os indicadores correspondem aos eventos registrados.

**Cenários de aceitação**:

1. **Dado** que uma sessão terminou, **quando** o resumo for exibido, **então** mostrará intenção,
   duração planejada e realizada, intervenções e decisões com linguagem neutra.
2. **Dado** que existem sessões registradas, **quando** o estudante abrir seus indicadores,
   **então** verá métricas pessoais e evolução temporal calculadas apenas a partir de seus dados.
3. **Dado** que há respostas a reflexões, **quando** qualquer visão escolar ou de grupo for aberta,
   **então** essas respostas não serão expostas.

---

### História de Usuário 5 — Colaborar em um grupo com mascote (Prioridade: P3)

Como estudante, quero participar de um grupo, contribuir para uma meta coletiva e acompanhar um
mascote evoluir para sentir progresso compartilhado sem competição individual.

**Por que esta prioridade**: Demonstra colaboração positiva após o núcleo individual estar pronto.

**Teste independente**: Pode ser testada criando ou entrando em um grupo, escolhendo um mascote,
registrando contribuições coletivas e verificando três estágios sem revelar dados individuais.

**Cenários de aceitação**:

1. **Dado** que o estudante pode participar de grupos, **quando** criar ou entrar em um grupo,
   **então** verá nome, objetivo, participantes, meta coletiva, progresso e mascote.
2. **Dado** que o grupo escolheu um mascote, **quando** atividades elegíveis elevarem o progresso
   aos marcos definidos, **então** o mascote avançará por pelo menos três estágios visuais.
3. **Dado** que um integrante deixa de usar a plataforma, **quando** o progresso for atualizado,
   **então** nenhum progresso conquistado será removido e ninguém será exposto por baixa participação.
4. **Dado** que uma sessão é associada ao grupo, **quando** um estudante participar, **então** ele
   continuará controlando integralmente seu próprio smartphone.

---

### História de Usuário 6 — Sugerir atividade pedagógica (Prioridade: P4)

Como professor, quero sugerir contexto, objetivo, duração e aplicativos para facilitar o início de
uma atividade pelos estudantes sem controlar seus aparelhos ou acessar histórico individual.

**Por que esta prioridade**: Conecta o uso consciente ao contexto pedagógico sem substituir a
autonomia do estudante.

**Teste independente**: Pode ser testada criando uma sugestão, recebendo-a como estudante e
confirmando que origem e natureza da configuração ficam claras.

**Cenários de aceitação**:

1. **Dado** que um professor criou uma sugestão, **quando** o estudante a visualizar, **então** verá
   origem, objetivo, duração e aplicativos sugeridos claramente diferenciados de escolhas próprias.
2. **Dado** que o professor acompanha a atividade, **quando** consultar seus resultados, **então**
   não verá mensagens, conteúdo, respostas privadas nem histórico detalhado individual.
3. **Dado** que a sugestão chega ao estudante em um aparelho pessoal, **quando** ele interagir com
   ela, **então** poderá aceitá-la, adaptá-la ou ignorá-la; a sugestão nunca retirará seu controle.

---

### História de Usuário 7 — Acompanhar tendências agregadas (Prioridade: P5)

Como representante autorizado da escola, quero consultar indicadores agregados de turma ou grupo
para avaliar tendências gerais sem vigiar estudantes individualmente.

**Por que esta prioridade**: Atende à demonstração institucional somente depois de proteger a
experiência e os dados do estudante.

**Teste independente**: Pode ser testada com dados representativos de uma turma, confirmando que a
visão contém tendências úteis e não permite reconstruir históricos individuais.

**Cenários de aceitação**:

1. **Dado** que existem dados elegíveis de uma turma, **quando** a escola abrir o painel, **então**
   verá somente indicadores agregados autorizados.
2. **Dado** que a escola consulta indicadores no MVP, **quando** visualizar uma turma ou grupo,
   **então** verá apenas seus totais agregados, sem filtros que permitam isolar indivíduos ou
   subconjuntos.

### Casos de Borda

- Se um aplicativo for aberto por notificação, link ou outro aplicativo, a intervenção ocorrerá
  somente quando o fluxo puder ser identificado; quando não puder, a sessão continuará sem acusar
  o estudante de descumprimento.
- Um aplicativo com finalidades diferentes será classificado por sessão, nunca permanentemente.
- Se um aplicativo relacionado for removido ou ficar indisponível durante a sessão, os demais
  recursos continuam ativos e o estudante recebe informação neutra.
- Se o dispositivo reiniciar ou a plataforma for encerrada durante uma sessão, a sessão deverá ser
  restaurada de modo coerente a partir do último estado local conhecido.
- Se o horário do aparelho mudar durante uma sessão, a duração realizada não poderá ficar negativa
  nem gerar progresso artificial.
- Se o estudante cancelar ou encerrar antecipadamente uma sessão, isso será permitido, registrado
  como estado da sessão e não tratado como punição.
- Se não houver aplicativos relacionados, o estudante deverá confirmar conscientemente a sessão e
  ainda manter acesso aos aplicativos.
- Se uma sugestão, atualização de grupo ou sincronização chegar durante uma sessão offline, ela
  poderá ser incorporada após reconexão sem alterar retroativamente decisões pessoais.
- Se dois participantes atualizarem uma meta coletiva enquanto estão offline, nenhuma contribuição
  válida deverá ser perdida após a reconciliação.
- Se o administrador tentar sair do grupo, deverá transferir a administração; se isso não ocorrer,
  o sistema designará outro integrante elegível antes de concluir sua saída.
- Participantes, professores e escola nunca poderão usar filtros, exportações ou combinações de
  indicadores para acessar mensagens, respostas privadas ou histórico individual de aplicativos.
- Se o estudante excluir uma sessão, ela deixará de compor imediatamente seu histórico e seus
  indicadores pessoais; a exclusão total removerá todas as sessões e dados pessoais derivados.

## Requisitos *(obrigatório)*

### Requisitos Funcionais

- **RF-001**: O sistema DEVE oferecer uma experiência de tela inicial que apresente os aplicativos
  disponíveis e acesso claro à criação de sessão, grupos e indicadores pessoais.
- **RF-002**: O sistema DEVE permitir contextos como aula, estudo, trabalho em grupo, leitura,
  intervalo e pessoal, sem tornar essa lista permanente ou exclusiva. No MVP, contexto é um modelo
  reutilizável e editável de nome, intenção sugerida e aplicativos relacionados; aplicá-lo preenche
  a criação da sessão sem retirar o controle do estudante.
- **RF-003**: O estudante DEVE poder declarar e visualizar claramente sua intenção atual.
- **RF-004**: O estudante DEVE poder criar uma sessão com intenção ou título, duração e aplicativos
  relacionados.
- **RF-005**: A sessão DEVE registrar horário de início, estado e horário de conclusão quando
  aplicável, admitindo os estados planejada, ativa, pausada, concluída e cancelada.
- **RF-006**: Durante uma sessão, o launcher DEVE destacar aplicativos relacionados e PODE reduzir
  o destaque ou exigir uma ação adicional para aplicativos fora do contexto, sem bloqueio permanente.
- **RF-007**: A relação entre aplicativo e contexto DEVE pertencer à sessão e poder variar entre
  sessões.
- **RF-008**: Quando conseguir identificar uma tentativa de abrir aplicativo fora do contexto, o
  sistema DEVE apresentar intervenção consciente antes da abertura.
- **RF-009**: A intervenção DEVE informar a intenção atual e oferecer, no mínimo, continuar focado
  e abrir mesmo assim, sem linguagem punitiva ou ameaça de exposição.
- **RF-010**: O estudante DEVE poder abrir o aplicativo fora do contexto, cancelar ou encerrar a
  sessão e alterar sua intenção conscientemente.
- **RF-011**: Após a escolha de abrir fora do contexto, o sistema PODE apresentar uma pergunta breve
  de motivo; a resposta DEVE ser opcional e privada.
- **RF-012**: O sistema DEVE registrar localmente intervenções, decisões, pausas e mudanças de
  intenção sem classificá-las automaticamente como falha.
- **RF-013**: O estudante DEVE poder pausar e retomar uma sessão, com registro do início e duração da
  pausa.
- **RF-014**: Ao finalizar uma sessão, o sistema DEVE mostrar resumo neutro com duração planejada e
  realizada, intervenções, decisões de continuar focado e mudanças conscientes de intenção.
- **RF-015**: O estudante DEVE poder consultar histórico e indicadores pessoais de sessões, duração,
  conclusões, intervenções, decisões, mudanças de intenção, consistência e evolução temporal.
- **RF-016**: O primeiro acesso DEVE explicar propósito, launcher, intenção, sessões, intervenções,
  privacidade, grupos, mascotes e compartilhamento institucional.
- **RF-017**: Toda permissão solicitada DEVE ser precedida por explicação compreensível de sua
  finalidade e consequência.
- **RF-018**: O estudante DEVE poder controlar configurações de privacidade e visualizar quais dados
  são mantidos localmente ou compartilhados.
- **RF-019**: O estudante DEVE poder criar, entrar por convite ou código, sair e visualizar um grupo
  de estudo.
- **RF-020**: Um grupo DEVE possuir nome, descrição, participantes, objetivo, mascote, progresso,
  metas e sessões coletivas associadas.
- **RF-021**: O grupo DEVE escolher um mascote entre opções da plataforma e visualizar pelo menos
  três estágios de evolução no MVP.
- **RF-022**: O progresso do mascote DEVE considerar atividades coletivas elegíveis e NÃO DEVE ser
  removido como punição por ausência ou baixa participação.
- **RF-023**: Grupos DEVEM poder definir e acompanhar uma meta coletiva simples no MVP.
- **RF-024**: Participantes DEVEM visualizar progresso coletivo, metas, quantidade de sessões,
  atividades coletivas e mascote, mas NÃO DEVEM visualizar histórico de aplicativos, respostas às
  intervenções, mensagens ou comportamento detalhado de outros participantes.
- **RF-025**: Cada participante DEVE manter controle do próprio aparelho durante sessões coletivas.
- **RF-026**: Professor ou escola DEVE poder sugerir contexto pedagógico com origem, objetivo,
  duração e aplicativos sugeridos explicitamente identificados.
- **RF-026A**: Sugestões de professor ou escola em aparelhos pessoais DEVEM ser opcionais; o
  estudante DEVE poder aceitá-las, adaptá-las ou ignorá-las.
- **RF-027**: A escola DEVE poder organizar turmas e consultar indicadores agregados autorizados,
  sem acesso automático a dados individuais detalhados.
- **RF-028**: A visualização institucional PODE incluir número de sessões, duração média, percentual
  de conclusão, intervenções e decisões agregadas, evolução semanal e participação pedagógica.
- **RF-028A**: No MVP, a visualização institucional DEVE apresentar somente totais da turma ou grupo
  e NÃO DEVE oferecer filtros que isolem indivíduos ou subconjuntos.
- **RF-029**: O sistema NÃO DEVE ler mensagens, conteúdo ou histórico de navegação; capturar telas;
  nem usar câmera, microfone ou localização para vigilância.
- **RF-030**: Dados detalhados de aplicativos, decisões e reflexões DEVEM permanecer locais sempre
  que possível e NÃO DEVEM ser compartilhados automaticamente com escola, professor ou grupo.
- **RF-031**: Launcher, intenção, criação e condução de sessão, organização dos aplicativos,
  intervenções, registro, resumo e indicadores pessoais básicos DEVEM funcionar sem internet.
- **RF-032**: Funcionalidades dependentes de colaboração DEVEM informar o estado de sincronização e
  preservar dados locais enquanto não houver conexão.
- **RF-033**: A gamificação NÃO DEVE incluir ranking público, exposição de baixa participação, perda
  punitiva de progresso, comparação por tempo de tela ou incentivo ao uso compulsivo.
- **RF-034**: A plataforma NÃO DEVE conceder a professor, escola ou grupo controle remoto do aparelho.
- **RF-035**: O MVP DEVE permitir uma demonstração institucional com dados agregados representativos,
  sem exigir dados privados reais de estudantes.
- **RF-036**: O criador do grupo DEVE ser seu administrador inicial e DEVE poder gerenciar membros,
  metas, configurações, mascote e transferência da administração.
- **RF-037**: Para sair do grupo, o administrador DEVE transferir a administração; se não o fizer, o
  sistema DEVE designar outro integrante elegível antes de concluir sua saída.
- **RF-038**: O histórico pessoal DEVE permanecer no aparelho até o estudante excluí-lo; o estudante
  DEVE poder excluir uma sessão específica ou todo o histórico e seus dados pessoais derivados.

### Requisitos Não Funcionais

- **RNF-001 — Autonomia**: Toda intervenção ou sugestão DEVE preservar uma saída clara pela qual o
  estudante retoma o controle de sua decisão.
- **RNF-002 — Privacidade**: Toda coleta ou compartilhamento DEVE ter finalidade explícita, usar o
  mínimo de dados e ser compreensível para o estudante.
- **RNF-003 — Linguagem**: Textos de sessão, resumo e gamificação DEVEM ser neutros, inclusivos e
  livres de culpa, ameaça, humilhação ou julgamento moral.
- **RNF-004 — Continuidade offline**: Os fluxos individuais essenciais definidos no RF-031 DEVEM
  poder ser concluídos integralmente sem conexão.
- **RNF-005 — Clareza**: Origem própria, do professor ou institucional de cada contexto ou sugestão
  DEVE permanecer visível antes de o estudante aceitá-la.
- **RNF-006 — Acessibilidade**: Fluxos essenciais DEVEM ser utilizáveis com tecnologias assistivas,
  ampliação de texto e navegação sem depender exclusivamente de cor, animação ou som.
- **RNF-007 — Recuperação**: Interrupções inesperadas NÃO DEVEM apagar uma sessão em andamento nem
  produzir registros contraditórios para o estudante.
- **RNF-008 — Transparência**: O estudante DEVE conseguir identificar quais categorias de dados
  deixam o aparelho e para qual finalidade antes do compartilhamento.

### Critérios de Aceitação Globais

- **CA-001**: O cenário principal completo deve ser demonstrável: abrir launcher, criar intenção e
  sessão, adaptar aplicativos, intervir em desvio, registrar decisão, concluir e exibir resumo.
- **CA-002**: Em todos os caminhos de intervenção testados, o estudante consegue abrir o aplicativo,
  mudar a intenção ou encerrar a sessão sem autorização externa.
- **CA-003**: Todos os fluxos individuais essenciais permanecem utilizáveis com a conexão desativada.
- **CA-004**: Nenhuma tela de professor, escola ou grupo revela lista individual de aplicativos,
  conteúdo, mensagens ou respostas privadas.
- **CA-005**: Um grupo consegue escolher um mascote, acompanhar uma meta e demonstrar pelo menos três
  estágios de evolução sem ranking nem perda de progresso.
- **CA-006**: A visualização institucional demonstra tendências agregadas sem usar dados privados
  reais nem oferecer detalhamento individual.

### Entidades Principais

- **Estudante**: Usuário principal, proprietário de suas intenções, sessões, decisões e indicadores.
- **Intenção**: Declaração consciente do objetivo atual; pode mudar durante uma sessão.
- **Contexto**: Organização temporária de aplicativos e informações, com origem própria, docente ou
  institucional.
- **Sessão**: Período planejado ou realizado com intenção, duração, aplicativos relacionados,
  horários, pausas e estado.
- **Aplicativo relacionado**: Associação contextual e não permanente entre um aplicativo e uma sessão.
- **Intervenção**: Momento de reflexão diante de possível desvio, incluindo decisão e eventual motivo
  privado.
- **Resumo e indicador pessoal**: Visões derivadas do histórico local para uso prioritário do
  estudante; são recalculadas após exclusões.
- **Grupo de estudo**: Espaço colaborativo com participantes, objetivo, metas, sessões e regras de
  administração.
- **Mascote**: Representação visual do progresso coletivo, com estágios e itens sem regressão punitiva.
- **Meta coletiva**: Objetivo mensurável do grupo alimentado por atividades elegíveis.
- **Sugestão pedagógica**: Contexto proposto por professor ou escola, sempre identificado por origem.
- **Indicador agregado**: Medida institucional de tendência coletiva que não deve revelar nem permitir
  inferir comportamento detalhado individual.

## Critérios de Sucesso *(obrigatório)*

### Resultados Mensuráveis

- **CS-001**: Pelo menos 90% dos participantes de teste conseguem iniciar uma sessão com intenção,
  duração e aplicativos relacionados em até 2 minutos, sem ajuda.
- **CS-002**: Em 100% dos cenários demonstráveis de aplicativo fora do contexto, o estudante encontra
  uma opção clara para manter o foco e outra para prosseguir conscientemente.
- **CS-003**: O fluxo individual completo, do launcher ao resumo, é concluído com sucesso em 100% dos
  testes previstos com o dispositivo sem internet.
- **CS-004**: Pelo menos 90% dos participantes identificam corretamente sua intenção atual, o estado
  da sessão e o tempo restante ao observar a tela principal.
- **CS-005**: Pelo menos 85% dos estudantes em teste classificam as intervenções como neutras,
  compreensíveis e não punitivas.
- **CS-006**: Resumos apresentam contagens e durações coerentes com os eventos realizados em 100% dos
  cenários de aceitação.
- **CS-007**: Um grupo de teste consegue criar ou acessar um grupo, escolher um mascote, avançá-lo por
  três estágios e acompanhar uma meta coletiva em até 5 minutos de demonstração.
- **CS-008**: Em 100% das inspeções de visões de grupo, professor e escola, não são exibidos mensagens,
  respostas privadas, conteúdo ou histórico individual detalhado de aplicativos.
- **CS-009**: Pelo menos 90% dos estudantes distinguem corretamente uma configuração própria de uma
  sugestão docente ou institucional antes de aceitá-la.
- **CS-010**: A escola consegue interpretar pelo menos três tendências coletivas na demonstração sem
  consultar identificação ou histórico detalhado individual.

## Requisitos do MVP

### Obrigatórios

1. Launcher funcional e visualização dos aplicativos disponíveis.
2. Definição e exibição da intenção atual.
3. Criação de sessão com duração e escolha de aplicativos relacionados.
4. Adaptação da experiência do launcher durante a sessão.
5. Identificação de tentativa fora do contexto quando possível no modelo escolhido.
6. Intervenção consciente com opções de continuar focado ou abrir o aplicativo.
7. Registro local da decisão, finalização e resumo básico da sessão.
8. Indicadores pessoais básicos.

### Importantes para a demonstração

1. Criação ou entrada representativa em grupo de estudo.
2. Escolha de mascote com pelo menos três estágios visuais.
3. Progresso do mascote associado a atividades coletivas.
4. Uma meta coletiva simples.
5. Indicadores agregados representativos para turma ou grupo.

## Funcionalidades Posteriores

- Sessões coletivas em tempo real e convites completos para grupos.
- Sincronização avançada entre dispositivos e regras adicionais de reconciliação.
- Painel escolar completo e contextos sugeridos por professores em produção.
- Personalização avançada do mascote, conquistas e gráficos avançados.
- Notificações e planejamento de sessões futuras.
- Administração, moderação e limites avançados de grupos após definição das regras.

### Ciclo remoto mínimo aprovado após o núcleo

- Depois do P0 e da demonstração colaborativa local, a entrega atual PODE incluir autenticação
  simples, entrada em grupo por convite/código, sincronização mínima por outbox e painel Angular com
  totais agregados.
- Esse ciclo é P2, NÃO DEVE bloquear o launcher e NÃO substitui Room como fonte de verdade do fluxo
  individual.
- Organização institucional limita-se a criar/listar turmas e seus vínculos mínimos; painel completo,
  filtros, administração avançada e sessões coletivas em tempo real permanecem posteriores.

## Fora do Escopo Inicial

- Gestão completa de dispositivos, controle parental ou bloqueio remoto pela escola.
- Leitura de mensagens, análise de conversas, histórico de navegação ou conteúdo de outros aplicativos.
- Captura de tela, gravação de áudio ou vídeo e localização contínua para monitoramento.
- Sistema disciplinar, punição automática ou ranking público de estudantes.
- Inteligência artificial obrigatória para o funcionamento básico.
- Personalização avançada, operação colaborativa em tempo real e painel institucional completo.

## Premissas

- O estudante pode encerrar, cancelar, pausar ou alterar uma sessão sem autorização externa; essas
  ações são registradas de forma neutra.
- “Ocultar” significa retirar da área principal sem impedir acesso; “desencorajar” significa exigir
  uma escolha reflexiva adicional; “bloquear” significa impedir acesso e está fora do escopo.
- A tentativa fora do contexto só é contabilizada quando a plataforma consegue identificá-la de
  forma confiável, inclusive em aberturas por notificação, link ou outro aplicativo.
- A finalidade de um aplicativo é definida pelo estudante para cada sessão.
- Dados pessoais detalhados permanecem locais; colaboração pode depender de sincronização e deve
  compartilhar somente dados coletivos necessários.
- O histórico pessoal não expira automaticamente e permanece local até exclusão total ou por sessão
  solicitada pelo estudante.
- No MVP, grupos, evolução e indicadores institucionais podem usar fluxos ou dados representativos,
  desde que a demonstração não afirme uma integração inexistente.
- A lista de opções de mascote e os limiares de três estágios serão definidos no planejamento sem
  alterar a regra de não punição.
- Regras adicionais de anonimização, tamanho mínimo de grupo e filtros institucionais ficam para uma
  etapa posterior; no MVP, a ausência de filtros e detalhamento individual é a proteção adotada.
- Requisitos legais específicos de consentimento, idade e relação institucional serão tratados antes
  de disponibilização pública; o MVP não presume consentimento para vigilância ou dados detalhados.

## Dependências

- O dispositivo precisa permitir ao estudante selecionar a plataforma como experiência de tela
  inicial para demonstrar o launcher completo.
- A identificação de tentativas fora do contexto depende das capacidades legitimamente disponíveis
  no dispositivo; limitações devem ser comunicadas ao estudante sem criar monitoramento invasivo.
- Funcionalidades colaborativas e institucionais dependem de sincronização quando usadas entre
  aparelhos, mas essa dependência não afeta o fluxo individual offline.
