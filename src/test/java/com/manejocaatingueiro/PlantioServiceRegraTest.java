package com.manejocaatingueiro;

import com.manejocaatingueiro.model.ClimatologiaMensal;
import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.model.FaseCultura;
import com.manejocaatingueiro.model.PerfilProdutivo;
import com.manejocaatingueiro.model.ParametrosConsumo;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.repository.ClimaRepository;
import com.manejocaatingueiro.repository.PlantioRepository;
import com.manejocaatingueiro.repository.ParametrosConsumoRepository;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import com.manejocaatingueiro.service.PlantioService;
import com.manejocaatingueiro.service.ParametrosConsumoService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlantioServiceRegraTest {
    @Test
    void calculaAutonomiaComReservaEConsumoLiquidoDoPerfil() {
        PlantioRepository plantio = mock(PlantioRepository.class);
        ClimaRepository clima = mock(ClimaRepository.class);
        ReservatorioRepository reservatorios = mock(ReservatorioRepository.class);
        ParametrosConsumoService parametros = parametros();
        when(clima.buscarLocalidade(1)).thenReturn(Optional.of(new com.manejocaatingueiro.model.Localidade()));
        when(clima.listarClimatologia(1)).thenReturn(List.of());
        when(clima.listarPrecipitacoes(1)).thenReturn(List.of());
        when(plantio.listarCulturas()).thenReturn(List.of(new Cultura(1, "Milho", 650.0, 400.0, "fonte", 120)));
        when(reservatorios.listarPorUsuario(7L)).thenReturn(List.of(reservatorio(1000, 50)));

        PerfilProdutivo perfil = new PerfilProdutivo(2, 1, 0, 0, 0);
        perfil.setConsumoHumanoLitrosPessoaDia(15);
        perfil.setEntradasContinuasLitrosDia(20);
        Usuario usuario = usuario(7, perfil);

        var resultado = new PlantioService(plantio, clima, reservatorios, parametros)
                .avaliarAptidao(1, usuario);

        assertThat((Double) resultado.get("reservaUtilLitros")).isEqualTo(500);
        assertThat((Double) resultado.get("consumoDiarioLitros")).isEqualTo(81.5);
        assertThat((Double) resultado.get("autonomiaDias")).isEqualTo(8.1);
        assertThat(resultado.get("statusAutonomia")).isEqualTo("CRITICO");
        java.util.Map<?, ?> cultura = (java.util.Map<?, ?>) ((List<?>) resultado.get("culturas")).get(0);
        assertThat(cultura.get("status")).isEqualTo("DADOS_INSUFICIENTES");
    }

    @Test
    void calculaDemandaETcKcQuandoClimatologiaTemDezAnos() {
        PlantioRepository plantio = mock(PlantioRepository.class);
        ClimaRepository clima = mock(ClimaRepository.class);
        ReservatorioRepository reservatorios = mock(ReservatorioRepository.class);
        ParametrosConsumoService parametros = parametros();
        when(clima.buscarLocalidade(1)).thenReturn(Optional.of(new com.manejocaatingueiro.model.Localidade()));
        when(clima.listarClimatologia(1)).thenReturn(java.util.stream.IntStream.rangeClosed(1, 12)
                .mapToObj(mes -> {
                    ClimatologiaMensal normal = new ClimatologiaMensal();
                    normal.setLocalidadeId(1);
                    normal.setMes(mes);
                    normal.setAnosBase(10);
                    normal.setPrecipitacaoMediaMm(100.0);
                    normal.setFonte("Open-Meteo");
                    normal.setAtualizadoEm(Instant.now());
                    return normal;
                }).toList());
        when(clima.listarPrecipitacoes(1)).thenReturn(List.of(
                new Precipitacao(1L, 1, 25.0, "PLUVIOMETRO_PROPRIO", Instant.now())
        ));
        Cultura milho = new Cultura(1, "Milho", 650.0, 400.0, "Souza et al. (2015)", 120);
        milho.setFases(List.of(
                new FaseCultura("Inicial", 0.86, 25),
                new FaseCultura("Desenvolvimento", 1.23, 35),
                new FaseCultura("Meio", 0.97, 35),
                new FaseCultura("Final", 0.52, 25)
        ));
        when(plantio.listarCulturas()).thenReturn(List.of(milho));
        when(reservatorios.listarPorUsuario(7L)).thenReturn(List.of(reservatorio(50000, 50)));

        var resultado = new PlantioService(plantio, clima, reservatorios, parametros)
                .avaliarAptidao(1, usuario(7, new PerfilProdutivo(2, 0, 0, 0, 1000)),
                        LocalDate.now().minusDays(1));
        java.util.Map<?, ?> cultura = (java.util.Map<?, ?>) ((List<?>) resultado.get("culturas")).get(0);

        assertThat((Double) cultura.get("demandaMm")).isEqualTo(702.5);
        assertThat(cultura.get("dadosInsuficientes")).isEqualTo(false);
        assertThat(cultura.get("indiceViabilidade")).isNotNull();
    }

    private Usuario usuario(long id, PerfilProdutivo perfil) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setLocalidadeId(1);
        usuario.setPerfilProdutivo(perfil);
        return usuario;
    }

    private Reservatorio reservatorio(double capacidade, double nivel) {
        Reservatorio reservatorio = new Reservatorio();
        reservatorio.setId(1L);
        reservatorio.setUsuarioResponsavelId(7L);
        reservatorio.setCapacidadeLitros(capacidade);
        reservatorio.setNivelPercentual(nivel);
        reservatorio.setLocalidadeId(1);
        return reservatorio;
    }

    private ParametrosConsumoService parametros() {
        return new ParametrosConsumoService(new ParametrosConsumoRepository() {
            private final ParametrosConsumo padrao = new ParametrosConsumo();

            @Override
            public Optional<ParametrosConsumo> buscar() {
                return Optional.of(padrao);
            }

            @Override
            public ParametrosConsumo salvar(ParametrosConsumo parametros) {
                return parametros;
            }
        });
    }
}
