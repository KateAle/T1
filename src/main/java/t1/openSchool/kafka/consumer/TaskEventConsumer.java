package t1.openSchool.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import t1.openSchool.dto.TaskStatusChangeEvent;
import t1.openSchool.service.NotificationService;

@Component
@RequiredArgsConstructor
public class TaskEventConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "task-status-changes", groupId = "task-notification-group")
    public void handleStatusChange(TaskStatusChangeEvent event) {
        notificationService.sendStatusChangeNotification(
                event.taskId(),
                event.newStatus()
        );
    }
}