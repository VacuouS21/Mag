package ru.magistr.views.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import ru.magistr.dtos.RegistrationRequestDTO;
import ru.magistr.services.AuthenticationService;

import java.io.InputStream;

@Route("register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final AuthenticationService authenticationService;
    private final Binder<RegistrationRequestDTO> binder;

    // Поля формы
    private RadioButtonGroup<String> roleGroup;
    private TextField lastNameField;
    private TextField firstNameField;
    private TextField middleNameField;
    private TextField groupNumberField;
    private EmailField emailField;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Upload avatarUpload;
    private Checkbox agreementCheckbox;
    private Button registerButton;

    // Для хранения загруженного аватара
    private byte[] avatarData;

    public RegisterView(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        this.binder = new Binder<>(RegistrationRequestDTO.class);

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(false);
        setSpacing(false);
        setSizeFull();

        // Основной контейнер с белым фоном
        Div mainContainer = new Div();
        mainContainer.setMaxWidth("600px");
        mainContainer.setMinHeight("100vh");
        mainContainer.getStyle().set("background", "white");
        mainContainer.getStyle().set("box-shadow", "0 0 30px rgba(0,0,0,0.1)");
        mainContainer.getStyle().set("padding", "40px");
        mainContainer.getStyle().set("overflow-y", "auto");

        // Заголовок
        H1 title = new H1("Регистрация");
        title.getStyle().set("color", "#1a1a1a").set("margin-top", "0");
        Paragraph subtitle = new Paragraph("Создайте учетную запись в системе");
        subtitle.getStyle().set("color", "#666").set("margin-bottom", "30px");

        // Форма
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        // Выбор роли
        roleGroup = new RadioButtonGroup<>();
        roleGroup.setLabel("Тип пользователя *");
        roleGroup.setItems("Учитель", "Ученик");
        roleGroup.setValue("Учитель");
        roleGroup.addValueChangeListener(event -> updateGroupFieldVisibility());

        // Фамилия
        lastNameField = new TextField("Фамилия *");
        lastNameField.setPlaceholder("Введите фамилию");
        lastNameField.setRequired(true);
        lastNameField.setWidth("100%");

        // Имя
        firstNameField = new TextField("Имя *");
        firstNameField.setPlaceholder("Введите имя");
        firstNameField.setRequired(true);
        firstNameField.setWidth("100%");

        // Отчество
        middleNameField = new TextField("Отчество");
        middleNameField.setPlaceholder("Введите отчество");
        middleNameField.setWidth("100%");

        // Номер группы (только для студентов)
        groupNumberField = new TextField("Номер группы *");
        groupNumberField.setPlaceholder("Например: БИ-21-1");
        groupNumberField.setVisible(false);
        groupNumberField.setWidth("100%");

        // Email
        emailField = new EmailField("Email *");
        emailField.setPlaceholder("your.email@example.com");
        emailField.setRequired(true);
        emailField.setWidth("100%");

        // Логин
        usernameField = new TextField("Логин *");
        usernameField.setPlaceholder("Выберите уникальный логин");
        usernameField.setRequired(true);
        usernameField.setWidth("100%");

        // Пароль
        passwordField = new PasswordField("Пароль *");
        passwordField.setPlaceholder("Минимум 8 символов");
        passwordField.setRequired(true);
        passwordField.setWidth("100%");

        // Подтверждение пароля
        confirmPasswordField = new PasswordField("Подтвердите пароль *");
        confirmPasswordField.setPlaceholder("Повторите пароль");
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setWidth("100%");

        // Загрузка аватара
        MemoryBuffer buffer = new MemoryBuffer();
        avatarUpload = new Upload(buffer);
        avatarUpload.setMaxFileSize(5 * 1024 * 1024); // 5MB
        avatarUpload.setAcceptedFileTypes("image/*");
        avatarUpload.setUploadButton(new Button("Загрузить аватар"));
        avatarUpload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            try {
                avatarData = inputStream.readAllBytes();
                Notification.show("Аватар загружен успешно").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                Notification.show("Ошибка при загрузке аватара").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Согласие на условия
        agreementCheckbox = new Checkbox("Я согласен с условиями использования *");

        // Добавляем поля в форму
        formLayout.add(roleGroup);
        formLayout.add(lastNameField);
        formLayout.add(firstNameField);
        formLayout.add(middleNameField);
        formLayout.add(groupNumberField);
        formLayout.add(emailField);
        formLayout.add(usernameField);
        formLayout.add(passwordField);
        formLayout.add(confirmPasswordField);
        formLayout.add(avatarUpload);
        formLayout.add(agreementCheckbox);

        formLayout.setColspan(roleGroup, 2);
        formLayout.setColspan(lastNameField, 1);
        formLayout.setColspan(firstNameField, 1);
        formLayout.setColspan(middleNameField, 2);
        formLayout.setColspan(groupNumberField, 2);
        formLayout.setColspan(emailField, 2);
        formLayout.setColspan(usernameField, 2);
        formLayout.setColspan(passwordField, 1);
        formLayout.setColspan(confirmPasswordField, 1);
        formLayout.setColspan(avatarUpload, 2);
        formLayout.setColspan(agreementCheckbox, 2);

        // Кнопки
        registerButton = new Button("Зарегистрироваться", event -> handleRegistration());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidth("100%");
        registerButton.getStyle().set("height", "44px").set("font-size", "16px").set("font-weight", "600");

        Button backButton = new Button("Уже есть учетная запись?", event -> {
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.setWidth("100%");

        HorizontalLayout buttonsLayout = new HorizontalLayout(registerButton, backButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setSpacing(true);

        // Настройка Binder с валидацией
        setupBinder();

        // Добавляем все в контейнер
        mainContainer.add(title, subtitle, formLayout, buttonsLayout);
        add(mainContainer);

        getStyle().set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("margin", "0")
                .set("padding", "0");
    }

    private void setupBinder() {
        // Валидация фамилии
        binder.forField(lastNameField)
                .asRequired("Фамилия обязательна")
                .withValidator(new StringLengthValidator("Фамилия должна быть от 2 до 50 символов", 2, 50))
                .bind("lastName");

        // Валидация имени
        binder.forField(firstNameField)
                .asRequired("Имя обязательно")
                .withValidator(new StringLengthValidator("Имя должно быть от 2 до 50 символов", 2, 50))
                .bind("firstName");

        // Валидация отчества (опционально)
        binder.forField(middleNameField)
                .bind("middleName");

        // Валидация группы для студентов
        binder.forField(groupNumberField)
                .withValidator((value, context) -> {
                    if ("Ученик".equals(roleGroup.getValue()) && (value == null || value.isEmpty())) {
                        return com.vaadin.flow.data.binder.ValidationResult.error("Номер группы обязателен для студентов");
                    }
                    return com.vaadin.flow.data.binder.ValidationResult.ok();
                })
                .bind("groupNumber");

        // Валидация email
        binder.forField(emailField)
                .asRequired("Email обязателен")
                .withValidator(new EmailValidator("Email некорректный"))
                .bind("email");

        // Валидация логина
        binder.forField(usernameField)
                .asRequired("Логин обязателен")
                .withValidator(new StringLengthValidator("Логин должен быть от 3 до 20 символов", 3, 20))
                .bind("username");

        // Валидация пароля
        binder.forField(passwordField)
                .asRequired("Пароль обязателен")
                .withValidator(new StringLengthValidator("Пароль должен быть не менее 8 символов", 8, 255))
                .bind("password");

        // Валидация подтверждения пароля
        binder.forField(confirmPasswordField)
                .asRequired("Подтверждение пароля обязательно")
                .withValidator((value, context) -> {
                    if (!value.equals(passwordField.getValue())) {
                        return com.vaadin.flow.data.binder.ValidationResult.error("Пароли не совпадают");
                    }
                    return com.vaadin.flow.data.binder.ValidationResult.ok();
                })
                .bind("password");
    }

    private void updateGroupFieldVisibility() {
        boolean isStudent = "Ученик".equals(roleGroup.getValue());
        groupNumberField.setVisible(isStudent);
    }

    private void handleRegistration() {
        try {
            if (!agreementCheckbox.getValue()) {
                Notification.show("Вы должны согласиться с условиями использования")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            RegistrationRequestDTO registrationData = new RegistrationRequestDTO();
            binder.writeBean(registrationData);

            // Устанавливаем тип пользователя
            registrationData.setUserType(roleGroup.getValue());

            // Устанавливаем аватар если был загружен
            if (avatarData != null) {
                registrationData.setAvatarData(avatarData);
            }

            // Вызываем сервис для регистрации
            authenticationService.register(registrationData);

            Notification.show("Регистрация успешна! Перенаправление на вход...")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            getUI().ifPresent(ui -> ui.navigate("login"));

        } catch (ValidationException e) {
            Notification.show("Пожалуйста, заполните все поля корректно")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Ошибка при регистрации: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
