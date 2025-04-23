package t1.openSchool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import t1.openSchool.dto.TaskDto;
import t1.openSchool.kafka.producer.TaskEventProducer;
import t1.openSchool.mapper.TaskMapper;
import t1.openSchool.model.Task;
import t1.openSchool.model.TaskStatus;
import t1.openSchool.repository.TaskRepository;
import t1.openSchool.service.TaskService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskEventProducer taskEventProducer;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTask() {
        TaskDto inputDto = new TaskDto();
        inputDto.setTitle("Test Task");

        Task task = new Task();
        task.setTitle("Test Task");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");

        TaskDto outputDto = new TaskDto();
        outputDto.setId(1L);
        outputDto.setTitle("Test Task");

        when(taskMapper.taskDtoToTask(inputDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.taskToTaskDto(savedTask)).thenReturn(outputDto);

        TaskDto result = taskService.createTask(inputDto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(taskRepository).save(task);
    }

    @Test
    void shouldGetTaskById() {
        Task task = new Task();
        task.setId(1L);

        TaskDto taskDto = new TaskDto();
        taskDto.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.taskToTaskDto(task)).thenReturn(taskDto);

        TaskDto result = taskService.getTask(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnNullWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskDto result = taskService.getTask(1L);

        assertThat(result).isNull();
    }

    @Test
    void shouldUpdateTask() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setStatus(TaskStatus.CREATED);

        TaskDto inputDto = new TaskDto();
        inputDto.setTitle("Updated Task");
        inputDto.setStatus(TaskStatus.IN_PROGRESS);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto outputDto = new TaskDto();
        outputDto.setId(1L);
        outputDto.setTitle("Updated Task");
        outputDto.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskMapper.taskDtoToTask(inputDto)).thenReturn(updatedTask);
        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);
        when(taskMapper.taskToTaskDto(updatedTask)).thenReturn(outputDto);

        TaskDto result = taskService.updateTask(1L, inputDto);

        assertThat(result.getTitle()).isEqualTo("Updated Task");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        verify(taskEventProducer).sendStatusChange(1L, TaskStatus.IN_PROGRESS);
    }

    @Test
    void shouldNotSendEventWhenStatusNotChanged() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto inputDto = new TaskDto();
        inputDto.setStatus(TaskStatus.IN_PROGRESS);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto outputDto = new TaskDto();
        outputDto.setId(1L);
        outputDto.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskMapper.taskDtoToTask(inputDto)).thenReturn(updatedTask);
        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);
        when(taskMapper.taskToTaskDto(updatedTask)).thenReturn(outputDto);

        taskService.updateTask(1L, inputDto);

        verify(taskEventProducer, never()).sendStatusChange(any(), any());
    }

    @Test
    void shouldReturnNullWhenUpdatingNonExistingTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskDto result = taskService.updateTask(1L, new TaskDto());

        assertThat(result).isNull();
    }

    @Test
    void shouldDeleteTask() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void shouldGetAllTasks() {
        Task task1 = new Task();
        task1.setId(1L);

        Task task2 = new Task();
        task2.setId(2L);

        TaskDto dto1 = new TaskDto();
        dto1.setId(1L);

        TaskDto dto2 = new TaskDto();
        dto2.setId(2L);

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
        when(taskMapper.taskToTaskDto(task1)).thenReturn(dto1);
        when(taskMapper.taskToTaskDto(task2)).thenReturn(dto2);

        List<TaskDto> result = taskService.getAllTasks();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }
}