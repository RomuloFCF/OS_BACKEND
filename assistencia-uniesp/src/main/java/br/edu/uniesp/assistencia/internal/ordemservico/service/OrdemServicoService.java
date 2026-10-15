package br.edu.uniesp.assistencia.internal.ordemservico.service;

import br.edu.uniesp.assistencia.internal.cliente.entity.ClienteEntity;
import br.edu.uniesp.assistencia.internal.cliente.repository.ClienteRepository;
import br.edu.uniesp.assistencia.internal.equipamento.entity.EquipamentoEntity;
import br.edu.uniesp.assistencia.internal.equipamento.repository.EquipamentoRepository;
import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoRequest;
import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoResponse;
import br.edu.uniesp.assistencia.internal.ordemservico.entity.OrdemServicoEntity;
import br.edu.uniesp.assistencia.internal.ordemservico.mapper.OrdemServicoMapper;
import br.edu.uniesp.assistencia.internal.ordemservico.repository.OrdemServicoRepository;
import br.edu.uniesp.assistencia.shared.exception.RecursoNaoEncontradoException;
import br.edu.uniesp.assistencia.shared.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    @Transactional
    public CriarOrdemServicoResponse abrirOrdemServico(CriarOrdemServicoRequest request) {
        // 1. Verifica se o cliente existe (lança 404 com 2 argumentos se não existir)
        ClienteEntity cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", request.clienteId()));

        // 2. Verifica se o equipamento existe (lança 404 com 2 argumentos se não existir)
        EquipamentoEntity equipamento = equipamentoRepository.findById(request.equipamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento", request.equipamentoId()));

        // 3. Regra de Negócio: Equipamento deve pertencer obrigatoriamente ao cliente informado (422)
        if (!equipamento.getCliente().getId().equals(cliente.getId())) {
            throw new RegraNegocioException("O equipamento informado não pertence ao cliente informado.");
        }

        // 4. Gera protocolo único no padrão OS-YYYYMMDD-XXXX
        String numeroProtocolo = gerarNumeroProtocoloUnico();

        // 5. Instancia a OS usando o construtor da entidade
        // (O construtor já valida se cliente.isAtivo(), define status RECEBIDA, dataAbertura e cria o Histórico com "SISTEMA")
        OrdemServicoEntity novaOs = new OrdemServicoEntity(
                numeroProtocolo,
                cliente,
                equipamento,
                request.descricaoProblema()
        );

        OrdemServicoEntity osSalva = ordemServicoRepository.save(novaOs);

        return ordemServicoMapper.toCriarResponse(osSalva);
    }

    private String gerarNumeroProtocoloUnico() {
        String data = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD
        String protocolo;
        do {
            String sufixo = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            protocolo = "OS-" + data + "-" + sufixo;
        } while (ordemServicoRepository.existsByNumeroProtocolo(protocolo));

        return protocolo;
    }
}