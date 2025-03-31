package br.com.gabxdev.repository;

import br.com.gabxdev.commons.TransacaoUtils;
import br.com.gabxdev.config.TestcontainersConfig;
import br.com.gabxdev.response.EstatisticaGetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.DoubleStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, TransacaoUtils.class})
@ActiveProfiles("testIT")
@Sql(scripts = "/sql/transacao/reset_table_transacao.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TransacaoRepositoryIT {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private TransacaoUtils transacaoUtils;

    @Test
    @DisplayName("getEstatistica returns empty Object when no transacoes.")
    void getEstatistica_ReturnsEmptyObject_WhenNoTransacoes() {
        var estatisticas = repository.getEstatistica(OffsetDateTime.now().minusYears(10L));

        assertThat(estatisticas.count())
                .isNotNull()
                .isZero();
    }

    @Test
    @DisplayName("getEstatistica returns report with data from the last 60 seconds When successful.")
    void getEstatistica_ReturnsReportWithDataFromTheLast60Seconds_WhenSuccessful() {
        var fixedNow = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        var transacoes = transacaoUtils.newTransacaoList(fixedNow);

        transacaoUtils.initTransacaoData(transacoes);

        var timeLimit = fixedNow.minusSeconds(60L);

        var doubleSummaryStatistics = transacoes.stream()
                .filter(t -> !t.getDataHora().isBefore(timeLimit))
                .flatMapToDouble(t -> DoubleStream.of(t.getValor().doubleValue()))
                .summaryStatistics();

        var expectedReport = EstatisticaGetResponse.builder()
                .count(doubleSummaryStatistics.getCount())
                .avg(doubleSummaryStatistics.getAverage())
                .sum(BigDecimal.valueOf(doubleSummaryStatistics.getSum()))
                .max(BigDecimal.valueOf(doubleSummaryStatistics.getMax()))
                .min(BigDecimal.valueOf(doubleSummaryStatistics.getMin()))
                .build();

        var resultReport = repository.getEstatistica(timeLimit);

        assertThat(resultReport).isEqualTo(expectedReport);
    }
}