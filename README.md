# Manejo Caatingueiro — Java

Aplicação em camadas para apoiar agricultores do Semiárido com painel JavaFX, API Spring Boot, persistência local e cálculos consultivos de água e plantio.

## Executar

Requisitos: Java 17+ e Maven.

```bash
mvn test
mvn spring-boot:run
```

A API fica em `http://localhost:3000/api/v1`. Para abrir o painel, em um ambiente com desktop gráfico:

```bash
mvn javafx:run
```

O painel começa pela tela de login e permite criar usuário com perfil produtivo. A conta demonstrativa é `demo@manejocaatingueiro.org.br` / `123456`.

## Arquitetura

```text
JavaFX / controller
        ↓
services (casos de uso e regras)
        ↓
repositories (contratos)
        ↓
infrastructure/persistence (adaptadores JDBC/H2)
```

Os dados são salvos em H2 no diretório `data/`, criado na primeira execução. A base não é apagada ao reiniciar. O arquivo do banco e `target/` são ignorados pelo Git. As credenciais de usuários são armazenadas com BCrypt; a API emite tokens JWT assinados, válidos por 12 horas. Rotas de escrita e dados de propriedade exigem `Authorization: Bearer <token>`. Cadastros públicos recebem o perfil `AGRICULTOR`.

## Funcionalidades implementadas

- Cadastro, login e perfil produtivo com pessoas, animais, área irrigada, cota humana configurável e consentimento explícito opcional para uso de localização.
- Registro de precipitação por data e fonte; diagnóstico mensal e da estação chuvosa, comparação com normal climatológica, frescura e estado explícito de dados insuficientes.
- Ingestão de chuva recente e série histórica pela Open-Meteo. A atualização automática começa após 10 minutos e se repete diariamente; a rota autenticada de atualização manual é `POST /api/v1/localidades/{id}/clima/atualizar`.
- Culturas iniciais, limiar editável por usuário, sugestão consultiva e cálculo de autonomia/viabilidade, estruturados pelas classes `CalculoAutossuficiencia`, `ResultadoViabilidade`, `SugestaoPlantio`, `ParametroAgronomico` e `SerieHistoricaClimatica`.
- Reservatórios vinculados ao responsável, atualização de nível percentual ou qualitativo, histórico diário com substituição no mesmo dia e checklist sanitário.
- Monitor comunitário agregado e pseudonimizado (`MonitorComunitario`); só informa níveis e riscos quando há pelo menos cinco reservatórios com consentimento explícito.
- Alertas locais de chuva e risco de safra; simulação de déficit hídrico; histórico de plantios e resultados.
- Canal de solicitação de água, com regra de nível até 25% e transições de status, preparado para Pós-MVP. A abertura permanece desativada por padrão: depende de validação do piloto e pactuação institucional conforme RN14. Pode ser habilitada com `SOLICITACAO_AGUA_ATIVO=true` após esses gates.

## Rotas principais

- `GET /api/v1/health`, `GET /api/v1/localidades`, `GET /api/v1/localidades/{id}/diagnostico`
- `POST /api/v1/auth/register`, `POST /api/v1/auth/login`
- `POST/GET /api/v1/localidades/{id}/precipitacoes`
- `GET /api/v1/culturas`, `PUT /api/v1/culturas/{id}/limiar-usuario`
- `GET /api/v1/autossuficiencia/{usuarioId}`, `POST /api/v1/simulacoes/deficit-hidrico`, `GET /api/v1/sincronizacao/status`
- `GET/POST /api/v1/reservatorios`, `PUT /api/v1/reservatorios/{id}/nivel`, `POST /api/v1/reservatorios/{id}/checklist`
- `GET /api/v1/alertas/ativos`, `GET/POST /api/v1/comunidade/avisos`
- `POST /api/v1/safras`, `GET /api/v1/safras`, `PUT /api/v1/safras/{id}/resultado`, `POST /api/v1/safras/{id}/avaliar-risco`

Alterações globais de parâmetros de consumo e emissão manual de alertas exigem perfil administrativo/comunitário apropriado. O registro público não permite atribuir esses privilégios.

