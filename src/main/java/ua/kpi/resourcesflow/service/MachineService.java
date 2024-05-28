package ua.kpi.resourcesflow.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.resourcesflow.model.TimePeriod;
import ua.kpi.resourcesflow.model.Machine;
import ua.kpi.resourcesflow.repository.MachineRepository;
import ua.kpi.resourcesflow.repository.TimePeriodRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class MachineService {
    private TimePeriodRepository timePeriodRepository;
    private MachineRepository machineRepository;

    public void saveMachine(Machine machine, int month, int year) {
        timePeriodRepository.findByMonthAndYear(month, year).ifPresentOrElse(machine::setTimePeriod, () -> {
            TimePeriod timePeriod = new TimePeriod();
            timePeriod.setMonth(month);
            timePeriod.setYear(year);
            machine.setTimePeriod(timePeriod);
        });
        machineRepository.save(machine);
    }

    public List<Machine> getAllMachinesByDate(int month, int year) {
        List<Machine> machines = machineRepository.findByTimePeriod_MonthAndTimePeriod_Year(month, year);
        machines.forEach(m -> m.getElements().stream()
                .map(e -> e.getAmount().multiply(e.getType().getUnitPrice()))
                .toList()
                .forEach(p -> m.setTotal(m.getTotal().add(p))));
        return machines;
    }
}
