-- Таблица для контрольных работ
CREATE TABLE control_work (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Внутренний технический ID',
                              external_id VARCHAR(255) NOT NULL UNIQUE COMMENT 'Уникальный ID, заполняемый извне',
                              name VARCHAR(255) COMMENT 'Наименование работы', -- <-- ДОБАВИЛИ ЭТУ СТРОКУ
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата создания записи'
);

-- Таблица для вопросов и ответов
CREATE TABLE question_answer (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Внутренний технический ID вопроса-ответа',
                                 control_work_id BIGINT NOT NULL COMMENT 'Внешний ключ к контрольной работе',
                                 question_text TEXT NOT NULL COMMENT 'Текст вопроса',
                                 answer_text TEXT NOT NULL COMMENT 'Текст ответа',
                                 question_order INT DEFAULT 0 COMMENT 'Порядок вопроса в контрольной',
                                 FOREIGN KEY (control_work_id) REFERENCES control_work(id) ON DELETE CASCADE
);

-- Индекс для быстрого поиска по внешнему ключу
CREATE INDEX idx_question_answer_control_work_id ON question_answer(control_work_id);


-- Таблица пользователей (преподаватели и студенты)
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    user_type VARCHAR(20) NOT NULL CHECK (user_type IN ('teacher', 'student')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица групп
CREATE TABLE groups (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    description TEXT,
    teacher_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Таблица участников групп (связь многие-ко-многим)
CREATE TABLE group_members (
    group_member_id SERIAL PRIMARY KEY,
    group_id INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE (group_id, student_id)
);

-- Таблица контрольных работ
CREATE TABLE tests (
    test_id SERIAL PRIMARY KEY,
    test_name VARCHAR(200) NOT NULL,
    description TEXT,
    teacher_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Таблица вопросов контрольных работ
CREATE TABLE questions (
    question_id SERIAL PRIMARY KEY,
    test_id INTEGER NOT NULL,
    question_text TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    question_order INTEGER NOT NULL,
    points DECIMAL(5,2) DEFAULT 1.00,
    FOREIGN KEY (test_id) REFERENCES tests(test_id) ON DELETE CASCADE
);

-- Таблица назначений контрольных работ группам
CREATE TABLE test_assignments (
    assignment_id SERIAL PRIMARY KEY,
    test_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES tests(test_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE,
    UNIQUE (test_id, group_id)
);

-- Таблица сданных работ студентами
CREATE TABLE test_submissions (
    submission_id SERIAL PRIMARY KEY,
    test_id INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'in_progress'
        CHECK (status IN ('in_progress', 'under_review', 'reviewed')),
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    total_score DECIMAL(5,2),
    FOREIGN KEY (test_id) REFERENCES tests(test_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Таблица ответов студентов на вопросы
CREATE TABLE student_answers (
    answer_id SERIAL PRIMARY KEY,
    submission_id INTEGER NOT NULL,
    question_id INTEGER NOT NULL,
    student_answer TEXT,
    is_correct BOOLEAN,
    points_earned DECIMAL(5,2),
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES test_submissions(submission_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    UNIQUE (submission_id, question_id)
);

-- Таблица чатов
CREATE TABLE chats (
    chat_id SERIAL PRIMARY KEY,
    chat_type VARCHAR(20) NOT NULL CHECK (chat_type IN ('personal', 'group')),
    group_id INTEGER,
    chat_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE CASCADE
);

-- Таблица участников чатов (для личных чатов)
CREATE TABLE chat_participants (
    chat_participant_id SERIAL PRIMARY KEY,
    chat_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE (chat_id, user_id)
);

-- Таблица сообщений в чатах
CREATE TABLE messages (
    message_id SERIAL PRIMARY KEY,
    chat_id INTEGER NOT NULL,
    sender_id INTEGER NOT NULL,
    message_text TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_users_login ON users(login);
CREATE INDEX idx_users_type ON users(user_type);
CREATE INDEX idx_groups_teacher ON groups(teacher_id);
CREATE INDEX idx_group_members_group ON group_members(group_id);
CREATE INDEX idx_group_members_student ON group_members(student_id);
CREATE INDEX idx_tests_teacher ON tests(teacher_id);
CREATE INDEX idx_questions_test ON questions(test_id);
CREATE INDEX idx_test_assignments_test ON test_assignments(test_id);
CREATE INDEX idx_test_assignments_group ON test_assignments(group_id);
CREATE INDEX idx_submissions_test ON test_submissions(test_id);
CREATE INDEX idx_submissions_student ON test_submissions(student_id);
CREATE INDEX idx_submissions_status ON test_submissions(status);
CREATE INDEX idx_student_answers_submission ON student_answers(submission_id);
CREATE INDEX idx_chats_group ON chats(group_id);
CREATE INDEX idx_messages_chat ON messages(chat_id);
CREATE INDEX idx_messages_sender ON messages(sender_id);
