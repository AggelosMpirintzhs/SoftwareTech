package com.example.traineeship_app.controllers;

import com.example.traineeship_app.Dto.ProfessorDto;
import com.example.traineeship_app.domainmodel.Evaluation;
import com.example.traineeship_app.domainmodel.Professor;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.services.ProfessorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfessorController.class)
@Import(ProfessorControllerTest.Config.class)
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfessorService professorService;

    @TestConfiguration
    static class Config {
        @Bean
        public ProfessorService professorService() {
            return Mockito.mock(ProfessorService.class);
        }
    }

    @Test
    @WithMockUser(username = "profUser")
    void showDashboard_withProfile() throws Exception {
        when(professorService.hasProfile("profUser")).thenReturn(true);

        mockMvc.perform(get("/professor_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/professor_dashboard"));
    }

    @Test
    @WithMockUser(username = "profUser")
    void showDashboard_withoutProfile() throws Exception {
        when(professorService.hasProfile("profUser")).thenReturn(false);

        mockMvc.perform(get("/professor_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/create_professor_profile"));
    }

    @Test
    @WithMockUser(username = "profUser")
    void saveProfessorProfile_redirects() throws Exception {
        mockMvc.perform(post("/create_professor_profile")
                        .param("full_name", "Dr. Smith")
                        .param("interests", "AI,ML")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor_dashboard"));

        verify(professorService).createProfile(any(ProfessorDto.class), eq("profUser"));
    }

    @Test
    @WithMockUser(username = "profUser")
    void showEditProfile_populatesModel() throws Exception {
        Professor prof = new Professor();
        prof.setProfessorName("Dr. Nick");
        prof.setInterests(List.of("AI", "ML"));

        when(professorService.getProfessorByUsername("profUser")).thenReturn(prof);

        mockMvc.perform(get("/Edit_professor_profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("professor"))
                .andExpect(model().attributeExists("interestsString"))
                .andExpect(view().name("professor/edit_profile"));
    }

    @Test
    @WithMockUser(username = "profUser")
    void updateProfessorProfile_redirects() throws Exception {
        mockMvc.perform(post("/Edit_professor_profile")
                        .param("professor_name", "Updated Name")
                        .param("interests", "AI,Data")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor_dashboard"));

        verify(professorService).updateProfessorProfile(any(ProfessorDto.class));
    }

    @Test
    @WithMockUser(username = "profUser")
    void getSupervisedPositions_loadsPage() throws Exception {
        Professor prof = new Professor();
        prof.setProfessorName("Dr. Test");

        when(professorService.getProfessorByUsername("profUser")).thenReturn(prof);

        mockMvc.perform(get("/professor_positions"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("professor"))
                .andExpect(view().name("professor/professor_positions"));
    }

    @Test
    @WithMockUser(username = "professorUser", roles = {"PROFESSOR"})
    void showEvaluationForm_returnsView() throws Exception {

        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(1);
        position.setTitle("AI Internship");

        Evaluation evaluation = new Evaluation();
        evaluation.setMotivation(5);
        evaluation.setEfficiency(4);
        evaluation.setEffectiveness(3);
        evaluation.setFacilities(4);
        evaluation.setGuidance(5);

        when(professorService.getEvaluationData(1)).thenReturn(Map.of(
                "position", position,
                "evaluation", evaluation
        ));

        mockMvc.perform(get("/evaluate_position/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/professor_evaluation"))
                .andExpect(model().attribute("position", position))
                .andExpect(model().attribute("evaluation", evaluation));
    }


    @Test
    @WithMockUser
    void saveEvaluation_redirects() throws Exception {
        mockMvc.perform(post("/evaluate_position/1")
                        .param("motivation", "5")
                        .param("efficiency", "4")
                        .param("effectiveness", "3")
                        .param("facilities", "4")
                        .param("guidance", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor_positions"));

        verify(professorService).saveOrUpdateProfessorEvaluation(1, 5, 4, 3, 4, 5);
    }
}
