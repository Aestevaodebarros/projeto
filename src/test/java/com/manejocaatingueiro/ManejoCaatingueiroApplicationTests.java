package com.manejocaatingueiro;

import com.manejocaatingueiro.model.PerfilProdutivo;
import com.manejocaatingueiro.model.PerfilUsuario;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.service.PlantioService;
import com.manejocaatingueiro.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ManejoCaatingueiroApplicationTests {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PlantioService plantioService;

    @Test
    void contextLoads() {
        assertThat(usuarioService).isNotNull();
        assertThat(plantioService).isNotNull();
    }

    @Test
    void deveRegistrarEAutenticarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setEmail("joao@test.com");
        usuario.setTelefone("75988776655");
        usuario.setPerfil(PerfilUsuario.AGRICULTOR);
        usuario.setPerfilProdutivo(new PerfilProdutivo(6, 3, 4, 8, 500));
        usuario.setSenhaHash("123456");

        Usuario salvo = usuarioService.registrar(usuario);
        Usuario autenticado = usuarioService.autenticar("joao@test.com", "123456");

        assertThat(salvo.getId()).isNotNull();
        assertThat(autenticado.getEmail()).isEqualTo("joao@test.com");
    }

    @Test
    void deveCalcularAutonomiaEViabilidade() {
        Usuario usuario = new Usuario();
        usuario.setId(99L);
        usuario.setLocalidadeId(1);
        usuario.setPerfilProdutivo(new PerfilProdutivo(5, 2, 2, 10, 300));

        var resultado = plantioService.avaliarAptidao(1, usuario);
        assertThat(resultado).containsKeys("autonomiaDias", "calculoAutossuficiencia");
        assertThat(resultado.get("calculoAutossuficiencia"))
                .isInstanceOf(com.manejocaatingueiro.model.CalculoAutossuficiencia.class);
        assertThat(resultado.get("culturas")).isNotNull();
        java.util.Map<?, ?> cultura = (java.util.Map<?, ?>) ((java.util.List<?>) resultado.get("culturas")).get(0);
        assertThat(java.util.List.of("resultadoViabilidade", "sugestaoPlantio", "serieHistoricaClimatica"))
                .allMatch(cultura::containsKey);
    }
}
