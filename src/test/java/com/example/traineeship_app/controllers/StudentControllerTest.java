package com.example.traineeship_app.controllers;

import com.example.traineeship_app.Dto.StudentDto;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.services.StudentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(StudentControllerTest.Config.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @TestConfiguration
    static class Config {
        @Bean
        public StudentService studentService() {
            return Mockito.mock(StudentService.class);
        }
    }

    @Test
    @WithMockUser(username = "studentUser")
    void showStudentDashboard_WithProfile() throws Exception {
        when(studentService.hasProfile("studentUser")).thenReturn(true);

        mockMvc.perform(get("/student_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/student_dashboard"));
    }

    @Test
    @WithMockUser(username = "studentUser")
    void showStudentDashboard_WithoutProfile() throws Exception {
        when(studentService.hasProfile("studentUser")).thenReturn(false);

        mockMvc.perform(get("/student_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/create_student_profile"));
    }

    @Test
    @WithMockUser(username = "studentUser")
    void createStudentProfile_redirects() throws Exception {
        mockMvc.perform(post("/create_student_profile")
                        .param("full_name", "Nick Papas")
                        .param("university_id", "1234")
                        .param("preferred_location", "Athens")
                        .param("interests", "AI,ML,Data")
                        .param("skills", "Java,Spring Boot")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student_dashboard"));

        verify(studentService).saveProfile(any(StudentDto.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "studentUser")
    void showEditStudentProfile() throws Exception {
        Student student = new Student();
        student.setStudentName("Nick");
        student.setInterests(List.of("AI", "ML"));
        student.setSkills(List.of("Java", "Spring"));

        when(studentService.getStudentByUsername("studentUser")).thenReturn(student);

        mockMvc.perform(get("/Edit_student_profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("interestsString"))
                .andExpect(model().attributeExists("skillsString"))
                .andExpect(view().name("student/edit_profile"));
    }

    @Test
    @WithMockUser(username = "studentUser")
    void updateStudentProfile_redirects() throws Exception {
        mockMvc.perform(post("/Edit_student_profile")
                        .param("full_name", "Nick Updated")
                        .param("university_id", "1234")
                        .param("preferred_location", "Athens")
                        .param("interests", "AI,ML")
                        .param("skills", "Java,Spring Boot")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student_dashboard"));

        verify(studentService).updateStudentProfile(any());
    }

    @Test
    @WithMockUser(username = "studentUser")
    void updateLogbook_redirects() throws Exception {
        mockMvc.perform(post("/Logbook")
                        .param("logbook", "Week 1 log")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student_dashboard"));

        verify(studentService).updateLogbook("studentUser", "Week 1 log");
    }

    @Test
    @WithMockUser(username = "studentUser")
    void setLookingForTraineeshipTrue_redirects() throws Exception {
        mockMvc.perform(post("/apply").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/Apply_for_traineeship"));

        verify(studentService).setLookingForTraineeshipTrue("studentUser");
    }
}
