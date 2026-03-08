//package ru.magistr.view;
//
//import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.html.Anchor;
//import com.vaadin.flow.component.html.H3;
//import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import ru.magistr.entity.GroupDTO;
//
//import java.util.Arrays;
//import java.util.List;
//
//public abstract class AbstractGridView <T> extends VerticalLayout {
//    private Grid<T> grid = new Grid<>();
//
//    public AbstractGridView() {
//        createLayout();
//        configureGridLayout();
//        createFilterLayout();
//        configureFilterLayout
//        getData();
//    }
//
//    public void createLayout() {
//        // Ссылка для открытия фильтров
//        Anchor filterLink = new Anchor("#", "Фильтры");
//        filterLink.addAttachListener(e -> toggleFilterPanel());
//        filterLink.getStyle().set("cursor", "pointer").set("margin-bottom", "10px");
//
//        // Основное расположение компонентов
//        add(filterLink, filterPanel, grid);
//        setSizeFull();
//        expand(grid);
//    }
//    public void configureGrid() {
//        grid.addColumn(GroupDTO::getGroupNumber).setHeader("Номер группы").setSortable(true);
//        grid.addColumn(GroupDTO::getDirection).setHeader("Направление");
//        grid.addColumn(GroupDTO::getStudentCount).setHeader("Количество студентов");
//        grid.setWidthFull();
//    }
//
//    public void configureFilterPanel() {
//        // Настройка панели фильтров
//        filterPanel.setVisible(false);
//        filterPanel.getStyle()
//                .set("position", "absolute")
//                .set("right", "0")
//                .set("top", "0")
//                .set("bottom", "0")
//                .set("background", "#f8f8f8")
//                .set("padding", "20px")
//                .set("width", "300px")
//                .set("border-left", "1px solid #ddd")
//                .set("box-shadow", "-2px 0 5px rgba(0,0,0,0.1)")
//                .set("z-index", "100");
//
//        // Настройка поля фильтра
//        groupFilterField.setPlaceholder("Фильтр по номеру группы");
//        groupFilterField.setClearButtonVisible(true);
//        groupFilterField.setWidthFull();
//        groupFilterField.addValueChangeListener(e -> filterGrid(e.getValue()));
//
//        // Кнопка закрытия панели
//        Button closeButton = new Button("Закрыть", VaadinIcon.CLOSE.create());
//        closeButton.addClickListener(e -> filterPanel.setVisible(false));
//        closeButton.getStyle().set("margin-top", "10px");
//
//        // Компоновка панели
//        VerticalLayout panelContent = new VerticalLayout(
//                (Component) new H3("Фильтры"),
////                (Component) new Label("Номер группы:"),
//                groupFilterField,
//                closeButton
//        );
//        panelContent.setSpacing(true);
//        filterPanel.add(panelContent);
//    }
//
//    private List<T> getData() {
//        return null;
//    }
//}
