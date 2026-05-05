package ru.magistr.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import ru.magistr.data.entity.QuestionDto;

import java.util.*;

@Route(value = "take-work", layout = MainView.class)
@PermitAll
public class TakeControlWorkView extends VerticalLayout implements HasUrlParameter<String> {

    private String currentWorkId;
    private List<QuestionDto> questions;
    private int currentQuestionIndex = 0;

    // Хранилища состояния
    private final Map<QuestionDto, String> answers = new HashMap<>();
    private final Set<QuestionDto> visitedQuestions = new HashSet<>();

    // UI Компоненты, которые будут обновляться
    private Span questionNumberBadge;
    private Span questionTextSpan;
    private TextArea answerArea;
    private HorizontalLayout paginationLayout;
    private final List<Button> navButtons = new ArrayList<>();

    public TakeControlWorkView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent event, String workId) {
        this.currentWorkId = workId;

        // Загружаем вопросы
        this.questions = fetchQuestionsMock(workId);

        // Сброс состояния
        this.answers.clear();
        this.visitedQuestions.clear();
        this.currentQuestionIndex = 0;
        this.navButtons.clear();

        removeAll();
        buildUI();

        // Показываем первый вопрос, если список не пуст
        if (questions != null && !questions.isEmpty()) {
            showQuestion(0);
        } else {
            add(new Span("В этой контрольной работе нет вопросов."));
        }
    }

    private void buildUI() {
        VerticalLayout contentContainer = new VerticalLayout();
        contentContainer.setMaxWidth("900px");
        contentContainer.setWidthFull();
        contentContainer.setPadding(true);

        // 1. Заголовок
        H2 header = new H2("Выполнение контрольной работы (ID: " + currentWorkId + ")");
        header.addClassNames(LumoUtility.Margin.Bottom.LARGE, LumoUtility.Margin.Top.NONE);

        // 2. Карточка вопроса (Создаем один раз, потом просто меняем текст)
        HorizontalLayout questionCard = createActiveQuestionCard();

        // 3. Панель навигации (кнопки с номерами)
        paginationLayout = new HorizontalLayout();
        paginationLayout.setWidthFull();
        paginationLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        paginationLayout.setPadding(true);
        paginationLayout.getStyle().set("flex-wrap", "wrap"); // Перенос на новую строку, если кнопок много
        initPaginationButtons();

        // Кнопки "Назад" / "Вперед"
        Button prevButton = new Button("Предыдущий", e -> navigate(-1));
        Button nextButton = new Button("Следующий", e -> navigate(1));
        HorizontalLayout prevNextLayout = new HorizontalLayout(prevButton, nextButton);
        prevNextLayout.setWidthFull();
        prevNextLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // 4. Кнопка отправки всей работы
        Button submitButton = new Button("Завершить и отправить работу", e -> confirmAndSubmit());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        submitButton.setWidthFull();
        submitButton.addClassNames(LumoUtility.Margin.Top.XLARGE);

        contentContainer.add(header, questionCard, prevNextLayout, paginationLayout, submitButton);
        add(contentContainer);
    }

    private HorizontalLayout createActiveQuestionCard() {
        HorizontalLayout card = new HorizontalLayout();
        card.setWidthFull();
        card.setAlignItems(FlexComponent.Alignment.START);

        card.addClassNames(
                LumoUtility.Padding.LARGE, LumoUtility.BorderRadius.LARGE,
                LumoUtility.Background.BASE, LumoUtility.BoxShadow.SMALL
        );
        card.getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");

        // Элементы, которые мы будем менять в showQuestion()
        questionNumberBadge = new Span();
        questionNumberBadge.setWidth("40px");
        questionNumberBadge.setHeight("40px");
        questionNumberBadge.addClassNames(
                LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER, LumoUtility.Background.PRIMARY_10,
                LumoUtility.TextColor.PRIMARY, LumoUtility.FontWeight.BOLD,
                LumoUtility.FontSize.LARGE
        );
        questionNumberBadge.getStyle().set("border-radius", "50%");
        questionNumberBadge.getStyle().set("flex-shrink", "0");

        questionTextSpan = new Span();
        questionTextSpan.setWidthFull();
        questionTextSpan.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD, LumoUtility.Margin.Bottom.MEDIUM);

        answerArea = new TextArea();
        answerArea.setWidthFull();
        answerArea.setPlaceholder("Введите ваш ответ здесь...");
        answerArea.setMinHeight("150px");

        // ВАЖНО: Обновляем состояние ответов при каждом вводе символа (LAZY = после небольшой паузы)
        answerArea.setValueChangeMode(ValueChangeMode.LAZY);
        answerArea.addValueChangeListener(e -> {
            QuestionDto currentQ = questions.get(currentQuestionIndex);
            answers.put(currentQ, e.getValue());
            updatePaginationButtonStyles(); // Сразу перекрашиваем кнопку внизу
        });

        VerticalLayout textAndAnswerLayout = new VerticalLayout(questionTextSpan, answerArea);
        textAndAnswerLayout.setPadding(false);
        textAndAnswerLayout.setSpacing(false);

        card.add(questionNumberBadge, textAndAnswerLayout);
        card.setFlexGrow(1, textAndAnswerLayout);

        return card;
    }

    // --- ЛОГИКА НАВИГАЦИИ И ПАГИНАЦИИ ---

    private void initPaginationButtons() {
        for (int i = 0; i < questions.size(); i++) {
            int targetIndex = i;
            Button btn = new Button(String.valueOf(i + 1));
            btn.addClickListener(e -> showQuestion(targetIndex));
            navButtons.add(btn);
            paginationLayout.add(btn);
        }
    }

    private void navigate(int step) {
        int newIndex = currentQuestionIndex + step;
        if (newIndex >= 0 && newIndex < questions.size()) {
            showQuestion(newIndex);
        }
    }

    private void showQuestion(int index) {
        currentQuestionIndex = index;
        QuestionDto q = questions.get(index);

        // Отмечаем вопрос как посещенный
        visitedQuestions.add(q);

        // Обновляем UI карточки
        questionNumberBadge.setText(String.valueOf(index + 1));
        questionTextSpan.setText(q.getText());

        // Загружаем сохраненный ответ или пустоту
        answerArea.setValue(answers.getOrDefault(q, ""));

        // Обновляем цвета кнопок внизу
        updatePaginationButtonStyles();
    }

    private void updatePaginationButtonStyles() {
        for (int i = 0; i < questions.size(); i++) {
            Button btn = navButtons.get(i);
            QuestionDto q = questions.get(i);

            // Сбрасываем старые стили
            btn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_ERROR);
            btn.getStyle().remove("border");

            String ans = answers.get(q);
            boolean isAnswered = ans != null && !ans.trim().isEmpty();
            boolean isVisited = visitedQuestions.contains(q);

            // Логика раскраски
            if (isAnswered) {
                // Ответили -> Зеленый
                btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            } else if (isVisited) {
                // Посетили, но не ответили -> Красный
                btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            } else {
                // Не посещали -> Обычный (сероватый/вторичный)
            }

            // Выделяем текущий выбранный вопрос (например, обводкой)
            if (i == currentQuestionIndex) {
                btn.getStyle().set("border", "2px solid var(--lumo-contrast)");
            }
        }
    }

    // --- ЛОГИКА ОТПРАВКИ ---

    private void confirmAndSubmit() {
        long emptyAnswers = questions.stream()
                .filter(q -> !answers.containsKey(q) || answers.get(q).trim().isEmpty())
                .count();

        String dialogText = emptyAnswers > 0
                ? "У вас осталось " + emptyAnswers + " вопросов без ответа (помечены красным или серым). Вы точно хотите завершить работу?"
                : "Вы ответили на все вопросы. Отправить работу на проверку?";

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Подтверждение отправки");
        dialog.setText(dialogText);
        dialog.setCancelable(true);
        dialog.setCancelText("Отмена");
        dialog.setConfirmText("Отправить");
        dialog.setConfirmButtonTheme("success primary");
        dialog.addConfirmListener(event -> processSubmission());

        dialog.open();
    }

    private void processSubmission() {
        System.out.println("=== ИТОГОВЫЕ ОТВЕТЫ ===");
        for (QuestionDto q : questions) {
            System.out.println("Вопрос " + q.getNumber() + ": " + answers.getOrDefault(q, "[Нет ответа]"));
        }

        Notification.show("Работа успешно отправлена!", 4000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        UI.getCurrent().navigate("documents");
    }

    // --- ЗАГЛУШКА БАЗЫ ДАННЫХ ---
    private List<QuestionDto> fetchQuestionsMock(String workId) {
        return List.of(
                new QuestionDto(1, "В каком году императрица Елизавета Петровна втянула Россию в Семилетнюю войну?", ""),
                new QuestionDto(2, "Опишите причины, по которым Россия решила принять участие в этом конфликте.", ""),
                new QuestionDto(3, "Назовите основных союзников и противников Российской Империи в этой войне.", ""),
                new QuestionDto(4, "Каковы были итоги Семилетней войны для России?", ""),
                new QuestionDto(5, "Кто сменил Елизавету Петровну на престоле и как это повлияло на ход войны?", "")
        );
    }
}