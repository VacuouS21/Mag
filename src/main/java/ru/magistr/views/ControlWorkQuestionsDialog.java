package ru.magistr.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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

import java.util.ArrayList;
import java.util.List;

@SpringComponent
@Scope("prototype")
public class ControlWorkQuestionsDialog extends Dialog {

    private final ControlWorkService controlWorkService;
    private VerticalLayout questionsListLayout;
    private int questionCounter = 0;

    private final List<TextArea> questionInputs = new ArrayList<>();
    private final List<TextArea> answerInputs = new ArrayList<>();

    public ControlWorkQuestionsDialog(ControlWorkService controlWorkService) {
        this.controlWorkService = controlWorkService;
    }

    public void openDialog(String controlWorkId, boolean isReadOnly) {
        removeAll();
        getHeader().removeAll();
        getFooter().removeAll();
        questionCounter = 0;

        questionInputs.clear();
        answerInputs.clear();

        setWidth("900px");
        setMaxHeight("85vh");

        H3 title = new H3(isReadOnly ? "Просмотр работы ID: " + controlWorkId : "Редактирование работы ID: " + controlWorkId);
        title.getStyle().set("margin-top", "0");
        getHeader().add(title);

        questionsListLayout = new VerticalLayout();
        questionsListLayout.setPadding(false);
        questionsListLayout.setSpacing(true);

        List<QuestionDto> questions = controlWorkService.getQuestionsByWorkId(controlWorkId);

        for (QuestionDto q : questions) {
            questionCounter++;
            questionsListLayout.add(createQuestionCard(questionCounter, q.getText(), q.getAnswer(), isReadOnly));
        }

        Scroller scroller = new Scroller(questionsListLayout);
        scroller.setSizeFull();
        add(scroller);

        setupFooter(isReadOnly, scroller, controlWorkId);

        open();
    }

    private void setupFooter(boolean isReadOnly, Scroller scroller, String controlWorkId) {
        Button closeButton = new Button("Закрыть", e -> close());

        if (isReadOnly) {
            getFooter().add(closeButton);
        } else {
            Button addQuestionButton = new Button("Добавить вопрос", e -> {
                questionCounter++;
                questionsListLayout.add(createQuestionCard(questionCounter, "", "", false));
                scroller.getElement().executeJs("this.scrollTop = this.scrollHeight;");
            });

            Button saveButton = new Button("Сохранить", e -> {
                List<QuestionDto> updatedQuestions = new ArrayList<>();

                for (int i = 0; i < questionInputs.size(); i++) {
                    String qText = questionInputs.get(i).getValue();
                    String aText = answerInputs.get(i).getValue();

                    if ((qText != null && !qText.isBlank()) || (aText != null && !aText.isBlank())) {
                        updatedQuestions.add(new QuestionDto(i + 1, qText, aText));
                    }
                }

                try {
                    controlWorkService.updateControlWorkQuestions(controlWorkId, updatedQuestions);
                    Notification.show("Изменения успешно сохранены!", 3000, Notification.Position.BOTTOM_START);
                    close();
                } catch (Exception ex) {
                    Notification.show("Ошибка при сохранении: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                    ex.printStackTrace();
                }
            });
            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            HorizontalLayout rightFooterButtons = new HorizontalLayout(closeButton, saveButton);
            getFooter().add(addQuestionButton, rightFooterButtons);
        }
    }

    private HorizontalLayout createQuestionCard(int number, String questionText, String existingAnswer, boolean isReadOnly) {
        HorizontalLayout card = new HorizontalLayout();
        card.setWidthFull();
        card.setAlignItems(Alignment.START);

        card.addClassNames(
                LumoUtility.Padding.MEDIUM, LumoUtility.BorderRadius.LARGE,
                LumoUtility.Background.BASE, LumoUtility.BoxShadow.XSMALL
        );
        card.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");

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

        Component questionComponent;
        TextArea questionArea = null; // Выделяем переменную, чтобы передать в удаление
        if (isReadOnly) {
            questionComponent = new Span(questionText);
            ((Span) questionComponent).setWidthFull();
            questionComponent.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.Top.XSMALL);
        } else {
            questionArea = new TextArea();
            questionArea.setValue(questionText != null ? questionText : "");
            questionArea.setPlaceholder("Введите текст вопроса...");
            questionArea.setWidthFull();
            questionComponent = questionArea;

            questionInputs.add(questionArea);
        }

        TextArea answerField = new TextArea();
        answerField.setWidth("300px");
        answerField.setPlaceholder(isReadOnly ? "Ответ отсутствует" : "Введите ответ...");
        answerField.getStyle().set("flex-shrink", "0");
        answerField.setReadOnly(isReadOnly);

        if (existingAnswer != null && !existingAnswer.isEmpty()) {
            answerField.setValue(existingAnswer);
        }

        if (!isReadOnly) {
            answerInputs.add(answerField);

            // ДОБАВЛЯЕМ КНОПКУ УДАЛЕНИЯ ВОПРОСА
            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteBtn.getStyle().set("flex-shrink", "0");

            // Копируем ссылки для использования внутри лямбда-выражения
            TextArea finalQuestionArea = questionArea;
            TextArea finalAnswerField = answerField;

            deleteBtn.addClickListener(e -> {
                // 1. Убираем карточку с экрана
                questionsListLayout.remove(card);
                // 2. Убираем поля из списков для сохранения
                questionInputs.remove(finalQuestionArea);
                answerInputs.remove(finalAnswerField);
                // 3. Пересчитываем нумерацию
                updateQuestionNumbers();
            });

            // Добавляем элементы в карточку вместе с кнопкой удаления
            card.add(numberBadge, questionComponent, answerField, deleteBtn);
        } else {
            // В режиме просмотра кнопку не добавляем
            card.add(numberBadge, questionComponent, answerField);
        }

        card.setFlexGrow(1, questionComponent);

        return card;
    }

    /**
     * Метод-помощник для пересчета порядковых номеров карточек после удаления.
     */
    private void updateQuestionNumbers() {
        int index = 1;
        // Проходимся по всем оставшимся карточкам в контейнере
        for (Component child : questionsListLayout.getChildren().toList()) {
            if (child instanceof HorizontalLayout) {
                HorizontalLayout card = (HorizontalLayout) child;
                // Кружок с номером — это первый добавленный компонент
                Component badge = card.getComponentAt(0);
                if (badge instanceof Span) {
                    ((Span) badge).setText(String.valueOf(index));
                }
                index++;
            }
        }
        // Обновляем глобальный счетчик, чтобы новый добавленный вопрос имел правильный номер
        questionCounter = index - 1;
    }
}