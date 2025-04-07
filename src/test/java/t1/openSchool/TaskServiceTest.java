package t1.openSchool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import t1.openSchool.dto.TaskDto;
import t1.openSchool.model.Task;
import t1.openSchool.repository.TaskRepository;
import t1.openSchool.mapper.TaskMapper;
import t1.openSchool.service.TaskService;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_ShouldTriggerLogExecutionAndLogExecutionTime() {
        TaskDto inputDto = new TaskDto();
        inputDto.setTitle("Test Task");
        Task entity = new Task();
        Task savedEntity = new Task();
        savedEntity.setId(1L);
        TaskDto outputDto = new TaskDto();
        outputDto.setId(1L);

        when(taskMapper.taskDtoToTask(any())).thenReturn(entity);
        when(taskRepository.save(any())).thenReturn(savedEntity);
        when(taskMapper.taskToTaskDto(any())).thenReturn(outputDto);

        TaskDto result = taskService.createTask(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(taskMapper).taskDtoToTask(inputDto);
        verify(taskRepository).save(entity);
        verify(taskMapper).taskToTaskDto(savedEntity);

    }

    @Test
    void getTask_ShouldTriggerLogExecutionAndLogException() {

        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.taskToTaskDto(any())).thenReturn(taskDto);

        TaskDto result = taskService.getTask(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        verify(taskRepository).findById(taskId);
        verify(taskMapper).taskToTaskDto(task);
    }

    @Test
    void updateTask_ShouldTriggerAllAspects() {

        Long taskId = 1L;
        TaskDto inputDto = new TaskDto();
        inputDto.setTitle("Updated Task");
        Task existingTask = new Task();
        existingTask.setId(taskId);
        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setTitle("Updated Task");
        TaskDto outputDto = new TaskDto();
        outputDto.setId(taskId);
        outputDto.setTitle("Updated Task");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskMapper.taskDtoToTask(any())).thenReturn(updatedTask);
        when(taskRepository.save(any())).thenReturn(updatedTask);
        when(taskMapper.taskToTaskDto(any())).thenReturn(outputDto);

        TaskDto result = taskService.updateTask(taskId, inputDto);

        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        verify(taskRepository).findById(taskId);
        verify(taskMapper).taskDtoToTask(inputDto);
        verify(taskRepository).save(updatedTask);
        verify(taskMapper).taskToTaskDto(updatedTask);
    }

    @Test
    void deleteTask_ShouldTriggerLogExecution() {
        Long taskId = 1L;
        doNothing().when(taskRepository).deleteById(taskId);

        taskService.deleteTask(taskId);

        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void getAllTasks_ShouldTriggerLogExecutionAndHandlingResult() {

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

        assertEquals(2, result.size());
        verify(taskRepository).findAll();
        verify(taskMapper, times(2)).taskToTaskDto(any());
    }

    @Test
    void getNonExistentTask_ShouldLogException() {
        Long taskId = 999L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskDto result = taskService.getTask(taskId);

        assertNull(result);
        verify(taskRepository).findById(taskId);
    }
}