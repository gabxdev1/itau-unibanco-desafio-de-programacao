package br.com.gabxdev.service;

import br.com.gabxdev.commons.ClockProvider;
import br.com.gabxdev.commons.TransacaoUtils;
import br.com.gabxdev.model.Transacao;
import br.com.gabxdev.repository.TransacaoRepository;
import br.com.gabxdev.response.EstatisticaGetResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.DoubleStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService service;

    @Mock
    private TransacaoRepository repository;

    private List<Transacao> transacoes;

    @Mock
    private ClockProvider clockProvider;

    @Test
    @DisplayName("save creates a transacao")
    @Order(7)
    void save_CreatesTransacao_WhenSuccessful() {
        transacoes = new TransacaoUtils().newTransacaoList(OffsetDateTime.now());

        BDDMockito.given(repository.save(BDDMockito.any())).willReturn(transacoes.getFirst().withId(1L));

        service.save(transacoes.getFirst());
    }

    @Test
    @DisplayName("deleteAll delete all transactions when successful.")
    void deleteAll_DeleteAllTransacao_WhenSuccessful() {
        BDDMockito.willDoNothing().given(repository).deleteAll();
        service.deleteAll();
    }

    @Test
    @DisplayName("getEstatistica returns report with data from the last 60 seconds When successful.")
    void reportEstatistica_ReturnsReportWithDataFromTheLast60Seconds_WhenSuccessful() {
        var fixedNow = OffsetDateTime.now();

        var transacoes = new TransacaoUtils().newTransacaoList(fixedNow);

        var timeLimit = fixedNow.minusSeconds(60L);

        var doubleSummaryStatistics = transacoes.stream()
                .filter(t -> !t.getDataHora().isBefore(timeLimit))
                .flatMapToDouble(t -> DoubleStream.of(t.getValor().doubleValue()))
                .summaryStatistics();

        var response = EstatisticaGetResponse.builder()
                .count(doubleSummaryStatistics.getCount())
                .avg(doubleSummaryStatistics.getAverage())
                .sum(BigDecimal.valueOf(doubleSummaryStatistics.getSum()))
                .max(BigDecimal.valueOf(doubleSummaryStatistics.getMax()))
                .min(BigDecimal.valueOf(doubleSummaryStatistics.getMin()))
                .build();

        BDDMockito.when(repository.getEstatistica(BDDMockito.any())).thenReturn(response);
        BDDMockito.when(clockProvider.now()).thenReturn(OffsetDateTime.now());

        var estatisticaGetResponse = service.reportEstatistica(60L);

        Assertions.assertThat(estatisticaGetResponse)
                .isNotNull()
                .isEqualTo(response);
    }

    @Test
    @DisplayName("getEstatistica returns empty Object when no transacoes.")
    void getEstatistica_ReturnsEmptyObject_WhenNoTransacoes() {
        var response = EstatisticaGetResponse.builder()
                .count(0L)
                .sum(BigDecimal.ZERO)
                .avg(0D)
                .min(BigDecimal.ZERO)
                .max(BigDecimal.ZERO)
                .build();

        BDDMockito.when(repository.getEstatistica(BDDMockito.any())).thenReturn(EstatisticaGetResponse.builder().build().withCount(0L));
        BDDMockito.when(clockProvider.now()).thenReturn(OffsetDateTime.now());

        var estatisticas = service.reportEstatistica(60L);

        assertThat(estatisticas)
                .isNotNull()
                .isEqualTo(response);
    }
}