package br.com.gabxdev.controller;

import br.com.gabxdev.commons.Constants;
import br.com.gabxdev.mapper.TransacaoMapperImpl;
import br.com.gabxdev.repository.TransacaoRepository;
import br.com.gabxdev.request.TransacaoPostRequest;
import br.com.gabxdev.service.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static br.com.gabxdev.commons.Constants.TRANSACAO_PATH;


@WebMvcTest(controllers = TransacaoController.class)
@Import({TransacaoMapperImpl.class, TransacaoService.class, TransacaoRepository.class})
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private TransacaoService service;

    @MockitoBean
    private TransacaoRepository repository;

    @ParameterizedTest
    @MethodSource("postTransacaoUnprocessableEntitySource")
    @DisplayName("POST /transacao Throws Unprocessable Entity when fields are invalid or data invalid.")
    void postTransacao_ThrowsUnprocessableEntity_WhenFieldsAreInvalidOrDataInvalid(TransacaoPostRequest transacaoToSave, List<String> errorsMessages) throws Exception {
        var transacaoToSaveJson = mapper.writeValueAsString(transacaoToSave);

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(TRANSACAO_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transacaoToSaveJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull().isInstanceOf(MethodArgumentNotValidException.class);

        Assertions.assertThat(resolvedException.getMessage()).contains(errorsMessages);
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