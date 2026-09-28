# Checkpoint 5 — Bug Hunt PetFiap

## Identificação

**Grupo:** ___

| Integrante | RM | Turma |
|---|---|---|
| Fernando Caires Silva | 563415 | 2CCPO |
| Guilherme Martins Rezende | 563500 | 2CCPO |
| Raphael Mischiatti de Souza | 563567 | 2CCPO |

| Campo | |
|---|---|
| **Total de bugs corrigidos** | 12 / 12 |
| **Total de ajustes de Clean Code** | 6 / 6 |
| **Total de testes novos escritos** | 6 / 6 |
| **Suíte final (Run As → JUnit Test)** | 26 testes, 0 falhas (20 originais + 6 novos) |

> Observação sobre a numeração: o commit `refactor: clean01 - renomeando avaliacao_readme_template para readme` é apenas a renomeação do template exigida na seção "Entrega" do enunciado, não uma correção de Clean Code — por isso ele **não é contado**. Os 6 ajustes de Clean Code reais são `clean02` a `clean07`, na numeração dos commits.

---

## Parte 1 — Bugs encontrados

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | Ao montar um atendimento pelo Builder (`comPet`), o `petNome` do objeto final vinha sempre `null`, mesmo informando o nome do pet — `AtendimentoBuilderTest.deveMontarAtendimentoCompleto` falhava (`expected: <Rex> but was: <null>`). | `AtendimentoBuilder.java`, método `comPet` (linha ~24): `petNome = petNome;` — o parâmetro era reatribuído a ele mesmo (shadowing), o atributo da instância nunca era tocado. | Corrigido para `this.petNome = petNome;`. | `this` e escopo de variáveis / shadowing entre parâmetro e atributo — Aula 3/4. |
| bug02 | Ao agendar um atendimento do tipo TOSA, o objeto criado era instância de `Banho`, não de `Tosa` — herdava preço, pontos e duração de banho. `AtendimentoFactoryTest.deveCriarTosaQuandoTipoForTosa` falhava. | `AtendimentoFactory.java`, método `criar` (linha ~16): `case "TOSA" -> new Banho(p, n, po, tu, d);` — case trocado por engano. | Corrigido para `case "TOSA" -> new Tosa(p, n, po, tu, d);`. | Factory Method (Aula 14) — o "único lugar que conhece as subclasses concretas" precisa mapear corretamente tipo → subclasse. |
| bug03 | Toda `ConsultaVeterinaria` criada nascia com `petNome`, `petPorte`, `tutorNome`, `dataHora` e `protocolo` nulos, e `status` nunca virava `"AGENDADO"`. `AtendimentoFactoryTest.devePreencherOsDadosDoPetNaConsulta` falhava (`expected: <Mimi> but was: <null>`). | `ConsultaVeterinaria.java`, construtor (linha ~16): `super();` — chama o construtor vazio do pai, ignorando todos os parâmetros recebidos. | Corrigido para `super(protocolo, petNome, petPorte, tutorNome, dataHora);`. | Herança e encadeamento de construtores (`super(...)`) — Aula 6/7. |
| bug04 | Dois agendamentos do mesmo pet no mesmo horário eram aceitos como se não houvesse conflito. `AgendaServiceTest.deveRecusarAgendamentoComHorarioJaOcupado` falhava mesmo simulando o cenário exato de conflito. | `AgendaService.java`, método `agendar` (linha ~23): `a.getPetNome() == novo.getPetNome() && a.getDataHora() == novo.getDataHora()` — comparação por referência (`==`), não por valor. "Funciona por sorte" com `String` por causa do *string pool* de literais, mas nunca com `LocalDateTime` (nunca internado) vindo de objetos diferentes. | Trocado para `a.getPetNome().equals(novo.getPetNome()) && a.getDataHora().equals(novo.getDataHora())`. | `==` (referência) vs `.equals()` (conteúdo) — Aula 7. |
| bug05 | Buscar um atendimento inexistente não lançava a exceção esperada — o método devolvia `null` silenciosamente. Bug em cascata: `concluir()`/`cancelar()` de um id inexistente explodiam com `NullPointerException` em vez de retornar 404. `AgendaServiceTest.deveLancarExcecaoQuandoAtendimentoNaoExiste` falhava. | `AgendaService.java`, método `buscarPorId` (linha ~37-42): `catch (Exception e) { return null; }` — captura genérica que engolia a própria `AtendimentoNaoEncontradoException` lançada linhas acima. | Removido o `try/catch` por completo; o `orElseThrow` agora propaga a exceção naturalmente até o controller. | Tratamento de exceções — catch genérico "engole" erros; contrato de não retornar `null` — Aula 11. |
| bug06 | Cada chamada a `GeradorProtocolo.getInstancia()` devolvia um objeto diferente, e a numeração de protocolo reiniciava do zero a cada chamada. `GeradorProtocoloTest.deveManterUmaUnicaInstancia` e `deveGerarProtocolosSequenciais` falhavam. | `GeradorProtocolo.java`, método `getInstancia` (linha ~18): `return new GeradorProtocolo();` dentro do `if (instancia == null)`, sem nunca atribuir o resultado à variável estática `instancia`. | Corrigido para atribuir antes de retornar: `instancia = new GeradorProtocolo(); return instancia;`. | Padrão Singleton (Aula 14) — garantir uma única instância global. |
| bug07 | Era possível montar (e agendar) um atendimento sem nome do pet ou sem porte — nenhuma exceção era lançada. `AtendimentoBuilderTest.deveRecusarMontagemSemNomeDoPet` e `deveRecusarMontagemSemPorte` falhavam. | `AtendimentoBuilder.java`, método `construir` (linha ~41): a validação era delegada ao controller (o próprio comentário do código admitia isso), sem nenhuma checagem no builder — contrariando a regra "o objeto só nasce válido". | Adicionada validação no início de `construir()`: lança `IllegalArgumentException` se `petNome` ou `petPorte` forem nulos ou vazios. | Padrão Builder (Aula 14) — o builder deve garantir a validade do objeto antes de entregá-lo. |
| bug08 | Banho de porte PEQUENO custava R$ 100,00 e de porte GRANDE custava R$ 60,00 — valores invertidos em relação ao contrato (R$ 60 / 80 / 100). Não aparecia em nenhum dos 20 testes entregues; só foi encontrado na leitura do código e passou a ser protegido pelo teste novo de preço por porte (ver `teste04`, Parte 3). | `Banho.java`, método `calcularPreco` (linha ~27-32): os valores de retorno de PEQUENO e GRANDE estavam trocados entre si. | Corrigido: PEQUENO → `60.0`, GRANDE (retorno padrão) → `100.0`. | Regra de negócio no model / polimorfismo — Aula 13/14. |
| bug09 | `tosa.getDuracaoMinutos()` (sem argumento) retornava 30 minutos em vez de 60. Não aparecia em nenhum dos 20 testes entregues; só foi encontrado na leitura do código e passou a ser protegido pelo teste novo de duração da Tosa (ver `teste05`, Parte 3). | `Tosa.java` (linha ~40): `public int getDuracaoMinutos(String porte)` — parâmetro extra e sem `@Override` criava um método **novo** (sobrecarga/overload), em vez de sobrescrever (override) o método de `Atendimento`, que continuava retornando o padrão `30`. | Assinatura corrigida para `getDuracaoMinutos()` (sem parâmetro) e anotação `@Override` adicionada. | Sobrescrita (override) vs sobrecarga (overload) — Aula 7. |
| bug10 | Era possível cancelar um atendimento que já estava `CONCLUIDO` ou já `CANCELADO`. Não aparecia em nenhum dos 20 testes entregues (não havia nenhum teste de `cancelar()` na suíte original); só foi encontrado na leitura do código e passou a ser protegido pelos testes novos (ver `teste02` e `teste03`, Parte 3). | `Atendimento.java`, método `cancelar` (linha ~63-65): `status = "CANCELADO";` era executado incondicionalmente, sem nenhuma verificação do status atual (diferente de `concluir()`, que já validava). | Adicionada a mesma checagem usada em `concluir()`: lança `StatusInvalidoException` se o status não for `"AGENDADO"`. | Máquina de estados / validação de transição de estado no model — Aula 13. |
| bug11 | Era possível agendar um atendimento com data/hora no passado — nenhuma validação existia. Não aparecia em nenhum dos 20 testes entregues; só foi encontrado na leitura do código e passou a ser protegido pelo teste novo (ver `teste01`, Parte 3). | `AgendaService.java`, método `agendar` (linha ~20): nenhuma checagem de data era feita antes de consultar o repositório e salvar. | Adicionado `if (novo.getDataHora().isBefore(LocalDateTime.now())) { throw new IllegalArgumentException(...); }` logo no início do método, antes de qualquer acesso ao repository. | Validação de invariante de negócio antes de tocar a persistência — Aula 11/13. |
| bug12 | Nenhum dos 20 testes unitários pegava isso, porque todos usam `@Mock` no repository (Aula 15). Ao subir a API de verdade e tentar persistir um atendimento no Oracle, o `save()` falhava por falta de estratégia de geração de chave primária. Encontrado só de ler o código com atenção, como um code review de verdade. | `Atendimento.java`, campo `id` (linha ~15): `@Id private Long id;` sem `@GeneratedValue` — o Hibernate esperava um id atribuído manualmente, que nunca era setado em lugar nenhum do código. | Corrigido para `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;`. | Mapeamento JPA / geração de chave primária — Aula 12/13. |

