package com.example.traineeship_app.controllers;

import com.example.traineeship_app.Dto.StudentDto;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.services.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.security.Principal;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {this.studentService = studentService;}

    @GetMapping("/student_dashboard")
    public String showCompanyDashboard(@RequestParam(value = "error", required = false) String error,
                                       Principal principal,
                                       Model model) {
        String username = principal.getName();
        model.addAttribute("error", error);
        if (studentService.hasProfile(username)) {
            return "student/student_dashboard";
        } else {
            return "student/create_student_profile";
        }
    }

    @PostMapping("/create_student_profile")
    public String saveStudentProfile(
            @RequestParam("full_name") String fullName,
            @RequestParam("university_id") String universityId,
            @RequestParam("interests") String interests,
            @RequestParam("skills") String skills,
            @RequestParam("preferred_location") String preferredLocation,
            Principal principal,
            Model model) {

        String username = principal.getName();

        Student existing = studentService.getStudentByAm(universityId);
        if (existing != null) {
            model.addAttribute("error", "University ID already exists. Please use a different one.");
            model.addAttribute("full_name", fullName);
            model.addAttribute("university_id", universityId);
            model.addAttribute("interests", interests);
            model.addAttribute("skills", skills);
            model.addAttribute("preferred_location", preferredLocation);
            return "student/create_student_profile";
        }

        List<String> interestsList = Arrays.asList(interests.split(","));
        List<String> skillsList = Arrays.asList(skills.split(","));

        StudentDto dto = new StudentDto(username, fullName, universityId, preferredLocation, interestsList, skillsList);
        studentService.saveProfile(dto, username);

        return "redirect:/student_dashboard";
    }

    @GetMapping("/Edit_student_profile")
    public String showEditStudentProfile(Model model, Principal principal) {
        String username = principal.getName();
        Student student = studentService.getStudentByUsername(username);

        String interestsString = String.join(", ", student.getInterests());
        String skillsString = String.join(", ", student.getSkills());

        model.addAttribute("student", student);
        model.addAttribute("interestsString", interestsString);
        model.addAttribute("skillsString", skillsString);

        return "student/edit_profile";
    }

    @PostMapping("/Edit_student_profile")
    public String updateStudentProfile(
            @RequestParam("full_name") String fullName,
            @RequestParam("university_id") String universityId,
            @RequestParam("interests") String interests,
            @RequestParam("skills") String skills,
            @RequestParam("preferred_location") String preferredLocation,
            Principal principal,
            Model model) {

        String username = principal.getName();
        Student currentStudent = studentService.getStudentByUsername(username);

        Student existing = studentService.getStudentByAm(universityId);
        if (existing != null && !existing.getUsername().equals(username)) {
            model.addAttribute("student", currentStudent);
            model.addAttribute("interestsString", interests);
            model.addAttribute("skillsString", skills);
            model.addAttribute("error", "The University ID is already used by another student.");
            return "student/edit_profile";
        }

        List<String> interestsList = new ArrayList<>(Arrays.asList(interests.split(",")));
        List<String> skillsList = new ArrayList<>(Arrays.asList(skills.split(",")));

        StudentDto dto = new StudentDto(username, fullName, universityId, preferredLocation, interestsList, skillsList);
        studentService.updateStudentProfile(dto);

        return "redirect:/student_dashboard";
    }

    @GetMapping("/Logbook")
    public String showLogbookForm(Principal principal, Model model) {
        String username = principal.getName();
        Student student = studentService.getStudentByUsername(username);
        if (student.getAssignedTraineeship() == null) {
            return "redirect:/student_dashboard?error=logbook";
        }
        model.addAttribute("traineeship", student.getAssignedTraineeship());
        return "student/logbook";
    }

    @PostMapping("/Logbook")
    public String saveLogbook(@RequestParam("logbook") String logbook, Principal principal) {
        String username = principal.getName();
        studentService.updateLogbook(username, logbook);
        return "redirect:/student_dashboard";
    }

    @GetMapping("/Apply_for_traineeship")
    public String showApplyPage(Model model, Principal principal) {
        String username = principal.getName();
        Student student = studentService.getStudentByUsername(username);
        model.addAttribute("student", student);
        return "student/apply_for_traineeship";
    }

    @PostMapping("/apply")
    public String applyForTraineeship(Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        studentService.setLookingForTraineeshipTrue(username);
        redirectAttributes.addFlashAttribute("successMessage", "Your application was successfully submitted.");
        return "redirect:/Apply_for_traineeship";
    }
}
