package ru.magistr.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class StudentDTO {
    private int number;
    private String fullName;
}
