package t1.openSchool.dto;

import t1.openSchool.model.TaskStatus;

public record TaskStatusChangeEvent(Long taskId, TaskStatus newStatus) {
}