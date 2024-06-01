package ua.kpi.resourcesflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.resourcesflow.model.Machine;

import java.util.List;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    List<Machine> findByChannels_Expenses_TimePeriod(String timePeriod);
}
