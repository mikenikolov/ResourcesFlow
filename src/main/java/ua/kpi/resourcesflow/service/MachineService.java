package ua.kpi.resourcesflow.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.resourcesflow.exception.BadRequestException;
import ua.kpi.resourcesflow.model.Element;
import ua.kpi.resourcesflow.model.Expense;
import ua.kpi.resourcesflow.model.Machine;
import ua.kpi.resourcesflow.model.TimePeriod;
import ua.kpi.resourcesflow.repository.MachineRepository;
import ua.kpi.resourcesflow.repository.TimePeriodRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MachineService {
    private final MachineRepository machineRepository;
    private final TimePeriodRepository timePeriodRepository;

    public List<Machine> getAllMachinesByDate(int month, int year) {
        TimePeriod timePeriod = timePeriodRepository.findByMonthAndYear(month, year)
                .orElseThrow(() -> new BadRequestException(String.format("There are no expenses for %d/%d period.", month, year)));
        List<Machine> machines = machineRepository.findMachinesWithExpensesForTimePeriod(timePeriod);
        machines.forEach(machine -> {
            machine.getElements().forEach(element -> {
                element.setExpenses(
                        element.getExpenses().stream()
                                .filter(expense -> expense.getTimePeriod().equals(timePeriod))
                                .collect(Collectors.toList())
                );
            });
        });
        machines.forEach(machine -> machine.getElements().forEach(element -> {
            BigDecimal amount = element.getExpenses().stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal total = element.getExpenses().stream()
                    .map(expense -> expense.getAmount().multiply(element.getType().getUnitPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            element.setAmount(amount.setScale(1, RoundingMode.UP));
            element.setTotal(total.setScale(1, RoundingMode.UP));
        }));
        machines.forEach(machine -> {
            BigDecimal totalMachine = machine.getElements().stream()
                    .map(Element::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            machine.setTotal(totalMachine.setScale(1, RoundingMode.UP));
        });
        return machines;
    }

    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }

    public void saveMachine(Machine machine) {
        machineRepository.save(machine);
    }
}
