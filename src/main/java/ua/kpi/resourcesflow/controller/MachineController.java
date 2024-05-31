package ua.kpi.resourcesflow.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.kpi.resourcesflow.exception.BadRequestException;
import ua.kpi.resourcesflow.model.*;
import ua.kpi.resourcesflow.service.MachineService;
import ua.kpi.resourcesflow.service.TypeService;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/machines")
@AllArgsConstructor
public class MachineController {

    private final MachineService machineService;
    private final TypeService typeService;

    @GetMapping("/add")
    public String showForm(Model model) {
        Machine machine = new Machine();
        machine.setElements(new ArrayList<>());
        machine.getElements().add(new Element());
        model.addAttribute("machine", machine);
        model.addAttribute("types", typeService.getAllTypes());
        return "add-machine";
    }

    @PostMapping("/add")
    public String submitForm(@ModelAttribute("machine") Machine machine, RedirectAttributes redirectAttributes) {
        machineService.saveMachine(machine);
        redirectAttributes.addFlashAttribute("success", "Machine has been successfully created!");
        return "redirect:/machines";
    }

    @GetMapping
    public String showMachines(Model model) {
        List<Machine> machines = machineService.getAllMachines();
        model.addAttribute("machines", machines);
        return "registered-machines";
    }

    @GetMapping("/{machineId}/add-expenses")
    public String showAddExpenseForm(@PathVariable Long machineId, Model model) {
        Machine machine = machineService.getById(machineId)
                .orElseThrow(() -> new BadRequestException(String.format("Machine with ID:%d is not exists!", machineId)));
        ElementWrapper expensesList = new ElementWrapper();
        for (Element element : machine.getElements()) {
            element.getExpenses().clear();
        }
        expensesList.setElements(machine.getElements());
        model.addAttribute("machineId", machineId);
        model.addAttribute("expensesList", expensesList);
        return "add-expenses";
    }


    @PostMapping("/{machineId}/add-expenses")
    public String addExpenses(@PathVariable Long machineId,
                              @RequestParam("timePeriod") String timePeriod,
                              @ModelAttribute("expensesList") ElementWrapper expensesList,
                              RedirectAttributes redirectAttributes) {
        Machine machine = machineService.addExpenses(machineId, timePeriod, expensesList);
        redirectAttributes.addFlashAttribute("success", String.format("New expenses for %s has been added!", machine.getName()));
        return "redirect:/machines";
    }

    @GetMapping("/statistic")
    public String viewMachinesByDate(Model model, @RequestParam(name = "timePeriod", defaultValue = "01/2019") String timePeriod) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth yearMonth = YearMonth.parse(timePeriod, formatter);
        String formattedTimePeriod = yearMonth.format(formatter);
        model.addAttribute("machines", machineService.getAllMachinesByDate(formattedTimePeriod));
        model.addAttribute("timePeriod", formattedTimePeriod);
        return "statistic-machine";
    }
}
