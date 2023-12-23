package ensaj.planning.repository;

import ensaj.planning.entities.AffectationModuleGroupeTeacher;
import ensaj.planning.entities.Module;
import ensaj.planning.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s from Session s where s.etudiant.id=?1")
    List<Session> getSessionsByStudent(Long id);
}
