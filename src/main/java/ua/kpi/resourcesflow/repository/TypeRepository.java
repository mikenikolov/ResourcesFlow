package ua.kpi.resourcesflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.resourcesflow.model.Type;

public interface TypeRepository extends JpaRepository<Type, Long> {
}
