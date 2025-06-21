package ru.magistr.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class GroupDTO {
    private String groupNumber;
    private String direction;
    private int studentCount;
}
