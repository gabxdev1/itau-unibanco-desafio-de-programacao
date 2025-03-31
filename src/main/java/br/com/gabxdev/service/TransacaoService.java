package br.com.gabxdev.service;

import br.com.gabxdev.model.Transacao;
import br.com.gabxdev.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository repository;

    public void save(Transacao transacao) {
        repository.save(transacao);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
