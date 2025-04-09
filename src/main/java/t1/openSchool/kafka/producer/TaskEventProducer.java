package t1.openSchool.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import t1.openSchool.dto.TaskStatusChangeEvent;
import t1.openSchool.model.TaskStatus;

@Component
@RequiredArgsConstructor
public class TaskEventProducer {
    private final KafkaTemplate<String, TaskStatusChangeEvent> kafkaTemplate;
    private static final String TOPIC = "task-status-changes";

    public void sendStatusChange(Long taskId, TaskStatus newStatus) {
        kafkaTemplate.send(TOPIC, new TaskStatusChangeEvent(taskId, newStatus));
    }
}