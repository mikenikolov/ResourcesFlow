package ua.kpi.resourcesflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.resourcesflow.model.TimePeriod;

import java.util.Optional;

public interface TimePeriodRepository extends JpaRepository<TimePeriod, Long> {
    Optional<TimePeriod> findByMonthAndYear(int month, int year);
}
