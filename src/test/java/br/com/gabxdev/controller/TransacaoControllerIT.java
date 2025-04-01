package br.com.gabxdev.controller;

import br.com.gabxdev.commons.ClockProvider;
import br.com.gabxdev.commons.Constants;
import br.com.gabxdev.commons.TransacaoUtils;
import br.com.gabxdev.config.RestAssuredConfig;
import br.com.gabxdev.config.TestcontainersConfig;
import br.com.gabxdev.exception.ApiError;
import br.com.gabxdev.model.Transacao;
import br.com.gabxdev.repository.TransacaoRepository;
import br.com.gabxdev.request.TransacaoPostRequest;
import br.com.gabxdev.response.EstatisticaGetResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfig.class, RestAssuredConfig.class, TransacaoUtils.class})
@ActiveProfiles("testIT")
@Sql(scripts = "/sql/transacao/reset_table_transacao.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TransacaoControllerIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RequestSpecification requestSpecification;

    @Autowired
    private TransacaoUtils transacaoUtils;

    @Autowired
    private TransacaoRepository repository;

    @MockitoBean
    private ClockProvider clockProvider;

    @BeforeEach
    void setUp() {
        RestAssured.requestSpecification = requestSpecification;
    }

    @Test
    @DisplayName("POST /transacao creates a transacao when successful.")
    void postTransacao_CreatesTransacao_WhenSuccessful() throws JsonProcessingException {
        var transacaoToSave = transacaoUtils.newTransacaoList(OffsetDateTime.now()).getFirst();

        var expectedTransacaoSaved = transacaoToSave.withId(1L);

        var transacaoToSaveJson = objectMapper.writeValueAsString(transacaoToSave);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(transacaoToSaveJson)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value());

        var savedTransacaoOpt = repository.findById(expectedTransacaoSaved.getId());

        assertTrue(savedTransacaoOpt.isPresent());
    }

    @Test
    @DisplayName("DELETE /transacao delete all transactions when successful")
    void deleteAllTransacao_Delete_WhenSuccessful() {
        transacaoUtils.initTransacaoData(transacaoUtils.newTransacaoList(OffsetDateTime.now()));

        RestAssured.given()
                .when()
                .delete()
                .then()
                .statusCode(HttpStatus.OK.value());

        var allTransaction = repository.findAll();

        assertTrue(allTransaction.isEmpty());
    }

    @Test
    @DisplayName("GET /estatistica returns transactions from the last 60 seconds when not given parameter")
    void reportEstatisticaTransacao_ReturnsTransactionsFromTheLast60Seconds_WhenNotGivenParameter() throws JsonProcessingException {
        var fixedNow = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        var transacoes = transacaoUtils.newTransacaoList(fixedNow);
        transacaoUtils.initTransacaoData(transacoes);

        var timeLimit = fixedNow.minusSeconds(60L);

        var doubleSummaryStatistics = getSummaryStatisticsByTransactionsList(transacoes, timeLimit);

        var expectedReport = EstatisticaGetResponse.builder()
                .count(doubleSummaryStatistics.getCount())
                .avg(doubleSummaryStatistics.getAverage())
                .sum(BigDecimal.valueOf(doubleSummaryStatistics.getSum()))
                .max(BigDecimal.valueOf(doubleSummaryStatistics.getMax()))
                .min(BigDecimal.valueOf(doubleSummaryStatistics.getMin()))
                .build();

        var expectedReportJson = objectMapper.writeValueAsString(expectedReport);

        BDDMockito.when(clockProvider.now()).thenReturn(fixedNow);

        RestAssured.given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/estatistica")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .body(Matchers.equalTo(expectedReportJson));
    }

    private DoubleSummaryStatistics getSummaryStatisticsByTransactionsList(List<Transacao> transacoes, OffsetDateTime timeLimit) {
        return transacoes.stream()
                .filter(t -> !t.getDataHora().isBefore(timeLimit))
                .flatMapToDouble(t -> DoubleStream.of(t.getValor().doubleValue()))
                .summaryStatistics();
    }

    @ParameterizedTest
    @MethodSource("postTransacaoUnprocessableEntitySource")
    @DisplayName("POST /transacao throws Unprocessable Entity when fields are invalid or data invalid.")
    void postTransacao_ThrowsUnprocessableEntity_WhenFieldsAreInvalidOrDataInvalid(TransacaoPostRequest transacaoToSave, List<String> errorsMessages) throws Exception {
        var transacaoToSaveJson = objectMapper.writeValueAsString(transacaoToSave);

        var expectedBodyError = ApiError.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .timestamp(OffsetDateTime.now())
                .message(String.join(", ", errorsMessages))
                .path(Constants.TRANSACAO_PATH)
                .build();

        var responseBody = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(transacaoToSaveJson)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .extract().body().asString();

        JsonAssertions.assertThatJson(responseBody)
                .whenIgnoringPaths("timestamp", "message")
                .isEqualTo(expectedBodyError);

        JsonAssertions.assertThatJson(responseBody)
                .inPath("$.message")
                .asString()
                .contains(errorsMessages);
    }

    private static Stream<Arguments> postTransacaoUnprocessableEntitySource() {
        var dataHoraRequiredError = "The field 'dataHora' is required";
        var valorRequiredError = "The field 'valor' is required";
        var dateTimeFutureError = "The transaction MUST NOT happen in the future";
        var negativeAmountError = "The transaction MUST NOT have a negative value";

        return Stream.of(
                Arguments.of(TransacaoPostRequest.builder().build(), List.of(dataHoraRequiredError, valorRequiredError)),
                Arguments.of(TransacaoPostRequest.builder().valor(BigDecimal.TWO).dataHora(OffsetDateTime.now().plusMinutes(1L)).build(), List.of(dateTimeFutureError)),
                Arguments.of(TransacaoPostRequest.builder().valor(BigDecimal.valueOf(-1L)).dataHora(OffsetDateTime.now().minusMinutes(1L)).build(), List.of(negativeAmountError))
        );
    }
}