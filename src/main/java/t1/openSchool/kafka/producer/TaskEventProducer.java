package t1.openSchool.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import t1.openSchool.dto.TaskStatusChangeEvent;
import t1.openSchool.model.TaskStatus;

@Component
@RequiredArgsConstructor
public class TaskEventProducer {
    private final KafkaTemplate<String, TaskStatusChangeEvent> kafkaTemplate;
    @Value("${app.kafka.topics.task-status-changes}")
    private String topic;

    public void sendStatusChange(Long taskId, TaskStatus newStatus) {
        kafkaTemplate.send(topic, new TaskStatusChangeEvent(taskId, newStatus));
    }
}