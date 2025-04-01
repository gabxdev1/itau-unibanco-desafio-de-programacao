package br.com.gabxdev.controller;

import br.com.gabxdev.exception.ApiError;
import br.com.gabxdev.mapper.TransacaoMapper;
import br.com.gabxdev.request.TransacaoPostRequest;
import br.com.gabxdev.response.EstatisticaGetResponse;
import br.com.gabxdev.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@Slf4j
public class TransacaoController {

    private final TransacaoService service;

    private final TransacaoMapper transacaoMapper;

    @PostMapping
    @Operation(
            summary = "Creates a new transaction",
            description = "This endpoint creates a new transaction. The transaction is saved to the database.",
            tags = {"Transactions"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction created successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request, with invalid transaction data or incorrect json.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Incorrect or invalid data.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<Void> postTransacao(
            @Parameter(description = "Details of the transaction to be created.", required = true, content = @Content(schema = @Schema(implementation = TransacaoPostRequest.class)))
            @Valid @RequestBody TransacaoPostRequest request) {
        log.debug("Post transacao request: {}", request);

        var newTransacao = transacaoMapper.toEntity(request);

        service.save(newTransacao);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    @Operation(
            summary = "Delete all transactions",
            description = "This endpoint deletes all transactions from the database.\n",
            tags = {"Transactions"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All transactions have been deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<Void> deleteAllTransacao() {
        log.debug("Delete transacao request");

        service.deleteAll();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/estatistica")
    @Operation(
            summary = "Transaction Statistics Report",
            description = "This endpoint returns statistics of transactions made in the last X seconds. If no parameter is provided, the default value is 60 seconds.",
            tags = {"Statistics"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistics report returned successfully.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EstatisticaGetResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid 'ultimosSegundos' parameter.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))
            )
    })
    public ResponseEntity<EstatisticaGetResponse> reportEstatisticaTransacao(
            @Parameter(description = "Number of seconds to filter transactions, with a default value of 60.")
            @RequestParam(required = false, defaultValue = "60") Long ultimosSegundos) {
        log.debug("Report estatistica request");

        var estatisticaGetResponse = service.reportEstatistica(ultimosSegundos);

        return ResponseEntity.ok(estatisticaGetResponse);
    }
}
