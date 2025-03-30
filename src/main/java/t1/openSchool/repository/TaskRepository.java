package t1.openSchool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import t1.openSchool.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}