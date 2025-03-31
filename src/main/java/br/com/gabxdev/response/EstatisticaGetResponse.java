package br.com.gabxdev.response;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Builder
public record EstatisticaGetResponse(
        Long count,

        BigDecimal sum,

        Double avg,

        BigDecimal min,

        BigDecimal max
) {
}
