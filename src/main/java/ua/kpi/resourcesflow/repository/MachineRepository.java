package ua.kpi.resourcesflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.kpi.resourcesflow.model.Machine;
import ua.kpi.resourcesflow.model.TimePeriod;

import java.util.List;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    @Query("SELECT DISTINCT m FROM Machine m " +
            "JOIN m.elements e " +
            "JOIN e.expenses exp " +
            "WHERE exp.timePeriod = :timePeriod")
    List<Machine> findMachinesWithExpensesForTimePeriod(@Param("timePeriod") TimePeriod timePeriod);
}
