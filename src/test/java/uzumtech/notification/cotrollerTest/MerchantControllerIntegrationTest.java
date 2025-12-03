package uzumtech.notification.cotrollerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uzumtech.notification.dto.merchant.MerchantCreateRequestDto;
import uzumtech.notification.dto.merchant.MerchantResponseDto;
import uzumtech.notification.dto.merchant.MerchantUpdateRequestDto;
import uzumtech.notification.service.MerchantService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@uzumtech.notification.cotrollerTest.AutoConfigureMockMvc
public class MerchantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MerchantService merchantService;

    private MerchantResponseDto mockMerchant() {
        MerchantResponseDto dto = new MerchantResponseDto();
        dto.setId(1L);
        dto.setLogin("merchant1");
        dto.setEmail("merchant@mail.com");
        dto.setName("Merchant Company");
        return dto;
    }

    // --------------------------------------------------------------
    // CREATE
    // --------------------------------------------------------------
    @Test
    void createMerchant_shouldReturn201() throws Exception {
        MerchantCreateRequestDto request = new MerchantCreateRequestDto(
                "merchant1", "merchant@mail.com", "Merchant Company"
        );

        Mockito.when(merchantService.create(any())).thenReturn(mockMerchant());

        mockMvc.perform(post("/api/merchants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.login").value("merchant1"));
    }

    // --------------------------------------------------------------
    // GET BY ID
    // --------------------------------------------------------------
    @Test
    void getMerchantById_shouldReturn200() throws Exception {
        Mockito.when(merchantService.findById(1L)).thenReturn(mockMerchant());

        mockMvc.perform(get("/api/merchants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("merchant@mail.com"));
    }

    // --------------------------------------------------------------
    // GET BY LOGIN
    // --------------------------------------------------------------
    @Test
    void getMerchantByLogin_shouldReturn200() throws Exception {
        Mockito.when(merchantService.findByLogin("merchant1")).thenReturn(mockMerchant());

        mockMvc.perform(get("/api/merchants/login/merchant1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.login").value("merchant1"));
    }

    // --------------------------------------------------------------
    // GET BY EMAIL
    // --------------------------------------------------------------
    @Test
    void getMerchantByEmail_shouldReturn200() throws Exception {
        Mockito.when(merchantService.findByEmail("merchant@mail.com")).thenReturn(mockMerchant());

        mockMvc.perform(get("/api/merchants/email/merchant@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("merchant@mail.com"));
    }

    // --------------------------------------------------------------
    // GET ALL
    // --------------------------------------------------------------
    @Test
    void getAllMerchants_shouldReturn200() throws Exception {
        Mockito.when(merchantService.findAll()).thenReturn(List.of(mockMerchant()));

        mockMvc.perform(get("/api/merchants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].login").value("merchant1"));
    }

    // --------------------------------------------------------------
    // UPDATE
    // --------------------------------------------------------------
    @Test
    void updateMerchant_shouldReturn200() throws Exception {
        MerchantUpdateRequestDto request = new MerchantUpdateRequestDto(
                "newMerchant", "new@mail.com", "New Company"
        );

        MerchantResponseDto updated = mockMerchant();
        updated.setLogin("newMerchant");
        updated.setEmail("new@mail.com");

        Mockito.when(merchantService.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/merchants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.login").value("newMerchant"));
    }

    // --------------------------------------------------------------
    // DELETE
    // --------------------------------------------------------------
    @Test
    void deleteMerchant_shouldReturn204() throws Exception {
        Mockito.doNothing().when(merchantService).delete(1L);

        mockMvc.perform(delete("/api/merchants/1"))
                .andExpect(status().isNoContent());
    }

    // --------------------------------------------------------------
    // HEAD EXISTS
    // --------------------------------------------------------------
    @Test
    void checkMerchantExists_shouldReturn200() throws Exception {
        Mockito.when(merchantService.exists(1L)).thenReturn(true);

        mockMvc.perform(head("/api/merchants/1"))
                .andExpect(status().isOk());
    }

    @Test
    void checkMerchantExists_shouldReturn404() throws Exception {
        Mockito.when(merchantService.exists(99L)).thenReturn(false);

        mockMvc.perform(head("/api/merchants/99"))
                .andExpect(status().isNotFound());
    }
}
