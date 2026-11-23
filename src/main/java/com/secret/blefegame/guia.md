# Secret Hitler Web: guia do projeto

Versão multiplayer web do Secret Hitler com **Java 21+**, **Spring Boot 3+** e **WebSockets (STOMP)**.

**Objetivos de estudo:** WebSockets em tempo real, estado concorrente e separação conceitual de CQRS (commands que alteram a partida vs. projeções filtradas por jogador).
**Meta futura:** plugar RabbitMQ como STOMP Broker Relay sem reescrever regras.

> Este arquivo é um mapa de trabalho. As caixas `[ ]` e `[x]` da seção 9 mostram o progresso.

---

## 1. Princípios de arquitetura

1. **Domínio puro.** Nada em `domain/` importa `org.springframework`. Se importar, algo vazou.
2. **Dependências apontam para dentro.** `infra` → `application` → `domain`. Nunca o contrário.
3. **Domínio não publica nada.** Cada ação devolve uma lista de `EventoDominio`. Quem publica é a infra.
4. **Segredo nunca sai do domínio por acidente.** Sem getters públicos para papel e cartas, sem `toString` com dados ocultos, sem serialização automática de entidades.
5. **Publicar fora do lock.** O handler trava a partida, executa, solta o lock e só depois publica os eventos.
6. **Sem JPA e sem `@Entity` no domínio.** O estado vive em memória. Se um dia persistir, crie classes de persistência separadas e converta nas bordas.
7. **Lombok só em DTOs e infra.** Nas classes de domínio ele esconde exatamente o que você quer controlar.

## 2. Fluxo de uma ação

```
Cliente → STOMP → AcaoController → CommandHandler → Partida → eventos
                                                                 ↓
Cliente ← STOMP ← EventPublisher ← Projector ←──────────────────┘
```

1. `AcaoController` descobre quem enviou (pelo `Principal`), monta o command e repassa. Sem regra de negócio.
2. `PartidaCommandHandler` busca a partida, trava, chama o método da `Partida`, coleta os eventos.
3. `Partida` valida fase e permissão, muda o estado e devolve eventos.
4. Fora do lock, `EventPublisher` entrega: eventos públicos no tópico da sala, e para cada jogador o `EstadoJogadorDTO` montado pelo projector.

## 3. Estrutura de pacotes

Pacote base: `com.secret.blefegame`

```
src/main/java/com/secret/blefegame/
├── BlefeGameApplication.java
├── domain/
│   ├── Partida.java
│   ├── Inscrito.java          (lobby: id e nome, sem papel)
│   ├── Jogador.java           (partida iniciada: id, nome, papel, vivo)
│   ├── Papel.java
│   ├── Faccao.java
│   ├── Baralho.java
│   ├── Politica.java
│   ├── Tabuleiro.java
│   ├── Governo.java
│   ├── Votacao.java
│   ├── Voto.java
│   ├── SessaoLegislativa.java
│   ├── FaseDoJogo.java
│   ├── PoderExecutivo.java
│   ├── evento/                (EventoDominio e records)
│   └── excecao/               (AcaoInvalidaException etc.)
├── application/
│   ├── command/               (records dos commands)
│   ├── PartidaCommandHandler.java
│   ├── SalaService.java
│   ├── PartidaRepository.java (interface)
│   └── EventPublisher.java    (interface)
├── projector/
│   ├── PlayerStateProjector.java
│   └── dto/                   (EstadoJogadorDTO, EstadoPublicoDTO)
└── infra/
    ├── websocket/
    │   ├── WebSocketConfig.java
    │   ├── AcaoController.java
    │   ├── AuthChannelInterceptor.java
    │   └── StompEventPublisher.java
    ├── rest/SalaRestController.java
    └── memory/InMemoryPartidaRepository.java
```

> Atenção: `domain.evento` é **outro pacote** para fins de visibilidade. O que for "só do domínio" fica direto em `domain`.

## 4. O que cada classe do domínio deve ter

