package ru.magistr.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;


@Route("home")
@PermitAll
//@RolesAllowed("USER")
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new H1("Welcome to the Home Page!"));
    }
}