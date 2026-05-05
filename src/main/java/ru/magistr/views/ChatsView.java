package ru.magistr.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import ru.magistr.data.entity.ChatDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Route(value = "chats", layout = MainView.class)
@PermitAll
public class ChatsView extends SplitLayout {

    private Grid<ChatDto> chatsGrid;
    private MessageList messageList;
    private MessageInput messageInput;
    private VerticalLayout chatArea;
    private VerticalLayout placeholderArea;

    private ChatDto currentActiveChat;

    public ChatsView() {
        setSizeFull();
        setOrientation(Orientation.HORIZONTAL);

        // 1. Инициализируем панели
        initLeftPanel();
        initRightPanel();

        // 2. Создаем постоянный контейнер для правой части
        VerticalLayout rightPanelContainer = new VerticalLayout();
        rightPanelContainer.setSizeFull();
        rightPanelContainer.setPadding(false);
        rightPanelContainer.setSpacing(false);
        // Добавляем ОБЕ панели в контейнер раз и навсегда
        rightPanelContainer.add(placeholderArea, chatArea);

        // 3. Распределяем по SplitLayout
        addToPrimary(createLeftPanelLayout());
        addToSecondary(rightPanelContainer);

        setSplitterPosition(30);

        // 4. Начальное состояние: показываем заглушку, скрываем чат
        placeholderArea.setVisible(true);
        chatArea.setVisible(false);
    }

    private void initLeftPanel() {
        chatsGrid = new Grid<>();
        chatsGrid.setSizeFull(); // Гарантируем, что грид тянется по высоте
        chatsGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        chatsGrid.addComponentColumn(chat -> {
            VerticalLayout layout = new VerticalLayout();
            // ДОБАВЛЕНО: Жестко задаем ширину на 100%, чтобы контент не схлопывался при кликах
            layout.setWidthFull();
            layout.setPadding(true); // Добавим немного отступов для красоты
            layout.setSpacing(false);

            Span name = new Span(chat.participantName());
            name.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.MEDIUM);
            name.setWidthFull(); // Защита от схлопывания текста

            Span lastMsg = new Span(chat.lastMessage());
            lastMsg.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
            lastMsg.setWidthFull(); // Защита от схлопывания текста

            layout.add(name, lastMsg);
            return layout;
        });

        chatsGrid.setItems(getMockChats());

        chatsGrid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(this::openChat);
        });
    }

    private VerticalLayout createLeftPanelLayout() {
        H2 header = new H2(getTranslation("chat.view.title"));
        header.addClassNames(LumoUtility.Margin.MEDIUM);

        VerticalLayout layout = new VerticalLayout(header, chatsGrid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setFlexGrow(1, chatsGrid); // Заставляем грид занимать все место под заголовком
        layout.getStyle().set("border-right", "1px solid var(--lumo-contrast-10pct)");
        return layout;
    }

    private void initRightPanel() {
        placeholderArea = new VerticalLayout();
        placeholderArea.setSizeFull();
        placeholderArea.setAlignItems(FlexComponent.Alignment.CENTER);
        placeholderArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Span placeholderText = new Span(getTranslation("chat.view.placeholder"));
        placeholderText.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.LARGE);
        placeholderArea.add(placeholderText);

        chatArea = new VerticalLayout();
        chatArea.setSizeFull();
        chatArea.setPadding(true);

        messageList = new MessageList();
        messageList.setSizeFull();

        messageInput = new MessageInput();
        messageInput.setWidthFull();

        messageInput.setI18n(new MessageInputI18n()
                .setSend(getTranslation("chat.input.send"))
                .setMessage(getTranslation("chat.input.placeholder")));

        messageInput.addSubmitListener(event -> {
            sendMessage(event.getValue());
        });

        chatArea.add(messageList, messageInput);
    }

    private void openChat(ChatDto chat) {
        this.currentActiveChat = chat;

        // ВМЕСТО ПЕРЕСТРОЕНИЯ DOM ПРОСТО ПЕРЕКЛЮЧАЕМ ВИДИМОСТЬ
        placeholderArea.setVisible(false);
        chatArea.setVisible(true);

        List<MessageListItem> messages = fetchMessagesMock(chat.id());
        messageList.setItems(messages);
    }

    private void sendMessage(String text) {
        if (currentActiveChat == null) return;

        MessageListItem newMessage = new MessageListItem(
                text,
                Instant.now(),
                getTranslation("chat.message.you")
        );
        newMessage.setUserColorIndex(2);

        List<MessageListItem> currentMessages = new ArrayList<>(messageList.getItems());
        currentMessages.add(newMessage);
        messageList.setItems(currentMessages);
    }

    private List<ChatDto> getMockChats() {
        return List.of(
                new ChatDto("1", "Иван Иванов (Преподаватель)", "Не забудьте сдать работу!"),
                new ChatDto("2", "Анна Смирнова", "Подскажите по 3 задаче?")
        );
    }

    private List<MessageListItem> fetchMessagesMock(String chatId) {
        MessageListItem msg1 = new MessageListItem("Здравствуйте! Вы проверили мою работу?", Instant.now().minusSeconds(3600), getTranslation("chat.message.you"));
        MessageListItem msg2 = new MessageListItem("Да, оценка уже в системе.", Instant.now().minusSeconds(1800), "Иван Иванов");

        msg1.setUserColorIndex(1);
        msg2.setUserColorIndex(2);

        return List.of(msg1, msg2);
    }
}
