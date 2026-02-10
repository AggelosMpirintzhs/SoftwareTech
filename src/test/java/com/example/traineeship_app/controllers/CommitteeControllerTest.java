package com.example.traineeship_app.controllers;

import com.example.traineeship_app.Dto.PositionWithScoreDto;
import com.example.traineeship_app.Dto.ProfessorMatchDto;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.services.CommitteeService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommitteeController.class)
@Import(CommitteeControllerTest.Config.class)
@WithMockUser(username = "committeeUser", roles = {"COMMITTEE"})
public class CommitteeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommitteeService committeeService;

    @TestConfiguration
    static class Config {
        @Bean
        public CommitteeService committeeService() {
            return Mockito.mock(CommitteeService.class);
        }
    }

    @Test
    void committeeDashboard_returnsCorrectView() throws Exception {
        mockMvc.perform(get("/committee_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("committee/committee_dashboard"));
    }

    @Test
    void showTraineeshipApplicants_populatesModelAndReturnsView() throws Exception {
        when(committeeService.retrieveTraineeshipApplicants()).thenReturn(List.of(new Student()));

        mockMvc.perform(get("/Manage_students"))
                .andExpect(status().isOk())
                .andExpect(view().name("committee/manage_students"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    void showSearchStrategies_setsStudentId() throws Exception {
        mockMvc.perform(get("/assign_position").param("studentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("committee/search_for_traineeship"))
                .andExpect(model().attributeExists("studentId"));
    }

    @Test
    void showSearchResults_returnsPositionList() throws Exception {
        when(committeeService.getScoredPositionsForApplicant(anyInt(), anyString())).thenReturn(List.of());

        mockMvc.perform(get("/Search_results")
                        .param("searchType", "default")
                        .param("studentId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("committee/suitable_positions"))
                .andExpect(model().attributeExists("positions"))
                .andExpect(model().attributeExists("studentId"));
    }


    @Test
    void assignPositionToStudent_redirects() throws Exception {
        mockMvc.perform(post("/assign_position_to_student")
                        .param("studentId", "1")
                        .param("positionId", "10")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/Manage_students"));
    }

    @Test
    void viewAssignedTraineeships_populatesModelAndReturnsView() throws Exception {
        when(committeeService.getAssignedInProgressPositions()).thenReturn(List.of());
        when(committeeService.getCompletableFlags(any())).thenReturn(List.of());
        when(committeeService.getCompanyEvalFlags(any())).thenReturn(Map.of());
        when(committeeService.getProfessorEvalFlags(any())).thenReturn(Map.of());

        mockMvc.perform(get("/In_progress_traineeships"))
                .andExpect(status().isOk())
                .andExpect(view().name("committee/assigned_traineeships"))
                .andExpect(model().attributeExists("positions"))
                .andExpect(model().attributeExists("isCompletable"))
                .andExpect(model().attributeExists("hasCompanyEvalMap"))
                .andExpect(model().attributeExists("hasProfessorEvalMap"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void showSearchStrategySelection_setsPositionId() throws Exception {
        mockMvc.perform(get("/Search_professor").param("positionId", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("committee/search_for_professor"))
                .andExpect(model().attributeExists("positionId"));
    }

    @Test
    void searchProfessors_returnsProfessorsList() throws Exception {
        when(committeeService.findSuitableProfessors(anyInt(), anyString())).thenReturn(List.of());

        mockMvc.perform(get("/Assign_professor_selection")
                        .param("positionId", "5")
                        .param("strategy", "simple"))
                .andExpect(status().isOk())
                .andExpect(view().name("committee/suitable_professors"))
                .andExpect(model().attributeExists("professors"))
                .andExpect(model().attributeExists("positionId"))
                .andExpect(model().attributeExists("selectedStrategy"));
    }

    @Test
    void assignProfessor_successRedirects() throws Exception {
        when(committeeService.assignProfessorToPosition(anyInt(), anyInt())).thenReturn(true);

        mockMvc.perform(post("/Assign_professor")
                        .param("positionId", "5")
                        .param("professorId", "2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/In_progress_traineeships"));
    }

    @Test
    void completeTraineeship_successRedirects() throws Exception {
        mockMvc.perform(post("/Complete_traineeship")
                        .param("positionId", "5")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/In_progress_traineeships"));
    }
}
