package br.com.gabxdev.response;

import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;

@Builder
@With
public record EstatisticaGetResponse(
        Long count,

        BigDecimal sum,

        Double avg,

        BigDecimal min,

        BigDecimal max
) {
}
