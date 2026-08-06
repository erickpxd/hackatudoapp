<!--
Sync Impact Report
- Version change: scaffold sem versão -> 1.0.0
- Modified principles:
  - placeholders genéricos -> I. Autonomia e Consciência do Estudante
  - placeholders genéricos -> II. Privacidade desde a Concepção
  - placeholders genéricos -> III. Experiência Local-First e Resiliente
  - placeholders genéricos -> IV. Colaboração sem Competição ou Punição
  - placeholders genéricos -> V. Simplicidade, Clareza e MVP Funcional
- Added sections:
  - Restrições Técnicas e de Arquitetura
  - Fluxo de Desenvolvimento e Qualidade
  - Governança
- Removed sections: nenhuma; placeholders do scaffold foram concretizados
- Follow-up TODOs: nenhum
-->
# Constituição do Hackatudo App

## Princípios Fundamentais

### I. Autonomia e Consciência do Estudante

O sistema DEVE ajudar o estudante a perceber quando seu comportamento digital se afasta de uma
intenção definida conscientemente. Intervenções DEVEM estimular reflexão antes de qualquer
restrição e DEVEM permitir que o estudante mantenha o controle sobre continuar focado ou alterar
sua intenção. O produto NÃO DEVE ser concebido, descrito ou implementado como bloqueador de
aplicativos, ferramenta parental ou mecanismo de vigilância escolar. Padrões de design
manipulativos ou viciantes NÃO DEVEM ser utilizados.

**Razão:** mudanças sustentáveis de comportamento dependem de agência, compreensão e escolhas
conscientes, não de punição ou coerção.

### II. Privacidade desde a Concepção

A privacidade DEVE orientar requisitos, arquitetura, implementação e revisão desde o início. Dados
detalhados de uso do smartphone DEVEM permanecer no dispositivo sempre que possível. A escola NÃO
DEVE receber histórico individual detalhado dos aplicativos utilizados; somente indicadores
agregados ou dados estritamente necessários e explicitamente justificados PODEM ser enviados. O
sistema NÃO DEVE ler mensagens, capturar conteúdo de outros aplicativos nem usar câmera, microfone
ou localização para vigilância. Toda coleta, retenção e transmissão DEVE ser minimizada e vinculada
a um requisito claro.

**Razão:** o contexto educacional não reduz o direito do estudante à privacidade nem legitima
monitoramento invasivo.

### III. Experiência Local-First e Resiliente

O fluxo principal DEVE funcionar localmente sempre que possível. A ausência de internet NÃO DEVE
impedir o acesso à experiência de launcher, a definição ou alteração de intenções, sessões de foco
ou intervenções conscientes. O registro local da sessão e os recursos pessoais essenciais DEVEM
continuar disponíveis sem conectividade. Qualquer dependência de rede DEVE ser identificada na
especificação e possuir comportamento de contingência explícito.

**Razão:** foco e autonomia precisam estar disponíveis no momento de uso, independentemente da
qualidade da conexão.

### IV. Colaboração sem Competição ou Punição

Grupos de estudo DEVEM incentivar colaboração, constância e bem-estar, sem exposição ou competição
individual. Rankings públicos de estudantes NÃO DEVEM existir. O mascote do grupo DEVE representar
evolução coletiva por sessões, metas coletivas, consistência e atividades colaborativas; ele NÃO
DEVE perder progresso, sofrer punições por falta de uso nem revelar o desempenho individual de um
membro. Mecânicas sociais DEVEM ser avaliadas quanto a pressão indevida, comparação e manipulação.

**Razão:** o pertencimento ao grupo deve apoiar o aprendizado sem transformar métricas pessoais em
instrumentos de constrangimento.

### V. Simplicidade, Clareza e MVP Funcional

O desenvolvimento DEVE priorizar um MVP demonstrável e funcional antes de funcionalidades
secundárias. A ordem obrigatória de prioridade é: (1) funcionamento básico do launcher; (2) intenção
de uso; (3) sessões de foco; (4) organização de aplicativos por contexto; (5) intervenções
conscientes; (6) registro local da sessão; (7) resumo e indicadores pessoais; (8) grupos de estudo;
(9) evolução básica do mascote; e (10) indicadores agregados para a escola. Uma etapa posterior NÃO
DEVE comprometer a conclusão confiável das anteriores. Funcionalidades sem requisitos claros NÃO
DEVEM ser implementadas, e abstrações prematuras ou dependências externas desnecessárias DEVEM ser
evitadas.

