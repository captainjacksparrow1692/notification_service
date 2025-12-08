package uzumtech.notification.cotrollerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.controller.NotificationController;
import uzumtech.notification.dto.NotificationResponseDto;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.service.NotificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private NotificationMapper notificationMapper;

    @Test
    void sendNotification_shouldReturn201() throws Exception {
        NotificationSendRequestDto requestDto = NotificationSendRequestDto.builder()
                .title("Test title")
                .body("Test body")
                .merchantId(1L)
                .receiver("user@example.com")
                .type(NotificationType.EMAIL)
                .build();

        Notification saved = new Notification();
        saved.setId(1L);
        when(notificationService.sendFromDto(any())).thenReturn(saved);

        mockMvc.perform(
                        post("/api/notifications/send")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").value(1L));
    }

    @Test
    void getNotification_shouldReturn200() throws Exception {
        Notification notification = new Notification();
        notification.setId(5L);
        NotificationResponseDto responseDto = NotificationResponseDto.builder().id(5L).build();

        when(notificationService.findById(5L)).thenReturn(notification);
        when(notificationMapper.toResponseDto(notification)).thenReturn(responseDto);

        mockMvc.perform(get("/api/notifications/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(5L));
    }
}
