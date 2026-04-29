package com.park.energy.repository;

import com.park.energy.model.Bill;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class BillRepository {

    private final Map<String, Bill> bills = new ConcurrentHashMap<>();

    public Bill save(Bill bill) {
        if (bill.getId() == null) {
            bill.setId(UUID.randomUUID().toString());
        }
        if (bill.getCreatedAt() == null) {
            bill.setCreatedAt(LocalDateTime.now());
        }
        bills.put(bill.getId(), bill);
        return bill;
    }

    public Optional<Bill> findById(String id) {
        return Optional.ofNullable(bills.get(id));
    }

    public List<Bill> findAll() {
        return new ArrayList<>(bills.values());
    }

    public List<Bill> findByEnterpriseId(String enterpriseId) {
        return bills.values().stream()
                .filter(b -> enterpriseId.equals(b.getEnterpriseId()))
                .sorted(Comparator.comparing(Bill::getYear).reversed()
                        .thenComparing(Bill::getMonth).reversed())
                .collect(Collectors.toList());
    }

    public Optional<Bill> findByEnterpriseIdAndYearMonth(String enterpriseId, Integer year, Integer month) {
        return bills.values().stream()
                .filter(b -> enterpriseId.equals(b.getEnterpriseId())
                        && year.equals(b.getYear())
                        && month.equals(b.getMonth()))
                .findFirst();
    }

    public List<Bill> findByYearMonth(Integer year, Integer month) {
        return bills.values().stream()
                .filter(b -> year.equals(b.getYear()) && month.equals(b.getMonth()))
                .collect(Collectors.toList());
    }

    public List<Bill> findByStatus(Bill.BillStatus status) {
        return bills.values().stream()
                .filter(b -> status == b.getStatus())
                .collect(Collectors.toList());
    }

    public List<Bill> findOverdueBills() {
        return bills.values().stream()
                .filter(b -> Bill.BillStatus.OVERDUE == b.getStatus()
                        || (Bill.BillStatus.CONFIRMED == b.getStatus()
                        && b.getGeneratedAt() != null
                        && b.getGeneratedAt().isBefore(LocalDateTime.now().minusDays(15))))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        bills.remove(id);
    }
}
