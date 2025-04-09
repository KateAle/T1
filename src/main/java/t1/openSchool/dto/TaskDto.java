package t1.openSchool.dto;

import lombok.Data;
import t1.openSchool.model.TaskStatus;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private TaskStatus status;
}