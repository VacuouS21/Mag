//package ru.magistr.service;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import ru.magistr.data.entity.RegistrationRequestDTO;
//import ru.magistr.data.entity.User;
//import ru.magistr.data.repository.UserRepository;
//
//@Service
//public class AuthenticationService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    // Внедряем зависимости через конструктор
//    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Transactional
//    public boolean register(RegistrationRequestDTO request) {
//        // Базовая валидация (лучше оставить, хотя Binder на фронте тоже проверяет)
//        if (request.getPassword() == null || request.getPassword().length() < 8) {
//            throw new IllegalArgumentException("Пароль должен быть не менее 8 символов");
//        }
//
//        // Проверка уникальности логина в БД
//        if (isUsernameExists(request.getUsername())) {
//            throw new IllegalArgumentException("Логин уже занят");
//        }
//
//        // Трансформируем русские роли с формы в английские для базы (согласно CHECK constraints)
//        String dbUserType;
//        if ("Учитель".equalsIgnoreCase(request.getUserType())) {
//            dbUserType = "teacher";
//        } else if ("Ученик".equalsIgnoreCase(request.getUserType())) {
//            dbUserType = "student";
//            if (request.getGroupNumber() == null || request.getGroupNumber().isEmpty()) {
//                throw new IllegalArgumentException("Для студентов обязательно указать номер группы");
//            }
//        } else {
//            throw new IllegalArgumentException("Неизвестный тип пользователя");
//        }
//
//        // Создаем и заполняем сущность
//        User newUser = new User();
//        newUser.setLogin(request.getUsername());
//        // Обязательно хешируем пароль перед сохранением!
//        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
//        newUser.setLastName(request.getLastName());
//        newUser.setFirstName(request.getFirstName());
//        newUser.setMiddleName(request.getMiddleName());
//        newUser.setUserType(dbUserType);
//
//        // Сохраняем пользователя в БД
//        userRepository.save(newUser);
//
//        // TODO: Логика добавления студента в группу (таблицы groups и group_members)
//        // Если dbUserType == "student", нужно найти группу по request.getGroupNumber()
//        // и сделать запись в group_members.
//
//        System.out.println("Пользователь успешно зарегистрирован: " + request.getUsername());
//        return true;
//    }
//
//    public boolean isUsernameExists(String username) {
//        return userRepository.existsByLogin(username);
//    }
//
//    public boolean isEmailExists(String email) {
//        // Если добавишь email в базу: return userRepository.existsByEmail(email);
//        return false;
//    }
//
//    public boolean authenticate(String username, String password) {
//        return userRepository.findByLogin(username)
//                // Сравниваем введенный сырой пароль с хешем из базы
//                .map(user -> passwordEncoder.matches(password, user.getPasswordHash()))
//                .orElse(false);
//    }
//}