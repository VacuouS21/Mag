//package ru.magistr.view;
//
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.grid.dnd.GridDragSource;
//import com.vaadin.flow.component.grid.dnd.GridDragStartEvent;
//import com.vaadin.flow.component.grid.dnd.GridDropTarget;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.router.Route;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Route
//public class WorkEditView extends VerticalLayout {
//
//    // Класс элемента данных
//    public static class Item {
//        private final int id;
//        private final String name;
//
//        public Item(int id, String name) {
//            this.id = id;
//            this.name = name;
//        }
//
//        public int getId() { return id; }
//        public String getName() { return name; }
//    }
//
//    // Данные для гридов
//    private List<Item> sourceItems = new ArrayList<>();
//    private List<Item> targetItems = new ArrayList<>();
//
//    public WorkEditView() {
//        setSizeFull();
//        setPadding(true);
//        setSpacing(true);
//
//        // Инициализация тестовых данных
//        initializeData();
//
//        // Создание гридов
//        Grid<Item> sourceGrid = createGrid("Source Grid", sourceItems);
//        Grid<Item> targetGrid = createGrid("Target Grid", targetItems);
//
//        // Настройка Drag and Drop
//        configureDragAndDrop(sourceGrid, targetGrid);
//
//        // Кнопка добавления
//        Button addButton = new Button("Add Item", e -> {
//            int newId = targetItems.size() + 1;
//            targetItems.add(new Item(newId, "New Item " + newId));
//            targetGrid.getDataProvider().refreshAll();
//        });
//
//        // Компоновка интерфейса
//        HorizontalLayout gridLayout = new HorizontalLayout(sourceGrid, targetGrid);
//        gridLayout.setSizeFull();
//        gridLayout.setSpacing(true);
//
//        add(gridLayout, addButton);
//        setFlexGrow(1, gridLayout);
//    }
//
//    private void initializeData() {
//        for (int i = 1; i <= 5; i++) {
//            sourceItems.add(new Item(i, "Item " + i));
//        }
//    }
//
//    private Grid<Item> createGrid(String title, List<Item> items) {
//        Grid<Item> grid = new Grid<>();
//        grid.setWidth("400px");
//        grid.setHeight("300px");
//
//        // Добавляем колонки
//        grid.addColumn(Item::getId).setHeader("ID");
//        grid.addColumn(Item::getName).setHeader("Name");
//
//        // Устанавливаем данные
//        grid.setItems(items);
//
//        // Включаем возможность перетаскивания строк
//        grid.setRowsDraggable(true);
//
//        // Стилизация
//        grid.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
//        grid.setHeaderTitle(title);
//
//        return grid;
//    }
//
//    private void configureDragAndDrop(Grid<Item> source, Grid<Item> target) {
//        // Настройка источника перетаскивания для исходного грида
//        GridDragStartEvent<Item> sourceDragSource = new GridDragStartEvent<>(source);
//        sourceDragSource.addGridDragStartListener(e ->
//                e.setDragData(e.getDraggedItems().get(0)));
//
//        // Настройка цели для перетаскивания в целевом гриде
//        GridDropTarget<Item> targetDropTarget = new GridDropTarget<>(target);
//        targetDropTarget.addDropListener(e -> {
//            // Получаем перетаскиваемый элемент
//            Item draggedItem = (Item) e.getDragData().orElse(null);
//
//            if (draggedItem != null && sourceItems.contains(draggedItem)) {
//                // Удаляем из исходного грида
//                sourceItems.remove(draggedItem);
//                source.getDataProvider().refreshAll();
//
//                // Добавляем в целевой грид
//                targetItems.add(draggedItem);
//                target.getDataProvider().refreshAll();
//            }
//        });
//    }
//}