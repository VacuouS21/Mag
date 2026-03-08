package ru.magistr.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.magistr.data.entity.QuestionDto;

import java.util.ArrayList;
import java.util.List;

// Наследуемся от Dialog, теперь этот класс САМ является окном
public class ControlWorkQuestionsDialog extends Dialog {

    // Теперь принимаем только ID (предполагаем, что это String, как на скрине "cc1d13f3")
    public ControlWorkQuestionsDialog(String controlWorkId) {
        setWidth("900px");
        setMaxHeight("85vh");

        H3 title = new H3("Контрольная работа ID: " + controlWorkId);
        title.getStyle().set("margin-top", "0");

        VerticalLayout questionsListLayout = new VerticalLayout();
        questionsListLayout.setPadding(false);
        questionsListLayout.setSpacing(true);

        // 1. ПОЛУЧАЕМ ДАННЫЕ ПО ID (пока через метод-заглушку)
        List<QuestionDto> questions = fetchQuestionsMock(controlWorkId);

        // 2. ОТРИСОВЫВАЕМ КАРТОЧКИ НА ОСНОВЕ DTO
        for (QuestionDto q : questions) {
            questionsListLayout.add(
                    createQuestionCard(q.getNumber(), q.getText(), q.getAnswer())
            );
        }

        Scroller scroller = new Scroller(questionsListLayout);
        scroller.setSizeFull();

        Button closeButton = new Button("Закрыть", e -> close());
        Button saveButton = new Button("Сохранить");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        getHeader().add(title);
        add(scroller);
        getFooter().add(closeButton, saveButton);
    }

    // --- ЗАГЛУШКА ИМИТАЦИИ БАЗЫ ДАННЫХ (РАСШИРЕННАЯ) ---
    private List<QuestionDto> fetchQuestionsMock(String workId) {
        List<QuestionDto> mockData = new ArrayList<>();

        mockData.add(new QuestionDto(1, "В каком году императрица Елизавета Петровна втянула Россию в Семилетнюю войну?", "В 1756 году"));
        mockData.add(new QuestionDto(2, "Каким действием Россия вступила в Семилетнюю войну?", "Подписание союзного договора с Австрией"));
        mockData.add(new QuestionDto(3, "Усиления какого государства опасалась Елизавета Петровна перед вступлением в войну?", "")); // Оставим без ответа для теста
        mockData.add(new QuestionDto(4, "С каким государством Россия подписала союзный договор в 1756 году?", "С Австрией"));
        mockData.add(new QuestionDto(5, "Когда Наполеон Бонапарт вторгся в пределы Российской империи?", "В июне 1812 года"));
        mockData.add(new QuestionDto(6, "Через какую реку переправилась армия Наполеона при вторжении в Россию?", "Через реку Неман"));
        mockData.add(new QuestionDto(7, "Как именно Наполеон Бонапарт начал кампанию против Российской империи?", "Без объявления войны"));
        mockData.add(new QuestionDto(8, "С помощью чего Наполеон осуществил переправу через Неман?", "")); // Оставим без ответа
        mockData.add(new QuestionDto(9, "Кто являлся инициатором вторжения в Российскую империю в 1812 году?", "Наполеон Бонапарт"));
        mockData.add(new QuestionDto(10, "Какая армия противостояла Российской империи при форсировании реки Неман?", "Огромная армия Наполеона"));

        return mockData;
    }

    // --- МЕТОД ОТРИСОВКИ КАРТОЧКИ (с исправленным CSS для круга) ---
    private HorizontalLayout createQuestionCard(int number, String questionText, String existingAnswer) {
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
        numberBadge.getStyle().set("border-radius", "50%"); // Фикс для круга
        numberBadge.getStyle().set("flex-shrink", "0");

        Span questionSpan = new Span(questionText);
        questionSpan.setWidthFull();
        questionSpan.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Padding.Top.XSMALL);

        TextArea answerField = new TextArea();
        answerField.setWidth("300px");
        answerField.setPlaceholder("Введите ответ...");
        answerField.getStyle().set("flex-shrink", "0");

        if (existingAnswer != null && !existingAnswer.isEmpty()) {
            answerField.setValue(existingAnswer);
        }

        card.add(numberBadge, questionSpan, answerField);
        card.setFlexGrow(1, questionSpan);

        return card;
    }
}