package ru.magistr.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.security.PermitAll;

import java.io.InputStream;
import java.util.Scanner;

@Route(value = "generate",layout = MainView.class)
@SpringComponent
@UIScope
@PermitAll
public class GenerateWorkView extends VerticalLayout {

    private TextArea textArea;
    private NumberField numberOfQuestionsField;
    private Upload upload;
    private Button generateButton;

    public GenerateWorkView() {
        initComponents();
        addComponents();
        setAlignItems(Alignment.CENTER); // Центрируем всё по горизонтали
        setSizeFull(); // Занимаем весь экран
    }

    private void initComponents() {
        // Заголовок формы
        H1 header = new H1("Генерация вопросов");
        header.getStyle().set("margin-bottom", "20px"); // Отступ снизу

        // Текстовое поле
        textArea = new TextArea("Введите текст:");
        textArea.setWidth("50%"); // Ширина 50% от экрана
        textArea.setHeight("300px");

        // Поле для количества вопросов
        numberOfQuestionsField = new NumberField("Количество вопросов:");
        numberOfQuestionsField.setValue(10.0); // Значение по умолчанию

        // Компонент для загрузки файла
        MemoryBuffer buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".txt");
        upload.addSucceededListener(event -> {
            InputStream inputStream = buffer.getInputStream();
            String fileContent = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
            textArea.setValue(fileContent);
        });

        // Кнопка "Сгенерировать"
        generateButton = new Button("Сгенерировать вопросы", event -> {
            String text = textArea.getValue();
            int numberOfQuestions = numberOfQuestionsField.getValue().intValue();
            // Логика генерации вопросов
            Notification.show("Генерация вопросов для текста: " + text + ", количество вопросов: " + numberOfQuestions);
        });
    }

    private void addComponents() {
        // Заголовок
        add(new H1("Генерация вопросов"));

        // Контейнер для текстового поля и загрузки файла
        VerticalLayout inputLayout = new VerticalLayout();
        inputLayout.add(textArea, upload);
        inputLayout.setWidth("50%"); // Ширина 50% от экрана
        inputLayout.setAlignItems(Alignment.CENTER); // Центрируем элементы внутри

        // Контейнер для поля количества вопросов
        VerticalLayout questionsLayout = new VerticalLayout();
        questionsLayout.add(numberOfQuestionsField);
        questionsLayout.setWidth("50%"); // Ширина 50% от экрана
        questionsLayout.setAlignItems(Alignment.CENTER); // Центрируем элементы внутри

        // Контейнер для кнопки "Сгенерировать"
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(generateButton);
        buttonLayout.setWidth("50%"); // Ширина 50% от экрана
        buttonLayout.setJustifyContentMode(JustifyContentMode.END); // Выравниваем кнопку справа

        // Добавляем всё в основной layout
        add(inputLayout, questionsLayout, buttonLayout);
        setFlexGrow(1, inputLayout); // Растягиваем inputLayout на всё доступное пространство
    }
}