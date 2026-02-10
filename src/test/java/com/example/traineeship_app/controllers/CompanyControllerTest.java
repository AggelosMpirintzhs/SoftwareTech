package com.example.traineeship_app.controllers;

import com.example.traineeship_app.Dto.CompanyDto;
import com.example.traineeship_app.domainmodel.Company;
import com.example.traineeship_app.domainmodel.Evaluation;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.services.CompanyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
@Import(CompanyControllerTest.Config.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyService companyService;

    @TestConfiguration
    static class Config {
        @Bean
        public CompanyService companyService() {
            return Mockito.mock(CompanyService.class);
        }
    }

    @Test
    @WithMockUser(username = "companyUser")
    void showCompanyDashboard_withProfile() throws Exception {
        when(companyService.hasProfile("companyUser")).thenReturn(true);

        mockMvc.perform(get("/company_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("company/company_dashboard"));
    }

    @Test
    @WithMockUser(username = "companyUser")
    void showCompanyDashboard_withoutProfile() throws Exception {
        when(companyService.hasProfile("companyUser")).thenReturn(false);

        mockMvc.perform(get("/company_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("company/create_company_profile"));
    }

    @Test
    @WithMockUser(username = "companyUser")
    void saveCompanyProfile_redirectsToDashboard() throws Exception {
        mockMvc.perform(post("/create_company_profile")
                        .param("company_name", "Test Co")
                        .param("location", "Athens")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/company_dashboard"));

        verify(companyService).createProfile(any(CompanyDto.class), eq("companyUser"));
    }

    @Test
    @WithMockUser(username = "companyUser")
    void showCompanyPositions_returnsPositionsView() throws Exception {
        when(companyService.getAvailablePositions("companyUser"))
                .thenReturn(List.of(new TraineeshipPosition()));

        mockMvc.perform(get("/Company_Positions"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("positions"))
                .andExpect(view().name("company/company_positions"));
    }

    @Test
    @WithMockUser
    void showAddPositionForm_returnsView() throws Exception {
        mockMvc.perform(get("/Add_position"))
                .andExpect(status().isOk())
                .andExpect(view().name("/company/add_position"));
    }

    @Test
    @WithMockUser
    void deletePosition_redirects() throws Exception {
        mockMvc.perform(get("/delete/5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/Company_Positions"));

        verify(companyService).deleteTraineeship(5);
    }

    @Test
    @WithMockUser(username = "companyUser", roles = {"COMPANY"})
    void showEvaluationForm_populatesModel() throws Exception {
        // Arrange
        TraineeshipPosition mockPosition = new TraineeshipPosition();
        mockPosition.setId(7);
        mockPosition.setTitle("Test Internship");

        Evaluation eval = new Evaluation();
        eval.setMotivation(5);
        eval.setEfficiency(4);
        eval.setEffectiveness(3);

        Map<String, Object> mockResponse = Map.of(
                "position", mockPosition,
                "evaluation", eval
        );

        when(companyService.getEvaluationData(7)).thenReturn(mockResponse);

        // Act + Assert
        mockMvc.perform(get("/evaluate/7"))
                .andExpect(status().isOk())
                .andExpect(view().name("company/company_evaluation"))
                .andExpect(model().attribute("position", mockPosition))
                .andExpect(model().attribute("evaluation", eval));
    }


    @Test
    @WithMockUser
    void saveEvaluation_redirects() throws Exception {
        mockMvc.perform(post("/evaluate/7")
                        .param("motivation", "5")
                        .param("efficiency", "4")
                        .param("effectiveness", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/Company_Positions"));

        verify(companyService).saveOrUpdateCompanyEvaluation(7, 5, 4, 3);
    }

    @Test
    @WithMockUser(username = "companyUser")
    void showEditProfile_populatesModel() throws Exception {
        Company company = new Company();
        company.setCompanyName("Test");

        when(companyService.getCompanyByUsername("companyUser")).thenReturn(company);

        mockMvc.perform(get("/Edit_company_profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("company"))
                .andExpect(view().name("company/edit_profile"));
    }

    @Test
    @WithMockUser(username = "companyUser")
    void updateProfile_redirects() throws Exception {
        mockMvc.perform(post("/Edit_company_profile")
                        .param("company_name", "UpdatedCo")
                        .param("location", "Thessaloniki")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/company_dashboard"));

        verify(companyService).updateCompanyProfile(any(CompanyDto.class));
    }

    @Test
    @WithMockUser(username = "companyUser")
    void addPosition_redirects() throws Exception {
        when(companyService.getCompanyByUsername("companyUser")).thenReturn(new Company());

        mockMvc.perform(post("/Add_position")
                        .param("title", "Java Intern")
                        .param("start_date", LocalDate.now().toString())
                        .param("end_date", LocalDate.now().plusMonths(3).toString())
                        .param("description", "Develop cool apps")
                        .param("skills", "Java,Spring Boot")
                        .param("topics", "Backend")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/Company_Positions"));

        verify(companyService).addTraineeshipPosition(eq("companyUser"), any());
    }
}
