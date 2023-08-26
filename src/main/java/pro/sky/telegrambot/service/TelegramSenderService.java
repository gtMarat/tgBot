package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelegramSenderService {
    Logger logger = LoggerFactory.getLogger(TelegramSenderService.class);
    private final TelegramBot telegramBot;

    public TelegramSenderService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void send(long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse response = telegramBot.execute(sendMessage);

        if (!response.isOk()) {
            logger.error("Error occured during sending message to bot. Error : " + response.description());
        }
    }
}
