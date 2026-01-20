package com.unizg.fer.plan;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.unizg.fer.content.ContentService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unizg.fer.content.ContentRepository;
import com.unizg.fer.emailManager.EmailService;
import com.unizg.fer.emailManager.JSONValues;
import com.unizg.fer.emailManager.TemplateType;
import com.unizg.fer.user.UserRepository;

@Service
public class PlanManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanManager.class);

    @Autowired
    private PlanRepository planRepo;

    @Autowired
    private ContentRepository contentRepo;

    @Autowired
    private EmailService emails;

    @Autowired
    UserRepository userRepo;
    private SystemMetricsAutoConfiguration systemMetricsAutoConfiguration;
    @Autowired
    private ContentService contentService;

    /**
     * Create a new plan with the number of random content specified in the
     * PlanLevel and set createdAt at now
     * 
     * @param userId
     * @param level
     */
    @SuppressWarnings("null")
    public void createPlan(String userId, PlanLevel level) {
        // create a list of plan entry from a random stream of video type of content
        var toWatch = contentService.findRandomByType(level.getValue(), "video").stream()
                .map(content -> PlanEntry.builder()
                        .content(new ObjectId(content.getId()))
                        .notified(false)
                        .build())
                .toList();
        System.out.println(toWatch);
        planRepo.save(Plan.builder()
                .userId(userId)
                .toWatch(toWatch)
                .level(level)
                .createdAt(LocalDateTime.now())
                .build());

    }

    /***
     * Delete all plans where all the content has been notified to user
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void deletePassedPlans() {
        planRepo.streamAll()
                .filter(plan -> plan.getToWatch()
                        .stream()
                        // check if all plan entry are notified
                        .allMatch(PlanEntry::isNotified))
                .forEach(planRepo::delete);
    }

    /**
     * Iterate on all plans and notifies the user about the video he needs to wathe
     * today
     * Join Document Plan with userName not unique (1 user can have mutiple plans at
     * the same time)
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional(readOnly = true)
    public void notifyPlan() {
        try (Stream<Plan> stream = planRepo.streamAll()) {
            stream.filter(this::hasUnnotifiedEntries)
                    .forEach(this::notifyNextEntry);
        }
    }

    private boolean hasUnnotifiedEntries(Plan plan) {
        return plan.getToWatch().stream()
                .anyMatch(entry -> !entry.isNotified());
    }

    @SuppressWarnings("null")
    private void notifyNextEntry(Plan plan) {
        plan.getToWatch().stream()
                .filter(entry -> !entry.isNotified())
                .findFirst()
                .ifPresent(entry -> {
                    try {

                        var content = contentRepo
                                .findById(String.valueOf(entry.getContent()))
                                .get();
                        var user = userRepo.findById(plan.getUserId())
                                .get();

                        Map<JSONValues, String> variables = new HashMap<>();
                        variables.put(JSONValues.USER_NAME, user.getFirstName());
                        variables.put(JSONValues.VIDEO_DURATION, String.valueOf(content.getDurationMin()));
                        variables.put(JSONValues.VIDEO_TITLE, content.getTitle());
                        emails.sendEmail(user.getEmail(), TemplateType.PLAN, variables);

                        entry.setNotified(true);
                        planRepo.save(plan);
                    } catch (Exception e) {
                        // Non bloking error
                        LOGGER.error("Failed to notify plan: " + plan.getId(), e);
                    }
                });
    }

    public Iterable<Plan> getAllFromUser(String userId) {
        return planRepo.findAllByUserId(userId);
    }

}
