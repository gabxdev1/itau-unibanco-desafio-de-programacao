package br.com.gabxdev.commons;

import br.com.gabxdev.model.Transacao;
import br.com.gabxdev.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransacaoUtils {

    @Autowired
    private TransacaoRepository repository;


    public List<Transacao> newTransacaoList(OffsetDateTime fixedNow) {

        var transacao1 = Transacao.builder()
                .valor(BigDecimal.valueOf(1242.52))
                .dataHora(fixedNow.minusSeconds(30L))
                .build();

        var transacao2 = Transacao.builder()
                .valor(BigDecimal.valueOf(4242.33))
                .dataHora(fixedNow.minusYears(1L))
                .build();


        var transacao3 = Transacao.builder()
                .valor(BigDecimal.valueOf(242.52))
                .dataHora(fixedNow.minusSeconds(55L))
                .build();


        var transacao4 = Transacao.builder()
                .valor(BigDecimal.valueOf(1242.22))
                .dataHora(fixedNow.minusSeconds(61L))
                .build();

        var transacao5 = Transacao.builder()
                .valor(BigDecimal.valueOf(342.20))
                .dataHora(fixedNow.minusSeconds(59L))
                .build();

        return new ArrayList<>(List.of(transacao1, transacao2, transacao3, transacao4, transacao5));
    }

    public void initTransacaoData(List<Transacao> transacoes) {
        repository.saveAll(transacoes);
    }


}
