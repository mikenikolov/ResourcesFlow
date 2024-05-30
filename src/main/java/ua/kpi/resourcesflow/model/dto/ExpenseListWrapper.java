package ua.kpi.resourcesflow.model.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ua.kpi.resourcesflow.model.Expense;

@Getter
@Setter
public class ExpenseListWrapper {
    private List<Expense> expenses = new ArrayList<>();
}
