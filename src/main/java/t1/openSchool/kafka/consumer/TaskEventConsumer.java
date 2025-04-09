package t1.openSchool.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import t1.openSchool.dto.TaskStatusChangeEvent;
import t1.openSchool.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "task-status-changes", groupId = "task-notification-group")
    public void handleStatusChange(TaskStatusChangeEvent event,
                                   Acknowledgment acknowledgment) {
        try {
            notificationService.sendStatusChangeNotification(
                    event.taskId(),
                    event.newStatus()
            );
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }
}