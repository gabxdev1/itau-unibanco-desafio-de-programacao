package br.com.gabxdev.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "Transaction amount.", example = "32.14", minimum = "0")
        BigDecimal valor,

        @NotNull(message = "The field 'dataHora' is required")
        @Past(message = "The transaction MUST NOT happen in the future")
        @Schema(description = "Transaction date and time.", example = "2020-08-07T12:34:56.789-03:00")
        OffsetDateTime dataHora
) {
}
