package t1.openSchool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import t1.openSchool.model.TaskStatus;
import t1.openSchool.service.NotificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldSendStatusChangeNotification() {
        notificationService.sendStatusChangeNotification(1L, TaskStatus.COMPLETED);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}