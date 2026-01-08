package ru.magistr.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import ru.magistr.view.services.ControlWorkService;

import java.util.List;

@Route(value = "documents", layout = MainView.class)
@SpringComponent
@UIScope
@PermitAll
public class WorksListView extends VerticalLayout {

    private Grid<ControlWork> grid;
    private final ControlWorkService controlWorkService;
    private Button actionsButton;
    private Button createWorkButton;

    public WorksListView(ControlWorkService controlWorkService) {
        this.controlWorkService = controlWorkService;

        initComponents();
        addComponents();
        setSizeFull();
        setPadding(false);
        setSpacing(false);
    }

    private void initComponents() {
        // Инициализация грида
        initGrid();

        // Кнопка действий над гридом
        initActionsButton();

        // Кнопка создания новой контрольной работы
        createWorkButton = new Button("Создать контрольную работу", new Icon(VaadinIcon.PLUS));
        createWorkButton.addClickListener(event -> {
            // Переход на страницу генерации
            getUI().ifPresent(ui -> ui.navigate("generate"));
        });
    }

    private void initGrid() {
        // Создаем грид без привязки к классу данных для полного контроля
        grid = new Grid<>();

        // Добавляем колонки вручную
        Grid.Column<ControlWork> nameColumn = grid.addColumn(ControlWork::getName)
                .setHeader("Наименование")
                .setAutoWidth(true)
                .setResizable(true);

        Grid.Column<ControlWork> idColumn = grid.addColumn(ControlWork::getUniqueId)
                .setHeader("Уникальный номер")
                .setAutoWidth(true)
                .setResizable(true);

        Grid.Column<ControlWork> questionsColumn = grid.addColumn(ControlWork::getQuestionsCount)
                .setHeader("Количество вопросов")
                .setAutoWidth(true)
                .setResizable(true);

        // Устанавливаем данные из сервиса
        grid.setItems(controlWorkService.getControlWorks());

        // Обработка двойного клика
        grid.addItemDoubleClickListener(event -> {
            ControlWork work = event.getItem();
            openWorkDetails(work);
        });

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setSizeFull();
    }

    private void initActionsButton() {
        actionsButton = new Button("Действия", new Icon(VaadinIcon.ELLIPSIS_DOTS_V));

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(actionsButton);
        contextMenu.setOpenOnClick(true);

        contextMenu.addItem("Назначить студенту", e -> assignToStudent());
        contextMenu.addItem("Удалить работу", e -> deleteWork());
        contextMenu.addItem("Статистика", e -> showStatistics());
    }

    private void assignToStudent() {
        List<ControlWork> selected = grid.getSelectedItems().stream().toList();
        if (selected.isEmpty()) {
            Notification.show("Выберите работу для назначения");
            return;
        }
        Notification.show("Назначение работы '" + selected.get(0).getName() + "' студенту");
    }

    private void deleteWork() {
        List<ControlWork> selected = grid.getSelectedItems().stream().toList();
        if (selected.isEmpty()) {
            Notification.show("Выберите работу для удаления");
            return;
        }
        selected.forEach(controlWorkService::removeControlWork);
        grid.getDataProvider().refreshAll();
        Notification.show("Удалено работ: " + selected.size());
    }

    private void showStatistics() {
        List<ControlWork> selected = grid.getSelectedItems().stream().toList();
        if (selected.isEmpty()) {
            Notification.show("Выберите работу для просмотра статистики");
            return;
        }
        Notification.show("Статистика по работе '" + selected.get(0).getName() + "'");
    }

    private void openWorkDetails(ControlWork work) {
        Notification.show("Открытие деталей работы: " + work.getName());
        // Здесь логика открытия вкладки с вопросами
        // getUI().ifPresent(ui -> ui.navigate("work-details/" + work.getUniqueId()));
    }

    // Метод для обновления данных в гриде
    public void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    private void addComponents() {
        // Заголовок
        H1 header = new H1("Управление контрольными работами");
        header.getStyle().set("margin-top", "0");
        header.getStyle().set("margin-bottom", "10px");

        // Панель с кнопками
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        buttonsLayout.getStyle().set("margin-bottom", "10px");
        buttonsLayout.add(createWorkButton, actionsButton);

        // Контейнер для грида, который займет все оставшееся пространство
        VerticalLayout gridContainer = new VerticalLayout();
        gridContainer.setSizeFull();
        gridContainer.setPadding(false);
        gridContainer.setSpacing(false);
        gridContainer.add(grid);

        // Основной layout
        setSizeFull();
        setPadding(true);
        setSpacing(false);

        add(header, buttonsLayout, gridContainer);
        setFlexGrow(1, gridContainer); // Грид занимает все доступное пространство
    }

    // Внутренний класс для представления контрольной работы
    public static class ControlWork {
        private String name;
        private String uniqueId;
        private int questionsCount;

        public ControlWork(String name, String uniqueId, int questionsCount) {
            this.name = name;
            this.uniqueId = uniqueId;
            this.questionsCount = questionsCount;
        }

        public String getName() {
            return name;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public int getQuestionsCount() {
            return questionsCount;
        }
    }
}