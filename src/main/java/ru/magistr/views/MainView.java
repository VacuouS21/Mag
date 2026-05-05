package ru.magistr.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route("")
@PermitAll
public class MainView extends AppLayout {

    public MainView() {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Проверочные работы");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Avatar avatar = new Avatar("Пользователь");
        avatar.getStyle()
                .set("cursor", "pointer")
                .set("margin-right", "var(--lumo-space-m)");

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(avatar); // Привязываем меню к аватару
        contextMenu.setOpenOnClick(true); // Открывать при клике

        contextMenu.addItem("Профиль", e -> showProfile());
        contextMenu.addItem("Настройки", e -> openSettings());
        contextMenu.addItem("Выход", e -> logout());

// Контейнер для правой части
        HorizontalLayout rightSection = new HorizontalLayout(avatar);
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);

        // Основной контейнер для навбара
        HorizontalLayout navbarLayout = new HorizontalLayout(title, rightSection);
        navbarLayout.setWidthFull();
        navbarLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN); // Растянуть на всю ширину
        navbarLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        SideNav nav = getSideNav();

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
        addToNavbar(toggle, navbarLayout);
    }

    private SideNav getSideNav() {
        SideNav sideNav = new SideNav();
        sideNav.addItem(
                new SideNavItem("Мои студенты", "/group-grid",
                        VaadinIcon.USER_HEART.create()),
                new SideNavItem("Создание контрольных", "/generate",
                        VaadinIcon.PACKAGE.create()),
                new SideNavItem("Созданные работы", "/documents",
                        VaadinIcon.RECORDS.create()),
                new SideNavItem("Назначенные работы", "/tasks", VaadinIcon.LIST.create()),
                new SideNavItem("Мои чаты", "/chats",
                        VaadinIcon.CHART.create()),
                new SideNavItem("Результаты", "/analitycs",
                        VaadinIcon.CHAT.create()));
        return sideNav;
    }

    private void showProfile() {
        // Логика открытия профиля
        getUI().ifPresent(ui -> ui.navigate("profile"));
    }

    private void openSettings() {
        // Логика открытия настроек
        getUI().ifPresent(ui -> ui.navigate("settings"));
    }

    private void logout() {
        // Логика выхода
        getUI().ifPresent(ui -> ui.navigate("login"));
    }
}