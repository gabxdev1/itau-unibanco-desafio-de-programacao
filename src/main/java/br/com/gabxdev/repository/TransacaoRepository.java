package br.com.gabxdev.repository;


import br.com.gabxdev.model.Transacao;
import br.com.gabxdev.response.EstatisticaGetResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("""
            SELECT new br.com.gabxdev.response.EstatisticaGetResponse(COUNT(t.id), SUM(t.valor), AVG(t.valor), MIN(t.valor), MAX(t.valor))
            FROM Transacao t
            WHERE t.dataHora >= :dataHora
            """)
    EstatisticaGetResponse getEstatistica(@Param("dataHora") OffsetDateTime dataHora);
}