**Razão:** uma equipe de hackathon entrega mais valor com um fluxo central compreensível, integrado
e verificável do que com muitas funcionalidades incompletas.

## Restrições Técnicas e de Arquitetura

- O aplicativo Android DEVE ser desenvolvido em Kotlin, com Jetpack Compose e Material Design 3.
- A arquitetura DEVE seguir MVVM. Presentation, Domain e Data DEVEM ser separados quando houver
  responsabilidade distinta que justifique a divisão, sem criar camadas artificiais.
- Telas Compose NÃO DEVEM conter regras de negócio. O estado de tela DEVE ser gerenciado por
  `ViewModel`, exposto reativamente com `StateFlow`.
- Operações assíncronas DEVEM utilizar Kotlin Coroutines.
- APIs oficiais do Android DEVEM ser priorizadas. Toda dependência externa DEVE ter necessidade e
  impacto registrados no planejamento.
- Classes, arquivos, métodos e variáveis DEVEM ter nomes em inglês e seguir convenções idiomáticas
  de Kotlin.
- Documentação, especificações, planos técnicos, tarefas e respostas do Codex ao desenvolvedor
  DEVEM ser escritos em português do Brasil.
- O código DEVE permanecer simples e compreensível para a equipe. Complexidade adicional DEVE ser
  justificada por um requisito atual, não por uma possibilidade futura.

## Fluxo de Desenvolvimento e Qualidade

- Antes da implementação de qualquer funcionalidade, sua especificação DEVE ser consultada.
- Requisitos NÃO DEVEM ser inventados, alterados ou reduzidos para facilitar a implementação.
- Ambiguidades que afetem comportamento, privacidade, autonomia, escopo ou critérios de aceitação
  DEVEM ser identificadas e resolvidas antes da implementação correspondente.
- Decisões técnicas relevantes, incluindo dependências, permissões, persistência, sincronização e
  desvios arquiteturais, DEVEM ser registradas no planejamento.
- Funcionalidades críticas DEVEM possuir critérios de aceitação verificáveis e testes quando
  viável. Quando um teste viável não for criado, a justificativa e a estratégia de validação manual
  DEVEM ser registradas.
- Toda revisão DEVE verificar conformidade com autonomia, privacidade, funcionamento offline,
  simplicidade e prioridade do MVP antes de aprovar a mudança.
- Permissões Android e fluxos de dados DEVEM ser revisados explicitamente para garantir que nenhum
  mecanismo de vigilância seja introduzido direta ou indiretamente.

## Governança

Esta constituição prevalece sobre convenções, planos e decisões conflitantes do projeto. Toda
especificação, planejamento, tarefa, implementação e revisão DEVE demonstrar conformidade com seus
princípios. Exceções temporárias exigem justificativa documentada, impacto sobre estudantes e
privacidade, responsável, prazo de remoção e aprovação explícita da equipe; os princípios centrais
de autonomia e não vigilância não admitem exceção.

Emendas DEVEM ser propostas por escrito, explicar motivação e impacto, atualizar o relatório de
sincronização e ser aprovadas pela equipe responsável pelo produto antes de vigorar. Mudanças que
removam ou redefinam de modo incompatível um princípio exigem versão MAJOR; novos princípios ou
expansões materiais exigem MINOR; esclarecimentos sem mudança normativa exigem PATCH. Toda emenda
DEVE registrar a data em formato ISO e avaliar se especificações ou planos existentes precisam de
adequação.

A conformidade DEVE ser revisada antes do início da implementação de uma funcionalidade e novamente
antes de sua aceitação. Violações conhecidas DEVEM bloquear a aceitação até serem corrigidas ou
formalmente tratadas pelo processo de emenda aplicável.

**Versão**: 1.0.0 | **Ratificada em**: 2026-09-12 | **Última alteração**: 2026-09-12
