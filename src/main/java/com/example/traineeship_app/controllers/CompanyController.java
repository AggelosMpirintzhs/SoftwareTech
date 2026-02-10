package com.example.traineeship_app.controllers;

import com.example.traineeship_app.Dto.CompanyDto;
import com.example.traineeship_app.Dto.TraineeshipPositionDto;
import com.example.traineeship_app.domainmodel.Company;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.mappers.TraineeshipPositionMapper;
import com.example.traineeship_app.services.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class CompanyController {


    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }


    @GetMapping("/company_dashboard")
    public String showCompanyDashboard(Principal principal) {
        String username = principal.getName();
        if (companyService.hasProfile(username)) {
            return "company/company_dashboard";
        } else {
            return "company/create_company_profile";
        }
    }

    @PostMapping("/create_company_profile")
    public String saveCompanyProfile(@RequestParam("company_name") String companyName,
                                     @RequestParam("location") String location,
                                     Principal principal) {

        String username = principal.getName();

        CompanyDto dto = new CompanyDto(username, companyName, location);
        companyService.createProfile(dto, username);

        return "redirect:/company_dashboard";
    }

    @GetMapping("/Company_Positions")
    public String showCompanyPositions(Principal principal, Model model) {
        String username = principal.getName();
        List<TraineeshipPosition> positions = companyService.getAvailablePositions(username);
        model.addAttribute("positions", positions);
        return "company/company_positions";
    }

    @GetMapping("/Add_position")
    public String showAddPositionForm() {
        return "/company/add_position";
    }

    @PostMapping("/Add_position")
    public String addPosition(@RequestParam("title") String title,
                              @RequestParam("start_date") String startDate,
                              @RequestParam("end_date") String endDate,
                              @RequestParam("description") String description,
                              @RequestParam("skills") String skills,
                              @RequestParam("topics") String topics,
                              Principal principal) {

        String username = principal.getName();

        TraineeshipPositionDto dto = new TraineeshipPositionDto(
                title,
                description,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
                topics,
                skills,
                false
        );

        TraineeshipPosition position = TraineeshipPositionMapper.toEntity(dto);

        Company company = companyService.getCompanyByUsername(username);
        position.setCompany(company);

        companyService.addTraineeshipPosition(username, dto);

        return "redirect:/Company_Positions";
    }

    @GetMapping("/delete/{positionId}")
    public String deletePosition(@PathVariable("positionId") Integer positionId) {
        companyService.deleteTraineeship(positionId);
        return "redirect:/Company_Positions";
    }

    @GetMapping("/evaluate/{positionId}")
    public String showEvaluationForm(@PathVariable("positionId") Integer positionId, Model model) {
        Map<String, Object> data = companyService.getEvaluationData(positionId);
        model.addAttribute("position", data.get("position"));
        model.addAttribute("evaluation", data.get("evaluation"));
        return "company/company_evaluation";
    }

    @PostMapping("/evaluate/{positionId}")
    public String saveEvaluation(@PathVariable("positionId") Integer positionId,
                                 @RequestParam(value = "motivation", required = false) Integer motivation,
                                 @RequestParam(value = "efficiency", required = false) Integer efficiency,
                                 @RequestParam(value = "effectiveness", required = false) Integer effectiveness) {

        companyService.saveOrUpdateCompanyEvaluation(positionId, motivation, efficiency, effectiveness);
        return "redirect:/Company_Positions";
    }

    @GetMapping("/Edit_company_profile")
    public String showEditProfile(Model model, Principal principal) {
        String username = principal.getName();
        Company company = companyService.getCompanyByUsername(username);
        model.addAttribute("company", company);
        return "company/edit_profile";
    }

    @PostMapping("/Edit_company_profile")
    public String updateProfile(@RequestParam("company_name") String companyName,
                                @RequestParam("location") String location,
                                Principal principal) {
        String username = principal.getName();
        CompanyDto dto = new CompanyDto(username, companyName, location);
        companyService.updateCompanyProfile(dto);
        return "redirect:/company_dashboard";
    }
}


