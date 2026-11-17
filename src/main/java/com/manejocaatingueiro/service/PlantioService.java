package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.model.ClimatologiaMensal;
import com.manejocaatingueiro.model.CalculoAutossuficiencia;
import com.manejocaatingueiro.model.ResultadoViabilidade;
import com.manejocaatingueiro.model.SugestaoPlantio;
import com.manejocaatingueiro.model.ParametroAgronomico;
import com.manejocaatingueiro.model.SerieHistoricaClimatica;
import com.manejocaatingueiro.model.SimulacaoDeficitHidrico;
import com.manejocaatingueiro.model.FaseCultura;
import com.manejocaatingueiro.model.PerfilProdutivo;
import com.manejocaatingueiro.model.ParametrosConsumo;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.repository.ClimaRepository;
import com.manejocaatingueiro.repository.PlantioRepository;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.Instant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlantioService {
    private final PlantioRepository plantioRepository;
    private final ClimaRepository climaRepository;
    private final ReservatorioRepository reservatorioRepository;
    private final ParametrosConsumoService parametrosConsumoService;

    public PlantioService(PlantioRepository plantioRepository, ClimaRepository climaRepository,
                         ReservatorioRepository reservatorioRepository,
                         ParametrosConsumoService parametrosConsumoService) {
        this.plantioRepository = plantioRepository;
        this.climaRepository = climaRepository;
        this.reservatorioRepository = reservatorioRepository;
        this.parametrosConsumoService = parametrosConsumoService;
    }

    public List<Cultura> listarCulturas() {
        return plantioRepository.listarCulturas();
    }

    public double calibrarLimiar(Integer culturaId, Long usuarioId, Double limiarMm) {
        if (culturaId == null || plantioRepository.buscarCultura(culturaId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Cultura não encontrada.");
        }
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("Usuário inválido.");
        }
        if (limiarMm == null || !Double.isFinite(limiarMm) || limiarMm <= 0) {
            throw new IllegalArgumentException("Limiar em milímetros deve ser maior que zero.");
        }
        plantioRepository.salvarLimiar(culturaId, usuarioId, limiarMm);
        return limiarMm;
    }

    public Map<String, Object> avaliarAptidao(Integer localidadeId, Usuario usuario) {
        return avaliarAptidao(localidadeId, usuario, LocalDate.now(ZoneId.of("America/Sao_Paulo")));
    }

    public Map<String, Object> avaliarAptidao(Integer localidadeId, Usuario usuario, LocalDate dataPlantio) {
        if (usuario == null || usuario.getPerfilProdutivo() == null) {
            throw new IllegalArgumentException("Perfil produtivo da propriedade não cadastrado.");
        }
        if (localidadeId == null || climaRepository.buscarLocalidade(localidadeId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Localidade não encontrada.");
        }
        LocalDate dataPlantioConsiderada = dataPlantio == null
                ? LocalDate.now(ZoneId.of("America/Sao_Paulo"))
                : dataPlantio;

        PerfilProdutivo perfil = usuario.getPerfilProdutivo();
        validarPerfilProdutivo(perfil);
        ParametrosConsumo parametros = parametrosConsumoService.consultar();
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        double consumoDiario = (perfil.getNumeroPessoas() * perfil.getConsumoHumanoLitrosPessoaDia())
                + (perfil.getBovinos() * parametros.getBovinosLitrosDia())
                + (perfil.getCaprinosOvinos() * parametros.getCaprinosOvinosLitrosDia())
                + (perfil.getAves() * parametros.getAvesLitrosDia())
                + (perfil.getCavalosJumentos() * parametros.getEquinosLitrosDia())
                + (perfil.getSuinos() * parametros.getSuinosLitrosDia())
                + (perfil.getAreaIrrigadaM2() * parametros.getIrrigacaoLitrosM2Dia());
        double consumoLiquido = consumoDiario - perfil.getEntradasContinuasLitrosDia();
        double reservaUtil = reservatorioRepository.listarPorUsuario(usuario.getId()).stream()
                .mapToDouble(this::calcularVolumeUtilLitros)
                .sum();
        Double autonomiaDias = consumoLiquido > 0 ? reservaUtil / consumoLiquido : null;
        String statusAutonomia = consumoLiquido <= 0 || autonomiaDias > 120 ? "SEGURO"
                : autonomiaDias >= 60 ? "ATENCAO" : "CRITICO";

        CalculoAutossuficiencia calculo = new CalculoAutossuficiencia();
        calculo.setUsuarioId(usuario.getId());
        calculo.setLocalidadeId(localidadeId);
        calculo.setReservaUtilLitros(reservaUtil);
        calculo.setConsumoDiarioLitros(consumoDiario);
        calculo.setEntradasContinuasLitrosDia(perfil.getEntradasContinuasLitrosDia());
        calculo.setConsumoLiquidoDiarioLitros(Math.max(consumoLiquido, 0));
        calculo.setAutonomiaDias(autonomiaDias);
        calculo.setStatusHidrico(statusAutonomia);
        calculo.setFonteParametrosConsumo(parametros.getFonte());
        calculo.setVersaoParametrosConsumo(parametros.getVersao());
        calculo.setDataCalculo(Instant.now());

        List<Map<String, Object>> culturas = new ArrayList<>();
        Map<Integer, ClimatologiaMensal> normais = new HashMap<>();
        climaRepository.listarClimatologia(localidadeId).forEach(normal -> normais.put(normal.getMes(), normal));
        boolean climatologiaSuficiente = normais.size() == 12 && normais.values().stream()
                .allMatch(normal -> normal.getAnosBase() != null && normal.getAnosBase() >= 10);
        List<Precipitacao> chuvasObservadas = climaRepository.listarPrecipitacoes(localidadeId).stream()
                .filter(item -> item.getData() != null && item.getVolumeMm() != null)
                .filter(item -> !"DEMONSTRACAO".equals(item.getFonte()))
                .filter(item -> !item.getData().isAfter(hoje.atTime(23, 59).atZone(ZoneId.of("America/Sao_Paulo")).toInstant()))
                .toList();
        for (Cultura cultura : plantioRepository.listarCulturas()) {
            LocalDate fimCiclo = dataPlantioConsiderada.plusDays(cultura.getCicloDias() - 1L);
            double chuvaAcumulada = chuvasObservadas.stream()
                    .filter(item -> !item.getData().isBefore(dataPlantioConsiderada
                            .atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant()))
                    .mapToDouble(Precipitacao::getVolumeMm)
                    .sum();
            Double chuvaEsperada = climatologiaSuficiente
                    ? calcularChuvaEsperada(dataPlantioConsiderada.isAfter(hoje)
                    ? dataPlantioConsiderada : hoje.plusDays(1), fimCiclo, normais)
                    : null;
            double areaCultivada = perfil.getAreaIrrigadaM2();
            double aguaReservadaMm = areaCultivada > 0 ? reservaUtil / areaCultivada : 0;
            double demandaMm = calcularDemanda(cultura);
            boolean dadosInsuficientes = !climatologiaSuficiente || chuvaEsperada == null || chuvasObservadas.isEmpty();
            Double ofertaHidrica = dadosInsuficientes
                    ? null
                    : chuvaAcumulada + chuvaEsperada + aguaReservadaMm;
            Double indiceViabilidade = ofertaHidrica == null ? null : ofertaHidrica / demandaMm;
            String status = dadosInsuficientes ? "DADOS_INSUFICIENTES"
                    : indiceViabilidade >= 1.0 ? "VIAVEL"
                    : indiceViabilidade >= 0.7 ? "VIAVEL_COM_RESTRICAO" : "INVIAVEL";
            String mensagem = switch (status) {
                case "VIAVEL" -> "A estimativa indica condição favorável; acompanhe a chuva e decida conforme a realidade da roça.";
                case "VIAVEL_COM_RESTRICAO" -> "A estimativa indica restrição; considere reduzir proporcionalmente a área, se fizer sentido para sua propriedade.";
                case "INVIAVEL" -> "A oferta estimada está abaixo da referência; avalie alternativas sem que isso limite sua decisão.";
                default -> "Ainda não há série climática confiável suficiente para estimar a viabilidade desta cultura.";
            };

            int anosBaseMinimos = normais.values().stream()
                    .map(ClimatologiaMensal::getAnosBase)
                    .filter(java.util.Objects::nonNull)
                    .min(Integer::compareTo).orElse(0);
            Double mediaHistorica = normais.values().stream()
                    .map(ClimatologiaMensal::getPrecipitacaoMediaMm)
                    .filter(java.util.Objects::nonNull)
                    .mapToDouble(Double::doubleValue).average().stream().boxed().findFirst().orElse(null);
            SerieHistoricaClimatica serie = new SerieHistoricaClimatica();
            serie.setLocalidadeId(localidadeId);
            serie.setMediaHistorica(mediaHistorica);
            serie.setChuvaAcumulada(chuvaAcumulada);
            serie.setChuvaEsperada(chuvaEsperada);
            serie.setAnosBase(anosBaseMinimos);
            serie.setDadosSuficientes(climatologiaSuficiente && !chuvasObservadas.isEmpty());
            serie.setFonte(climatologiaSuficiente ? "Open-Meteo Archive (10 anos)" : "Climatologia indisponível");
            serie.setDataAtualizacao(Instant.now());

            ResultadoViabilidade resultadoViabilidade = new ResultadoViabilidade();
            resultadoViabilidade.setCulturaId(cultura.getId());
            resultadoViabilidade.setCulturaNome(cultura.getNome());
            resultadoViabilidade.setOfertaHidricaMm(ofertaHidrica);
            resultadoViabilidade.setDemandaCulturaMm(demandaMm);
            resultadoViabilidade.setIndiceViabilidade(indiceViabilidade);
            resultadoViabilidade.setClassificacao(status);
            resultadoViabilidade.setDadosInsuficientes(dadosInsuficientes);
            resultadoViabilidade.setChuvaAcumuladaMm(chuvaAcumulada);
            resultadoViabilidade.setChuvaEsperadaMm(chuvaEsperada);
            resultadoViabilidade.setAguaReservadaMm(aguaReservadaMm);
            resultadoViabilidade.setDataPlantio(dataPlantioConsiderada);
            resultadoViabilidade.setFonteClimatologica(serie.getFonte());
            resultadoViabilidade.setFonteAgronomica(cultura.getFonte());
            resultadoViabilidade.setDataCalculo(Instant.now());

            SugestaoPlantio sugestao = new SugestaoPlantio();
            sugestao.setUsuarioId(usuario.getId());
            sugestao.setCulturaId(cultura.getId());
            sugestao.setRecomendacao(status);
            sugestao.setGrauConfianca(dadosInsuficientes ? "INSUFICIENTE" : "MEDIA");
            sugestao.setJustificativa(mensagem);
            sugestao.setFonte(cultura.getFonte() + "; " + serie.getFonte());
            sugestao.setDataGeracao(Instant.now());

            Map<String, Object> item = new HashMap<>();
            item.put("culturaId", cultura.getId());
            item.put("nome", cultura.getNome());
            item.put("status", status);
            item.put("ofertaHidricaMm", ofertaHidrica == null ? null : arredondar(ofertaHidrica));
            item.put("chuvaAcumuladaDesdePlantioMm", arredondar(chuvaAcumulada));
            item.put("chuvaEsperadaNoCicloMm", chuvaEsperada == null ? null : arredondar(chuvaEsperada));
            item.put("aguaReservadaEquivalenteMm", arredondar(aguaReservadaMm));
            item.put("demandaMm", arredondar(demandaMm));
            item.put("indiceViabilidade", indiceViabilidade == null ? null : arredondar(indiceViabilidade));
            item.put("dadosInsuficientes", dadosInsuficientes);
            item.put("dataPlantioConsiderada", dataPlantioConsiderada.toString());
            item.put("fonteClimatologica", climatologiaSuficiente ? "Open-Meteo Archive (10 anos)" : "Climatologia indisponível");
            item.put("mensagem", mensagem);
            item.put("grauConfianca", sugestao.getGrauConfianca());
            item.put("fonte", cultura.getFonte());
            item.put("versaoParametros", cultura.getVersaoParametros());
            item.put("serieHistoricaClimatica", serie);
            item.put("resultadoViabilidade", resultadoViabilidade);
            item.put("sugestaoPlantio", sugestao);
            item.put("atualizadoEm", Instant.now().toString());
            culturas.add(item);
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("localidadeId", localidadeId);
        resultado.put("usuarioId", usuario.getId());
        resultado.put("reservaUtilLitros", arredondar(reservaUtil));
        resultado.put("consumoDiarioLitros", arredondar(consumoDiario));
        resultado.put("entradasContinuasLitrosDia", arredondar(perfil.getEntradasContinuasLitrosDia()));
        resultado.put("consumoLiquidoDiarioLitros", arredondar(Math.max(consumoLiquido, 0)));
        resultado.put("autonomiaDias", autonomiaDias == null ? null : arredondar(autonomiaDias));
        resultado.put("autonomiaIndeterminada", autonomiaDias == null);
        resultado.put("statusAutonomia", statusAutonomia);
        resultado.put("status", statusAutonomia);
        resultado.put("dadosClimaticosInsuficientes", !climatologiaSuficiente || chuvasObservadas.isEmpty());
        resultado.put("fonteParametrosConsumo", parametros.getFonte());
        resultado.put("versaoParametrosConsumo", parametros.getVersao());
        resultado.put("calculoAutossuficiencia", calculo);
        resultado.put("culturas", culturas);
        resultado.put("fonteDadosAgronomicos", "Kc/ETc por cultura; duração das fases provisória até validação técnica (RNF11)");
        resultado.put("atualizadoEm", Instant.now().toString());
        return resultado;
    }

    public Map<String, Object> simularCenario(Usuario usuario, LocalDate dataPlantio,
                                               Double fatorChuva, int acrescimoPessoas,
                                               int acrescimoBovinos, int acrescimoCaprinosOvinos,
                                               int acrescimoAves, int acrescimoCavalosJumentos,
                                               int acrescimoSuinos) {
        if (acrescimoPessoas < 0 || acrescimoBovinos < 0 || acrescimoCaprinosOvinos < 0
                || acrescimoAves < 0 || acrescimoCavalosJumentos < 0 || acrescimoSuinos < 0
                || fatorChuva == null || !Double.isFinite(fatorChuva) || fatorChuva < 0 || fatorChuva > 2) {
            throw new IllegalArgumentException("Os parâmetros do cenário estão fora dos limites permitidos.");
        }
        Map<String, Object> base = avaliarAptidao(usuario.getLocalidadeId(), usuario, dataPlantio);
        PerfilProdutivo perfil = usuario.getPerfilProdutivo();
        ParametrosConsumo parametros = parametrosConsumoService.consultar();
        double consumoAdicional = acrescimoPessoas * perfil.getConsumoHumanoLitrosPessoaDia()
                + acrescimoBovinos * parametros.getBovinosLitrosDia()
                + acrescimoCaprinosOvinos * parametros.getCaprinosOvinosLitrosDia()
                + acrescimoAves * parametros.getAvesLitrosDia()
                + acrescimoCavalosJumentos * parametros.getEquinosLitrosDia()
                + acrescimoSuinos * parametros.getSuinosLitrosDia();
        double novoConsumoLiquido = Math.max(0,
                (double) base.get("consumoDiarioLitros") + consumoAdicional
                        - perfil.getEntradasContinuasLitrosDia());
        double reserva = (double) base.get("reservaUtilLitros");
        Double autonomia = novoConsumoLiquido > 0 ? reserva / novoConsumoLiquido : null;
        String statusAutonomia = novoConsumoLiquido <= 0 || autonomia > 120 ? "SEGURO"
                : autonomia >= 60 ? "ATENCAO" : "CRITICO";

        List<Map<String, Object>> culturasSimuladas = new ArrayList<>();
        Object culturasOriginais = base.get("culturas");
        if (culturasOriginais instanceof List<?> lista) {
            for (Object item : lista) {
                if (!(item instanceof Map<?, ?> cultura)) {
                    continue;
                }
                Map<String, Object> simulacao = new HashMap<>();
                cultura.forEach((chave, valor) -> simulacao.put(String.valueOf(chave), valor));
                Object chuvaAcumulada = cultura.get("chuvaAcumuladaDesdePlantioMm");
                Object chuvaEsperada = cultura.get("chuvaEsperadaNoCicloMm");
                Object aguaReservada = cultura.get("aguaReservadaEquivalenteMm");
                Object indiceBase = cultura.get("indiceViabilidade");
                if (chuvaAcumulada instanceof Number acumulada
                        && chuvaEsperada instanceof Number esperada
                        && aguaReservada instanceof Number reservaMm
                        && indiceBase instanceof Number) {
                    double oferta = (acumulada.doubleValue() + esperada.doubleValue()) * fatorChuva
                            + reservaMm.doubleValue();
                    double demanda = ((Number) cultura.get("demandaMm")).doubleValue();
                    double indice = oferta / demanda;
                    simulacao.put("ofertaHidricaMm", arredondar(oferta));
                    simulacao.put("indiceViabilidade", arredondar(indice));
                    simulacao.put("status", classificarViabilidade(indice));
                }
                culturasSimuladas.add(simulacao);
            }
        }
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("simulacao", true);
        resultado.put("fatorChuva", fatorChuva);
        resultado.put("consumoAdicionalLitrosDia", arredondar(consumoAdicional));
        resultado.put("consumoLiquidoDiarioLitros", arredondar(novoConsumoLiquido));
        resultado.put("autonomiaDias", autonomia == null ? null : arredondar(autonomia));
        resultado.put("statusAutonomia", statusAutonomia);
        SimulacaoDeficitHidrico simulacaoTipada = new SimulacaoDeficitHidrico();
        simulacaoTipada.setUsuarioId(usuario.getId());
        simulacaoTipada.setVariacaoConsumo(consumoAdicional);
        simulacaoTipada.setVariacaoOferta(fatorChuva);
        simulacaoTipada.setAutonomiaSimulada(autonomia);
        simulacaoTipada.setResultado(statusAutonomia);
        simulacaoTipada.setDataSimulacao(Instant.now());
        resultado.put("simulacaoDeficitHidrico", simulacaoTipada);
        resultado.put("culturas", culturasSimuladas);
        resultado.put("aviso", "Cenário hipotético e consultivo; não altera seu cadastro nem determina sua decisão.");
        resultado.put("atualizadoEm", Instant.now().toString());
        return resultado;
    }

    private double calcularVolumeUtilLitros(Reservatorio reservatorio) {
        if (reservatorio.getNivelPercentual() == null) {
            return 0;
        }
        if (reservatorio.getCapacidadeLitros() != null && reservatorio.getCapacidadeLitros() > 0) {
            return reservatorio.getCapacidadeLitros() * reservatorio.getNivelPercentual() / 100.0;
        }
        if ("POCO".equalsIgnoreCase(reservatorio.getTipo())
                && reservatorio.getDiametroM() != null && reservatorio.getDiametroM() > 0
                && reservatorio.getProfundidadeTotalM() != null && reservatorio.getProfundidadeTotalM() > 0
                && reservatorio.getProfundidadeLençolM() != null) {
            double laminaAguaM = Math.max(0,
                    reservatorio.getProfundidadeTotalM() - reservatorio.getProfundidadeLençolM());
            return Math.PI * Math.pow(reservatorio.getDiametroM() / 2.0, 2) * laminaAguaM * 1000.0;
        }
        return 0;
    }

    private double calcularDemanda(Cultura cultura) {
        if (cultura.getFases() == null || cultura.getFases().isEmpty()) {
            return cultura.getDemandaTotalMm();
        }
        if (cultura.getEtoReferenciaMmDia() == null || !Double.isFinite(cultura.getEtoReferenciaMmDia())
                || cultura.getEtoReferenciaMmDia() <= 0) {
            return cultura.getDemandaTotalMm();
        }
        BigDecimal demandaTotal = BigDecimal.ZERO;
        int diasCobertos = 0;
        for (ParametroAgronomico parametro : mapearParametrosAgronomicos(cultura)) {
            if (parametro.getKc() == null || parametro.getDuracaoFase() == null
                    || parametro.getKc() <= 0 || parametro.getDuracaoFase() <= 0
                    || parametro.getEto() == null || parametro.getEto() <= 0) {
                return cultura.getDemandaTotalMm();
            }
            BigDecimal demandaFase = BigDecimal.valueOf(parametro.getEto())
                    .multiply(BigDecimal.valueOf(parametro.getKc()))
                    .multiply(BigDecimal.valueOf(parametro.getDuracaoFase()));
            demandaTotal = demandaTotal.add(demandaFase);
            diasCobertos += parametro.getDuracaoFase();
        }
        return diasCobertos >= cultura.getCicloDias()
                ? demandaTotal.doubleValue()
                : cultura.getDemandaTotalMm();
    }

    private List<ParametroAgronomico> mapearParametrosAgronomicos(Cultura cultura) {
        List<ParametroAgronomico> parametros = new ArrayList<>();
        int indice = 1;
        for (FaseCultura fase : cultura.getFases()) {
            ParametroAgronomico parametro = new ParametroAgronomico();
            parametro.setIdParametro(indice++);
            parametro.setCulturaId(cultura.getId());
            parametro.setFase(fase.getNome());
            parametro.setKc(fase.getKc());
            parametro.setEto(cultura.getEtoReferenciaMmDia());
            parametro.setDuracaoFase(fase.getDuracaoDias());
            if (fase.getKc() != null && fase.getDuracaoDias() != null && cultura.getEtoReferenciaMmDia() != null) {
                parametro.setNecessidadeHidricaMm(fase.getKc() * cultura.getEtoReferenciaMmDia() * fase.getDuracaoDias());
            }
            parametro.setFonte(cultura.getFonte());
            parametro.setVersao(cultura.getVersaoParametros());
            parametros.add(parametro);
        }
        return parametros;
    }

    private Double calcularChuvaEsperada(LocalDate inicio, LocalDate fim,
                                         Map<Integer, ClimatologiaMensal> normais) {
        if (inicio.isAfter(fim)) {
            return 0.0;
        }
        double total = 0;
        LocalDate cursor = inicio;
        while (!cursor.isAfter(fim)) {
            YearMonth mes = YearMonth.from(cursor);
            LocalDate limiteMes = mes.atEndOfMonth().isBefore(fim) ? mes.atEndOfMonth() : fim;
            boolean estacaoChuvosa = mes.getMonthValue() >= 11 || mes.getMonthValue() <= 4;
            if (estacaoChuvosa) {
                ClimatologiaMensal normal = normais.get(mes.getMonthValue());
                if (normal == null || normal.getPrecipitacaoMediaMm() == null) {
                    return null;
                }
                long diasNoPeriodo = java.time.temporal.ChronoUnit.DAYS.between(cursor, limiteMes) + 1;
                total += normal.getPrecipitacaoMediaMm() * diasNoPeriodo / mes.lengthOfMonth();
            }
            cursor = limiteMes.plusDays(1);
        }
        return total;
    }

    private void validarPerfilProdutivo(PerfilProdutivo perfil) {
        if (perfil.getNumeroPessoas() < 0 || perfil.getBovinos() < 0 || perfil.getCaprinosOvinos() < 0
                || perfil.getAves() < 0 || perfil.getAreaIrrigadaM2() < 0
                || perfil.getCavalosJumentos() < 0 || perfil.getSuinos() < 0
                || !Double.isFinite(perfil.getConsumoHumanoLitrosPessoaDia())
                || perfil.getConsumoHumanoLitrosPessoaDia() < 5
                || perfil.getConsumoHumanoLitrosPessoaDia() > 100
                || !Double.isFinite(perfil.getEntradasContinuasLitrosDia())
                || perfil.getEntradasContinuasLitrosDia() < 0) {
            throw new IllegalArgumentException("Perfil produtivo contém valores inválidos para consumo ou quantidade.");
        }
    }

    private double arredondar(double valor) {
        return BigDecimal.valueOf(valor).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private String classificarViabilidade(double indice) {
        return indice >= 1.0 ? "VIAVEL" : indice >= 0.7 ? "VIAVEL_COM_RESTRICAO" : "INVIAVEL";
    }
}
