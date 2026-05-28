package ru.magistr.views.works;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;
import ru.magistr.data.entity.ControlWorkDTO;
import ru.magistr.service.ControlWorkService;
import ru.magistr.views.MainView;

import java.io.InputStream;
import java.util.UUID;
import java.util.Scanner;

@Route(value = "generate", layout = MainView.class)
@SpringComponent
@UIScope
@PermitAll
public class GenerateWorkView extends VerticalLayout {

    private TextArea textArea;
    private NumberField numberOfQuestionsField;
    private Upload upload;
    private Button generateButton;
    private TextField workNameField;
    private TextField uniqueIdField;
    private RadioButtonGroup<String> inputMethodGroup;
    private VerticalLayout textInputLayout;
    private VerticalLayout fileInputLayout;
    private final ControlWorkService controlWorkService;

    public GenerateWorkView(ControlWorkService controlWorkService) {
        this.controlWorkService = controlWorkService;
        initComponents();
        addComponents();
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private void initComponents() {
        // Генерация уникального ID
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        // Поле для наименования контрольной работы
        workNameField = new TextField("Наименование контрольной работы:");
        workNameField.setWidth("100%");
        workNameField.setRequired(true);

        // Поле для уникального номера (только для чтения)
        uniqueIdField = new TextField("Уникальный номер контрольной работы:");
        uniqueIdField.setValue(uniqueId);
        uniqueIdField.setReadOnly(true);
        uniqueIdField.setWidth("100%");

        // Группа радиокнопок для выбора метода ввода
        inputMethodGroup = new RadioButtonGroup<>();
        inputMethodGroup.setLabel("Способ ввода:");
        inputMethodGroup.setItems("Ввод текста", "Загрузка файла");
        inputMethodGroup.setValue("Ввод текста");
        inputMethodGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);

        // Текстовое поле
        textArea = new TextArea("Введите текст:");
        textArea.setWidth("100%");
        textArea.setHeight("300px");

        // Компонент для загрузки файла
        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".txt");
        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            String fileContent = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            textArea.setValue(fileContent);
        });

        // Поле для количества вопросов
        numberOfQuestionsField = new NumberField("Количество вопросов:");
        numberOfQuestionsField.setValue(10.0);
        numberOfQuestionsField.setWidth("100%");

        // Кнопка "Сгенерировать"
        generateButton = new Button("Сгенерировать вопросы", event -> {
            // 1. Проверяем, что обязательные поля заполнены в UI
            if (workNameField.isEmpty()) {
                Notification.show("Введите наименование контрольной работы");
                return;
            }
            if (uniqueIdField.isEmpty()) {
                Notification.show("Уникальный ID контрольной работы не сгенерирован");
                return;
            }

            String text = textArea.getValue();
            if (text == null || text.trim().isEmpty()) {
                Notification.show("Необходимо ввести текст для генерации вопросов");
                return;
            }

            // Извлекаем значения из компонентов интерфейса
            int numberOfQuestions = numberOfQuestionsField.getValue().intValue();
            String workName = workNameField.getValue();
            String uniqueIdValue = uniqueIdField.getValue();

            try {
                // 2. Запускаем процесс: Сохранение шапки -> Запрос к API -> Дозапись вопросов в БД
                controlWorkService.generateAndSaveWork(workName, uniqueIdValue, text, numberOfQuestions);

                // 3. Если всё прошло успешно, уведомляем пользователя
                Notification.show("Контрольная работа '" + workName + "' успешно создана, вопросы сохранены в базу.");

                // Возвращаем пользователя на страницу со списком документов/работ
                getUI().ifPresent(ui -> ui.navigate("documents"));

            } catch (Exception e) {
                // В случае падения API или БД выводим ошибку на экран
                Notification.show("Ошибка при обработке: " + e.getMessage(),
                        5000, Notification.Position.MIDDLE);
                e.printStackTrace();
            }
        });

        // Обработчик изменения способа ввода
        inputMethodGroup.addValueChangeListener(event -> {
            updateInputMethodVisibility();
        });

        // Создаем контейнеры для разных методов ввода
        textInputLayout = new VerticalLayout(textArea);
        textInputLayout.setWidth("100%");
        textInputLayout.setPadding(false);
        textInputLayout.setSpacing(false);

        fileInputLayout = new VerticalLayout(upload);
        fileInputLayout.setWidth("100%");
        fileInputLayout.setPadding(false);
        fileInputLayout.setSpacing(false);

        // Изначально показываем только текстовый ввод
        updateInputMethodVisibility();
    }

    private void updateInputMethodVisibility() {
        if ("Ввод текста".equals(inputMethodGroup.getValue())) {
            textInputLayout.setVisible(true);
            fileInputLayout.setVisible(false);
        } else {
            textInputLayout.setVisible(false);
            fileInputLayout.setVisible(true);
        }
    }

    private void addComponents() {
        // Заголовок
        add(new H1("Генерация вопросов"));

        // Основной горизонтальный контейнер
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidth("80%");
        mainLayout.setSpacing(true);

        // Левая колонка - информация о работе
        VerticalLayout leftColumn = new VerticalLayout();
        leftColumn.setWidth("50%");
        leftColumn.add(workNameField, uniqueIdField);
        leftColumn.setSpacing(true);
        leftColumn.setPadding(true);

        // Правая колонка - ввод данных
        VerticalLayout rightColumn = new VerticalLayout();
        rightColumn.setWidth("50%");

        // Контейнер для выбора метода ввода и соответствующих полей
        VerticalLayout inputContainer = new VerticalLayout();
        inputContainer.add(inputMethodGroup, textInputLayout, fileInputLayout);
        inputContainer.setWidth("100%");
        inputContainer.setSpacing(true);

        rightColumn.add(inputContainer, numberOfQuestionsField);
        rightColumn.setSpacing(true);
        rightColumn.setPadding(true);

        mainLayout.add(leftColumn, rightColumn);

        // Контейнер для кнопки "Сгенерировать"
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("80%");
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.add(generateButton);

        // Добавляем всё в основной layout
        add(mainLayout, buttonLayout);
    }
}