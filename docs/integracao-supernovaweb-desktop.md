# Integração visual SupernovaWeb → JavaFX Desktop

## O que foi reutilizado

- Identidade editorial sertaneja e paleta visual do SupernovaWeb.
- `logo.png`, `logo-mark.svg` e ilustrações de chuva, açudes, calendário e estados da roça.
- Os SVGs foram mantidos em `src/main/resources/images/` e convertidos para PNG para exibição nativa em `ImageView` do JavaFX.
- Telas desktop com login/cadastro, visão geral, perfil, plantio e água, reservatórios, comunidade, safras/cenários e solicitações.

## APIs e compatibilidade

O pacote SupernovaWeb contém um backend TypeScript/Express com rotas `/api/v1/...`, Firebase/Firestore e contratos de autenticação próprios. O projeto desktop atualizado é Java 17 + JavaFX + Spring Boot + H2, com controladores e serviços Java próprios. As rotas não são intercambiáveis: por exemplo, o frontend web usa `/api/v1/auth/login`, `/api/v1/precipitacoes` e `/api/v1/reservatorio`, enquanto a API Java expõe `/api/v1/auth/login`, `/api/v1/localidades/{id}/precipitacoes` e `/api/v1/reservatorios`.

Para não quebrar autenticação, persistência ou segurança, as janelas JavaFX usam os serviços Java já existentes (`UsuarioService`, `ClimaService`, `PlantioService`, `ReservatorioService`, `ComunidadService`, `RegistroSafraService`, `SincronizacaoService`) e a API Spring Boot definida em `ApiController`. As integrações climáticas já existentes no projeto Java permanecem ativas por `OpenMeteoClient`/`ClimaIngestionService`.

Não foram transplantadas as rotas Firebase/Gemini/Google OAuth do servidor web. Elas exigem configuração própria (`.env`, credenciais de serviço e chaves) e adaptação explícita dos contratos para o backend Java. Nenhuma chave privada ou segredo foi copiado.

## Jakarta x interface desktop

Jakarta Validation e Jakarta Servlet são usadas no backend Spring. Jakarta Faces é uma tecnologia web, não um toolkit de janelas desktop. Para manter a aplicação desktop nativa e coerente com o `pom.xml`, as janelas são implementadas com JavaFX Controls e os recursos visuais são carregados por JavaFX `ImageView`.