---

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean02 | `AtendimentoFactory.criar(int p, String t, String n, String po, String tu, LocalDateTime d)` | Nomes de parâmetros de uma letra só, ilegíveis — quem lê o método precisa adivinhar o que é cada um. | Parâmetros e referências no `switch` renomeados para `protocolo, tipo, petNome, petPorte, tutorNome, dataHora`. |
| clean03 | `AtendimentoController.java` — método privado `calcularDescontoFidelidade(int pontos)`, comentado como "feature futura" mas nunca chamado. | Código morto (YAGNI) — método sem uso poluindo a classe. | Método e o comentário associado removidos. |
| clean04 | `GeradorProtocolo.java` — comentário de classe `// Thread-safe para o uso concorrente do pet shop.` | Comentário mentiroso: `getInstancia()` não tem nenhuma sincronização, então a classe não é thread-safe. | Comentário incorreto removido, para o código não prometer o que não garante. |
| clean05 | `GeradorProtocolo.java` — `System.out.println("GeradorProtocolo criado!")` no construtor. | Log de debug esquecido no código de produção (efeito colateral de I/O dentro de um construtor). | `println` removido. |
| clean06 | `AgendaService.agendar` — `System.out.println("Recibo: atendimento ...")` após o `save`. | Violação de responsabilidade única: a camada de serviço não deveria imprimir recibo no console (apresentação/log misturados com regra de negócio). | `println` removido; o método devolve apenas o atendimento salvo. |
| clean07 | `Atendimento.java`, `Banho.java`, `Tosa.java` e `AgendaService.java` — strings mágicas (`"AGENDADO"`, `"CONCLUIDO"`, `"CANCELADO"`, `"PEQUENO"`, `"MEDIO"`, `"GRANDE"`) repetidas em várias classes. Além disso, `Atendimento` tinha setters sem nenhum uso (`setId`, `setProtocolo`, `setPetNome`, `setPetPorte`, `setTutorNome`, `setDataHora`). | Magic strings: um erro de digitação não é pego pelo compilador e o valor precisa ser repetido em cada arquivo; setters não usados expõem estado que deveria ser imutável depois da criação. | Criadas constantes `public static final String` em `Atendimento` (`AGENDADO`, `CONCLUIDO`, `CANCELADO`, `PEQUENO`, `MEDIO`, `GRANDE`) e usadas no lugar dos literais; setters sem uso removidos (mantido só `setStatus`, usado pelos testes). O tipo do campo `status` continua `String` de propósito, para não quebrar os testes entregues. |

