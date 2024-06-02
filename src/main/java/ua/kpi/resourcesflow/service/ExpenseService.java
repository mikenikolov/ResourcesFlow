package ua.kpi.resourcesflow.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.resourcesflow.exception.BadRequestException;
import ua.kpi.resourcesflow.model.dto.ChannelWrapper;
import ua.kpi.resourcesflow.model.Machine;
import ua.kpi.resourcesflow.repository.MachineRepository;

@Service
@AllArgsConstructor
public class ExpenseService {
    private final MachineRepository machineRepository;

    public Machine addExpenses(Long machineId, String timePeriod, ChannelWrapper channelWrapper) {
        Machine machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new BadRequestException(String.format("Machine with ID:%d is not exists!", machineId)));
        channelWrapper.getChannels().forEach(updatedChannel -> machine.getChannels().forEach(channel -> {
                    if (updatedChannel.getId().equals(channel.getId())) {
                        updatedChannel.getExpenses().forEach(expense -> expense.setChannel(channel));
                        channel.getExpenses().addAll(updatedChannel.getExpenses());
                    }
                })
        );

        machine.getChannels().stream()
                .flatMap(channel -> channel.getExpenses().stream())
                .filter(expense -> expense.getTimePeriod() == null)
                .forEach(expense -> expense.setTimePeriod(timePeriod));
        machineRepository.save(machine);
        return machine;
    }

}
