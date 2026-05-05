package ru.magistr.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.magistr.data.entity.ControlWork;
import ru.magistr.data.entity.ControlWorkDTO;
import ru.magistr.data.entity.QuestionAnswer;
import ru.magistr.data.entity.QuestionDto;
import ru.magistr.data.repository.ControlWorkRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ControlWorkService {

    private final ControlWorkRepository repository;

    public ControlWorkService(ControlWorkRepository repository) {
        this.repository = repository;
    }

    // @Transactional обязателен, чтобы не словить LazyInitializationException
    // при подсчете количества вопросов (work.getQuestionAnswers().size())
    @Transactional(readOnly = true)
    public List<ControlWorkDTO> getControlWorks() {
        List<ControlWork> works = repository.findAll();

        return works.stream().map(work -> {
            // Формируем DTO для Vaadin Grid
            String name = "Работа " + work.getExternalId(); // Генерируем имя, так как в БД его нет
            String uniqueId = work.getExternalId();
            int questionsCount = work.getQuestionAnswers().size(); // Подтягиваем вопросы из БД

            return new ControlWorkDTO(work.getId(), name, uniqueId, questionsCount);
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
}