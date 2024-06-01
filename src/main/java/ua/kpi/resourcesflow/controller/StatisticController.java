package ua.kpi.resourcesflow.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.kpi.resourcesflow.service.MachineService;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Controller
@AllArgsConstructor
public class StatisticController {
    private final MachineService machineService;

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
