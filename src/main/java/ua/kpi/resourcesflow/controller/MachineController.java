package ua.kpi.resourcesflow.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.kpi.resourcesflow.exception.BadRequestException;
import ua.kpi.resourcesflow.model.*;
import ua.kpi.resourcesflow.service.ExpenseService;
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
    private final ExpenseService expenseService;
    private final MachineService machineService;
    private final TypeService typeService;

    @GetMapping("/add")
    public String showAddMachineForm(Model model) {
        Machine machine = new Machine();
        machine.setChannels(new ArrayList<>());
        machine.getChannels().add(new Channel());
        model.addAttribute("machine", machine);
        model.addAttribute("types", typeService.getAllTypes());
        return "add-machine";
    }

    @PostMapping("/add")
    public String saveNewMachine(@ModelAttribute("machine") Machine machine, RedirectAttributes redirectAttributes) {
        machine.getChannels().forEach(channel -> channel.setMachine(machine));
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
        ChannelWrapper channelList = new ChannelWrapper();
        for (Channel channel : machine.getChannels()) {
            channel.getExpenses().clear();
        }
        channelList.setChannels(machine.getChannels());
        model.addAttribute("machineId", machineId);
        model.addAttribute("channelList", channelList);
        return "add-expenses";
    }


    @PostMapping("/{machineId}/add-expenses")
    public String addExpenses(@PathVariable Long machineId,
                              @RequestParam("timePeriod") String timePeriod,
                              @ModelAttribute("channelList") ChannelWrapper channelWrapper,
                              RedirectAttributes redirectAttributes) {
        Machine machine = expenseService.addExpenses(machineId, timePeriod, channelWrapper);
        redirectAttributes.addFlashAttribute("success", String.format("New expenses for %s has been added!", machine.getName()));
        return "redirect:/machines";
    }
}
