package ensaj.planning.services;

import ensaj.planning.entities.Classe;
import ensaj.planning.entities.TimeSlotClasse;
import ensaj.planning.repository.ClasseRepository;
import ensaj.planning.repository.FiliereRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ensaj.planning.entities.Filiere;

import java.util.List;

@Service
@AllArgsConstructor
public class IFiliereServiceImpl implements IFiliereService {
    private FiliereRepository filiereRepository;
    private ClasseRepository  classeRepository;

    @Override
    public List<Filiere> getFilieres() {
        return filiereRepository.findAll();
    }

    @Override
    public Filiere addFiliere(Filiere filiere) {

        return filiereRepository.save(filiere);
    }


    @Override
    public String deleteFiliere(Long id) {
        try {

            getFiliereById(id);



            filiereRepository.deleteById(id);
            return "Votre supression est effectuée avec succès";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public Filiere getFiliereById(Long id) {
        return filiereRepository.findById(id).orElseThrow(() -> new RuntimeException("La filière n'existe pas!"));
    }

    @Override
    public Filiere updateFiliere(Long id, Filiere filiere) {
        filiere.setId(id);
        return filiereRepository.save(filiere);
    }

    @Override
    public Page<Filiere> getFilieres(Pageable pageable) {
        return filiereRepository.findAll(pageable);
    }

    @Override
    public Page<Filiere> searchFilieres(String keyword, Pageable pageable) {
        return filiereRepository.searchFilieres(keyword, pageable);
    }

}
