package ru.magistr.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import ru.magistr.entity.GroupDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "group-grid",layout = MainView.class)
@UIScope
@PermitAll
public class GroupGridView extends VerticalLayout {

    private final Grid<GroupDTO> grid = new Grid<>();
    private final TextField groupFilterField = new TextField();
    private final Div filterPanel = new Div();
    private final Div overlay = new Div();
    private boolean panelOpen = false;

    public GroupGridView() {
        createLayout();
        configureGrid();
        configureOverlay();
        configureFilterPanel();
        loadData("");
    }

    private void createLayout() {
        // Кнопка для открытия фильтров
        Button filterButton = new Button("Фильтры", VaadinIcon.FILTER.create());
        filterButton.addClickListener(e -> openFilterPanel());
        filterButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Основное расположение компонентов
        add(filterButton, overlay, filterPanel, grid);
        setSizeFull();
        expand(grid);
    }

    private void configureGrid() {
        grid.addColumn(GroupDTO::getGroupNumber).setHeader("Номер группы").setSortable(true);
        grid.addColumn(GroupDTO::getDirection).setHeader("Направление");
        grid.addColumn(GroupDTO::getStudentCount).setHeader("Количество студентов");
        grid.setWidthFull();

        // Обработчик двойного клика
        grid.addItemDoubleClickListener(event -> {
            GroupDTO group = event.getItem();
            openStudentGrid(group);
        });
    }
    private void openStudentGrid(GroupDTO group) {
        UI.getCurrent().navigate("students/" + group.getGroupNumber());
    }

    private void configureOverlay() {
        overlay.getStyle()
                .set("position", "fixed")
                .set("top", "0")
                .set("left", "0")
                .set("right", "0")
                .set("bottom", "0")
                .set("background", "rgba(0,0,0,0.5)")
                .set("z-index", "99")
                .set("display", "none");

        overlay.addClickListener(e -> closeFilterPanel());
    }

    private void configureFilterPanel() {
        // Начальное состояние - панель скрыта
        filterPanel.getStyle()
                .set("position", "fixed")
                .set("right", "0")
                .set("top", "0")
                .set("bottom", "0")
                .set("background", "#f8f8f8")
                .set("padding", "20px")
                .set("width", "300px")
                .set("border-left", "1px solid #ddd")
                .set("box-shadow", "-2px 0 5px rgba(0,0,0,0.1)")
                .set("z-index", "100")
                .set("transform", "translateX(100%)") // Начальное положение за пределами экрана
                .set("transition", "transform 0.3s ease");

        // Настройка поля фильтра
        groupFilterField.setPlaceholder("Фильтр по номеру группы");
        groupFilterField.setClearButtonVisible(true);
        groupFilterField.setWidthFull();
        groupFilterField.addValueChangeListener(e -> filterGrid(e.getValue()));

        // Кнопка закрытия панели
        Button closeButton = new Button("Закрыть", VaadinIcon.CLOSE.create());
        closeButton.addClickListener(e -> closeFilterPanel());
        closeButton.getStyle().set("margin-top", "10px");

        // Компоновка панели
        VerticalLayout panelContent = new VerticalLayout(
                new H3("Фильтры"),
                new NativeLabel("Номер группы:"),
                groupFilterField,
                closeButton
        );
        panelContent.setSpacing(true);
        filterPanel.add(panelContent);
    }

    private void openFilterPanel() {
        if (!panelOpen) {
            panelOpen = true;
            // После анимации скрываем полностью
            UI.getCurrent().getPage().executeJs(
                    "setTimeout(() => {$0.style.visibility = 'visible';}, 300)",
                    filterPanel
            );
            filterPanel.getStyle()
                    .set("transform", "translateX(0)");

            overlay.getStyle().set("display", "block");

        }
    }

    private void closeFilterPanel() {
        if (panelOpen) {
            panelOpen = false;
            filterPanel.getStyle().set("transform", "translateX(100%)");
            overlay.getStyle().set("display", "none");

            // После анимации скрываем полностью
            UI.getCurrent().getPage().executeJs(
                    "setTimeout(() => {$0.style.visibility = 'hidden';}, 300)",
                    filterPanel
            );
        }
    }

    private void filterGrid(String filterValue) {
        List<GroupDTO> filtered = getGroups().stream()
                .filter(group ->
                        group.getGroupNumber().toLowerCase().contains(filterValue.toLowerCase()))
                .collect(Collectors.toList());
        grid.setItems(filtered);
    }

    private void loadData(String filter) {
        grid.setItems(getGroups());
        groupFilterField.setValue(filter);
    }

    private List<GroupDTO> getGroups() {
        return Arrays.asList(
                new GroupDTO("ГР-101", "Информационные системы", 25),
                new GroupDTO("ГР-102", "Программная инженерия", 28),
                new GroupDTO("ГР-201", "Искусственный интеллект", 22),
                new GroupDTO("ИБ-301", "Кибербезопасность", 20),
                new GroupDTO("ДВ-501", "Разработка игр", 18)
        );
    }

}