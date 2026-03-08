package ru.magistr.service;

import ru.magistr.data.entity.ControlWorkDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ControlWorkService {

    private List<ControlWorkDTO> controlWorks = new ArrayList<>();

    public ControlWorkService() {
        // Добавляем тестовые данные для демонстрации
        controlWorks.add(new ControlWorkDTO(1,"Контрольная по математике", UUID.randomUUID().toString().substring(0, 8), 15));
        controlWorks.add(new ControlWorkDTO(2,"Тест по физике", UUID.randomUUID().toString().substring(0, 8), 10));
        controlWorks.add(new ControlWorkDTO(3,"Экзамен по программированию", UUID.randomUUID().toString().substring(0, 8), 20));
        controlWorks.add(new ControlWorkDTO(4,"Контрольная работа по истории", UUID.randomUUID().toString().substring(0, 8), 12));
        controlWorks.add(new ControlWorkDTO(5,"Тестирование по английскому языку", UUID.randomUUID().toString().substring(0, 8), 8));
        controlWorks.add(new ControlWorkDTO(6,"Практическая работа по химии", UUID.randomUUID().toString().substring(0, 8), 18));
    }

    public List<ControlWorkDTO> getControlWorks() {
        return controlWorks;
    }

    public void addControlWork(ControlWorkDTO controlWork) {
        controlWorks.add(0, controlWork);
    }

    public void removeControlWork(ControlWorkDTO controlWork) {
        controlWorks.remove(controlWork);
    }
}