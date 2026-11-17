package com.manejocaatingueiro;

import com.manejocaatingueiro.model.Sincronizacao;
import com.manejocaatingueiro.repository.SincronizacaoRepository;
import com.manejocaatingueiro.service.SincronizacaoService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SincronizacaoServiceTest {
    @Test
    void registraOperacaoEmFilaPersistenteDePendencias() {
        SincronizacaoRepository repository = mock(SincronizacaoRepository.class);
        when(repository.buscarPorUsuario(42L)).thenReturn(Optional.empty());
        when(repository.salvar(any(Sincronizacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SincronizacaoService service = new SincronizacaoService(repository);
        Sincronizacao estado = service.registrarPendencia(42L, "reservatorio", "ATUALIZAR_NIVEL", "7");

        assertThat(estado.getUsuarioId()).isEqualTo(42L);
        assertThat(estado.getIdSincronizacao()).isEqualTo(42L);
        assertThat(estado.getStatus()).isEqualTo("PENDENTE_SINCRONIZACAO_EXTERNA");
        assertThat(estado.getQuantidadePendencias()).isEqualTo(1);
        assertThat(estado.getPendencias().get(0).getEntidade()).isEqualTo("reservatorio");
        assertThat(estado.getPendencias().get(0).getOperacao()).isEqualTo("ATUALIZAR_NIVEL");
    }
}
