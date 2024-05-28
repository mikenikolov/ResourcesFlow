package ua.kpi.resourcesflow.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.resourcesflow.model.Type;
import ua.kpi.resourcesflow.repository.TypeRepository;

import java.util.List;
@Service
@AllArgsConstructor
public class TypeService {
    private TypeRepository typeRepository;

    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    public void saveType(Type type) {
        typeRepository.save(type);
    }
}
