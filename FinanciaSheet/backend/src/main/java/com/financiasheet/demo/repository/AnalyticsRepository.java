package com.financiasheet.demo.repository;

import com.financiasheet.demo.repository.proj.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface AnalyticsRepository extends Repository<com.financiasheet.demo.entity.Transaction, UUID> {

    // Resumo do período
    @Query(value = """
    select
      coalesce(sum(amount) filter (where amount > 0), 0) as received,
      coalesce(-sum(amount) filter (where amount < 0), 0) as spent,
      coalesce(sum(amount), 0) as balance,
      count(*) as tx_count
    from transactions t
    where t.user_id = :userId
      and t.date >= :from and t.date < :to
    """, nativeQuery = true)
    OverviewProj overview(@Param("userId") UUID userId,
                          @Param("from") Timestamp from,
                          @Param("to")   Timestamp to);

    // Top categorias (gastos)
    @Query(value = """
    select
      coalesce(nullif(category, ''), 'Sem categoria') as category,
      -sum(amount) as total
    from transactions t
    where t.user_id = :userId
      and t.amount < 0
      and t.date >= :from and t.date < :to
    group by 1
    order by total desc
    limit :limit
    """, nativeQuery = true)
    List<CategoryTotalProj> topCategories(@Param("userId") UUID userId,
                                          @Param("from") Timestamp from,
                                          @Param("to")   Timestamp to,
                                          @Param("limit") int limit);

    // Série diária
    @Query(value = """
    with days as (
      select generate_series(:from::date, (:to::date - interval '1 day')::date, interval '1 day')::date d
    ),
    agg as (
      select date::date d,
             coalesce(sum(amount) filter (where amount > 0), 0) as received,
             coalesce(-sum(amount) filter (where amount < 0), 0) as spent
      from transactions t
      where t.user_id = :userId
        and t.date >= :from and t.date < :to
      group by 1
    )
    select d,
           coalesce(a.received,0) as received,
           coalesce(a.spent,0)    as spent,
           coalesce(a.received,0) - coalesce(a.spent,0) as balance
    from days
    left join agg a using (d)
    order by d
    """, nativeQuery = true)
    List<DailyPointProj> daily(@Param("userId") UUID userId,
                               @Param("from") Timestamp from,
                               @Param("to")   Timestamp to);

    // Cashflow mensal (últimos N meses)
    @Query(value = """
    with months as (
      select date_trunc('month', now()) - (i || ' months')::interval as m
      from generate_series(0, :months - 1) as g(i)
    ),
    agg as (
      select date_trunc('month', date) m,
             coalesce(sum(amount) filter (where amount > 0), 0) as received,
             coalesce(-sum(amount) filter (where amount < 0), 0) as spent
      from transactions t
      where t.user_id = :userId
        and t.date >= (date_trunc('month', now()) - (:months || ' months')::interval)
      group by 1
    )
    select to_char(m.m, 'YYYY-MM') as month,
           coalesce(a.received,0) as received,
           coalesce(a.spent,0)    as spent,
           coalesce(a.received,0) - coalesce(a.spent,0) as balance
    from months m
    left join agg a on a.m = m.m
    order by m.m
    """, nativeQuery = true)
    List<MonthlyCashflowProj> monthly(@Param("userId") UUID userId,
                                      @Param("months") int months);

    // Maiores gastos por descrição
    @Query(value = """
    select trim(lower(description)) as description,
           -sum(amount) as total,
           count(*) as count
    from transactions t
    where t.user_id = :userId
      and t.amount < 0
      and t.date >= :from and t.date < :to
    group by 1
    order by total desc
    limit :limit
    """, nativeQuery = true)
    List<MerchantTotalProj> topMerchants(@Param("userId") UUID userId,
                                         @Param("from") Timestamp from,
                                         @Param("to")   Timestamp to,
                                         @Param("limit") int limit);

    // *** Defaults / seletor de mês ***

    // 1) Primeiro dia do mês mais recente com transações
    @Query(value = """
    select date_trunc('month', max(t.date))::date as month_start
    from transactions t
    where t.user_id = :userId
    """, nativeQuery = true)
    Date lastMonthWithData(@Param("userId") UUID userId);

    // 2) Lista de meses (primeiro dia de cada mês com movimento), desc
    @Query(value = """
    select distinct date_trunc('month', t.date)::date as month_start
    from transactions t
    where t.user_id = :userId
    order by month_start desc
    """, nativeQuery = true)
    List<Date> availableMonths(@Param("userId") UUID userId);
}