---

## Parte 3 — Testes novos (regras que estavam sem cobertura)

Os 6 testes foram escritos com um commit cada (`test: testeNN`), seguindo o padrão da suíte (AAA, nome `deve...Quando...`, mock onde há dependência). Como as correções de bug foram commitadas antes dos testes, todos passam (verde) no estado final e agora protegem cada regra contra regressão; a coluna "Bug que protege" indica qual defeito voltaria a ser pego se alguém o reintroduzisse.

| # | Teste escrito (classe.método) | Regra coberta | Bug que protege / resultado |
|---|---|---|---|
| teste01 | `AgendaServiceTest.deveRecusarAgendamentoQuandoDataHoraEstaNoPassado` | "Agendar com data/hora no passado → `IllegalArgumentException`, o banco nem é consultado" (`verify(repository, never()).save(any())`). | Protege o `bug11` (ausência de validação de data). 🟢 Verde após a correção. |
| teste02 | `AgendaServiceTest.deveRecusarCancelamentoDeAtendimentoJaConcluido` | "cancelar(): CONCLUIDO → recusa (`StatusInvalidoException`)"; nada é salvo. | Protege o `bug10` (`cancelar()` sem checagem de status). 🟢 Verde após a correção. |
| teste03 | `AgendaServiceTest.deveRecusarCancelamentoDeAtendimentoJaCancelado` | "cancelar(): CANCELADO → recusa (`StatusInvalidoException`)"; nada é salvo. | Protege o `bug10`. 🟢 Verde após a correção. |
| teste04 | `BanhoTest.deveCobrarPrecoConformeOPorteQuandoForBanho` | Preço do Banho por porte: R$ 60 (PEQUENO) / R$ 80 (MEDIO) / R$ 100 (GRANDE). | Protege o `bug08` (preços de PEQUENO e GRANDE trocados). 🟢 Verde após a correção. |
| teste05 | `TosaTest.deveDurar60MinutosQuandoForTosa` | Duração da Tosa: 60 minutos, chamando `getDuracaoMinutos()` pelo tipo abstrato `Atendimento` (polimorfismo, como o controller faz). | Protege o `bug09` (overload em vez de override). 🟢 Verde após a correção. |
| teste06 | `ConsultaVeterinariaTest.deveCobrar150ReaisQualquerQueSejaOPorteQuandoForConsulta` | Consulta tem preço fixo de R$ 150 para PEQUENO, MEDIO e GRANDE. | Regra que já estava correta (`calcularPreco()` retorna 150.0 fixo): 🟢 verde de cara; o teste só protege contra regressão. |

