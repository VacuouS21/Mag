package ru.magistr.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegistrationRequestDTO {
    private String userType; // "Учитель" или "Ученик"
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String username;
    private String password;
    private String groupNumber; // Только для студентов
    private byte[] avatarData; // Аватар пользователя

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "userType='" + userType + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                '}';
    }
}
