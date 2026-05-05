package ru.magistr.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.context.annotation.Scope;
import ru.magistr.data.entity.QuestionDto;
import ru.magistr.service.ControlWorkService;

import java.util.List;

@SpringComponent
@Scope("prototype")
public class ControlWorkQuestionsDialog extends Dialog {

    private final ControlWorkService controlWorkService;
    private VerticalLayout questionsListLayout;
    private int questionCounter = 0;

    // Теперь Spring САМ подставит сервис сюда при создании бина
    public ControlWorkQuestionsDialog(ControlWorkService controlWorkService) {
        this.controlWorkService = controlWorkService;
    }

    // Метод для передачи динамических параметров и отрисовки
    public void openDialog(String controlWorkId, boolean isReadOnly) {
        // Очищаем диалог на случай, если он переиспользуется (хотя prototype должен создавать новый)
        removeAll();
        getHeader().removeAll();
        getFooter().removeAll();
        questionCounter = 0;

        setWidth("900px");
        setMaxHeight("85vh");

        H3 title = new H3(isReadOnly ? "Просмотр работы ID: " + controlWorkId : "Редактирование работы ID: " + controlWorkId);
        title.getStyle().set("margin-top", "0");
        getHeader().add(title);

        questionsListLayout = new VerticalLayout();
        questionsListLayout.setPadding(false);
        questionsListLayout.setSpacing(true);

        // Используем внедренный сервис!
        List<QuestionDto> questions = controlWorkService.getQuestionsByWorkId(controlWorkId);

        for (QuestionDto q : questions) {
            questionCounter++;
            questionsListLayout.add(createQuestionCard(questionCounter, q.getText(), q.getAnswer(), isReadOnly));
        }

        Scroller scroller = new Scroller(questionsListLayout);
        scroller.setSizeFull();
        add(scroller);

        // Настройка кнопок футера (зависит от isReadOnly)
        setupFooter(isReadOnly, scroller);

        // Открываем диалог
        open();
    }

    private void setupFooter(boolean isReadOnly, Scroller scroller) {
        Button closeButton = new Button("Закрыть", e -> close());

        if (isReadOnly) {
            getFooter().add(closeButton);
        } else {
            Button addQuestionButton = new Button("Добавить вопрос", e -> {
                questionCounter++;
                questionsListLayout.add(createQuestionCard(questionCounter, "", "", false));
                scroller.getElement().executeJs("this.scrollTop = this.scrollHeight;");
            });

            Button saveButton = new Button("Сохранить", e -> close()); // Заглушка сохранения

            HorizontalLayout rightFooterButtons = new HorizontalLayout(closeButton, saveButton);
            getFooter().add(addQuestionButton, rightFooterButtons);
        }
    }

    // --- МЕТОД ОТРИСОВКИ КАРТОЧКИ ---
    private HorizontalLayout createQuestionCard(int number, String questionText, String existingAnswer, boolean isReadOnly) {
        HorizontalLayout card = new HorizontalLayout();
        card.setWidthFull();
        card.setAlignItems(Alignment.START);

        card.addClassNames(
                LumoUtility.Padding.MEDIUM, LumoUtility.BorderRadius.LARGE,
                LumoUtility.Background.BASE, LumoUtility.BoxShadow.XSMALL
        );
        card.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");

        // Кружок с номером
        Span numberBadge = new Span(String.valueOf(number));
        numberBadge.setWidth("36px");
        numberBadge.setHeight("36px");
        numberBadge.addClassNames(
                LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER, LumoUtility.Background.PRIMARY_10,
                LumoUtility.TextColor.PRIMARY, LumoUtility.FontWeight.BOLD
        );
        numberBadge.getStyle().set("border-radius", "50%");
        numberBadge.getStyle().set("flex-shrink", "0");

        // ПОЛЕ ВОПРОСА
        Component questionComponent;
        if (isReadOnly) {
            // В режиме просмотра оставляем красивый текст
            questionComponent = new Span(questionText);
            ((Span) questionComponent).setWidthFull();
            questionComponent.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.Top.XSMALL);
        } else {
            // В режиме редактирования превращаем в многострочное поле
            TextArea questionArea = new TextArea();
            questionArea.setValue(questionText != null ? questionText : "");
            questionArea.setPlaceholder("Введите текст вопроса...");
            questionArea.setWidthFull();
            questionComponent = questionArea;
        }

        // ПОЛЕ ОТВЕТА
        TextArea answerField = new TextArea();
        answerField.setWidth("300px");
        answerField.setPlaceholder(isReadOnly ? "Ответ отсутствует" : "Введите ответ...");
        answerField.getStyle().set("flex-shrink", "0");
        answerField.setReadOnly(isReadOnly); // Блокируем, если режим просмотра

        if (existingAnswer != null && !existingAnswer.isEmpty()) {
            answerField.setValue(existingAnswer);
        }

        card.add(numberBadge, questionComponent, answerField);
        card.setFlexGrow(1, questionComponent);

        return card;
    }
}