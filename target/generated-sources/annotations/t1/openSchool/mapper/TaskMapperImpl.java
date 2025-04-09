package t1.openSchool.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import t1.openSchool.dto.TaskDto;
import t1.openSchool.model.Task;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-09T20:33:32+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskDto taskToTaskDto(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskDto taskDto = new TaskDto();

        taskDto.setId( task.getId() );
        taskDto.setTitle( task.getTitle() );
        taskDto.setDescription( task.getDescription() );
        taskDto.setUserId( task.getUserId() );
        taskDto.setStatus( task.getStatus() );

        return taskDto;
    }

    @Override
    public Task taskDtoToTask(TaskDto taskDto) {
        if ( taskDto == null ) {
            return null;
        }

        Task task = new Task();

        task.setId( taskDto.getId() );
        task.setTitle( taskDto.getTitle() );
        task.setDescription( taskDto.getDescription() );
        task.setUserId( taskDto.getUserId() );
        task.setStatus( taskDto.getStatus() );

        return task;
    }
}
