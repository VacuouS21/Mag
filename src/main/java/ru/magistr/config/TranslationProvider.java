package ru.magistr.config;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class TranslationProvider implements I18NProvider {

    private final MessageSource messageSource;

    // Указываем поддерживаемые локали. В нашем случае это русская.
    public static final Locale LOCALE_RU = new Locale("ru", "RU");

    public TranslationProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(LOCALE_RU);
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            return "";
        }
        try {
            // Запрашиваем перевод у Spring
            return messageSource.getMessage(key, params, locale);
        } catch (NoSuchMessageException e) {
            // Если ключ не найден, возвращаем сам ключ, чтобы сразу увидеть это на UI
            return "!" + key + "!";
        }
    }
}