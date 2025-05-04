package com.achievix.dashboard;

import com.achievix.dashboard.dto.DashboardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard(@RequestParam String periodType) {
        DashboardDTO dashboard = dashboardService.getDashboard(periodType);
        return ResponseEntity.ok(dashboard);
    }
}