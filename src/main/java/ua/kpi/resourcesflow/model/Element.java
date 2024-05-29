package ua.kpi.resourcesflow.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Element {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String channel;
    private String name;
    @ManyToOne
    private Type type;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Expense> expenses = new ArrayList<>();
    @Transient
    private BigDecimal total = new BigDecimal(0);
    @Transient
    private BigDecimal amount = new BigDecimal(0);
}
