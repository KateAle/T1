package t1.openSchool.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import t1.openSchool.dto.TaskStatusChangeEvent;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TaskEventDeserializer extends JsonDeserializer<TaskStatusChangeEvent> {

    @Override
    public TaskStatusChangeEvent deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.error("Failed to deserialize message: {}",
                    data != null ? new String(data, StandardCharsets.UTF_8) : "null",
                    e);
            return null;
        }
    }

    @Override
    public TaskStatusChangeEvent deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.error("Failed to deserialize message with headers: {}",
                    data != null ? new String(data, StandardCharsets.UTF_8) : "null",
                    e);
            return null;
        }
    }
}