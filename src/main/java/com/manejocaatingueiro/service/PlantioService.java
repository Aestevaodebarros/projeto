package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.model.PerfilProdutivo;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.repository.PlantioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlantioService {
    private final PlantioRepository plantioRepository;

    public PlantioService(PlantioRepository plantioRepository) {
        this.plantioRepository = plantioRepository;
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
        if (usuario == null || usuario.getPerfilProdutivo() == null) {
            throw new IllegalArgumentException("Perfil produtivo da propriedade não cadastrado.");
        }

        PerfilProdutivo perfil = usuario.getPerfilProdutivo();
        double chuvaAcumulada = 320.0;
        double reservaUtil = 85000.0;
        double consumoDiario = (perfil.getNumeroPessoas() * 40.0)
                + (perfil.getBovinos() * 50.0)
                + (perfil.getCaprinosOvinos() * 6.0)
                + (perfil.getAves() * 0.3)
                + (perfil.getAreaIrrigadaM2() * 4.0);
        double autonomiaDias = reservaUtil / Math.max(consumoDiario, 1.0);

        List<Map<String, Object>> culturas = new ArrayList<>();
        for (Cultura cultura : plantioRepository.listarCulturas()) {
            double ofertaHidrica = chuvaAcumulada + (reservaUtil / 10000.0);
            double ratio = ofertaHidrica / cultura.getDemandaTotalMm();
            String status;
            String mensagem;
            if (ratio >= 1.0) {
                status = "VIAVEL";
                mensagem = "A oferta hídrica atual pode sustentar o ciclo; acompanhe as condições locais.";
            } else if (ratio >= 0.75) {
                status = "VIAVEL_COM_RESTRICAO";
                mensagem = "Pode exigir manejo cuidadoso da água e monitoramento da chuva.";
            } else {
                status = "INVIAVEL";
                mensagem = "A disponibilidade hídrica estimada está abaixo da demanda de referência.";
            }

            Map<String, Object> item = new HashMap<>();
            item.put("culturaId", cultura.getId());
            item.put("nome", cultura.getNome());
            item.put("status", status);
            item.put("autonomiaDias", arredondar(autonomiaDias));
            item.put("ofertaHidricaMm", arredondar(ofertaHidrica));
            item.put("demandaMm", cultura.getDemandaTotalMm());
            item.put("mensagem", mensagem);
            item.put("fonte", cultura.getFonte());
            item.put("atualizadoEm", Instant.now().toString());
            culturas.add(item);
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("localidadeId", localidadeId);
        resultado.put("usuarioId", usuario.getId());
        resultado.put("autonomiaDias", arredondar(autonomiaDias));
        resultado.put("status", autonomiaDias > 90 ? "SEGURO" : (autonomiaDias > 30 ? "ATENCAO" : "CRITICO"));
        resultado.put("culturas", culturas);
        resultado.put("fonteDadosAgronomicos", "Kc/ETc e climatologia da localidade");
        resultado.put("atualizadoEm", Instant.now().toString());
        return resultado;
    }

    private double arredondar(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }
}
