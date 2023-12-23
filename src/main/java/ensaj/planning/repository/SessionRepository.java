package ensaj.planning.repository;

import ensaj.planning.entities.AffectationModuleGroupeTeacher;
import ensaj.planning.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
