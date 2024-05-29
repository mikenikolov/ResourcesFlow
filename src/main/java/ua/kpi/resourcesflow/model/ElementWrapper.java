package ua.kpi.resourcesflow.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ElementWrapper {
    private List<Element> elements = new ArrayList<>();
}
