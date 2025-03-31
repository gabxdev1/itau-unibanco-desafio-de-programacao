package br.com.gabxdev.mapper;

import br.com.gabxdev.model.Transacao;
import br.com.gabxdev.request.TransacaoPostRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransacaoMapper {

    Transacao toEntity(TransacaoPostRequest transacaoPostRequest);
}
