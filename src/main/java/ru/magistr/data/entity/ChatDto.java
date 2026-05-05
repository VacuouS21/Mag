package ru.magistr.data.entity;

// Можно вынести в ru.magistr.data.entity
public record ChatDto(String id, String participantName, String lastMessage) {}