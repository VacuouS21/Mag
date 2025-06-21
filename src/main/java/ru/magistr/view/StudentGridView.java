package ru.magistr.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import ru.magistr.entity.StudentDTO;

import java.util.Arrays;
import java.util.List;

@Route(value = "/students/:groupNumber", layout = MainView.class)
@UIScope
@PermitAll
public class StudentGridView extends VerticalLayout implements BeforeEnterObserver {
    private final Button backButton = new Button("Назад к списку групп");
    private String groupNumber;

    public StudentGridView() {
        createLayout();
        configureGrid();
    }

    private void createLayout() {
        backButton.setIcon(VaadinIcon.ARROW_BACKWARD.create());
        backButton.addClickListener(e -> UI.getCurrent().navigate(GroupGridView.class));

        add(backButton);
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Получаем параметр из URL
        groupNumber = event.getRouteParameters().get("groupNumber").orElse("");

        // Устанавливаем заголовок
        H2 title = new H2("Студенты группы: " + groupNumber);
        title.getStyle().set("margin-top", "0");
        addComponentAsFirst(title);

        // Загружаем данные
        loadData();
    }

    private void configureGrid() {
        Grid<StudentDTO> grid = new Grid<>();

        grid.addColumn(StudentDTO::getNumber)
                .setHeader("№")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(StudentDTO::getFullName)
                .setHeader("ФИО студента")
                .setSortable(true);

        grid.addComponentColumn(student -> {
            Button messageButton = new Button("Сообщение", VaadinIcon.ENVELOPE.create());
            messageButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            messageButton.addClickListener(e ->
                    Notification.show("Отправлено сообщение для: " + student.getFullName())
            );
            return messageButton;
        }).setHeader("Действия").setWidth("150px").setFlexGrow(0);

        grid.setWidthFull();
        add(grid);
        expand(grid);
    }


    private void loadData() {
        // Заглушка данных - в реальном приложении загрузка из БД
        List<StudentDTO> students = Arrays.asList(
                new StudentDTO(1, "Иванов Иван Иванович"),
                new StudentDTO(2, "Петрова Мария Сергеевна"),
                new StudentDTO(3, "Сидоров Алексей Дмитриевич"),
                new StudentDTO(4, "Кузнецова Екатерина Владимировна")
        );

        // Установка данных в грид
        ((Grid<StudentDTO>) getChildren()
                .filter(c -> c instanceof Grid)
                .findFirst().get())
                .setItems(students);
    }

}
