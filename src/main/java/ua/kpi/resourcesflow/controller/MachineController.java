package ua.kpi.resourcesflow.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.kpi.resourcesflow.model.Element;
import ua.kpi.resourcesflow.model.ElementWrapper;
import ua.kpi.resourcesflow.model.Machine;
import ua.kpi.resourcesflow.model.TimePeriod;
import ua.kpi.resourcesflow.repository.MachineRepository;
import ua.kpi.resourcesflow.repository.TimePeriodRepository;
import ua.kpi.resourcesflow.service.MachineService;
import ua.kpi.resourcesflow.service.TypeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/machines")
@AllArgsConstructor
public class MachineController {

    private final MachineService machineService;
    private final TypeService typeService;
    private final TimePeriodRepository timePeriodRepository;
    private final MachineRepository machineRepository;

    @GetMapping("/add")
    public String showForm(Model model) {
        Machine machine = new Machine();
        machine.setElements(new ArrayList<>());
        machine.getElements().add(new Element());
        model.addAttribute("machine", machine);
        model.addAttribute("types", typeService.getAllTypes());
        return "addMachine";
    }

    @PostMapping("/add")
    public String submitForm(@ModelAttribute("machine") Machine machine) {
        machineService.saveMachine(machine);
        return "redirect:/machines";
    }

    @GetMapping
    public String showMachines(Model model) {
        List<Machine> machines = machineService.getAllMachines();
        model.addAttribute("machines", machines);
        return "machines";
    }

    @GetMapping("/{machineId}/addExpenses")
    public String showAddExpenseForm(@PathVariable Long machineId, Model model) {
        Machine machine = machineRepository.findById(machineId).get();
        ElementWrapper wrapper = new ElementWrapper();
        for (Element element : machine.getElements()) {
            element.getExpenses().clear();
        }

        wrapper.setElements(machine.getElements());
        model.addAttribute("machineId", machineId);
        model.addAttribute("elementWrapper", wrapper);
        return "addExpenses";
    }


    @PostMapping("/{machineId}/addExpenses")
    public String addExpenses(@PathVariable Long machineId,
                              @ModelAttribute("month") Integer month,
                              @ModelAttribute("year") Integer year,
                              @ModelAttribute("elementWrapper") ElementWrapper elementWrapper) {
        List<Element> elements = elementWrapper.getElements();
        Machine machine = machineRepository.findById(machineId).get();
        elements.forEach(e -> machine.getElements().forEach(me -> {
            if (e.getId().equals(me.getId())) {
                me.getExpenses().addAll(e.getExpenses());
            }
        }));
        TimePeriod timePeriod = timePeriodRepository.findByMonthAndYear(month, year).orElseGet(() -> {
            TimePeriod newTimePeriod = new TimePeriod();
            newTimePeriod.setMonth(month);
            newTimePeriod.setYear(year);
            return timePeriodRepository.save(newTimePeriod);
        });

        machine.getElements()
                .forEach(element -> element.getExpenses()
                        .forEach(expense -> expense.setTimePeriod(timePeriod)));

        machineRepository.save(machine);
        return "redirect:/machines";
    }

    @GetMapping("/date")
    public String viewMachinesByDate(Model model, @RequestParam(name = "date", defaultValue = "2019-01") String dateParam) {
        if (dateParam == null || !dateParam.matches("\\d{4}-\\d{2}")) {
            return "redirect:/machines?date=" + LocalDate.now().toString().substring(0, 7);
        }
        String[] date = dateParam.split("-");
        int month = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[0]);
        model.addAttribute("machines", machineService.getAllMachinesByDate(month, year));
        model.addAttribute("timePeriod", new TimePeriod(null, month, year));
        return "dateMachines";
    }
}
