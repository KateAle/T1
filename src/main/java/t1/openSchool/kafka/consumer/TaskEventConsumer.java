package t1.openSchool.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import t1.openSchool.aspect.annotation.HandlingResult;
import t1.openSchool.aspect.annotation.LogExecution;
import t1.openSchool.aspect.annotation.LogException;
import t1.openSchool.aspect.annotation.LogExecutionTime;
import t1.openSchool.dto.TaskStatusChangeEvent;
import t1.openSchool.service.NotificationService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "task-status-changes", groupId = "task-notification-group")
    @LogExecution
    @LogExecutionTime
    @LogException
    @HandlingResult
    public void consumeTaskStatusChange(TaskStatusChangeEvent event) {
        log.info("Received Task Status Change Event: {}", event);
        notificationService.sendTaskStatusChangeNotification(event);
    }
}