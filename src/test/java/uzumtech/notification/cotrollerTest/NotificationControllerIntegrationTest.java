package uzumtech.notification.cotrollerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.repository.MerchantRepository;
import uzumtech.notification.service.NotificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest

public class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private NotificationService notificationService;

    private NotificationMapper notificationMapper;

    private MerchantRepository merchantRepository;

    private Notification mockNotification(Long id) {
        Notification n = new Notification();
        n.setId(id);
        return n;
    }

    // -----------------------------------------------------------------------------
    // SEND NOTIFICATION (POST /api/notifications/send)
    // -----------------------------------------------------------------------------
    @Test
    void sendNotification_shouldReturn201() throws Exception {
        NotificationSendRequestDto requestDto =
                new NotificationSendRequestDto("merchant1", "Test message", "EMAIL");

        Notification mappedEntity = mockNotification(null);
        Notification savedEntity = mockNotification(1L);


        when(notificationMapper.toEntity(any(), any())).thenReturn(mappedEntity);


        when(notificationService.send(any())).thenReturn(savedEntity);

        mockMvc.perform(
                        post("/api/notifications/send")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").value(1L));
    }

    // -----------------------------------------------------------------------------
    // GET NOTIFICATION BY ID (GET /api/notifications/{id})
    // -----------------------------------------------------------------------------
    @Test
    void getNotification_shouldReturn200() throws Exception {
        Notification notification = mockNotification(5L);

        when(notificationService.findById(5L)).thenReturn(notification);

        mockMvc.perform(get("/api/notifications/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(5L));
    }
}
