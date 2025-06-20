package ru.magistr.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login") // Указывает, что это страница по адресу /login
@AnonymousAllowed // Разрешает доступ без аутентификации
public class LoginView extends VerticalLayout {

    public LoginView() {

        // Центрирование формы
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");
        add(new H1("Login Page"),
                loginForm);
    }
}