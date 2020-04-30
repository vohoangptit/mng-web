package com.nera.nms.components;

import com.nera.nms.services.JobAssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SchedulingStatusComponent {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private JobAssignmentService jobAssignmentService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void scheduleTaskWithCronExpression() {
        jobAssignmentService.findJobAssignPending();
        String dateTime = dateTimeFormatter.format(LocalDateTime.now());
        logger.info("Cron Task :: Execution Time - {}", dateTime);
    }
}
