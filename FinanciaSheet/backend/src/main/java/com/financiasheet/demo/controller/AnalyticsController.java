package com.financiasheet.demo.controller;

import com.financiasheet.demo.dto.analytics.*;
import com.financiasheet.demo.entity.User;
import com.financiasheet.demo.repository.UserRepository;
import com.financiasheet.demo.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService svc;
    private final UserRepository users;

    public AnalyticsController(AnalyticsService svc, UserRepository users) {
        this.svc = svc;
        this.users = users;
    }

    private UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User u = users.findByEmail(email).orElseThrow();
        return u.getId();
    }

    @GetMapping("/overview")
    public ResponseEntity<OverviewDTO> overview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(svc.overview(currentUserId(), from, to));
    }

    @GetMapping("/categories/top")
    public ResponseEntity<List<CategoryTotalDTO>> topCategories(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(svc.topCategories(currentUserId(), from, to, limit));
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyPointDTO>> daily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(svc.daily(currentUserId(), from, to));
    }

    @GetMapping("/cashflow/monthly")
    public ResponseEntity<List<MonthlyCashflowDTO>> monthly(@RequestParam(defaultValue = "12") int months) {
        return ResponseEntity.ok(svc.monthly(currentUserId(), months));
    }

    // maiores gastos agrupados pela descricao original
    @GetMapping("/merchants/top")
    public ResponseEntity<List<MerchantTotalDTO>> topMerchants(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(svc.topMerchants(currentUserId(), from, to, limit));
    }

    // meses disponiveis para o seletor do dashboard
    @GetMapping("/months")
    public ResponseEntity<List<LocalDate>> months() {
        return ResponseEntity.ok(svc.availableMonths(currentUserId()));
    }
}
