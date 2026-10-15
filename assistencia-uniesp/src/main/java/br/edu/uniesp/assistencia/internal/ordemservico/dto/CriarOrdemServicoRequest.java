package br.edu.uniesp.assistencia.internal.ordemservico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarOrdemServicoRequest(
        @NotNull(message = "O clienteId é obrigatório")
        Long clienteId,

        @NotNull(message = "O equipamentoId é obrigatório")
        Long equipamentoId,

        @NotBlank(message = "A descrição do problema é obrigatória")
        String descricaoProblema
) {
}
