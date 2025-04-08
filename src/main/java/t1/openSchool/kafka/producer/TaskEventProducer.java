package t1.openSchool.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import t1.openSchool.aspect.annotation.LogExecution;
import t1.openSchool.aspect.annotation.LogException;
import t1.openSchool.aspect.annotation.LogExecutionTime;
import t1.openSchool.aspect.annotation.LogTracking;
import t1.openSchool.dto.TaskStatusChangeEvent;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskEventProducer {

    private static final String TOPIC = "task-status-changes";

    private final KafkaTemplate<String, TaskStatusChangeEvent> kafkaTemplate;

    @LogExecution
    @LogExecutionTime
    @LogTracking
    @LogException
    public void sendTaskStatusChangeEvent(Long taskId, String newStatus, String email) {
        if (taskId == null || newStatus == null || email == null) {
            log.warn("Attempt to send event with null parameters: taskId={}, newStatus={}, email={}",
                    taskId, newStatus, email);
            throw new IllegalArgumentException("Event parameters cannot be null");
        }

        TaskStatusChangeEvent event = new TaskStatusChangeEvent(taskId, newStatus, email);
        log.info("Preparing to send task status change event: {}", event);

        try {
            CompletableFuture<SendResult<String, TaskStatusChangeEvent>> future =
                    kafkaTemplate.send(TOPIC, String.valueOf(taskId), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Successfully sent event: {}", event);
                    log.debug("Metadata: topic={}, partition={}, offset={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send event: {}", event, ex);
                }
            });
        } catch (Exception e) {
            log.error("Unexpected error while sending event: {}", event, e);
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }
}