---

## Parte 4 — Perguntas de reflexão

### 1. A suíte como contrato (Aula 15)

O projeto chegou com 20 testes, e 9 deles estavam falhando. Cada teste que falhava dizia com clareza o que estava errado. Por exemplo, um teste esperava o nome "Mimi" e recebeu vazio. Isso mostrou que a consulta veterinária estava ignorando os dados do pet (`bug03`). Outro teste esperava os protocolos 1, 2 e 3, mas recebeu sempre 1. Isso mostrou que o gerador de protocolos estava recomeçando do zero a cada uso (`bug06`).

Testar tudo à mão pela API seria bem mais lento e cansativo. A suíte roda em poucos segundos, sem precisar ligar a aplicação nem o banco de dados. Ela também testa vários cenários de uma vez, inclusive os de erro, como agendar no passado. O mais importante é que dá para rodar tudo de novo depois de cada correção e ver se a mudança não estragou outra coisa.

### 2. Mock e injeção de dependência (Aulas 13 a 15)

No teste do serviço de agenda, o `@Mock` cria um repositório de mentirinha. Ele funciona como um dublê: responde o que o teste pede, sem conversar com nenhum banco de verdade. O `@InjectMocks` entrega esse dublê para o serviço usar.

Na aplicação real, quem faz essa entrega é o Spring. Ao ligar, ele cria o repositório de verdade, conectado ao Oracle, e o entrega ao serviço automaticamente. No teste, o Mockito faz esse papel sem precisar do Spring nem do banco.

Por isso o `bug12` (falta do `@GeneratedValue`) não aparecia nos testes. O "salvar" era de mentira e nunca chegava ao banco de verdade. Esse tipo de problema só aparece quando a API está rodando.

### 3. `==` vs `.equals()` (Aula 7)

