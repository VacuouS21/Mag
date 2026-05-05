package ru.magistr.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.util.List;

@Route(value = "tasks", layout = MainView.class) // Убедитесь, что этот Route совпадает с вашим меню
@PermitAll
public class AssignedWorksListView extends VerticalLayout {

    private Grid<AssignedWorkDto> grid;

    public AssignedWorksListView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Заголовок страницы
        H2 header = new H2("Назначенные работы");
        header.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.MEDIUM);

        // Инициализация таблицы
        initGrid();

        add(header, grid);
    }

    private void initGrid() {
        grid = new Grid<>();
        grid.setSizeFull();
        grid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_WRAP_CELL_CONTENT
        );

        // Колонка: Название работы
        grid.addColumn(AssignedWorkDto::name)
                .setHeader("Название")
                .setAutoWidth(true)
                .setFlexGrow(2); // Даем больше места названию

        // Колонка: Преподаватель
        grid.addColumn(AssignedWorkDto::teacherName)
                .setHeader("Преподаватель")
                .setAutoWidth(true);

        // Колонка: Срок сдачи (с красивым бейджиком)
        grid.addComponentColumn(work -> {
            Span deadlineSpan = new Span(work.deadline().toString());
            // Если дедлайн сегодня или завтра, можно подкрасить красным
            if (work.deadline().isBefore(LocalDate.now().plusDays(2))) {
                deadlineSpan.addClassNames(LumoUtility.TextColor.ERROR, LumoUtility.FontWeight.BOLD);
            }
            return deadlineSpan;
        }).setHeader("Срок сдачи").setAutoWidth(true);

        // Колонка: Кнопка действия "В работу"
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, work) -> {
            button.setText("В работу");
            button.setIcon(VaadinIcon.EDIT.create());
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            // ЛОГИКА ПЕРЕХОДА:
            // При клике переходим на TakeControlWorkView, передавая ID работы в URL
            button.addClickListener(e -> {
                UI.getCurrent().navigate(TakeControlWorkView.class, work.workId());
            });
        })).setHeader("Действие").setAutoWidth(true).setFlexGrow(0);

        // Загружаем тестовые данные
        grid.setItems(getMockAssignedWorks());
    }

    // --- ЗАГЛУШКИ ДАННЫХ И DTO ---

    // Временная DTO для отображения в таблице (позже замените на вашу реальную сущность/DTO)
    public record AssignedWorkDto(String workId, String name, String teacherName, LocalDate deadline) {}

    private List<AssignedWorkDto> getMockAssignedWorks() {
        return List.of(
                new AssignedWorkDto("EXT-001-MATH", "Контрольная работа №1: Математический анализ", "Иван Иванов", LocalDate.now().plusDays(1)),
                new AssignedWorkDto("EXT-002-PHYSICS", "Тест по кинематике", "Петр Петров", LocalDate.now().plusDays(5)),
                new AssignedWorkDto("EXT-003-HISTORY", "Семилетняя война и Российская империя", "Иван Иванов", LocalDate.now().plusDays(10))
        );
    }
}
