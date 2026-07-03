package com.taskflowai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflowai.dto.ai.AiExtractionPayload;
import com.taskflowai.dto.auth.RegisterRequest;
import com.taskflowai.model.Priority;
import com.taskflowai.repository.TaskRepository;
import com.taskflowai.repository.UserRepository;
import com.taskflowai.service.GeminiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.data.mongodb.uri=mongodb://localhost:27017/taskflow_ai_test",
        "app.gemini.api-key=test-key"
})
class AiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @MockBean
    private GeminiClient geminiClient;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        RegisterRequest register = RegisterRequest.builder()
                .fullName("AI Tester")
                .email("ai@taskflow.ai")
                .password("password123")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andReturn();

        authToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        AiExtractionPayload payload = new AiExtractionPayload();
        payload.setClientName("Acme Corp");
        payload.setProjectName("Website");

        AiExtractionPayload.AiTaskPayload task1 = new AiExtractionPayload.AiTaskPayload();
        task1.setTitle("Update Login Page");
        task1.setDescription("Update login page");
        task1.setPriority("MEDIUM");
        task1.setDueDateText("Friday");

        AiExtractionPayload.AiTaskPayload task2 = new AiExtractionPayload.AiTaskPayload();
        task2.setTitle("Send Deployment Link");
        task2.setDescription("Send deployment link to client");
        task2.setPriority("LOW");

        payload.setTasks(List.of(task1, task2));

        when(geminiClient.extractTasks(anyString())).thenReturn(payload);
    }

    @Test
    void extractTasks_returnsParsedTasks() throws Exception {
        mockMvc.perform(post("/api/ai/extract")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "content", "Please update the login page before Friday and send deployment link.",
                                "saveTasks", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks", hasSize(2)))
                .andExpect(jsonPath("$.tasks[0].title", is("Update Login Page")))
                .andExpect(jsonPath("$.clientName", is("Acme Corp")));
    }

    @Test
    void extractAndSaveTasks_persistsToDatabase() throws Exception {
        mockMvc.perform(post("/api/ai/extract")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "content", "Please update the login page before Friday.",
                                "saveTasks", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savedTasks", hasSize(2)))
                .andExpect(jsonPath("$.savedTasks[0].priority", is("MEDIUM")));
    }
}
