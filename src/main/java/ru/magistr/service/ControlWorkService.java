package ru.magistr.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.magistr.data.entity.ControlWork;
import ru.magistr.data.entity.ControlWorkDTO;
import ru.magistr.data.entity.QuestionAnswer;
import ru.magistr.data.entity.QuestionDto;
import ru.magistr.data.repository.ControlWorkRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ru.magistr.request.GeneratedQuestionResponse;
import ru.magistr.request.GenerationRequest;

@Service
public class ControlWorkService {

    private final ControlWorkRepository repository;
    private final RestTemplate restTemplate;
    private final String apiBaseUrl = "http://localhost:8080";

    public ControlWorkService(ControlWorkRepository controlWorkRepository) {
        this.repository = controlWorkRepository;
        // Создаем классический RestTemplate
        this.restTemplate = new RestTemplate();
    }

    @Transactional(readOnly = true)
    public List<ControlWorkDTO> getControlWorks() {
        return repository.findAll().stream().map(work -> {

            // Если имя в БД есть, берем его. Если это старая запись без имени, делаем резервный вариант
            String displayName = (work.getName() != null && !work.getName().isEmpty())
                    ? work.getName()
                    : "Работа " + work.getExternalId();

            int questionsCount = (work.getQuestionAnswers() != null)
                    ? work.getQuestionAnswers().size()
                    : 0;

            // Конвертируем Entity -> DTO
            return new ControlWorkDTO(
                    work.getId(),
                    displayName,             // Теперь тут настоящее имя (например, "Тест по истории")
                    work.getExternalId(),
                    questionsCount
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void removeControlWork(ControlWorkDTO dto) {
        // Ищем работу по externalId и, если находим, удаляем
        Optional<ControlWork> workOptional = repository.findByExternalId(dto.getUniqueId());
        workOptional.ifPresent(repository::delete);
    }

    /**
     * Создает пустую контрольную работу (без вопросов)
     */
    //TODO
    @Transactional
    public ControlWork addControlWork(ControlWorkDTO externalId) {
        // Проверяем, нет ли уже работы с таким ID, чтобы избежать SQL ошибки
//        if (repository.findByExternalId(externalId).isPresent()) {
//            throw new IllegalArgumentException("Контрольная работа с ID " + externalId + " уже существует.");
//        }
//
//        ControlWork work = new ControlWork();
//        work.setExternalId(externalId);
//
//        return repository.save(work);
        return null;
    }

    /**
     * Создает контрольную работу сразу вместе со списком вопросов.
     * Здесь идеально раскрывается польза твоего хелпер-метода addQuestionAnswer!
     */
    @Transactional
    public ControlWork addControlWorkWithQuestions(String externalId, List<QuestionAnswer> questions) {
        if (repository.findByExternalId(externalId).isPresent()) {
            throw new IllegalArgumentException("Контрольная работа с ID " + externalId + " уже существует.");
        }

        ControlWork work = new ControlWork();
        work.setExternalId(externalId);

        // Привязываем вопросы к работе с помощью твоего метода из Entity
        if (questions != null) {
            for (QuestionAnswer qa : questions) {
                work.addQuestionAnswer(qa);
            }
        }

        // Каскадное сохранение (CascadeType.ALL) автоматически сохранит и саму работу, и все её вопросы
        return repository.save(work);
    }

    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsByWorkId(String externalId) {
        return repository.findByExternalId(externalId)
                .map(work -> work.getQuestionAnswers().stream()
                        // Сортируем вопросы по порядку, если нужно
                        // .sorted(Comparator.comparing(QuestionAnswer::getQuestionOrder))
                        .map(qa -> new QuestionDto(
                                qa.getOrder() != null ? qa.getOrder() : 0,
                                qa.getQuestionText(),
                                qa.getAnswerText()
                        ))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList()); // Если работа не найдена, возвращаем пустой список
    }

    @Transactional
    public void generateAndSaveWork(String workName, String externalId, String text, int questionCount) {

        // --- ЭТАП 1: Сохраняем "шапку" контрольной работы ---
        ControlWork controlWork = new ControlWork();
        controlWork.setExternalId(externalId);
        controlWork.setName(workName);
        // Сохраняем первичную запись, чтобы сгенерировался ID
        controlWork = repository.save(controlWork);

        List<GeneratedQuestionResponse> apiResponse = null;

        // --- ЭТАП 2: Попытка запроса к API или генерация заглушки ---
        try {
            // ЗАЩИТА: Если передали 0 или отрицательное число, делаем хотя бы 5 вопросов
            if (questionCount <= 0) {
                questionCount = 5;
            }

            GenerationRequest requestBody = new GenerationRequest(text, questionCount);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GenerationRequest> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<List<GeneratedQuestionResponse>> response = restTemplate.exchange(
                    apiBaseUrl + "/api/generate", // ВАЖНО: я изменил путь на /api/generate, чтобы не было конфликта с Vaadin UI
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<GeneratedQuestionResponse>>() {}
            );

            apiResponse = response.getBody();

            // Если API вернул ответ 200 OK, но сам список пустой - бросаем ошибку, чтобы включилась заглушка
            if (apiResponse == null || apiResponse.isEmpty()) {
                throw new RuntimeException("API вернул пустой список");
            }

            System.out.println("Данные успешно получены от внешнего API.");

        } catch (Exception e) {
            // Теперь мы ловим ВООБЩЕ ВСЕ ошибки (нет интернета, 404, не распарсился JSON, конфликт Vaadin)
            System.err.println("API недоступен или вернул ошибку. Включена заглушка. Причина: " + e.getMessage());
            apiResponse = generateMockQuestions(text, questionCount);
        }

        // --- ЭТАП 3: Дописываем вопросы и ответы в БД ---
        if (apiResponse != null && !apiResponse.isEmpty()) {
            for (int i = 0; i < apiResponse.size(); i++) {
                GeneratedQuestionResponse qaResponse = apiResponse.get(i);

                QuestionAnswer qa = new QuestionAnswer();
                qa.setQuestionText(qaResponse.getQuestion());
                qa.setAnswerText(qaResponse.getAnswer());
                qa.setOrder(i + 1);

                // Связываем сущности
                controlWork.addQuestionAnswer(qa);
            }

            // Финальное сохранение со всеми вопросами
            repository.save(controlWork);
        } else {
            // Сюда код дойдет только в случае критического системного сбоя в памяти
            throw new RuntimeException("Системная ошибка: Не удалось сгенерировать даже заглушки.");
        }
    }

    /**
     * Генератор-заглушка тестовых данных.
     */
    private List<GeneratedQuestionResponse> generateMockQuestions(String inputText, int count) {
        List<GeneratedQuestionResponse> mockList = new ArrayList<>();

        // Защита от нулевого текста
        String textLower = inputText != null ? inputText.toLowerCase() : "";

        String[][] baseQA = {
                {"Что является главным объектом исследования в тексте?", "Объект, описанный в первом абзаце."},
                {"Сформулируйте основную проблему.", "Проблема взаимосвязи элементов."},
                {"Какие выводы можно сделать?", "Требуется дальнейшее изучение структуры."},
                {"Каковы ключевые термины?", "Понятия, представленные автором."},
                {"В каком контексте упоминаются предпосылки?", "В контексте обоснования актуальности."}
        };

        if (textLower.contains("год") || textLower.contains("век") || textLower.contains("история")) {
            baseQA = new String[][]{
                    {"Какое историческое событие описывается?", "Событие, ставшее поворотным моментом."},
                    {"Каковы причины начала этих процессов?", "Совокупность социально-экономических факторов."},
                    {"К каким последствиям это привело?", "К трансформации общественного строя."}
            };
        } else if (textLower.contains("код") || textLower.contains("java") || textLower.contains("база")) {
            baseQA = new String[][]{
                    {"Какая архитектурная концепция описывается?", "Концепция разделения ответственности."},
                    {"Для решения каких задач это применяется?", "Для оптимизации работы с данными."},
                    {"С какими проблемами можно столкнуться?", "Неоптимальные запросы и утечки памяти."}
            };
        }

        // Гарантированно создаем нужное число вопросов
        for (int i = 0; i < count; i++) {
            int index = i % baseQA.length;
            GeneratedQuestionResponse qa = new GeneratedQuestionResponse();
            qa.setQuestion("[Тест №" + (i + 1) + "] " + baseQA[index][0]);
            qa.setAnswer("[Ответ] " + baseQA[index][1]);
            mockList.add(qa);
        }

        return mockList;
    }

    /**
     * Получить список всех контрольных работ из базы данных
     */
    @Transactional(readOnly = true)
    public List<ControlWork> getAllControlWorks() {
        return repository.findAll();
    }

    /**
     * Удалить контрольную работу по её системному ID
     */
    @Transactional
    public void deleteControlWork(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Контрольная работа с ID " + id + " не найдена.");
        }
    }

    /**
     * Обновляет список вопросов для существующей контрольной работы.
     */
    @Transactional
    public void updateControlWorkQuestions(String externalId, List<QuestionDto> updatedQuestions) {
        Optional<ControlWork> workOptional = repository.findByExternalId(externalId);

        if (workOptional.isPresent()) {
            ControlWork work = workOptional.get();

            // 1. Очищаем старые вопросы
            work.getQuestionAnswers().clear();

            // 2. Создаем новые сущности вопросов
            for (QuestionDto dto : updatedQuestions) {
                QuestionAnswer qa = new QuestionAnswer();
                qa.setQuestionText(dto.getText());
                qa.setAnswerText(dto.getAnswer());

                // ИСПОЛЬЗУЕМ getNumber() ИЗ QuestionDto
                qa.setOrder(dto.getNumber());

                // Привязываем вопрос к работе
                work.addQuestionAnswer(qa);
            }

            // 3. Сохраняем обновленную сущность
            repository.save(work);
        } else {
            throw new IllegalArgumentException("Контрольная работа с ID " + externalId + " не найдена.");
        }
    }
}