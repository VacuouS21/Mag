package ru.magistr.views.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setConfigView();
        add(getTittle(), getSubtitle(), getLoginView(), getButtonsLayout());
    }

    private void setConfigView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        setPadding(true);
        setSpacing(true);
    }

    private H1 getTittle() {
        // Используем метод getTranslation
        return new H1(getTranslation("login.title"));
    }

    private Paragraph getSubtitle() {
        return new Paragraph(getTranslation("login.subtitle"));
    }

    private HorizontalLayout getButtonsLayout() {
        Button signUpButton = new Button(getTranslation("login.button.signup"), event -> {
            getUI().ifPresent(ui -> ui.navigate("register"));
        });
        signUpButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttonsLayout = new HorizontalLayout(signUpButton);
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        return buttonsLayout;
    }

    private LoginForm getLoginView() {
        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");
//        loginForm.setForgotPasswordButtonVisible(false);

        LoginI18n i18n = LoginI18n.createDefault();

        // Подставляем переводы из properties
        LoginI18n.Form i18nForm = i18n.getForm();
        i18nForm.setTitle(getTranslation("login.form.title"));
        i18nForm.setUsername(getTranslation("login.form.username"));
        i18nForm.setPassword(getTranslation("login.form.password"));
        i18nForm.setSubmit(getTranslation("login.form.submit"));
        i18n.setForm(i18nForm);

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle(getTranslation("login.error.title"));
        i18nErrorMessage.setMessage(getTranslation("login.error.message"));
        i18n.setErrorMessage(i18nErrorMessage);

        loginForm.setI18n(i18n);

        loginForm.addLoginListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });
        loginForm.addForgotPasswordListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        return loginForm;
    }
}