package ensaj.planning.services;

import ensaj.planning.entities.*;
import ensaj.planning.entities.Module;
import ensaj.planning.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IModuleServiceImpl implements IModuleService {
    private ModuleRepository moduleRepository;
    private ClasseRepository classeRepository;
    private FiliereRepository filiereRepository;
    private UserRepository userRepository;
    private CriteriaRepository criteriaRepository;




    @Override
    public List<Module> getModules() {
        return moduleRepository.findAll();
    }

    @Override
    public List<Module> getModuleByClasse(Long id) {
        return moduleRepository.getModulesByClasse(id);
    }


    //reda type module
    @Override
    public String getTypeModule(Long id,Long idGroup) {
        Module module=moduleRepository.getbyId(id);
        List<Etudiant> etudiants = userRepository.findAllByGroupe(idGroup);
        boolean b = false;
        for(Etudiant e:etudiants){
            System.out.println(e.getId());
            Criteria c =criteriaRepository.getCriteriaByEtudiant(e.getId());
            if(c.getEquipment().equals("Yes") && c.getInfrastructure().equals("Yes") && c.getLearningSpace().equals("Yes") && c.getPreference().equals("Hybride")){
                b = true;
            }else{
                b = false;
                break;
            }
        }

        if(module.getVolumeHoraireOnRemote() == 0){
            b= false;
        }else {
            b = true;
        }
        if (b){
            return "Hybrid";
        }
        return "On site";
    }

    @Override
    public Module addModule(Module module, Long classeId,Long filiereId) {
        Classe classe= classeRepository.findById(classeId).orElse(null);
        Filiere filiere = filiereRepository.findById(filiereId).orElse(null);
        module.setClasse(classe);
        module.setFiliere(filiere   );

        return moduleRepository.save(module);
    }

    @Override
    public String deleteModule(Long id) {
        try {
            getModuleById(id);
            moduleRepository.deleteById(id);
            return "L'opération est bien effectuée";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public Module getModuleById(Long id) {

        return moduleRepository.findById(id).orElseThrow(() -> new RuntimeException("Ce module n'existe pas."));
    }

    @Override
    public Module updateModule(Long id, Module module) {

        module.setId(id);
        return moduleRepository.save(module);
    }

    @Override
    public List<Module> getModuleByEnseignant(Enseignant enseignant) {
        return moduleRepository.getModulesByEnseignant(enseignant);
    }
}
