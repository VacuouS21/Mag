package ru.magistr.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "control_work")
public class ControlWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId; // ID, который приходит из внешней системы

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Связь с вопросами: одна контрольная работа -> много вопросов
    @OneToMany(mappedBy = "controlWork", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();

    // Хелпер-методы для удобного добавления вопросов
    public void addQuestionAnswer(QuestionAnswer qa) {
        questionAnswers.add(qa);
        qa.setControlWork(this);
    }

    public void removeQuestionAnswer(QuestionAnswer qa) {
        questionAnswers.remove(qa);
        qa.setControlWork(null);
    }
}