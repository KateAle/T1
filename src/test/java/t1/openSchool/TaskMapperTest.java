package t1.openSchool;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import t1.openSchool.dto.TaskDto;
import t1.openSchool.mapper.TaskMapper;
import t1.openSchool.model.Task;
import t1.openSchool.model.TaskStatus;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {
    @InjectMocks
    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void shouldMapTaskToTaskDto() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUserId(100L);
        task.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto taskDto = taskMapper.taskToTaskDto(task);

        assertThat(taskDto.getId()).isEqualTo(1L);
        assertThat(taskDto.getTitle()).isEqualTo("Test Task");
        assertThat(taskDto.getDescription()).isEqualTo("Test Description");
        assertThat(taskDto.getUserId()).isEqualTo(100L);
        assertThat(taskDto.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void shouldMapTaskDtoToTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setUserId(100L);
        taskDto.setStatus(TaskStatus.IN_PROGRESS);

        Task task = taskMapper.taskDtoToTask(taskDto);

        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getTitle()).isEqualTo("Test Task");
        assertThat(task.getDescription()).isEqualTo("Test Description");
        assertThat(task.getUserId()).isEqualTo(100L);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void shouldReturnNullWhenTaskIsNull() {
        assertThat(taskMapper.taskToTaskDto(null)).isNull();
        assertThat(taskMapper.taskDtoToTask(null)).isNull();
    }
}