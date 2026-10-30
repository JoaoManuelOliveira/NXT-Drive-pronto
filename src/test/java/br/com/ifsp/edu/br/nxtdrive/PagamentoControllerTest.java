package br.com.ifsp.edu.br.nxtdrive;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoPixRequestDTO;
import br.com.ifsp.edu.br.nxtdrive.DTO.PagamentoResponseDTO;
import br.com.ifsp.edu.br.nxtdrive.service.PagamentoService;

@SpringBootTest
@AutoConfigureMockMvc
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoService pagamentoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Garante que POST /api/v1/pagamentos/pix recebe e retorna UUID para pedidoId")
    void deveCriarPagamentoPixComUuid() throws Exception {
        UUID pedidoUuid = UUID.randomUUID();
        PagamentoPixRequestDTO request = new PagamentoPixRequestDTO(
                new BigDecimal("120.00"), "teste@nxtdrive.com", "Pedido Teste", pedidoUuid);

        PagamentoResponseDTO mockResponse = new PagamentoResponseDTO(
                999888777L,
                pedidoUuid,
                "pending",
                "pending_waiting_transfer",
                "000201...",
                "base64img...",
                "Pix gerado com sucesso",
                new BigDecimal("120.00")
        );

        when(pagamentoService.processarPagamentoPix(any(PagamentoPixRequestDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/pagamentos/pix")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").value(pedidoUuid.toString()))
                .andExpect(jsonPath("$.paymentId").value(999888777L))
                .andExpect(jsonPath("$.status").value("pending"));
    }

    @Test
    @DisplayName("Garante que GET /api/v1/pagamentos/status/{pedidoId} consulta por UUID")
    void deveConsultarStatusPorUuidDoPedido() throws Exception {
        UUID pedidoUuid = UUID.randomUUID();

        PagamentoResponseDTO mockResponse = new PagamentoResponseDTO(
                123456789L,
                pedidoUuid,
                "approved",
                "accredited",
                null,
                null,
                "Aprovado",
                new BigDecimal("50.00")
        );

        when(pagamentoService.consultarStatusPorPedido(pedidoUuid)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/pagamentos/status/{pedidoId}", pedidoUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(pedidoUuid.toString()))
                .andExpect(jsonPath("$.paymentId").value(123456789L))
                .andExpect(jsonPath("$.status").value("approved"));
    }
}
