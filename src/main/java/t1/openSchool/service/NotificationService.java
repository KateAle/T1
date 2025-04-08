package t1.openSchool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import t1.openSchool.aspect.annotation.LogException;
import t1.openSchool.aspect.annotation.LogExecution;
import t1.openSchool.aspect.annotation.LogExecutionTime;
import t1.openSchool.dto.TaskStatusChangeEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @LogExecution
    @LogExecutionTime
    @LogException
    public void sendTaskStatusChangeNotification(TaskStatusChangeEvent event) {
        String subject = "Изменение статуса задачи";
        String message = String.format(
                "Статус задачи #%d изменен на: %s",
                event.getTaskId(),
                event.getNewStatus()
        );

        sendEmail(event.getEmail(), subject, message);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("notifications@openschool.local");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            log.info("Email успешно отправлен на адрес: {}. Тема: {}", to, subject);
            log.debug("Содержание письма: {}", text);
        } catch (MailException e) {
            log.error("Ошибка отправки email на адрес {}: {}", to, e.getMessage());
            throw new RuntimeException("Не удалось отправить email", e);
        }
    }
}