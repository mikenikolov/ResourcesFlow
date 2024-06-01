package ua.kpi.resourcesflow.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.resourcesflow.exception.BadRequestException;
import ua.kpi.resourcesflow.model.Channel;
import ua.kpi.resourcesflow.model.ChannelWrapper;
import ua.kpi.resourcesflow.model.Expense;
import ua.kpi.resourcesflow.model.Machine;
import ua.kpi.resourcesflow.repository.MachineRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MachineService {
    private final MachineRepository machineRepository;

    public List<Machine> getAllMachinesByDate(String timePeriod) {
        List<Machine> machines = machineRepository.findByChannels_Expenses_TimePeriod(timePeriod);
        machines.forEach(machine -> {
            machine.getChannels().forEach(element -> {
                element.setExpenses(
                        element.getExpenses().stream()
                                .filter(expense -> expense.getTimePeriod().equals(timePeriod))
                                .collect(Collectors.toList())
                );
            });
        });
        machines.forEach(machine -> machine.getChannels().forEach(element -> {
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
            BigDecimal totalMachine = machine.getChannels().stream()
                    .map(Channel::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            machine.setTotal(totalMachine.setScale(1, RoundingMode.UP));
        });
        return machines;
    }

    public Machine addExpenses(Long machineId, String timePeriod, ChannelWrapper expensesList) {
        List<Channel> channels = expensesList.getChannels();
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new BadRequestException(String.format("Machine with ID:%d is not exists!", machineId)));
        channels.forEach(e -> machine.getChannels().forEach(me -> {
            if (e.getId().equals(me.getId())) {
                me.getExpenses().addAll(e.getExpenses());
            }
        }));

        for (Channel channel : machine.getChannels()) {
            for (Expense expense : channel.getExpenses()) {
                if (expense.getTimePeriod() == null) {
                    expense.setTimePeriod(timePeriod);
                }
            }
        }
        machineRepository.save(machine);
        return machine;
    }

    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }

    public void saveMachine(Machine machine) {
        machineRepository.save(machine);
    }

    public Optional<Machine> getById(long machineId) {
        return machineRepository.findById(machineId);
    }
}
