package ru.magistr.data.entity;

public class QuestionDto {
    private int number;
    private String text;
    private String answer;

    public QuestionDto(int number, String text, String answer) {
        this.number = number;
        this.text = text;
        this.answer = answer;
    }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}