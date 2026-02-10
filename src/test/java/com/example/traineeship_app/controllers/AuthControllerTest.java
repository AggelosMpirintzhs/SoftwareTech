package com.example.traineeship_app.controllers;

import com.example.traineeship_app.domainmodel.User;
import com.example.traineeship_app.services.Impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // disables Spring Security filters
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Test
    @WithMockUser
    void showRegisterForm_returnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void registerUser_whenNewUser_redirects() throws Exception {
        when(userService.isUserPresent(any())).thenReturn(false);

        mockMvc.perform(post("/Save")
                        .param("username", "testUser")
                        .param("password", "pass")
                        .param("role", "STUDENT")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?message=User+registered+successfully!"));

        verify(userService).saveUser(any(User.class));
    }

    @Test
    void registerUser_whenUserExists_showsError() throws Exception {
        when(userService.isUserPresent(any())).thenReturn(true);

        mockMvc.perform(post("/Save")
                        .param("username", "testUser")
                        .param("password", "pass")
                        .param("role", "STUDENT")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessage"));
    }

}
