package com.manejocaatingueiro.controller;

import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.controller.dto.ChecklistRequest;
import com.manejocaatingueiro.controller.dto.LimiarRequest;
import com.manejocaatingueiro.controller.dto.LoginRequest;
import com.manejocaatingueiro.controller.dto.LoginResponse;
import com.manejocaatingueiro.controller.dto.NivelReservatorioRequest;
import com.manejocaatingueiro.controller.dto.RegistroUsuarioRequest;
import com.manejocaatingueiro.controller.dto.UsuarioResponse;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.service.ClimaService;
import com.manejocaatingueiro.service.ComunidadService;
import com.manejocaatingueiro.service.PlantioService;
import com.manejocaatingueiro.service.ReservatorioService;
import com.manejocaatingueiro.service.UsuarioService;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    private final ClimaService climaService;
    private final PlantioService plantioService;
    private final ReservatorioService reservatorioService;
    private final ComunidadService comunidadService;
    private final UsuarioService usuarioService;

    public ApiController(ClimaService climaService, PlantioService plantioService,
                        ReservatorioService reservatorioService, ComunidadService comunidadService,
                        UsuarioService usuarioService) {
        this.climaService = climaService;
        this.plantioService = plantioService;
        this.reservatorioService = reservatorioService;
        this.comunidadService = comunidadService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("java", "Spring Boot");
        health.put("modoOperacao", "MEMORIA_LOCAL");
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
        return new LoginResponse("jwt-demo-token-for-" + usuario.getId(), "Bearer", UsuarioResponse.de(usuario));
    }

    @GetMapping("/localidades")
    public List<?> localidades() {
        return climaService.listarLocalidades();
    }

    @GetMapping("/localidades/{id}/diagnostico")
    public Map<String, Object> diagnostico(@PathVariable Integer id) {
        return climaService.diagnostico(id);
    }

    @GetMapping("/culturas")
    public List<Cultura> culturas() {
        return plantioService.listarCulturas();
    }

    @PutMapping("/culturas/{id}/limiar-usuario")
    public Map<String, Object> calibrarLimiar(@PathVariable Integer id,
                                             @Valid @RequestBody LimiarRequest request) {
        return Map.of(
                "culturaId", id,
                "usuarioId", request.usuarioId(),
                "limiarMm", plantioService.calibrarLimiar(id, request.usuarioId(), request.limiarMm())
        );
    }

    @GetMapping("/autossuficiencia/{usuarioId}")
    public Map<String, Object> autossuficiencia(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        return plantioService.avaliarAptidao(usuario.getLocalidadeId(), usuario);
    }

    @GetMapping("/reservatorios")
    public List<Reservatorio> reservatorios(@RequestParam(required = false) Integer localidadeId) {
        if (localidadeId == null) {
            return reservatorioService.listarPorLocalidade(1);
        }
        return reservatorioService.listarPorLocalidade(localidadeId);
    }

    @PostMapping("/reservatorios")
    @ResponseStatus(HttpStatus.CREATED)
    public Reservatorio criarReservatorio(@RequestBody Reservatorio reservatorio) {
        return reservatorioService.salvar(reservatorio);
    }

    @PutMapping("/reservatorios/{id}/nivel")
    public Reservatorio atualizarNivel(@PathVariable Long id,
                                       @Valid @RequestBody NivelReservatorioRequest request) {
        return reservatorioService.atualizarNivel(id, request.nivelPercentual());
    }

    @PostMapping("/reservatorios/{id}/checklist")
    public Map<String, Object> checklist(@PathVariable Long id,
                                         @Valid @RequestBody ChecklistRequest request) {
        reservatorioService.registrarChecklist(id, request.item(), request.conforme());
        return Map.of("reservatorioId", id, "status", "registrado");
    }

    @GetMapping("/comunidade/avisos")
    public List<AvisoComunitario> avisos(@RequestParam(required = false) Integer localidadeId) {
        return comunidadService.listar(localidadeId);
    }

    @PostMapping("/comunidade/avisos")
    @ResponseStatus(HttpStatus.CREATED)
    public AvisoComunitario publicarAviso(@RequestBody AvisoComunitario aviso) {
        return comunidadService.publicar(aviso);
    }

    @GetMapping("/localidades/{id}/monitor-reservatorios")
    public Map<String, Object> monitorReservatorios(@PathVariable Integer id) {
        return comunidadService.monitorLocalidade(id);
    }

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("nome", "Manejo Caatingueiro");
        response.put("versao", "Java MVP");
        return response;
    }
}