O `bug04` estava na verificação de horário do agendamento. O código usava `==` para comparar a data e o nome do pet. Esse símbolo pergunta se os dois são exatamente o mesmo objeto na memória. Não pergunta se têm o mesmo conteúdo.

Com nomes escritos direto no código, como "Rex", isso funcionava por sorte, porque o Java reaproveita textos iguais. Mas quando a data ou o nome chegam de outra requisição, viram objetos diferentes com o mesmo conteúdo. Então o `==` dizia "diferente" e deixava passar um agendamento duplicado.

A correção foi trocar por `.equals()`, que compara o conteúdo. Assim o conflito de horário é detectado sempre, não importa de onde os dados vieram.

### 4. Sobrescrita vs sobrecarga (Aula 7)

O `bug09` estava na Tosa. Ela tinha um método de duração com um parâmetro a mais do que o método da classe mãe. Por causa dessa diferença, o Java não entendeu que era uma substituição. Entendeu como um método novo e separado.

O projeto compilava sem erro, mas o valor errado aparecia. Quando alguém pedia a duração da tosa, o Java usava a versão padrão da classe mãe (30 minutos) em vez dos 60 minutos da tosa.

A anotação `@Override` serve para evitar isso. Ela avisa o Java: "este método deve substituir um da classe mãe". Se a assinatura não bater, o compilador mostra erro na hora, em vez de deixar o engano passar em silêncio.

### 5. Singleton manual vs bean do Spring (Aula 14)

O `GeradorProtocolo` foi feito para existir em uma única cópia em toda a aplicação. Assim a numeração dos protocolos é uma só e segue em sequência. O `bug06` quebrava isso. A cada chamada, ele criava um gerador novo, sempre começando do 1, em vez de reaproveitar o que já existia.

O `AgendaService` não tem esse risco porque quem cuida dele é o Spring. Por padrão, o Spring cria uma única cópia de cada `@Service` e reaproveita em todo lugar. Não depende de uma regra escrita à mão, como o "se ainda não existe, crie", que pode ter um erro como o do gerador de protocolos.

### 6. Cobertura de testes: onde parar? (Aula 15)

Os 6 testes novos cobrem estes pontos:

- agendamento no passado;
- cancelamento de atendimento já concluído e já cancelado;
- preço do Banho por porte;
- duração da Tosa;
- preço fixo da Consulta.

Cinco deles protegem regras que escondiam bugs reais. O sexto, o preço fixo da Consulta, cobre uma regra que já estava certa.

Vale manter o teste que já nasceu passando. Ele dá pouco trabalho para escrever e funciona como uma proteção. Se alguém mudar o preço da Consulta no futuro e fizer o valor depender do porte, o teste falha na hora e o erro não passa despercebido.

Num projeto real com pouco tempo, eu começaria pelo que envolve dinheiro ou pode deixar dados errados, como os preços trocados e o status inconsistente que encontramos. Depois viria o uso comum das funcionalidades. Buscar 100% de cobertura vem por último. Testar só o que provavelmente já funciona, enquanto os erros mais caros continuam escondidos, gasta tempo à toa.

---

## Parte 5 — Espaço livre (opcional)

```
A parte que mais ajudou foi ter uma suíte de testes pronta como "termômetro": em vez de
ficar adivinhando se uma correção funcionou, bastava rodar a suíte de novo e ver o
vermelho virar verde (ou algum outro teste quebrar por regressão). Isso deixou o processo
bem mais parecido com o dia a dia real de dar manutenção em um código legado do que só
corrigir bugs soltos sem nenhuma rede de segurança.

O ponto mais difícil foi decidir exatamente quais eram as "6 regras sem cobertura" —
como o enunciado não entrega uma lista fechada delas, coube ao grupo cruzar as tabelas
da Parte C com os 20 testes entregues na mão para achar as lacunas, e é bem possível que
outro grupo tenha enxergado um recorte um pouco diferente do nosso (por exemplo, testar
separadamente cada combinação de status/status em vez de agrupar por operação). Achamos
que ficaria mais objetivo se o enunciado citasse quantas linhas de cada tabela já estão
cobertas pelos 20 testes originais, para reduzir essa margem de interpretação.
```
