package br.com.gabxdev.service;

import br.com.gabxdev.model.Transacao;
import br.com.gabxdev.repository.TransacaoRepository;
import br.com.gabxdev.response.EstatisticaGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository repository;

    public void save(Transacao transacao) {
        repository.save(transacao);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public EstatisticaGetResponse reportEstatistica(Long ultimosSegundos) {
        var estatisticas = repository.getEstatistica(OffsetDateTime.now().minusMinutes(ultimosSegundos));

        if (estatisticas.count() == 0) {
            return EstatisticaGetResponse.builder()
                    .count(0L)
                    .sum(BigDecimal.ZERO)
                    .avg(0D)
                    .min(BigDecimal.ZERO)
                    .max(BigDecimal.ZERO)
                    .build();
        }

        return estatisticas;
    }
}
