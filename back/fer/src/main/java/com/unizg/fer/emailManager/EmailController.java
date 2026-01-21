package com.unizg.fer.emailManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.unizg.fer.plan.PlanManager;
import com.unizg.fer.stats.StatsService;

@RestController
@RequestMapping("/api/v2/mail")
public class EmailController {

    @Autowired
    StatsService service;

    @Autowired
    PlanManager manager;

    /***
     * FOR ADMIN ONLY
     * Manual endpoint to trigger @cron methods
     * Since free version of render is in sleep mode in 15 min
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(path = "/trigger", method = RequestMethod.GET)
    public void triggerUserEngagement() {
        service.checkAndNotifyUserEngagement();
        manager.notifyPlan();
        manager.deletePassedPlans();
    }
}
