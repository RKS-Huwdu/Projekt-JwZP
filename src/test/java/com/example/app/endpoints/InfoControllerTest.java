package com.example.app.endpoints;

import com.example.app.services.InfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InfoService infoService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnAppInfo() throws Exception {
        when(infoService.getAppInfo()).thenReturn("App info here");

        mockMvc.perform(get("/info"))
                .andExpect(status().isOk())
                .andExpect(content().string("App info here"));
    }

    @Test
    void shouldNotReturnAppInfoIfUnauthorized() throws Exception {
        when(infoService.getAppInfo()).thenReturn("App info here");

        mockMvc.perform(get("/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnHealthForAdmin() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldNotReturnHealthForUser() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnLogsForValidDate() throws Exception {
        when(infoService.getLogs("2025-06-08")).thenReturn("log content");

        mockMvc.perform(get("/logs/2025-06-08"))
                .andExpect(status().isOk())
                .andExpect(content().string("log content"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestForInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/logs/bad-date"))
                .andExpect(status().isBadRequest()); // bo rzucasz IllegalArgumentException
    }
}