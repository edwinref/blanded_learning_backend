package ensaj.planning.repository;

import ensaj.planning.entities.Classe;
import ensaj.planning.entities.TimeSlotClasse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TimeSlotClasseRepository extends JpaRepository<TimeSlotClasse, Long> {

    @Query("SELECT t from TimeSlotClasse t where t.module.classe.id = ?1")
    List<TimeSlotClasse> getTimeSlotClasseByclasse(Long id);

}
