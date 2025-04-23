package t1.openSchool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import t1.openSchool.model.TaskStatus;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskStatusChangeEvent {
    private Long taskId;
    private TaskStatus newStatus;
}