### `Partida` (aggregate root)
- **Estado:** `codigoSala`, `fase`, inscritos (lobby), jogadores (após iniciar, ordem define a rotação da presidência), `Baralho`, `Tabuleiro`, `Governo` atual e anterior, `Votacao` e `SessaoLegislativa` em andamento (nulas fora das fases), contador de eleições fracassadas, resultado final.
- **Comportamento:** um método por ação (`entrar`, `iniciar`, `nomearChanceler`, `votar`, `presidenteDescarta`, `chancelerPromulga`, `executarPoder`...). Todos: validam fase, validam quem age, mudam estado, devolvem `List<EventoDominio>`.
- **Invariantes:** nenhuma ação fora da fase; só o presidente nomeia; só presidente e chanceler mexem nas cartas; morto não age nem vota; 5 a 10 jogadores para iniciar (**a validação do número mora aqui**).

### `Inscrito` (lobby)
- `id` e `nome`. Sem papel. Imutável. Valida id e nome não vazios.
- Decisão tomada: a `Partida` cria os `Jogador` completos ao iniciar.

### `Jogador`
- **Estado:** `id`, `nome`, `Papel` (final, atribuído na criação), `vivo`.
- **Comportamento:** `executar()` (falha se já morto), `estaVivo()`.
- **Invariantes:** papel nunca muda; o papel **não** é acessível fora do pacote `domain`; `toString` não mostra o papel; `equals` por `id`.

### `Papel` (record)
- `faccao` e `ehHitler`. Invariante: Hitler é fascista.
- `enxergaOutrosFascistas(int totalJogadores)`: liberal nunca; Hitler só com até 6 jogadores; fascista comum sempre.
- Não conhece `Jogador`. Quem monta a lista de ids visíveis é a `Partida`.

### `Faccao`
- Enum `LIBERAL`, `FASCISTA`. Fica em `domain`, não em `evento`.

### `Baralho`
- **Estado:** pilha de compra, descarte, fonte de aleatoriedade (injetada, para teste com semente fixa).
- **Comportamento:** `comprarTres()` (reembaralha o descarte se restam menos de 3), `comprarUma()`, `descartar()`, `espiarTres()` (não altera a ordem).
- **Invariantes:** compra + descarte + promulgadas = 17 cartas (6 liberais e 11 fascistas).

### `Tabuleiro`
- **Estado:** contagem das duas trilhas, tabela de poderes por casa conforme o nº de jogadores (5-6, 7-8, 9-10).
- **Comportamento:** `promulgar(politica)` devolve o poder liberado, se houver; `vitoriaPorPoliticas()`; `hitlerEleitoValeVitoria()` (3 ou mais fascistas); `vetoDisponivel()` (5 fascistas).

### `Governo` (value object imutável)
- `presidenteId`, `chancelerId` (pode ser nulo antes da nomeação). `comChanceler(id)` devolve cópia.

### `Votacao` e `Voto`
- Mapa `jogadorId → Voto`, conjunto de votantes esperados (vivos). `registrar`, `completa`, `resultado` (empate rejeita).
- Invariantes: um voto por jogador; **nenhum voto individual é legível antes de `completa()`**.

### `SessaoLegislativa`
- Cartas na mão (3, depois 2), dono atual, flag de veto proposto.
- `presidenteDescarta(indice)`, `chancelerEscolhe(indice)`. As cartas só são visíveis ao dono atual.

### `FaseDoJogo`
- Enum das fases abaixo, mais as que você decidir para veto e eleição fracassada.

### `EventoDominio`
- Interface selada com records. **Públicos:** `VotoRevelado`, `PoliticaPromulgada`, `FaseMudou`, `JogadorExecutado`. **Privados:** `PapelDistribuido`, `InvestigacaoRealizada`, `CartasEspiadas`.

## 5. Máquina de estados

| # | Fase | Resumo |
|---|---|---|
| 1 | `AGUARDANDO_JOGADORES` | Lobby, 5 a 10 inscritos |
| 2 | `DISTRIBUICAO_PAPEIS` | Sorteio pela tabela oficial; visões secretas |
| 3 | `ELEICAO_NOMEACAO` | Presidente escolhe chanceler elegível |
| 4 | `ELEICAO_VOTACAO` | Todos os vivos votam; revela só quando todos votaram |
| 5 | `SESSAO_LEGISLATIVA_PRESIDENTE` | Compra 3, descarta 1 |
| 6 | `SESSAO_LEGISLATIVA_CHANCELER` | Recebe 2, descarta 1, promulga 1 |
| 7 | `ACAO_EXECUTIVA` | Condicional: investigar, eleição especial, espiar, execução |
| 8 | `FIM_DE_JOGO` | Vitória liberal ou fascista |

