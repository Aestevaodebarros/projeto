# Guia de manutenção do projeto

Este repositório contém a aplicação **Manejo Caatingueiro** em Java 17, com Spring Boot, JavaFX e persistência local H2. Este arquivo é um guia de manutenção; não existe um agente autônomo de auditoria executando continuamente no projeto.

## Estrutura principal

- `src/main/java/.../controller`: API REST e DTOs.
- `src/main/java/.../service`: regras de negócio e casos de uso.
- `src/main/java/.../model`: classes de domínio.
- `src/main/java/.../repository`: contratos de persistência.
- `src/main/java/.../infrastructure/persistence`: adaptadores JDBC/H2.
- `src/main/java/.../ui`: interface desktop JavaFX.
- `src/test/java`: testes automatizados.
- `docs/diagrama-de-classes.puml`: diagrama de classes de domínio.

## Comandos de desenvolvimento

```bash
mvn test
mvn spring-boot:run
mvn javafx:run
```

Requer Java 17+ e Maven instalado. `mvn javafx:run` deve ser executado em ambiente com interface gráfica.

## Regras que devem ser preservadas

1. Sugestões de plantio são consultivas; a escolha final pertence ao agricultor (RN12).
2. Exibir fonte, data de atualização e incerteza dos dados climáticos/agronômicos (RN16 e RNF11).
3. A autonomia considera reserva útil, consumo de pessoas/animais/irrigação e entradas contínuas (RN17).
4. A viabilidade compara oferta hídrica e demanda da cultura usando os intervalos da RN18.
5. Não expor dados individuais dos reservatórios no monitor comunitário; aplicar agregação e consentimento com mínimo de cinco participantes no comportamento atual (RN15).
6. A fila de sincronização registra pendências locais. Não marcar como sincronizadas nem declarar sincronização entre dispositivos enquanto não existir integração remota funcional (RF17).
7. Solicitação de água, simulação de déficit e histórico de safras são funcionalidades Pós-MVP na especificação v4. A implementação existente mantém essas funcionalidades preparadas, mas sua disponibilização deve respeitar os gates de validação.

## Antes de alterar

- Ler `README.md` e a Especificação de Requisitos v4.
- Atualizar os testes quando regras de negócio ou respostas da API mudarem.
- Atualizar `docs/diagrama-de-classes.puml` quando o modelo de domínio mudar.
- Não adicionar credenciais, dados pessoais reais ou arquivos locais do H2 ao repositório.
