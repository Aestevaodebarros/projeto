package com.manejocaatingueiro;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.manejocaatingueiro.service.JwtTokenService;
import com.manejocaatingueiro.service.UsuarioService;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiLayerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private UsuarioService usuarioService;

    @Test
    void loginNaoDeveExporHashDaSenha() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"demo@manejocaatingueiro.org.br","senha":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.usuario.email").value("demo@manejocaatingueiro.org.br"))
                .andExpect(jsonPath("$.usuario.senhaHash").doesNotExist());
    }

    @Test
    void errosDeValidacaoRetornamRespostaPadronizada() throws Exception {
        mockMvc.perform(put("/api/v1/reservatorios/1/nivel")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDemo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nivelPercentual":101}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    void escritaSemTokenRetornaUnauthorized() throws Exception {
        mockMvc.perform(put("/api/v1/reservatorios/1/nivel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nivelPercentual":50}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioNaoPodeAlterarReservatorioDeOutraPropriedade() throws Exception {
        com.manejocaatingueiro.model.Usuario visitante = new com.manejocaatingueiro.model.Usuario();
        visitante.setNome("Visitante");
        visitante.setEmail("visitante-" + UUID.randomUUID() + "@test.com");
        visitante.setTelefone("75999990000");
        visitante.setSenhaHash("123456");
        visitante.setLocalidadeId(1);
        visitante = usuarioService.registrar(visitante);
        String token = jwtTokenService.criarToken(visitante);

        mockMvc.perform(put("/api/v1/reservatorios/1/nivel")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nivelPercentual":50}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void credenciaisInvalidasRetornamUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"demo@manejocaatingueiro.org.br","senha":"errada"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void jsonMalformadoRetornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("O corpo da requisição está ausente ou contém JSON inválido."));
    }

    @Test
    void emailDuplicadoRetornaConflict() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome":"Conta duplicada",
                                  "email":"demo@manejocaatingueiro.org.br",
                                  "telefone":"75999990000",
                                  "senha":"123456"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void localidadeInexistenteRetornaNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/localidades/999/diagnostico"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private String tokenDemo() {
        return jwtTokenService.criarToken(
                usuarioService.autenticar("demo@manejocaatingueiro.org.br", "123456")
        );
    }
}
