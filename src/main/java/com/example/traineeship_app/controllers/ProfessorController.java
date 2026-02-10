package com.example.traineeship_app.controllers;

import com.example.traineeship_app.Dto.ProfessorDto;
import com.example.traineeship_app.domainmodel.Professor;
import com.example.traineeship_app.services.ProfessorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping("/professor_dashboard")
    public String showProfessorDashboard(@RequestParam(value = "error", required = false) String error,
                                         Principal principal,
                                         Model model) {
        String username = principal.getName();
        model.addAttribute("error", error);
        if (professorService.hasProfile(username)) {
            return "professor/professor_dashboard";
        } else {
            return "professor/create_professor_profile";
        }
    }

    @PostMapping("/create_professor_profile")
    public String saveProfessorProfile(
            @RequestParam("full_name") String fullName,
            @RequestParam("interests") String interests,
            Principal principal) {

        String username = principal.getName();

        List<String> interestsList = Arrays.asList(interests.split(","));

        ProfessorDto dto = new ProfessorDto(username, fullName, interestsList);
        professorService.createProfile(dto, username);

        return "redirect:/professor_dashboard";
    }

    @GetMapping("/Edit_professor_profile")
    public String editProfessorProfile(Model model, Principal principal) {
        String username = principal.getName();
        Professor professor = professorService.getProfessorByUsername(username);

        String interestsString = String.join(", ", professor.getInterests());

        model.addAttribute("professor", professor);
        model.addAttribute("interestsString", interestsString);

        return "professor/edit_profile";
    }


    @PostMapping("/Edit_professor_profile")
    public String updateProfessorProfile(
            @RequestParam("professor_name") String fullName,
            @RequestParam("interests") String interests,
            Principal principal) {

        String username = principal.getName();
        List<String> interestsList = new ArrayList<>(Arrays.asList(interests.split(",")));
        ProfessorDto dto = new ProfessorDto(username, fullName, interestsList);
        professorService.updateProfessorProfile(dto);

        return "redirect:/professor_dashboard";
    }

    @GetMapping("/professor_positions")
    public String getSupervisedPositions(Model model, Principal principal) {
        String username = principal.getName();
        Professor professor = professorService.getProfessorByUsername(username);

        model.addAttribute("professor", professor);

        return "professor/professor_positions";
    }

    @GetMapping("/evaluate_position/{positionId}")
    public String showEvaluationForm(@PathVariable("positionId") Integer positionId, Model model) {
        Map<String, Object> data = professorService.getEvaluationData(positionId);
        model.addAttribute("position", data.get("position"));
        model.addAttribute("evaluation", data.get("evaluation"));
        return "professor/professor_evaluation";
    }

    @PostMapping("/evaluate_position/{positionId}")
    public String saveEvaluation(@PathVariable("positionId") Integer positionId,
                                 @RequestParam(value = "motivation", required = false) Integer motivation,
                                 @RequestParam(value = "efficiency", required = false) Integer efficiency,
                                 @RequestParam(value = "effectiveness", required = false) Integer effectiveness,
                                 @RequestParam(value = "facilities", required = false) Integer facilities,
                                 @RequestParam(value = "guidance", required = false) Integer guidance) {

        professorService.saveOrUpdateProfessorEvaluation(positionId, motivation, efficiency, effectiveness, facilities, guidance);
        return "redirect:/professor_positions";
    }
}