package ru.magistr.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentResultDTO {
    private String studentFio;
    private String workId;
    private String questionText;
    private double matchPercentage;
    private String expectedAnswer;
    private String studentAnswer;

    // Конструктор, геттеры и сеттеры
}