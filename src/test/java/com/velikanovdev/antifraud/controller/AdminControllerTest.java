package com.velikanovdev.antifraud.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velikanovdev.antifraud.dto.OperationDTO;
import com.velikanovdev.antifraud.dto.RoleDTO;
import com.velikanovdev.antifraud.entity.User;
import com.velikanovdev.antifraud.enums.Role;
import com.velikanovdev.antifraud.enums.UserAccess;
import com.velikanovdev.antifraud.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    private final String BASE_API_URL = "/api/auth";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void testSetUserRole_ValidRole_Success() throws Exception {
        // Arrange
        RoleDTO roleDTO = new RoleDTO("testUser", Role.MERCHANT);
        when(userService.setUserRole(roleDTO)).thenReturn(ResponseEntity.ok().build());

        // Act & Assert
        mockMvc.perform(put(BASE_API_URL + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(roleDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void testSetUserRole_InvalidRole_BadRequest() throws Exception {
        // Arrange
        String testUsername = "testUser";
        RoleDTO roleDTO = new RoleDTO(testUsername, Role.ADMINISTRATOR); // Correct username for the mocked user
        when(userService.setUserRole(any())).thenReturn(ResponseEntity.badRequest().build());

        // Act & Assert
        mockMvc.perform(put(BASE_API_URL + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(roleDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void testSetAccountAccess_ValidUser_Success() throws Exception {
        // Arrange
        String username = "testUser";
        OperationDTO operationDTO = new OperationDTO(username, UserAccess.UNLOCK);
        // Create a mocked User
        User mockedUser = mock(User.class);

        when(userService.getUserByUsername(username)).thenReturn(Optional.of(mockedUser));
        when(userService.setAccountAccess(operationDTO)).thenReturn(ResponseEntity.ok().build());

        // Act & Assert
        mockMvc.perform(put(BASE_API_URL + "/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(operationDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void testSetAccountAccess_InvalidUser_BadRequest() throws Exception {
        // Arrange
        OperationDTO operationDTO = new OperationDTO("nonExistingUser", UserAccess.UNLOCK);
        when(userService.setAccountAccess(any())).thenReturn(ResponseEntity.badRequest().build());

        // Act & Assert
        mockMvc.perform(put(BASE_API_URL + "/access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(operationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void testDeleteUser_ValidUser_Success() throws Exception {
        // Arrange
        String username = "testUser";
        User mockedUser = mock(User.class);
        when(userService.getUserByUsername(username)).thenReturn(Optional.of(mockedUser));
        when(userService.deleteUser(username)).thenReturn(ResponseEntity.ok().build());

        // Act & Assert
        mockMvc.perform(delete(BASE_API_URL + "/user/{username}", username)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void testDeleteUser_InvalidUser_NotFound() throws Exception {
        // Arrange
        String username = "nonExistingUser";
        when(userService.deleteUser(username)).thenReturn(ResponseEntity.notFound().build());

        // Act & Assert
        mockMvc.perform(delete("/user/{username}", username)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void testShowAllUsers_UsersExist_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setName("Test User");
        user.setRole(Role.MERCHANT);
        user.setEnabled(true);
        List<User> users = Collections.singletonList(user);
        when(userService.showAllUsers()).thenReturn(ResponseEntity.ok().build());

        // Act & Assert
        mockMvc.perform(get(BASE_API_URL + "/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(users)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void testShowAllUsers_NoUsers_NoContent() throws Exception {
        // Arrange
        when(userService.showAllUsers()).thenReturn(ResponseEntity.noContent().build());

        // Act & Assert
        mockMvc.perform(get(BASE_API_URL + "/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // Helper method to convert objects to JSON string
    private static String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
