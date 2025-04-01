package br.com.gabxdev.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ApiError(

        @Schema(description = "UTC date and time the error occurred.", example = "2025-04-01T00:47:40.1489206Z")
        OffsetDateTime timestamp,

        @Schema(description = "HTTP Status.", example = "400")
        int status,

        @Schema(description = "Error occurred.", example = "Bad Request")
        String error,

        @Schema(description = "Brief error reason message.", example = "JSON parse error")
        String message,

        @Schema(description = "Request path", example = "/transacao")
        String path
) {
}


