package ensaj.planning.web;

import ensaj.planning.entities.*;
import ensaj.planning.entities.Module;
import ensaj.planning.services.IEnseignantService;
import ensaj.planning.services.IModuleService;
import ensaj.planning.services.ISalleService;
import ensaj.planning.services.ITimeSlotClasseService;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/api/timeslot")
public class TimeTableController {
    @Autowired
     IModuleService iModuleService;

    @Autowired
    ITimeSlotClasseService iTimeSlotClasseService;

    private final ISalleService iSalleService;

    @Autowired
    public TimeTableController(ISalleService iSalleService) {
        this.iSalleService = iSalleService;
    }




    @GetMapping("/solve")
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
        saver(solution);



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

    private  void saver(TimeTable timeTable) {
        List<Salle> salleList = timeTable.getSalleList();
        List<Module> moduleList = timeTable.getModuleList();
        Map<Timeslot, Map<Salle, List<Module>>> moduleMap = moduleList.stream()
                .filter(module -> module.getTimeslot() != null && module.getSalle() != null)
                .collect(Collectors.groupingBy(Module::getTimeslot, Collectors.groupingBy(Module::getSalle)));

        for (Timeslot timeslot : timeTable.getTimeslotList()) {
            System.out.println("Timeslot: " + timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime());

            Map<Salle, List<Module>> timeslotModules = moduleMap.getOrDefault(timeslot, Collections.emptyMap());

            for (Salle salle : salleList) {
                System.out.println("| Modules    | " + salle.toString() + " |");

                List<Module> salleModules = timeslotModules.getOrDefault(salle, Collections.emptyList());

                for (Module module : salleModules) {
                    if (module.getTimeslot() != null && module.getTimeslot().equals(timeslot)) {




                        // Define the start and end months and years
                        Month startMonth = Month.SEPTEMBER;
                        int startYear = 2023;
                        Month endMonth = Month.JANUARY; // Last week of January
                        int endYear = 2024;



                        // Map to store days of the week
                        Map<DayOfWeek, List<LocalDate>> daysOfWeekMap = new HashMap<>();

                        // Get the first day of September
                        LocalDate startDate = LocalDate.of(startYear, startMonth, 1);

                        // Iterate through each day between the months
                        LocalDate currentDate = startDate;
                        while (!currentDate.isAfter(LocalDate.of(endYear, endMonth, 1))) {
                            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                            daysOfWeekMap.computeIfAbsent(dayOfWeek, k -> new ArrayList<>()).add(currentDate);
                            currentDate = currentDate.plusDays(1);
                        }

                        // Output the list of days for each day of the week
                        for (DayOfWeek day : daysOfWeekMap.keySet()) {
                            switch (day) {
                                case MONDAY:
                                    System.out.println("All Mondays:");
                                    break;
                                case TUESDAY:
                                    System.out.println("All Tuesdays:");
                                    break;
                                case WEDNESDAY:
                                    System.out.println("All Wednesdays:");
                                    break;
                                case THURSDAY:
                                    System.out.println("All Thursdays:");
                                    break;
                                case FRIDAY:
                                    System.out.println("All Fridays:");
                                    break;
                                case SATURDAY:
                                    System.out.println("All Saturdays:");
                                    break;
                                case SUNDAY:
                                    System.out.println("All Sundays:");
                                    break;
                            }

                            for (LocalDate date : daysOfWeekMap.get(timeslot.getDayOfWeek())) {
                                System.out.println(date);

                                TimeSlotClasse timeSlotClasse = new TimeSlotClasse();
                                timeSlotClasse.setTimeslot(date+ "T" + timeslot.getStartTime()+":00");
                                timeSlotClasse.setSalle(salle.toString());
                                timeSlotClasse.setModule(module);
                                iTimeSlotClasseService.addTimeSlotClasse(timeSlotClasse);
                                System.out.print("data saved");

                            }
                            System.out.println();
                        }



                    }
                }
            }
        }
    }
    private static void printTimetable(TimeTable timeTable) {
        List<Salle> salleList = timeTable.getSalleList();
        List<Module> moduleList = timeTable.getModuleList();
        Map<Timeslot, Map<Salle, List<Module>>> moduleMap = moduleList.stream()
                .filter(module -> module.getTimeslot() != null && module.getSalle() != null)
                .collect(Collectors.groupingBy(Module::getTimeslot, Collectors.groupingBy(Module::getSalle)));

        for (Timeslot timeslot : timeTable.getTimeslotList()) {
            System.out.println("Timeslot: " + timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime());

            Map<Salle, List<Module>> timeslotModules = moduleMap.getOrDefault(timeslot, Collections.emptyMap());

            for (Salle salle : salleList) {
                System.out.println("| Modules    | " + salle.toString() + " |");

                List<Module> salleModules = timeslotModules.getOrDefault(salle, Collections.emptyList());

                for (Module module : salleModules) {
                    if (module.getTimeslot() != null && module.getTimeslot().equals(timeslot)) {
                        System.out.print("|            | ");
                        System.out.print(String.format("%-12s", module.getLibelle()));
                        System.out.print(String.format("%-12s", module.getEnseignant().getNom()));
                        System.out.println(String.format("%-12s", module.getClasse().getLibelle() + " " + salle.toString()));
                    }
                }
            }
            System.out.println("|" + "-".repeat(13 * salleList.size() + 2) + "|");
        }
    }


    @GetMapping("/{id}")
    public List<TimeSlotClasse> getAllFilieres(@PathVariable Long id) {
        return iTimeSlotClasseService.getTimeSlots(id);
    }


}
