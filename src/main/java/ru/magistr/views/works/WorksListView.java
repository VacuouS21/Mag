package ru.magistr.views.works;

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
import ru.magistr.data.entity.ControlWorkDTO;
import ru.magistr.service.ControlWorkService;
import ru.magistr.views.ControlWorkQuestionsDialog;
import ru.magistr.views.MainView;

import java.util.List;
import java.util.Set;

@Route(value = "documents", layout = MainView.class)
@SpringComponent
@UIScope
@PermitAll
public class WorksListView extends VerticalLayout {

    private Grid<ControlWorkDTO> grid;
    private final ControlWorkService ControlWorkService;
    private Button actionsButton;
    private Button createWorkButton;

    public WorksListView(ControlWorkService ControlWorkDTOService) {
        this.ControlWorkService = ControlWorkDTOService;

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
        Grid.Column<ControlWorkDTO> nameColumn = grid.addColumn(ControlWorkDTO::getName)
                .setHeader("Наименование")
                .setAutoWidth(true)
                .setResizable(true);

        Grid.Column<ControlWorkDTO> idColumn = grid.addColumn(ControlWorkDTO::getUniqueId)
                .setHeader("Уникальный номер")
                .setAutoWidth(true)
                .setResizable(true);

        Grid.Column<ControlWorkDTO> questionsColumn = grid.addColumn(ControlWorkDTO::getQuestionsCount)
                .setHeader("Количество вопросов")
                .setAutoWidth(true)
                .setResizable(true);

        // Устанавливаем данные из сервиса
        grid.setItems(ControlWorkService.getControlWorks());

        // Обработка двойного клика
        grid.addItemDoubleClickListener(event -> {
            ControlWorkDTO work = event.getItem();
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
        contextMenu.addItem("Просмотр вопросов", e -> {
// Получаем список выделенных строк в гриде
            Set<ControlWorkDTO> selectedItems = grid.getSelectedItems();

            // Проверяем, выбрал ли пользователь хоть что-то
            if (selectedItems.isEmpty()) {
                Notification.show("Пожалуйста, выделите контрольную работу галочкой",
                        3000, Notification.Position.MIDDLE);
                return;
            }

            // Берем первую выделенную работу (если выделено несколько)
            ControlWorkDTO selectedWork = selectedItems.iterator().next();

            // Открываем наш диалог
            ControlWorkQuestionsDialog dialog = new ControlWorkQuestionsDialog(selectedWork.getUniqueId());
            dialog.open();
        });
    }

    private void assignToStudent() {
        List<ControlWorkDTO> selected = grid.getSelectedItems().stream().toList();
        if (selected.isEmpty()) {
            Notification.show("Выберите работу для назначения");
            return;
        }
        Notification.show("Назначение работы '" + selected.get(0).getName() + "' студенту");
    }

    private void deleteWork() {
        List<ControlWorkDTO> selected = grid.getSelectedItems().stream().toList();
        if (selected.isEmpty()) {
            Notification.show("Выберите работу для удаления");
            return;
        }
        selected.forEach(ControlWorkService::removeControlWork);
        grid.getDataProvider().refreshAll();
        Notification.show("Удалено работ: " + selected.size());
    }

    private void showStatistics() {
        List<ControlWorkDTO> selected = grid.getSelectedItems().stream().toList();
        if (selected.isEmpty()) {
            Notification.show("Выберите работу для просмотра статистики");
            return;
        }
        Notification.show("Статистика по работе '" + selected.get(0).getName() + "'");
    }

    private void openWorkDetails(ControlWorkDTO work) {
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


}