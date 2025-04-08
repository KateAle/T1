package t1.openSchool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import t1.openSchool.aspect.annotation.HandlingResult;
import t1.openSchool.aspect.annotation.LogException;
import t1.openSchool.aspect.annotation.LogExecution;
import t1.openSchool.aspect.annotation.LogExecutionTime;
import t1.openSchool.aspect.annotation.LogTracking;
import t1.openSchool.dto.TaskDto;
import t1.openSchool.kafka.producer.TaskEventProducer;
import t1.openSchool.mapper.TaskMapper;
import t1.openSchool.model.Task;
import t1.openSchool.repository.TaskRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskEventProducer taskEventProducer;

    @LogExecution
    @LogExecutionTime
    @LogTracking
    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        log.debug("Creating task: {}", taskDto);
        Task task = taskMapper.taskDtoToTask(taskDto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.taskToTaskDto(savedTask);
    }

    @LogExecution
    @LogException
    @Transactional(readOnly = true)
    public TaskDto getTask(Long id) {
        log.debug("Getting task by id: {}", id);
        return taskRepository.findById(id)
                .map(taskMapper::taskToTaskDto)
                .orElse(null);
    }

    @LogExecution
    @LogException
    @LogExecutionTime
    @Transactional
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        log.debug("Updating task id {}: {}", id, taskDto);
        return taskRepository.findById(id)
                .map(existingTask -> {
                    String oldStatus = existingTask.getStatus();
                    Task updatedTask = taskMapper.taskDtoToTask(taskDto);
                    updatedTask.setId(existingTask.getId());
                    Task savedTask = taskRepository.save(updatedTask);

                    if (!updatedTask.getStatus().equals(oldStatus)) {
                        log.info("Task status changed from {} to {}", oldStatus, updatedTask.getStatus());
                        taskEventProducer.sendTaskStatusChangeEvent(
                                savedTask.getId(),
                                savedTask.getStatus(),
                                "user@example.com"
                        );
                    }

                    return taskMapper.taskToTaskDto(savedTask);
                })
                .orElse(null);
    }

    @LogExecution
    @LogExecutionTime
    @Transactional
    public void deleteTask(Long id) {
        log.debug("Deleting task by id: {}", id);
        taskRepository.deleteById(id);
    }

    @LogExecution
    @LogExecutionTime
    @HandlingResult
    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks() {
        log.debug("Getting all tasks");
        return taskRepository.findAll().stream()
                .map(taskMapper::taskToTaskDto)
                .toList();
    }
}