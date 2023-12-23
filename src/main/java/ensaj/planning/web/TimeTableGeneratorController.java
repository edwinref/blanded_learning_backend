package ensaj.planning.web;

import ensaj.planning.entities.*;
import ensaj.planning.entities.Module;
import ensaj.planning.services.IModuleService;
import ensaj.planning.services.ISalleService;
import ensaj.planning.services.ITimeSlotClasseService;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/generate")
public class TimeTableGeneratorController {
    @Autowired
    IModuleService iModuleService;

    @Autowired
    ITimeSlotClasseService iTimeSlotClasseService;

    private final ISalleService iSalleService;

    @Autowired
    public TimeTableGeneratorController(ISalleService iSalleService) {
        this.iSalleService = iSalleService;
    }




    @GetMapping("/generate")
    public void Solve() {
        SolverFactory<TimeTable> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(TimeTable.class)
                .withEntityClasses(Module.class)
                .withConstraintProviderClass(TimeTableConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(5)));

        // Load the problem
        TimeTable problem = generateDemoData();

        // Solve the problem
        Solver<TimeTable> solver = solverFactory.buildSolver();
        TimeTable solution = solver.solve(problem);

        // Visualize the solution
        printTimetable(solution);
        saveResult(solution);
        printTimetableDetails(solution);



    }

    public  TimeTable generateDemoData() {
        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));


        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(15, 30), LocalTime.of(16, 30)));


        List<Salle> salleList=iSalleService.getSalles();

        List<Module> moduleList = iModuleService.getModules();


        return new TimeTable(timeslotList, salleList, moduleList);




    }

    private static void printTimetable(TimeTable timeTable) {
        System.out.println();
        List<Salle> salleList = timeTable.getSalleList();
        List<Module> moduleList = timeTable.getModuleList();
        Map<Timeslot, Map<Salle, List<Module>>> moduleMap = moduleList.stream()
                .filter(module -> module.getTimeslot() != null && module.getSalle() != null)
                .collect(Collectors.groupingBy(Module::getTimeslot, Collectors.groupingBy(Module::getSalle)));
        System.out.println("|              | " + salleList.stream()
                .map(salle -> String.format("%-13s", salle.toString())).collect(Collectors.joining(" | ")) + " |");
        System.out.println("|--------------|".repeat(salleList.size() + 1));
        for (Timeslot timeslot : timeTable.getTimeslotList()) {
            List<List<Module>> cellList = salleList.stream()
                    .map(salle -> {
                        Map<Salle, List<Module>> bySalleMap = moduleMap.get(timeslot);
                        if (bySalleMap == null) {
                            return Collections.<Module>emptyList();
                        }
                        List<Module> cellModuleList = bySalleMap.get(salle);
                        if (cellModuleList == null) {
                            return Collections.<Module>emptyList();
                        }
                        return cellModuleList;
                    })
                    .collect(Collectors.toList());

            System.out.println("| " + String.format("%-13s",
                    timeslot.getDayOfWeek().toString() + "T" +
                            timeslot.getStartTime().toString().replace(':', '-') + ":00") + " | "
                    + cellList.stream().map(cellModuleList ->
                            String.format("%-13s", cellModuleList.stream().map(Module::getSubject)
                                    .collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            System.out.println("|              | "
                    + cellList.stream().map(cellModuleList ->
                            String.format("%-13s", cellModuleList.stream().map(module ->
                                    module.getEnseignant().getNom()).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            System.out.println("|              | "
                    + cellList.stream().map(cellModuleList ->
                            String.format("%-13s", cellModuleList.stream().map(module ->
                                    module.getClasse().getLibelle()).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            System.out.println("|--------------|".repeat(salleList.size() + 1));
        }
    }

    private static void printTimetableDetails(TimeTable timeTable) {
        List<Module> moduleList = timeTable.getModuleList();

        for (Module module : moduleList) {
            Timeslot timeslot = module.getTimeslot();
            Salle salle = module.getSalle();
            Enseignant enseignant = module.getEnseignant();
            Classe classe = module.getClasse();

            if (timeslot != null && salle != null && enseignant != null && classe != null) {
                System.out.println("Day: " + timeslot.getDayOfWeek().toString() +
                        ", Start: " + "T" +
                        timeslot.getStartTime().toString()+":00"+
                        ", Module: " + module.getSubject() +
                        ", Enseignant: " + enseignant.getNom() +
                        ", Classe: " + classe.getLibelle() +
                        ", Salle: " + salle.toString());
            }

        }
    }

    /*private  void saveResult(TimeTable timeTable) {
        List<Module> moduleList = timeTable.getModuleList();

        for (Module module : moduleList) {
            Timeslot timeslot = module.getTimeslot();
            Salle salle = module.getSalle();
            Enseignant enseignant = module.getEnseignant();
            Classe classe = module.getClasse();

            if (timeslot != null && salle != null && enseignant != null && classe != null) {

                TimeSlotClasse timeSlotClasse = new TimeSlotClasse();


                timeSlotClasse.setDay(timeslot.getDayOfWeek().toString());
                timeSlotClasse.setStartTime(timeslot.getStartTime().toString()+":00");
                timeSlotClasse.setEndTime(timeslot.getEndTime().toString()+":00");
                timeSlotClasse.setModule(module);
                timeSlotClasse.setSalle(salle.toString());
                iTimeSlotClasseService.addTimeSlotClasse(timeSlotClasse);
                System.out.print("data saved");
            }

        }
    }*/


    /*private void saveResult(TimeTable timeTable) {
        List<Module> moduleList = timeTable.getModuleList();

        // Define start and end dates
        LocalDate startDate = LocalDate.of(2023, Month.SEPTEMBER, 1);
        LocalDate endDate = LocalDate.of(2023, Month.NOVEMBER, 30);

        // Loop through each day between start and end dates
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            // Create a map to store timeslots for each class and room on a specific day
            Map<String, Set<String>> classRoomTimeSlots = new HashMap<>();

            for (Module module : moduleList) {
                Timeslot timeslot = module.getTimeslot();
                Salle salle = module.getSalle();
                Enseignant enseignant = module.getEnseignant();
                Classe classe = module.getClasse();

                if (timeslot != null && salle != null && enseignant != null && classe != null) {
                    // Create date-time with the current date and time from timeslot
                    LocalDateTime dateTime = LocalDateTime.of(date, timeslot.getStartTime());

                    // Format the date-time as required: yyyy-MM-ddTHH:mm:ss
                    String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

                    String key = classe.getLibelle() + "-" + salle.toString();

                    // Check if the timeslot for the specific class and room already exists for this day
                    if (!classRoomTimeSlots.containsKey(key) || !classRoomTimeSlots.get(key).contains(formattedDateTime)) {
                        // Add the timeslot for the class and room on this day
                        classRoomTimeSlots.computeIfAbsent(key, k -> new HashSet<>()).add(formattedDateTime);

                        TimeSlotClasse timeSlotClasse = new TimeSlotClasse();
                        timeSlotClasse.setDay(formattedDateTime); // Store the formatted date
                        timeSlotClasse.setStartTime(timeslot.getStartTime().toString() + ":00");
                        timeSlotClasse.setEndTime(timeslot.getEndTime().toString() + ":00");
                        timeSlotClasse.setModule(module);
                        timeSlotClasse.setSalle(salle.toString());
                        iTimeSlotClasseService.addTimeSlotClasse(timeSlotClasse);
                        System.out.println("Data saved for date: " + formattedDateTime);
                    }
                }
            }
        }
    }*/


    private void saveResult(TimeTable timeTable) {
        List<Module> moduleList = timeTable.getModuleList();

        for (Module module : moduleList) {
            Timeslot timeslot = module.getTimeslot();
            Salle salle = module.getSalle();
            Enseignant enseignant = module.getEnseignant();
            Classe classe = module.getClasse();

            if (timeslot != null && salle != null && enseignant != null && classe != null) {
                String dayOfWeek = timeslot.getDayOfWeek().toString();
                List<String> dates = generateDatesForDayOfWeek(dayOfWeek);

                for (String formattedDate : dates) {
                    TimeSlotClasse timeSlotClasse = new TimeSlotClasse();
                    timeSlotClasse.setDay(formattedDate+"T"+timeslot.getStartTime().toString() + ":00");
                    timeSlotClasse.setStartTime(timeslot.getStartTime().toString() + ":00");
                    timeSlotClasse.setEndTime(timeslot.getEndTime().toString() + ":00");
                    timeSlotClasse.setModule(module);
                    timeSlotClasse.setSalle(salle.toString());
                    iTimeSlotClasseService.addTimeSlotClasse(timeSlotClasse);
                    System.out.print("Data saved for date: " + formattedDate);
                }
            }
        }
    }


    public List<String> generateDatesForDayOfWeek(String dayOfWeek) {
        List<String> dates = new ArrayList<>();

        // Define the start and end dates (September 1, 2023 to December 31, 2023)
        LocalDate startDate = LocalDate.of(2023, 9, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Loop through the dates and add those matching the given day of the week
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.valueOf(dayOfWeek)) {
                String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
                dates.add(formattedDate);
            }
        }

        return dates;
    }





    @GetMapping("/{id}")
    public List<TimeSlotClasse> getAllFilieres(@PathVariable Long id) {
        return iTimeSlotClasseService.getTimeSlots(id);
    }
}