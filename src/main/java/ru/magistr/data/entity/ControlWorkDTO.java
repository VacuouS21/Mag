package ru.magistr.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ControlWorkDTO {
    private int id;
    private String name;
    private String uniqueId;
    private int questionsCount;
}
