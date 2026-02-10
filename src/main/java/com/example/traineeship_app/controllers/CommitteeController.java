package com.example.traineeship_app.controllers;

import com.example.traineeship_app.Dto.PositionWithScoreDto;
import com.example.traineeship_app.Dto.ProfessorMatchDto;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.services.CommitteeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;


@Controller
public class CommitteeController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService) {
        this.committeeService = committeeService;
    }

    @GetMapping("/committee_dashboard")
    public String committeeDashboard() {
        return "committee/committee_dashboard";
    }

    @GetMapping("/Manage_students")
    public String showTraineeshipApplicants(Model model) {
        List<Student> students = committeeService.retrieveTraineeshipApplicants();
        model.addAttribute("students", students);
        return "committee/manage_students";
    }

    @GetMapping("/assign_position")
    public String showSearchStrategies(@RequestParam("studentId") Integer studentId, Model model) {
        model.addAttribute("studentId", studentId);
        return "committee/search_for_traineeship";
    }

    @GetMapping("/Search_results")
    public String showSearchResults(@RequestParam("searchType") String strategy,
                                    @RequestParam("studentId") Integer studentId,
                                    Model model) {

        List<PositionWithScoreDto> scoredPositions =
                committeeService.getScoredPositionsForApplicant(studentId, strategy);

        model.addAttribute("positions", scoredPositions);
        model.addAttribute("studentId", studentId);

        return "committee/suitable_positions";
    }

    @PostMapping("/assign_position_to_student")
    public String assignPositionToStudent(@RequestParam("studentId") Integer studentId,
                                          @RequestParam("positionId") Integer positionId) {

        committeeService.assignPositionToStudent(studentId, positionId);

        return "redirect:/Manage_students";
    }

    @GetMapping("/In_progress_traineeships")
    public String viewAssignedTraineeships(Model model,
                                           @ModelAttribute("message") String message) {
        List<TraineeshipPosition> positions = committeeService.getAssignedInProgressPositions();
        List<Boolean> isCompletable = committeeService.getCompletableFlags(positions);
        Map<Integer, Boolean> hasCompanyEvalMap = committeeService.getCompanyEvalFlags(positions);
        Map<Integer, Boolean> hasProfessorEvalMap = committeeService.getProfessorEvalFlags(positions);

        model.addAttribute("positions", positions);
        model.addAttribute("isCompletable", isCompletable);
        model.addAttribute("hasCompanyEvalMap", hasCompanyEvalMap);
        model.addAttribute("hasProfessorEvalMap", hasProfessorEvalMap);
        model.addAttribute("message", message);

        return "committee/assigned_traineeships";
    }

    @GetMapping("/Search_professor")
    public String showSearchStrategySelection(@RequestParam("positionId") int positionId, Model model) {
        model.addAttribute("positionId", positionId);
        return "committee/search_for_professor";
    }

    @GetMapping("/Assign_professor_selection")
    public String searchProfessors(@RequestParam("positionId") int positionId,
                                   @RequestParam("strategy") String strategy,
                                   Model model) {

        List<ProfessorMatchDto> matches = committeeService.findSuitableProfessors(positionId, strategy);

        model.addAttribute("professors", matches);
        model.addAttribute("positionId", positionId);
        model.addAttribute("selectedStrategy", strategy);

        return "committee/suitable_professors";
    }

    @PostMapping("/Assign_professor")
    public String assignProfessor(@RequestParam("positionId") int positionId,
                                  @RequestParam("professorId") int professorId,
                                  RedirectAttributes redirectAttributes) {

        boolean success = committeeService.assignProfessorToPosition(positionId, professorId);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "Professor assigned successfully.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Assignment failed.");
        }

        return "redirect:/In_progress_traineeships";
    }

    @PostMapping("/Complete_traineeship")
    public String completeTraineeship(@RequestParam("positionId") Integer positionId, RedirectAttributes redirectAttributes) {
        try {
            committeeService.completeTraineeship(positionId);
            redirectAttributes.addFlashAttribute("message", "Traineeship completed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error completing traineeship: " + e.getMessage());
        }
        return "redirect:/In_progress_traineeships";
    }
}
