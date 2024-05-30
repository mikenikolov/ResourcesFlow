package ua.kpi.resourcesflow.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.kpi.resourcesflow.exception.BadRequestException;
import ua.kpi.resourcesflow.model.*;
import ua.kpi.resourcesflow.repository.MachineRepository;
import ua.kpi.resourcesflow.repository.TimePeriodRepository;
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
    private final TimePeriodRepository timePeriodRepository;
    private final MachineRepository machineRepository;

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
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new BadRequestException(String.format("Machine with ID:%d is not exists!", machineId)));
        ElementWrapper wrapper = new ElementWrapper();
        for (Element element : machine.getElements()) {
            element.getExpenses().clear();
        }
        wrapper.setElements(machine.getElements());
        model.addAttribute("machineId", machineId);
        model.addAttribute("elementWrapper", wrapper);
        return "add-expenses";
    }


    @PostMapping("/{machineId}/add-expenses")
    public String addExpenses(@PathVariable Long machineId,
                              @RequestParam("date") String dateStr,
                              @ModelAttribute("elementWrapper") ElementWrapper elementWrapper,
                              RedirectAttributes redirectAttributes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth yearMonth = YearMonth.parse(dateStr, formatter);

        int month = yearMonth.getMonthValue();
        int year = yearMonth.getYear();
        List<Element> elements = elementWrapper.getElements();
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new BadRequestException(String.format("Machine with ID:%d is not exists!", machineId)));
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

        for (Element element : machine.getElements()) {
            for (Expense expense : element.getExpenses()) {
                if (expense.getTimePeriod() == null) {
                    expense.setTimePeriod(timePeriod);
                }
            }
        }
        redirectAttributes.addFlashAttribute("success", String.format("New expenses for %s has been added!", machine.getName()));
        machineRepository.save(machine);
        return "redirect:/machines";
    }

    @GetMapping("/statistic")
    public String viewMachinesByDate(Model model, @RequestParam(name = "date", defaultValue = "2019-01") String dateParam) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth yearMonth = YearMonth.parse(dateParam, formatter);

        int month = yearMonth.getMonthValue();
        int year = yearMonth.getYear();
        model.addAttribute("machines", machineService.getAllMachinesByDate(month, year));
        model.addAttribute("timePeriod", new TimePeriod(null, month, year));
        return "statistic-machine";
    }
}
