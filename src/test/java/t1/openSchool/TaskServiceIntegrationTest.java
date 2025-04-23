package t1.openSchool;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import t1.openSchool.dto.TaskDto;
import t1.openSchool.dto.TaskStatusChangeEvent;
import t1.openSchool.model.TaskStatus;
import t1.openSchool.repository.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(topics = {"task-status-changes"})
public class TaskServiceIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @MockBean
    private KafkaTemplate<String, TaskStatusChangeEvent> kafkaTemplate;

    private TaskDto createTestTaskDto() {
        TaskDto dto = new TaskDto();
        dto.setTitle("Test Task");
        dto.setDescription("Test Description");
        dto.setUserId(1L);
        dto.setStatus(TaskStatus.CREATED);
        return dto;
    }

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    public void shouldCreateTaskSuccessfully() throws Exception {
        TaskDto request = createTestTaskDto();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.status").value("CREATED"));

        assertThat(taskRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldReturnBadRequestWhenCreatingInvalidTask() throws Exception {
        TaskDto invalidRequest = createTestTaskDto();
        invalidRequest.setTitle("");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"));

        assertThat(taskRepository.count()).isZero();
    }

    @Test
    public void shouldGetTaskByIdSuccessfully() throws Exception {
        TaskDto created = createTaskViaApi(createTestTaskDto());

        mockMvc.perform(get("/tasks/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.title").value(created.getTitle()));
    }

    @Test
    public void shouldReturnNotFoundWhenTaskNotExists() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateTaskSuccessfully() throws Exception {
        TaskDto created = createTaskViaApi(createTestTaskDto());
        TaskDto updateRequest = createTestTaskDto();
        updateRequest.setTitle("Updated Title");
        updateRequest.setStatus(TaskStatus.IN_PROGRESS);

        mockMvc.perform(put("/tasks/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(kafkaTemplate, timeout(5000)).send(eq("task-status-changes"), any());
    }

    @Test
    public void shouldNotSendKafkaEventWhenStatusNotChanged() throws Exception {
        TaskDto created = createTaskViaApi(createTestTaskDto());
        TaskDto updateRequest = createTestTaskDto();
        updateRequest.setTitle("Updated Title");

        mockMvc.perform(put("/tasks/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(kafkaTemplate, timeout(5000).times(0)).send(any(), any());
    }

    @Test
    public void shouldReturnNotFoundWhenUpdatingNonExistingTask() throws Exception {
        mockMvc.perform(put("/tasks/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTestTaskDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteTaskSuccessfully() throws Exception {
        TaskDto created = createTaskViaApi(createTestTaskDto());

        mockMvc.perform(delete("/tasks/{id}", created.getId()))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(created.getId())).isFalse();
    }

    @Test
    public void shouldReturnNoContentWhenDeletingNonExistingTask() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", 999L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldGetAllTasksSuccessfully() throws Exception {
        createTaskViaApi(createTestTaskDto());
        createTaskViaApi(createTestTaskDto());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void shouldReturnEmptyListWhenNoTasksExist() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private TaskDto createTaskViaApi(TaskDto request) throws Exception {
        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, TaskDto.class);
    }
}