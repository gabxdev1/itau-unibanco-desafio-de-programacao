package br.com.gabxdev.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;

@Builder
@With
public record EstatisticaGetResponse(

        @Schema(description = "Number of transactions.", example = "10")
        Long count,

        @Schema(description = "Sum of transaction amounts.", example = "3212.55")
        BigDecimal sum,

        @Schema(description = "Average amount of the transfers <3212.55/10>", example = "321,255")
        Double avg,

        @Schema(description = "Min", example = "321,255")
        BigDecimal min,

        @Schema(description = "Max", example = "321,255")
        BigDecimal max
) {
}
