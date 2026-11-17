package com.manejocaatingueiro.controller;

import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.model.AlertaClimatico;
import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.model.PerfilUsuario;
import com.manejocaatingueiro.model.ParametrosConsumo;
import com.manejocaatingueiro.model.RegistroSafra;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.model.SolicitacaoAgua;
import com.manejocaatingueiro.model.StatusSolicitacaoAgua;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.controller.dto.AlertaRequest;
import com.manejocaatingueiro.controller.dto.ChecklistRequest;
import com.manejocaatingueiro.controller.dto.LimiarRequest;
import com.manejocaatingueiro.controller.dto.LoginRequest;
import com.manejocaatingueiro.controller.dto.LoginResponse;
import com.manejocaatingueiro.controller.dto.NivelReservatorioRequest;
import com.manejocaatingueiro.controller.dto.PrecipitacaoRequest;
import com.manejocaatingueiro.controller.dto.RegistroSafraRequest;
import com.manejocaatingueiro.controller.dto.ResultadoSafraRequest;
import com.manejocaatingueiro.controller.dto.SimulacaoCenarioRequest;
import com.manejocaatingueiro.controller.dto.SolicitacaoAguaRequest;
import com.manejocaatingueiro.controller.dto.StatusSolicitacaoRequest;
import com.manejocaatingueiro.controller.dto.RegistroUsuarioRequest;
import com.manejocaatingueiro.controller.dto.UsuarioResponse;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.service.ClimaService;
import com.manejocaatingueiro.service.ClimaIngestionService;
import com.manejocaatingueiro.service.JwtTokenService;
import com.manejocaatingueiro.service.AlertaService;
import com.manejocaatingueiro.service.RegistroSafraService;
import com.manejocaatingueiro.service.SolicitacaoAguaService;
import com.manejocaatingueiro.service.ParametrosConsumoService;
import com.manejocaatingueiro.service.ComunidadService;
import com.manejocaatingueiro.service.PlantioService;
import com.manejocaatingueiro.service.ReservatorioService;
import com.manejocaatingueiro.service.UsuarioService;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    private final ClimaService climaService;
    private final ClimaIngestionService climaIngestionService;
    private final PlantioService plantioService;
    private final ReservatorioService reservatorioService;
    private final ComunidadService comunidadService;
    private final UsuarioService usuarioService;
    private final JwtTokenService jwtTokenService;
    private final AlertaService alertaService;
    private final SolicitacaoAguaService solicitacaoAguaService;
    private final RegistroSafraService registroSafraService;
    private final ParametrosConsumoService parametrosConsumoService;

    public ApiController(ClimaService climaService, ClimaIngestionService climaIngestionService,
                        PlantioService plantioService,
                        ReservatorioService reservatorioService, ComunidadService comunidadService,
                        UsuarioService usuarioService, JwtTokenService jwtTokenService,
                        AlertaService alertaService, SolicitacaoAguaService solicitacaoAguaService,
                        RegistroSafraService registroSafraService,
                        ParametrosConsumoService parametrosConsumoService) {
        this.climaService = climaService;
        this.climaIngestionService = climaIngestionService;
        this.plantioService = plantioService;
        this.reservatorioService = reservatorioService;
        this.comunidadService = comunidadService;
        this.usuarioService = usuarioService;
        this.jwtTokenService = jwtTokenService;
        this.alertaService = alertaService;
        this.solicitacaoAguaService = solicitacaoAguaService;
        this.registroSafraService = registroSafraService;
        this.parametrosConsumoService = parametrosConsumoService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("java", "Spring Boot");
        health.put("modoOperacao", "H2_PERSISTENTE");
        return health;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse register(@Valid @RequestBody RegistroUsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setTelefone(request.telefone());
        usuario.setSenhaHash(request.senha());
        usuario.setPerfil(request.perfil());
        usuario.setAbastecimento(request.abastecimento());
        usuario.setLocalidadeId(request.localidadeId());
        usuario.setPerfilProdutivo(request.perfilProdutivo());
        return UsuarioResponse.de(usuarioService.registrar(usuario));
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.autenticar(request.email(), request.senha());
        return new LoginResponse(jwtTokenService.criarToken(usuario), "Bearer", UsuarioResponse.de(usuario));
    }

    @GetMapping("/localidades")
    public List<?> localidades() {
        return climaService.listarLocalidades();
    }

    @GetMapping("/localidades/{id}/diagnostico")
    public Map<String, Object> diagnostico(@PathVariable Integer id) {
        return climaService.diagnostico(id);
    }

    @GetMapping("/localidades/{id}/precipitacoes")
    public List<?> precipitacoes(@PathVariable Integer id) {
        return climaService.listarPrecipitacoes(id);
    }

    @PostMapping("/localidades/{id}/precipitacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public Object registrarPrecipitacao(@PathVariable Integer id,
                                        @Valid @RequestBody PrecipitacaoRequest request,
                                        HttpServletRequest httpRequest) {
        Usuario usuario = usuarioAutenticado(httpRequest);
        validarLocalidadeDoUsuario(id, usuario);
        return climaService.registrarPrecipitacao(id, request.volumeMm(), request.fonte(), request.data());
    }

    @PostMapping("/localidades/{id}/clima/atualizar")
    public Map<String, Object> atualizarClima(@PathVariable Integer id) {
        return climaIngestionService.atualizar(id);
    }

    @GetMapping("/culturas")
    public List<Cultura> culturas() {
        return plantioService.listarCulturas();
    }

    @GetMapping("/parametros-consumo")
    public ParametrosConsumo consultarParametrosConsumo() {
        return parametrosConsumoService.consultar();
    }

    @PutMapping("/parametros-consumo")
    public ParametrosConsumo atualizarParametrosConsumo(@RequestBody ParametrosConsumo parametros) {
        return parametrosConsumoService.atualizar(parametros);
    }

    @PutMapping("/culturas/{id}/limiar-usuario")
    public Map<String, Object> calibrarLimiar(@PathVariable Integer id,
                                             @Valid @RequestBody LimiarRequest request,
                                             HttpServletRequest httpRequest) {
        Usuario usuario = usuarioAutenticado(httpRequest);
        return Map.of(
                "culturaId", id,
                "usuarioId", usuario.getId(),
                "limiarMm", plantioService.calibrarLimiar(id, usuario.getId(), request.limiarMm())
        );
    }

    @GetMapping("/autossuficiencia/{usuarioId}")
    public Map<String, Object> autossuficiencia(@PathVariable Long usuarioId,
                                                @RequestParam(required = false) LocalDate dataPlantio,
                                                HttpServletRequest httpRequest) {
        Usuario usuario = usuarioAutenticado(httpRequest);
        if (!usuario.getId().equals(usuarioId)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Não é permitido consultar os dados de outra propriedade.");
        }
        return plantioService.avaliarAptidao(usuario.getLocalidadeId(), usuario, dataPlantio);
    }

    @GetMapping("/reservatorios")
    public List<Reservatorio> reservatorios(HttpServletRequest httpRequest) {
        return reservatorioService.listarPorUsuario(usuarioAutenticado(httpRequest).getId());
    }

    @PostMapping("/reservatorios")
    @ResponseStatus(HttpStatus.CREATED)
    public Reservatorio criarReservatorio(@RequestBody Reservatorio reservatorio,
                                          HttpServletRequest httpRequest) {
        Usuario usuario = usuarioAutenticado(httpRequest);
        reservatorio.setLocalidadeId(usuario.getLocalidadeId());
        return reservatorioService.salvar(reservatorio, usuario.getId());
    }

    @PutMapping("/reservatorios/{id}/nivel")
    public Reservatorio atualizarNivel(@PathVariable Long id,
                                       @Valid @RequestBody NivelReservatorioRequest request,
                                       HttpServletRequest httpRequest) {
        return reservatorioService.atualizarNivel(id, usuarioAutenticado(httpRequest).getId(),
                request.nivelPercentual(), request.nivelQualitativo(), request.profundidadeLencolM());
    }

    @PostMapping("/reservatorios/{id}/checklist")
    public Map<String, Object> checklist(@PathVariable Long id,
                                         @Valid @RequestBody ChecklistRequest request,
                                         HttpServletRequest httpRequest) {
        reservatorioService.registrarChecklist(id, usuarioAutenticado(httpRequest).getId(),
                request.item(), request.conforme());
        return Map.of("reservatorioId", id, "status", "registrado");
    }

    @GetMapping("/comunidade/avisos")
    public List<AvisoComunitario> avisos(HttpServletRequest httpRequest) {
        return comunidadService.listar(usuarioAutenticado(httpRequest).getLocalidadeId());
    }

    @PostMapping("/comunidade/avisos")
    @ResponseStatus(HttpStatus.CREATED)
    public AvisoComunitario publicarAviso(@RequestBody AvisoComunitario aviso,
                                          HttpServletRequest httpRequest) {
        Usuario usuario = usuarioAutenticado(httpRequest);
        aviso.setLocalidadeId(usuario.getLocalidadeId());
        aviso.setAutor(usuario.getNome());
        return comunidadService.publicar(aviso);
    }

    @GetMapping("/localidades/{id}/monitor-reservatorios")
    public Map<String, Object> monitorReservatorios(@PathVariable Integer id) {
        return comunidadService.monitorLocalidade(id);
    }

    @GetMapping("/alertas/ativos")
    public List<AlertaClimatico> alertasAtivos(HttpServletRequest request) {
        return alertaService.consultarAtivos(usuarioAutenticado(request).getLocalidadeId());
    }

    @PostMapping("/alertas")
    @ResponseStatus(HttpStatus.CREATED)
    public AlertaClimatico emitirAlerta(@Valid @RequestBody AlertaRequest request,
                                        HttpServletRequest httpRequest) {
        Usuario usuario = usuarioAutenticado(httpRequest);
        return alertaService.publicar(usuario.getLocalidadeId(), request.tipo(), request.severidade(),
                request.mensagem(), request.fonte());
    }

    @PostMapping("/solicitacoes-agua")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoAgua abrirSolicitacao(@Valid @RequestBody SolicitacaoAguaRequest request,
                                            HttpServletRequest httpRequest) {
        return solicitacaoAguaService.abrir(usuarioAutenticado(httpRequest),
                request.reservatorioId(), request.motivo());
    }

    @GetMapping("/solicitacoes-agua")
    public List<SolicitacaoAgua> listarSolicitacoes(
            @RequestParam(required = false) StatusSolicitacaoAgua status,
            HttpServletRequest httpRequest) {
        Usuario usuario = usuarioAutenticado(httpRequest);
        boolean localidadeCompleta = usuario.getPerfil() == PerfilUsuario.LIDER_COMUNITARIO
                || usuario.getPerfil() == PerfilUsuario.GESTOR_PUBLICO
                || usuario.getPerfil() == PerfilUsuario.ADMIN;
        return solicitacaoAguaService.listar(usuario, status, localidadeCompleta);
    }

    @PutMapping("/solicitacoes-agua/{id}/status")
    public SolicitacaoAgua atualizarSolicitacao(@PathVariable Long id,
                                                @Valid @RequestBody StatusSolicitacaoRequest request,
                                                HttpServletRequest httpRequest) {
        return solicitacaoAguaService.atualizarStatus(id, usuarioAutenticado(httpRequest), request.status());
    }

    @PostMapping("/simulacoes/deficit-hidrico")
    public Map<String, Object> simularCenario(@Valid @RequestBody SimulacaoCenarioRequest request,
                                              @RequestParam(required = false) LocalDate dataPlantio,
                                              HttpServletRequest httpRequest) {
        Usuario usuario = usuarioAutenticado(httpRequest);
        return plantioService.simularCenario(
                usuario,
                dataPlantio,
                request.fatorChuva() == null ? 1.0 : request.fatorChuva(),
                valorOuZero(request.acrescimoPessoas()),
                valorOuZero(request.acrescimoBovinos()),
                valorOuZero(request.acrescimoCaprinosOvinos()),
                valorOuZero(request.acrescimoAves()),
                valorOuZero(request.acrescimoCavalosJumentos()),
                valorOuZero(request.acrescimoSuinos())
        );
    }

    @PostMapping("/safras")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroSafra iniciarSafra(@Valid @RequestBody RegistroSafraRequest request,
                                      HttpServletRequest httpRequest) {
        return registroSafraService.iniciar(usuarioAutenticado(httpRequest), request.culturaId(),
                request.dataPlantio(), request.observacao());
    }

    @GetMapping("/safras")
    public List<RegistroSafra> listarSafras(HttpServletRequest httpRequest) {
        return registroSafraService.listar(usuarioAutenticado(httpRequest).getId());
    }

    @PutMapping("/safras/{id}/resultado")
    public RegistroSafra registrarResultadoSafra(@PathVariable Long id,
                                                 @Valid @RequestBody ResultadoSafraRequest request,
                                                 HttpServletRequest httpRequest) {
        return registroSafraService.registrarResultado(usuarioAutenticado(httpRequest).getId(),
                id, request.resultado(), request.observacao());
    }

    @PostMapping("/safras/{id}/avaliar-risco")
    public Map<String, Object> avaliarRiscoSafra(@PathVariable Long id, HttpServletRequest httpRequest) {
        return registroSafraService.avaliarRisco(usuarioAutenticado(httpRequest).getId(), id);
    }

    private Usuario usuarioAutenticado(HttpServletRequest request) {
        Object usuario = request.getAttribute(com.manejocaatingueiro.infrastructure.security.AutenticacaoFilter.USUARIO_ATRIBUTO);
        if (usuario instanceof Usuario autenticado) {
            return autenticado;
        }
        throw new IllegalStateException("Contexto de autenticação não foi disponibilizado.");
    }

    private void validarLocalidadeDoUsuario(Integer localidadeId, Usuario usuario) {
        if (usuario.getLocalidadeId() == null || !usuario.getLocalidadeId().equals(localidadeId)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN,
                    "A operação só pode ser realizada na localidade cadastrada.");
        }
    }

    private int valorOuZero(Integer valor) {
        return valor == null ? 0 : valor;
    }

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("nome", "Manejo Caatingueiro");
        response.put("versao", "Java MVP");
        return response;
    }
}
