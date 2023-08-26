package pro.sky.telegrambot.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramSenderService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class SendNotificationJob {

    private final Logger logger = LoggerFactory.getLogger(SendNotificationJob.class);

    private final NotificationTaskRepository notificationTaskRepository;

    private final TelegramSenderService telegramSenderService;

    public SendNotificationJob(NotificationTaskRepository notificationTaskRepository, TelegramSenderService telegramSenderService) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramSenderService = telegramSenderService;
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 1)
    public void sendNotifications() {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        logger.info("Sending notification job started for dateTime = " + currentDateTime);

        notificationTaskRepository.findAllByDateTime(currentDateTime).forEach(notificationTask -> {
       telegramSenderService.send(notificationTask.getChatId(),"Напоминание ! "+ notificationTask.getMessage());

        logger.info("Reminder for task with id = {} has been sent!" ,notificationTask.getId());
        });
        logger.info("Sending notification job finish for dateTime = " + currentDateTime);
    }
}
