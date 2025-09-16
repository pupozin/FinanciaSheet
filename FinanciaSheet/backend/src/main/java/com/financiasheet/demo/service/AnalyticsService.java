package com.financiasheet.demo.service;

import com.financiasheet.demo.dto.analytics.*;
import com.financiasheet.demo.repository.AnalyticsRepository;
import com.financiasheet.demo.repository.proj.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final AnalyticsRepository repo;

    public AnalyticsService(AnalyticsRepository repo) { this.repo = repo; }

    // --------- helpers ---------
    private record Range(LocalDate from, LocalDate toExclusive) {}
    private static Timestamp ts(LocalDate d) { return Timestamp.valueOf(d.atStartOfDay()); }

    /** Se from/to forem nulos, usa o mês mais recente com dados; se não houver dados, usa mês corrente. */
    private Range resolveRange(UUID userId, LocalDate from, LocalDate toExclusive) {
        if (from != null && toExclusive != null) return new Range(from, toExclusive);

        var last = repo.lastMonthWithData(userId);
        if (last != null) {
            LocalDate start = last.toLocalDate();
            return new Range(start, start.plusMonths(1));
        }
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        return new Range(start, start.plusMonths(1));
    }

    // --------- consultas ---------
    public OverviewDTO overview(UUID userId, LocalDate from, LocalDate toExclusive) {
        var r = resolveRange(userId, from, toExclusive);
        OverviewProj p = repo.overview(userId, ts(r.from), ts(r.toExclusive));
        return new OverviewDTO(p.getReceived(), p.getSpent(), p.getBalance(), p.getTxCount());
    }

    public List<CategoryTotalDTO> topCategories(UUID userId, LocalDate from, LocalDate toExclusive, int limit) {
        var r = resolveRange(userId, from, toExclusive);
        return repo.topCategories(userId, ts(r.from), ts(r.toExclusive), limit)
                .stream()
                .map(p -> new CategoryTotalDTO(p.getCategory(), p.getTotal()))
                .collect(Collectors.toList());
    }

    public List<DailyPointDTO> daily(UUID userId, LocalDate from, LocalDate toExclusive) {
        var r = resolveRange(userId, from, toExclusive);
        return repo.daily(userId, ts(r.from), ts(r.toExclusive))
                .stream()
                .map(p -> new DailyPointDTO(p.getD(), p.getReceived(), p.getSpent(), p.getBalance()))
                .collect(Collectors.toList());
    }

    public List<MonthlyCashflowDTO> monthly(UUID userId, int months) {
        return repo.monthly(userId, months)
                .stream()
                .map(p -> new MonthlyCashflowDTO(p.getMonth(), p.getReceived(), p.getSpent(), p.getBalance()))
                .collect(Collectors.toList());
    }

    public List<MerchantTotalDTO> topMerchants(UUID userId, LocalDate from, LocalDate toExclusive, int limit) {
        var r = resolveRange(userId, from, toExclusive);
        return repo.topMerchants(userId, ts(r.from), ts(r.toExclusive), limit)
                .stream()
                .map(p -> new MerchantTotalDTO(p.getDescription(), p.getTotal(), p.getCount()))
                .collect(Collectors.toList());
    }

    public List<LocalDate> availableMonths(UUID userId) {
        return repo.availableMonths(userId).stream().map(java.sql.Date::toLocalDate).toList();
    }
}
