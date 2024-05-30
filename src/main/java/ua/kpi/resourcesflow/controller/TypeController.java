package ua.kpi.resourcesflow.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.kpi.resourcesflow.model.Type;
import ua.kpi.resourcesflow.service.TypeService;

import java.util.List;

@Controller
@RequestMapping("/types")
@AllArgsConstructor
public class TypeController {
    private TypeService typeService;

    @GetMapping()
    public String showForm(Model model) {
        Type type = new Type();
        List<Type> types = typeService.getAllTypes();
        model.addAttribute("type", type);
        model.addAttribute("types", types);
        return "types";
    }

    @PostMapping()
    public String submitForm(@ModelAttribute Type type, RedirectAttributes redirectAttributes) {
        typeService.saveType(type);
        redirectAttributes.addFlashAttribute("success", "New type successfully added!");
        return "redirect:/types";
    }
}
