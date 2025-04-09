package t1.openSchool.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import t1.openSchool.model.TaskStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final String SUBJECT = "Task Status Update";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JavaMailSender mailSender;

    @Value("${task.notification.email}")
    private String notificationEmail;

    public void sendStatusChangeNotification(Long taskId, TaskStatus newStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationEmail);
        message.setSubject(SUBJECT);
        message.setText(buildMessageContent(taskId, newStatus));

        mailSender.send(message);
    }

    private String buildMessageContent(Long taskId, TaskStatus newStatus) {
        return String.format(
                "Task #%d status has been changed to: %s\n" +
                        "Change time: %s\n\n" +
                        "This is an automated notification. Please do not reply.",
                taskId,
                newStatus,
                LocalDateTime.now().format(DATE_FORMATTER)
        );
    }
}