package com.bandanize.backend.controllers;

import com.bandanize.backend.dtos.BandDTO;
import com.bandanize.backend.services.BandService;
import com.bandanize.backend.services.JwtService;
import com.bandanize.backend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(BandController.class)
class BandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BandService bandService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private com.bandanize.backend.repositories.UserRepository userRepository;

    @Test
    @WithMockUser
    void getAllBands_ReturnsListOfBands() throws Exception {
        BandDTO band1 = new BandDTO();
        band1.setId(1L);
        band1.setName("Test Band 1");

        BandDTO band2 = new BandDTO();
        band2.setId(2L);
        band2.setName("Test Band 2");

        List<BandDTO> bands = Arrays.asList(band1, band2);

        when(bandService.getAllBands()).thenReturn(bands);

        mockMvc.perform(get("/api/bands")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())) // CSRF is disabled in config but good practice to include in test context
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Band 1"))
                .andExpect(jsonPath("$[1].name").value("Test Band 2"));
    }
}