## Cálculos e limites de validação

- Autonomia: reserva útil dos reservatórios próprios ÷ consumo líquido diário, incluindo cotas humanas/animais, irrigação e entradas contínuas; faixas RN17: seguro acima de 120 dias, atenção de 60 a 120, crítico abaixo de 60.
- Viabilidade: oferta (chuva observada desde o plantio + climatologia restante da estação + reserva convertida em lâmina sobre a área irrigada) ÷ demanda estimada; faixas RN18: viável a partir de 1,0, restrição de 0,7 a menos de 1,0 e inviável abaixo de 0,7.
- Os Kc e ETo têm fonte registrada. A duração das fases do milho e feijão-caupi é provisória porque a especificação não informa essas durações; deve ser validada com técnico agrícola antes de uso operacional. Mamona usa a referência total de ciclo enquanto não há série de Kc validada.
- Open-Meteo e parâmetros agronômicos precisam de validação técnica/local; a integração não substitui o spike de dados e a validação de campo previstos na especificação.
- A base H2 permite leitura e escrita local sem internet. A implementação registra as operações mutáveis numa fila local persistente e expõe o estado em `GET /api/v1/sincronizacao/status`; ainda não existe consumidor/dispatcher para enviar essas pendências a Firebase/nuvem, nem sincronização entre dispositivos. O monitor não recebe dados inseridos em outros dispositivos até existir essa integração.
- RF14–RF16 e o monitor seguem sujeitos ao gate de validação em campo descrito na especificação. O software não comprova o gate, consentimento real de usuários, validação institucional ou agronômica.


## Modelo de domínio alinhado ao diagrama UML

O código mantém os nomes já usados pela API e inclui as classes de domínio que estavam ausentes no diagrama: `CalculoAutossuficiencia`, `ResultadoViabilidade`, `SugestaoPlantio`, `ParametroAgronomico`, `SerieHistoricaClimatica`, `MonitorComunitario`, `ChecklistSanitario` e `Sincronizacao`. `RegistroSafra` é a representação usada pela API para a classe conceitual `HistoricoSafra`; `RegistroNivelReservatorio` corresponde ao `RegistroNivel` do diagrama; `AvisoComunitario` corresponde ao `AvisoMural`.

O diagrama-fonte PlantUML está em `docs/diagrama-de-classes.puml`.

### Estado da sincronização

As escritas locais feitas por usuários autenticados são registradas como pendências na coleção H2 `sincronizacoes`. A rota `GET /api/v1/sincronizacao/status` informa a quantidade e os itens pendentes. O status não é marcado como sincronizado automaticamente porque esta versão ainda não possui integração remota; isso evita indicar uma sincronização que não ocorreu.

## Interface desktop e recursos visuais

A interface desktop continua sendo JavaFX (não Jakarta Faces): Jakarta EE não é uma biblioteca de janelas desktop. O projeto usa Jakarta Validation/Servlet no backend Spring Boot e JavaFX Controls para as janelas. Foram incorporadas ao desktop a identidade visual, a logo e ilustrações do pacote SupernovaWeb em `src/main/resources/images/`.

Telas desktop disponíveis: login, cadastro, visão geral, meu perfil (consulta), plantio e autonomia hídrica, reservatórios/checklist, comunidade/avisos, safras/simulações e solicitações de água (com o gate RN14 preservado).

As telas usam diretamente os serviços Java/Spring do próprio `projeto-main-atualizado`, mantendo a API em `ApiController` (`/api/v1`). Os scripts TypeScript/Express do pacote SupernovaWeb não foram copiados como backend porque usam contratos `/api/v1` diferentes e Firebase; misturá-los à aplicação Java/H2 causaria incompatibilidade de endpoints e autenticação. As APIs externas de clima continuam sendo consumidas pelo `OpenMeteoClient`/`ClimaIngestionService` do projeto Java.

Os SVGs do pacote visual foram preservados e também convertidos para PNG para renderização nativa no JavaFX. Origem dos recursos: pacote SupernovaWeb fornecido junto ao projeto.
