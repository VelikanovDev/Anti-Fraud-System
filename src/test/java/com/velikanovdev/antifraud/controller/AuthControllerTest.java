package com.velikanovdev.antifraud.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velikanovdev.antifraud.dto.UserDTO;
import com.velikanovdev.antifraud.entity.User;
import com.velikanovdev.antifraud.enums.Role;
import com.velikanovdev.antifraud.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    private static final String BASE_API_URL = "/api/auth/user";

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private UserService userService;

    @Test
    void testRegisterUser_ValidUser_Created() throws Exception {
        // Create a valid UserDTO for registration
        UserDTO validUserDTO = new UserDTO("John Doe", "john.doe", "password123");
        User user = new User(1L, validUserDTO.getName(), validUserDTO.getUsername(), validUserDTO.getPassword(),
                Role.ADMINISTRATOR, true);

        // Configure the UserService mock to return a CREATED response
        when(userService.registerUser(validUserDTO)).thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .body(user));

        mockMvc.perform(post(BASE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(asJsonString(user)));
    }

    @Test
    void testRegisterUser_InvalidUser_BadRequest() throws Exception {
        // Create an invalid UserDTO for registration (missing password field)
        UserDTO invalidUserDTO = new UserDTO("John Doe", "john.doe", "");

        // Configure the UserService mock to return a CONFLICT response
        when(userService.registerUser(invalidUserDTO)).thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post(BASE_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidUserDTO)))
                .andExpect(status().isBadRequest());
    }

    // Helper method to convert objects to JSON string
    private static String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
