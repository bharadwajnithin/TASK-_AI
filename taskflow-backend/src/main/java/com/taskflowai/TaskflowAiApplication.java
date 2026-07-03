package com.taskflowai;

import com.taskflowai.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class TaskflowAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskflowAiApplication.class, args);
    }
}
