package t1.openSchool.mapper;

import org.mapstruct.ReportingPolicy;
import t1.openSchool.dto.TaskDto;
import t1.openSchool.model.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskDto taskToTaskDto(Task task);
    Task taskDtoToTask(TaskDto taskDto);
}

