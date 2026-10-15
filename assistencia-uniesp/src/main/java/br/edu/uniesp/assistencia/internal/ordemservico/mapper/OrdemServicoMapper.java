package br.edu.uniesp.assistencia.internal.ordemservico.mapper;

import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoResponse;
import br.edu.uniesp.assistencia.internal.ordemservico.entity.OrdemServicoEntity;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoMapper {

    public CriarOrdemServicoResponse toCriarResponse(OrdemServicoEntity entity) {
        return new CriarOrdemServicoResponse(
                entity.getId(),
                entity.getNumeroProtocolo(),
                entity.getStatus(),
                entity.getDataAbertura()
        );
    }
}
