package com.taskflowai.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailPageResponse {

    private List<EmailResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
