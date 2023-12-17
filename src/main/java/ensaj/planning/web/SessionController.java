package ensaj.planning.web;

import ensaj.planning.entities.AffectationModuleGroupeTeacher;
import ensaj.planning.entities.Session;
import ensaj.planning.services.IAffect;
import ensaj.planning.services.ISession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/session")
@AllArgsConstructor
public class SessionController {

    @Autowired
    private ISession iSession;

    @PostMapping()
    Session save(@RequestBody Session session){
        return iSession.save(session);
    }
}
