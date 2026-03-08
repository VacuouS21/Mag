package ru.magistr.views.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Заголовок
        H1 title = new H1("Вход");
        Paragraph subtitle = new Paragraph("Добро пожаловать в систему контроля знаний");

        // Форма входа
        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);

        // Кнопка регистрации
        Button signUpButton = new Button("Зарегистрироваться", event -> {
            getUI().ifPresent(ui -> ui.navigate("register"));
        });
        signUpButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Контейнер для кнопок
        HorizontalLayout buttonsLayout = new HorizontalLayout(signUpButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        add(title, subtitle, loginForm, buttonsLayout);
    }
}