package br.com.gabxdev.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record TransacaoPostRequest(

        @NotNull(message = "The field 'valor' is required")
        @PositiveOrZero(message = "The transaction MUST NOT have a negative value")
        BigDecimal valor,

        @NotNull(message = "The field 'dataHora' is required")
        @Past(message = "The transaction MUST NOT happen in the future")
        OffsetDateTime dataHora
) {
}
