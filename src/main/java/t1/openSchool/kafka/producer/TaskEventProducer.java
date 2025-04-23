package t1.openSchool.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import t1.openSchool.dto.TaskStatusChangeEvent;
import t1.openSchool.model.TaskStatus;

@Component
public class TaskEventProducer {

    private final KafkaTemplate<String, TaskStatusChangeEvent> kafkaTemplate;
    private final String topic;

    public TaskEventProducer(
            KafkaTemplate<String, TaskStatusChangeEvent> kafkaTemplate,
            @Value("${app.kafka.topics.task-status-changes}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendStatusChange(Long taskId, TaskStatus newStatus) {
        TaskStatusChangeEvent event = new TaskStatusChangeEvent(taskId, newStatus);
        kafkaTemplate.send(topic, event);
    }
}