package com.taskflowai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.data.mongodb.uri=mongodb://localhost:27017/taskflow_ai_test"
})
class TaskflowAiApplicationTests {

    @Test
    void contextLoads() {
    }
}
