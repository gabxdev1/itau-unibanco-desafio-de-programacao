package br.com.gabxdev.controller;

import br.com.gabxdev.mapper.TransacaoMapper;
import br.com.gabxdev.request.TransacaoPostRequest;
import br.com.gabxdev.response.EstatisticaGetResponse;
import br.com.gabxdev.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacao")
@RequiredArgsConstructor
@Slf4j
public class TransacaoController {

    private final TransacaoService service;

    private final TransacaoMapper transacaoMapper;

    @PostMapping
    public ResponseEntity<Void> postTransacao(@Valid @RequestBody TransacaoPostRequest request) {
        log.debug("Post transacao request: {}", request);

        var newTransacao = transacaoMapper.toEntity(request);

        service.save(newTransacao);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTransacao() {
        log.debug("Delete transacao request");

        service.deleteAll();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/estatistica")
    public ResponseEntity<EstatisticaGetResponse> ReportEstatisticaTransacao(@RequestParam(required = false, defaultValue = "60") Long ultimosSegundos) {
        log.debug("Report estatistica request");

        var estatisticaGetResponse = service.reportEstatistica(ultimosSegundos);

        return ResponseEntity.ok(estatisticaGetResponse);
    }
}
