package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramSenderService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTaskRepository notificationTaskRepository;
    private final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final static Pattern PATTERN;

    static {
        PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    }

    private final TelegramBot telegramBot;
    private final TelegramSenderService telegramSenderService;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot, TelegramSenderService telegramSenderService) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
        this.telegramSenderService = telegramSenderService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            Long chatId = update.message().chat().id();
            String message = update.message().text();

            if ("/start".equals(message)) {
                telegramSenderService.send(chatId, "Добро пожаловать в Бота");
            } else {

                Matcher matcher = PATTERN.matcher(message);
                if (matcher.matches()) {
                    String task = matcher.group(3);
                    LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMAT);
                    notificationTaskRepository.save(new NotificationTask(
                            task ,chatId ,localDateTime ));
                    telegramSenderService.send(chatId , "Напоминание сохранено");
                } else {
                    telegramSenderService.send(chatId, "Неверный формат сообзения, попробуй еще раз");
                }
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;

    }

}