**Regras oficiais que mexem na máquina e precisam de decisão sua:**
- 3 eleições fracassadas seguidas: a carta do topo do baralho é promulgada automaticamente.
- Veto: liberado após a 5ª política fascista, adiciona um passo na fase do chanceler.
- Limite de mandato: o governo anterior eleito não pode ser indicado de novo (regra especial com poucos jogadores vivos).

Tabela de distribuição (liberais / fascistas comuns / Hitler):

| Jogadores | Liberais | Fascistas | Hitler |
|---|---|---|---|
| 5 | 3 | 1 | 1 |
| 6 | 4 | 1 | 1 |
| 7 | 4 | 2 | 1 |
| 8 | 5 | 2 | 1 |
| 9 | 5 | 3 | 1 |
| 10 | 6 | 3 | 1 |

## 6. Endpoints

**REST**
- `POST /api/salas`: cria a sala, devolve código e token do jogador.
- `POST /api/salas/{codigo}/jogadores`: entra na sala, devolve token.
- `GET /api/salas/{codigo}`: verifica se existe.

**STOMP**
- Conexão: `/ws-secret-hitler`
- Cliente → servidor: `/app/sala/{codigo}/acao` (comando tipado no payload)
- Ressincronização: `/app/sala/{codigo}/estado` (reconexão e F5)
- Servidor → cliente:
    - `/topic/sala/{codigo}/publico`
    - `/user/queue/estado`
    - `/user/queue/erros`

**Autenticação:** um `ChannelInterceptor` no frame `CONNECT` lê o token e define o `Principal` com o **id do jogador**, não da sessão. Sem isso `/user/queue/...` não funciona e a reconexão quebra.

## 7. Dependências Spring

- `spring-boot-starter-websocket`
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-test`
- `spring-boot-starter-actuator` (opcional)
- Depois, para RabbitMQ: `reactor-netty` e o plugin STOMP no RabbitMQ.

## 8. Decisões já tomadas

- Papel secreto é polimórfico (record com comportamento); **sem Role Object completo**. Cargos de turno são o value object `Governo`.
- `Papel` não conhece `Jogador`.
- Lobby usa `Inscrito`; a `Partida` cria os `Jogador` ao iniciar (opção B).
- Aleatoriedade do `Baralho` e do sorteio de papéis é injetada.

## 9. Progresso

- [x] `Faccao`
- [x] `Papel` (record, `enxergaOutrosFascistas`)
- [x] `Jogador` (falta: visibilidade de pacote, `executar` restrito, nomes de métodos consistentes, `isBlank`)
- [x] `Inscrito`
- [x] `Baralho` + testes
- [x] `Votacao` + `Voto` + testes
- [x] `Tabuleiro`
- [x] `Governo`
- [x] `SessaoLegislativa`
- [x] `FaseDoJogo`, `PoderExecutivo`
- [ ] `EventoDominio` e records
- [ ] `Partida`: lobby e `iniciar` (distribuição de papéis)
- [ ] `Partida`: eleição, legislativo, poderes, fim de jogo
- [ ] Commands e `PartidaCommandHandler`
- [ ] `PlayerStateProjector` e DTOs
- [ ] Infra: config WebSocket, interceptor, controllers, publisher, repositório em memória
- [ ] RabbitMQ como broker relay

## 10. Ordem de trabalho sugerida

1. Fechar `Jogador` e criar `Inscrito`.
2. `Baralho` e `Votacao` (sem dependências, testáveis com JUnit puro, sem subir Spring).
3. `Tabuleiro`, `Governo`, `SessaoLegislativa`.
4. `Partida`, começando por lobby e distribuição de papéis.
5. Commands, handler, projector.
6. Só então a infra WebSocket.

## 11. Perguntas em aberto para você decidir

1. Como o handler trava a partida: `synchronized`, `ReentrantLock` por partida, ou fila de comandos por partida? O que acontece se a ação lançar exceção no meio?
2. Quem decide o destinatário de cada evento: o próprio evento ou o projector pelo tipo? Qual dos dois dificulta mais esquecer um filtro?
3. O projector recebe a `Partida` inteira ou uma "visão" que a própria `Partida` constrói?
4. Eleição fracassada e veto viram fases próprias ou cabem nas existentes?