package ua.kpi.resourcesflow.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.kpi.resourcesflow.model.Element;
import ua.kpi.resourcesflow.model.Machine;
import ua.kpi.resourcesflow.model.TimePeriod;
import ua.kpi.resourcesflow.service.MachineService;
import ua.kpi.resourcesflow.service.TypeService;

import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequestMapping("/machines")
@AllArgsConstructor
public class MachineController {
    private MachineService machineService;
    private TypeService typeService;

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
    public String submitForm(@ModelAttribute("machine") Machine machine,
                             @RequestParam("month") int month,
                             @RequestParam("year") int year) {
        machineService.saveMachine(machine, month, year);
        return "redirect:/machines";
    }

    @GetMapping
    public String viewMachines(Model model, @RequestParam(name = "date", defaultValue = "2019-01") String dateParam) {
        if (dateParam == null || !dateParam.matches("\\d{4}-\\d{2}")) {
            return "redirect:/machines?date=" + LocalDate.now().toString().substring(0, 7);
        }
        String[] date = dateParam.split("-");
        int month = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[0]);
        model.addAttribute("machines", machineService.getAllMachinesByDate(month, year));
        model.addAttribute("timePeriod", new TimePeriod(null, month, year));
        return "machines";
    }
}
