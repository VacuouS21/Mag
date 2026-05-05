package ru.magistr.views.works;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
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
import org.springframework.beans.factory.ObjectProvider;
import ru.magistr.data.entity.ControlWorkDTO;
import ru.magistr.service.ControlWorkService;
import ru.magistr.views.ControlWorkQuestionsDialog;
import ru.magistr.views.MainView;
import ru.magistr.views.WorkResultsView;

import java.util.List;
import java.util.Set;

@Route(value = "documents", layout = MainView.class)
@SpringComponent
@UIScope
@PermitAll
public class WorksListView extends VerticalLayout {

    private Grid<ControlWorkDTO> grid;
    private final ControlWorkService controlWorkService;

    // Фабрика для получения свежих экземпляров диалога (Prototype)
    private final ObjectProvider<ControlWorkQuestionsDialog> dialogProvider;

    private Button actionsButton;
    private Button createWorkButton;

    // Внедряем ObjectProvider через конструктор
    public WorksListView(ControlWorkService controlWorkService,
                         ObjectProvider<ControlWorkQuestionsDialog> dialogProvider) {
        this.controlWorkService = controlWorkService;
        this.dialogProvider = dialogProvider;

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
        createWorkButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
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
        grid.setItems(controlWorkService.getControlWorks());

        // Обработка двойного клика
        grid.addItemDoubleClickListener(event -> {
            ControlWorkDTO work = event.getItem();
            openWorkDetails(work);
        });

        // Применяем встроенные стили Lumo
        grid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,       // Эффект "зебры" для удобного чтения строк
                GridVariant.LUMO_COLUMN_BORDERS,    // Разделители между колонками
                GridVariant.LUMO_WRAP_CELL_CONTENT  // Перенос длинного текста (чтобы он не обрезался многоточием)
        );

// Добавляем легкую тень и скругленные углы для более современного "карточного" вида
        grid.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        grid.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        grid.getStyle().set("overflow", "hidden"); // Чтобы углы не "срезались" содержимым

        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setSizeFull();
    }

    private void initActionsButton() {
        actionsButton = new Button("Действия", new Icon(VaadinIcon.ELLIPSIS_DOTS_V));

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setTarget(actionsButton);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("Назначить студенту/группе", e -> assignToStudent());
        contextMenu.addItem("Удалить работу", e -> deleteWork());
//        contextMenu.addItem("Статистика", e -> showStatistics());
        contextMenu.addItem("Редактировать работу", e -> {editControlWork();});
        contextMenu.addItem("Просмотреть результаты", e -> viewResults());
    }
    private void editControlWork(){
        Set<ControlWorkDTO> selectedItems = grid.getSelectedItems();
        if (selectedItems.isEmpty()) {
            Notification.show("Пожалуйста, выделите контрольную работу галочкой", 3000, Notification.Position.MIDDLE);
            return;
        }

        ControlWorkDTO selectedWork = selectedItems.iterator().next();

        // Просим у Spring НОВЫЙ экземпляр диалога со всеми внедренными сервисами
        ControlWorkQuestionsDialog dialog = dialogProvider.getObject();
        // Передаем данные и открываем
        dialog.openDialog(selectedWork.getUniqueId(), false);
    }

    private void openWorkDetails(ControlWorkDTO work) {
        // Просим у Spring НОВЫЙ экземпляр диалога
        ControlWorkQuestionsDialog dialog = dialogProvider.getObject();
        // Передаем данные и открываем в режиме просмотра
        dialog.openDialog(work.getUniqueId(), true);
    }

    // Добавьте сам метод перехода:
    private void viewResults() {
        Set<ControlWorkDTO> selectedItems = grid.getSelectedItems();
        if (selectedItems.isEmpty()) {
            Notification.show("Выберите контрольную работу для просмотра результатов", 3000, Notification.Position.MIDDLE);
            return;
        }

        ControlWorkDTO selectedWork = selectedItems.iterator().next();
        // Переходим на новый роут, передавая ID работы как параметр
        getUI().ifPresent(ui -> ui.navigate(WorkResultsView.class, selectedWork.getUniqueId()));
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
        selected.forEach(e->controlWorkService.removeControlWork(e));
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
        setPadding(true); // Даем отступы от краев браузера
        setSpacing(true);

        add(header, buttonsLayout, gridContainer);
        setFlexGrow(1, gridContainer); // Грид занимает все доступное пространство
    }


}