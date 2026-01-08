package ru.magistr.view.services;

import ru.magistr.view.WorksListView.ControlWork;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ControlWorkService {

    private List<ControlWork> controlWorks = new ArrayList<>();

    public ControlWorkService() {
        // Добавляем тестовые данные для демонстрации
        controlWorks.add(new ControlWork("Контрольная по математике", UUID.randomUUID().toString().substring(0, 8), 15));
        controlWorks.add(new ControlWork("Тест по физике", UUID.randomUUID().toString().substring(0, 8), 10));
        controlWorks.add(new ControlWork("Экзамен по программированию", UUID.randomUUID().toString().substring(0, 8), 20));
        controlWorks.add(new ControlWork("Контрольная работа по истории", UUID.randomUUID().toString().substring(0, 8), 12));
        controlWorks.add(new ControlWork("Тестирование по английскому языку", UUID.randomUUID().toString().substring(0, 8), 8));
        controlWorks.add(new ControlWork("Практическая работа по химии", UUID.randomUUID().toString().substring(0, 8), 18));
    }

    public List<ControlWork> getControlWorks() {
        return controlWorks;
    }

    public void addControlWork(ControlWork controlWork) {
        controlWorks.add(0, controlWork);
    }

    public void removeControlWork(ControlWork controlWork) {
        controlWorks.remove(controlWork);
    }
}