package t1.openSchool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusChangeEvent {
    private Long taskId;
    private String newStatus;
    private String email;
}
