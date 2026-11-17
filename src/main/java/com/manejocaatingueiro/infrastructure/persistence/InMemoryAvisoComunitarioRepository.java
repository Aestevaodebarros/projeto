package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.repository.AvisoComunitarioRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAvisoComunitarioRepository implements AvisoComunitarioRepository {
    private final CopyOnWriteArrayList<AvisoComunitario> avisos = new CopyOnWriteArrayList<>();
    private final AtomicLong sequencia = new AtomicLong(2);

    public InMemoryAvisoComunitarioRepository() {
        avisos.add(new AvisoComunitario(
                1L,
                1,
                "Mutirão de limpeza",
                "Amanhã, a partir das 8h, mutirão para limpar a área da cisterna comunitária.",
                "Líder comunitário"
        ));
    }

    @Override
    public List<AvisoComunitario> listarPorLocalidade(Integer localidadeId) {
        return avisos.stream()
                .filter(aviso -> localidadeId == null || localidadeId.equals(aviso.getLocalidadeId()))
                .sorted(Comparator.comparing(AvisoComunitario::getCriadoEm).reversed())
                .toList();
    }

    @Override
    public AvisoComunitario salvar(AvisoComunitario aviso) {
        if (aviso.getId() == null) {
            aviso.setId(sequencia.getAndIncrement());
        } else {
            sequencia.accumulateAndGet(aviso.getId() + 1, Math::max);
        }
        avisos.add(aviso);
        return aviso;
    }
}
