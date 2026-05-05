package ru.magistr.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import ru.magistr.data.entity.StudentResultDTO;
import ru.magistr.views.MainView;

import java.util.ArrayList;
import java.util.List;

@Route(value = "results", layout = MainView.class)
@PermitAll
public class WorkResultsView extends VerticalLayout implements HasUrlParameter<String> {

    private Grid<StudentResultDTO> resultsGrid = new Grid<>();
    private String workId;

    public WorkResultsView() {
        setSizeFull();
        setPadding(true);

        H2 header = new H2("Результаты прохождения");

        configureGrid();
        add(header, resultsGrid);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.workId = parameter;
        // Здесь вы должны вызвать метод вашего сервиса, например:
        // resultsGrid.setItems(controlWorkService.getResultsByWorkId(workId));

        // Для примера загрузим заглушку:
        loadMockData();
    }

    private void configureGrid() {
        resultsGrid.addColumn(StudentResultDTO::getStudentFio).setHeader("ФИО Студента").setSortable(true);
        resultsGrid.addColumn(StudentResultDTO::getQuestionText).setHeader("Вопрос").setFlexGrow(2);

        // Стилизация процента совпадения
        resultsGrid.addComponentColumn(result -> {
            Span span = new Span(result.getMatchPercentage() + "%");
            if (result.getMatchPercentage() >= 80) {
                span.getElement().getThemeList().add("badge success");
            } else if (result.getMatchPercentage() >= 50) {
                span.getElement().getThemeList().add("badge");
            } else {
                span.getElement().getThemeList().add("badge error");
            }
            return span;
        }).setHeader("Совпадение").setSortable(true);

        resultsGrid.addColumn(StudentResultDTO::getExpectedAnswer).setHeader("Ожидаемый ответ");
        resultsGrid.addColumn(StudentResultDTO::getStudentAnswer).setHeader("Ответ студента");

        resultsGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        resultsGrid.setSizeFull();
    }

    private void loadMockData() {
        List<StudentResultDTO> mockResults = new ArrayList<>();

        // Единое имя для всех записей, чтобы проверить отображение нескольких вопросов на одного студента
        String constantStudent = "Иванов Иван Иванович";

        // Ответ 1: Полное совпадение
        mockResults.add(new StudentResultDTO(
                constantStudent,
                workId,
                "Кто руководил засадой на набережной Екатерининского канала?",
                100.0,
                "Софья Перовская",
                "Софья Перовская"
        ));

        // Ответ 2: Высокое совпадение (синонимы)
        mockResults.add(new StudentResultDTO(
                constantStudent,
                workId,
                "Куда направлялся Александр II из Зимнего дворца 1 марта 1881 года?",
                92.0,
                "в Михайловский манеж",
                "Император ехал в Михайловский манеж"
        ));

        // Ответ 3: Среднее совпадение
        mockResults.add(new StudentResultDTO(
                constantStudent,
                workId,
                "Кто бросил вторую бомбу под ноги государю?",
                78.0,
                "Игнатий Гриневицкий",
                "Бомбу метнул Гриневицкий"
        ));

        // Ответ 4: Частичное совпадение (неполный ответ)
        mockResults.add(new StudentResultDTO(
                constantStudent,
                workId,
                "Какое событие стало причиной начала массовых арестов в Санкт-Петербурге?",
                55.0,
                "трагическая смерть монарха",
                "убийство царя"
        ));

        // Ответ 5: Низкое совпадение (фактическая ошибка)
        mockResults.add(new StudentResultDTO(
                constantStudent,
                workId,
                "С какой целью император подошел к схваченному террористу после первого взрыва?",
                20.0,
                "чтобы посмотреть преступнику в лицо",
                "хотел его допросить"
        ));

        resultsGrid.setItems(mockResults);
    }
}