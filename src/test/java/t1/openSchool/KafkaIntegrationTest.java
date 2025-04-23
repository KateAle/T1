package t1.openSchool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import t1.openSchool.dto.TaskStatusChangeEvent;
import t1.openSchool.kafka.consumer.TaskEventConsumer;
import t1.openSchool.kafka.producer.TaskEventProducer;
import t1.openSchool.model.TaskStatus;
import t1.openSchool.service.NotificationService;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        topics = "${app.kafka.topics.task-status-changes}",
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@ActiveProfiles("test")
public class KafkaIntegrationTest {

    @Autowired
    private TaskEventProducer taskEventProducer;

    @Autowired
    private KafkaTemplate<String, TaskStatusChangeEvent> kafkaTemplate;

    @SpyBean
    private TaskEventConsumer taskEventConsumer;

    @SpyBean
    private NotificationService notificationService;

    @Test
    void whenSendMessage_thenConsumerReceivesIt() {
        Long taskId = 1L;
        TaskStatus newStatus = TaskStatus.COMPLETED;
        taskEventProducer.sendStatusChange(taskId, newStatus);
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(notificationService, times(1))
                            .sendStatusChangeNotification(taskId, newStatus);
                });
    }
}