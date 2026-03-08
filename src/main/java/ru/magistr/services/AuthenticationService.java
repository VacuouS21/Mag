package ru.magistr.services;

import org.springframework.stereotype.Service;
import ru.magistr.dtos.RegistrationRequestDTO;

/**
 * Сервис для обработки аутентификации и регистрации
 */
@Service
public class AuthenticationService {

    /**
     * Регистрация нового пользователя
     * @param request DTO с данными регистрации
     * @return true если регистрация успешна
     */
    public boolean register(RegistrationRequestDTO request) {
        // Валидация пароля
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Пароль должен быть не менее 8 символов");
        }

        // Валидация группы для студентов
        if ("Ученик".equalsIgnoreCase(request.getUserType()) &&
                (request.getGroupNumber() == null || request.getGroupNumber().isEmpty())) {
            throw new IllegalArgumentException("Для студентов обязательно указать номер группы");
        }

        // Проверка уникальности логина
        if (isUsernameExists(request.getUsername())) {
            throw new IllegalArgumentException("Логин уже занят");
        }

        // Проверка уникальности email
        if (isEmailExists(request.getEmail())) {
            throw new IllegalArgumentException("Email уже зарегистрирован");
        }

        // TODO: Сохранить пользователя в БД
        // userRepository.save(new User(...));

        System.out.println("Пользователь зарегистрирован: " + request.getUsername());
        return true;
    }

    /**
     * Проверка существования пользователя по логину
     * @param username логин
     * @return true если пользователь существует
     */
    public boolean isUsernameExists(String username) {
        // TODO: Реализовать проверку в БД
        return false;
    }

    /**
     * Проверка существования пользователя по email
     * @param email email
     * @return true если пользователь существует
     */
    public boolean isEmailExists(String email) {
        // TODO: Реализовать проверку в БД
        return false;
    }

    /**
     * Аутентификация пользователя
     * @param username логин
     * @param password пароль
     * @return true если аутентификация успешна
     */
    public boolean authenticate(String username, String password) {
        // TODO: Реализовать проверку в БД с проверкой пароля
        return false;
    }
}
