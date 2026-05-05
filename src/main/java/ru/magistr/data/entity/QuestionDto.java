package ru.magistr.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class QuestionDto {
    private int number;
    private String text;
    private String answer;
}