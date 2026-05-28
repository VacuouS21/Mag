package ru.magistr.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationRequest {
    private String text;
    private int numberOfQuestions;
}