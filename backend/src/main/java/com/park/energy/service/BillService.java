package com.park.energy.service;

import com.park.energy.model.Adjustment;
import com.park.energy.model.Bill;
import com.park.energy.model.Enterprise;
import com.park.energy.model.Meter;
import com.park.energy.model.UsageStatistics;
import com.park.energy.repository.AdjustmentRepository;
import com.park.energy.repository.BillRepository;
import com.park.energy.repository.EnterpriseRepository;
import com.park.energy.repository.UsageStatisticsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UsageStatisticsRepository usageStatisticsRepository;
    private final AdjustmentRepository adjustmentRepository;
    private final UsageStatisticsService usageStatisticsService;

    @Value("${energy.quota.water-price:5.0}")
    private BigDecimal waterPrice;

    @Value("${energy.quota.electricity-price:1.0}")
    private BigDecimal electricityPrice;

    public BillService(BillRepository billRepository,
                       EnterpriseRepository enterpriseRepository,
                       UsageStatisticsRepository usageStatisticsRepository,
                       AdjustmentRepository adjustmentRepository,
                       UsageStatisticsService usageStatisticsService) {
        this.billRepository = billRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.usageStatisticsRepository = usageStatisticsRepository;
        this.adjustmentRepository = adjustmentRepository;
        this.usageStatisticsService = usageStatisticsService;
    }

    public Bill generateMonthlyBill(String enterpriseId, Integer year, Integer month) {
        Optional<Bill> existing = billRepository.findByEnterpriseIdAndYearMonth(enterpriseId, year, month);
        if (existing.isPresent()) {
            return existing.get();
        }

        Optional<Enterprise> enterpriseOpt = enterpriseRepository.findById(enterpriseId);
        if (!enterpriseOpt.isPresent()) {
            throw new RuntimeException("Enterprise not found: " + enterpriseId);
        }

        Enterprise enterprise = enterpriseOpt.get();

        usageStatisticsService.aggregateMonthlyStatistics(enterpriseId, Meter.MeterType.ELECTRICITY, year, month);
        usageStatisticsService.aggregateMonthlyStatistics(enterpriseId, Meter.MeterType.WATER, year, month);

        LocalDate firstDay = LocalDate.of(year, month, 1);
        List<UsageStatistics> monthlyStats = usageStatisticsRepository
                .findByEnterpriseIdAndPeriod(enterpriseId, UsageStatistics.StatisticsPeriod.MONTHLY).stream()
                .filter(s -> year.equals(s.getYear()) && month.equals(s.getMonth()))
                .collect(Collectors.toList());

        BigDecimal electricityConsumption = monthlyStats.stream()
                .filter(s -> Meter.MeterType.ELECTRICITY == s.getType())
                .findFirst()
                .map(UsageStatistics::getConsumption)
                .orElse(BigDecimal.ZERO);

        BigDecimal waterConsumption = monthlyStats.stream()
                .filter(s -> Meter.MeterType.WATER == s.getType())
                .findFirst()
                .map(UsageStatistics::getConsumption)
                .orElse(BigDecimal.ZERO);

        BigDecimal electricityQuota = enterprise.getMonthlyElectricityQuota();
        BigDecimal waterQuota = enterprise.getMonthlyWaterQuota();

        BigDecimal electricityOverQuota = electricityConsumption.max(electricityQuota).subtract(electricityQuota);
        BigDecimal waterOverQuota = waterConsumption.max(waterQuota).subtract(waterQuota);

        BigDecimal basicElectricityCost = electricityQuota.min(electricityConsumption).multiply(electricityPrice);
        BigDecimal basicWaterCost = waterQuota.min(waterConsumption).multiply(waterPrice);

        BigDecimal overQuotaElectricityCost = calculateTieredCost(electricityOverQuota, electricityPrice);
        BigDecimal overQuotaWaterCost = calculateTieredCost(waterOverQuota, waterPrice);

        BigDecimal totalAmount = basicElectricityCost.add(basicWaterCost)
                .add(overQuotaElectricityCost).add(overQuotaWaterCost);

        Bill bill = Bill.builder()
                .enterpriseId(enterpriseId)
                .year(year)
                .month(month)
                .electricityConsumption(electricityConsumption)
                .waterConsumption(waterConsumption)
                .electricityQuota(electricityQuota)
                .waterQuota(waterQuota)
                .electricityOverQuota(electricityOverQuota)
                .waterOverQuota(waterOverQuota)
                .basicElectricityCost(basicElectricityCost)
                .basicWaterCost(basicWaterCost)
                .overQuotaElectricityCost(overQuotaElectricityCost)
                .overQuotaWaterCost(overQuotaWaterCost)
                .totalAmount(totalAmount)
                .discountAmount(BigDecimal.ZERO)
                .finalAmount(totalAmount)
                .paidAmount(BigDecimal.ZERO)
                .status(Bill.BillStatus.DRAFT)
                .generatedAt(LocalDateTime.now())
                .build();

        return billRepository.save(bill);
    }

    private BigDecimal calculateTieredCost(BigDecimal overQuota, BigDecimal basePrice) {
        if (overQuota.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal remaining = overQuota;
        BigDecimal totalCost = BigDecimal.ZERO;

        BigDecimal tier1Threshold = new BigDecimal("20");
        BigDecimal tier1Multiplier = new BigDecimal("1.2");
        BigDecimal tier2Threshold = new BigDecimal("50");
        BigDecimal tier2Multiplier = new BigDecimal("1.5");
        BigDecimal tier3Multiplier = new BigDecimal("2.0");

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tier1Amount = remaining.min(tier1Threshold);
            totalCost = totalCost.add(tier1Amount.multiply(basePrice).multiply(tier1Multiplier));
            remaining = remaining.subtract(tier1Amount);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tier2Amount = remaining.min(tier2Threshold.subtract(tier1Threshold));
            totalCost = totalCost.add(tier2Amount.multiply(basePrice).multiply(tier2Multiplier));
            remaining = remaining.subtract(tier2Amount);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            totalCost = totalCost.add(remaining.multiply(basePrice).multiply(tier3Multiplier));
        }

        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    public Bill confirmBill(String billId, String confirmedBy) {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (!billOpt.isPresent()) {
            throw new RuntimeException("Bill not found: " + billId);
        }

        Bill bill = billOpt.get();
        if (Bill.BillStatus.CONFIRMED == bill.getStatus() || Bill.BillStatus.PAID == bill.getStatus()) {
            throw new RuntimeException("Bill is already confirmed or paid");
        }

        bill.setStatus(Bill.BillStatus.CONFIRMED);
        bill.setConfirmedAt(LocalDateTime.now());
        bill.setConfirmedBy(confirmedBy);

        return billRepository.save(bill);
    }

    public Adjustment createAdjustment(String billId, Adjustment.AdjustmentType type, BigDecimal amount,
                                        String reason, String createdBy) {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (!billOpt.isPresent()) {
            throw new RuntimeException("Bill not found: " + billId);
        }

        Bill bill = billOpt.get();
        if (Bill.BillStatus.DRAFT == bill.getStatus()) {
            throw new RuntimeException("Cannot create adjustment for draft bill, confirm it first");
        }

        Adjustment adjustment = Adjustment.builder()
                .billId(billId)
                .enterpriseId(bill.getEnterpriseId())
                .type(type)
                .amount(amount)
                .reason(reason)
                .createdBy(createdBy)
                .build();

        Adjustment saved = adjustmentRepository.save(adjustment);

        bill.getAdjustments().add(saved);
        recalculateBillAmount(bill);
        billRepository.save(bill);

        return saved;
    }

    private void recalculateBillAmount(Bill bill) {
        BigDecimal adjustmentTotal = bill.getAdjustments().stream()
                .map(adj -> {
                    if (adj.getType() == Adjustment.AdjustmentType.MANUAL_DEDUCT
                            || adj.getType() == Adjustment.AdjustmentType.ENERGY_SAVING_REWARD) {
                        return adj.getAmount().negate();
                    }
                    return adj.getAmount();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        bill.setDiscountAmount(adjustmentTotal.negate().max(BigDecimal.ZERO));
        bill.setFinalAmount(bill.getTotalAmount().add(adjustmentTotal).max(BigDecimal.ZERO));
    }

    public Bill payBill(String billId, BigDecimal amount) {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (!billOpt.isPresent()) {
            throw new RuntimeException("Bill not found: " + billId);
        }

        Bill bill = billOpt.get();
        if (Bill.BillStatus.DRAFT == bill.getStatus()) {
            throw new RuntimeException("Cannot pay draft bill, confirm it first");
        }

        BigDecimal newPaidAmount = bill.getPaidAmount().add(amount);
        bill.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(bill.getFinalAmount()) >= 0) {
            bill.setStatus(Bill.BillStatus.PAID);
            bill.setPaidAt(LocalDateTime.now());
        } else {
            bill.setStatus(Bill.BillStatus.PARTIALLY_PAID);
        }

        return billRepository.save(bill);
    }

    public void markOverdueBills() {
        List<Bill> overdueBills = billRepository.findOverdueBills();
        for (Bill bill : overdueBills) {
            if (Bill.BillStatus.OVERDUE != bill.getStatus()) {
                bill.setStatus(Bill.BillStatus.OVERDUE);
                billRepository.save(bill);

                Optional<Enterprise> enterpriseOpt = enterpriseRepository.findById(bill.getEnterpriseId());
                if (enterpriseOpt.isPresent()) {
                    Enterprise enterprise = enterpriseOpt.get();
                    enterprise.setServicesRestricted(true);
                    enterpriseRepository.save(enterprise);
                }
            }
        }
    }

    public List<Bill> getBillsByEnterprise(String enterpriseId) {
        return billRepository.findByEnterpriseId(enterpriseId);
    }

    public List<Bill> getBillsByYearMonth(Integer year, Integer month) {
        return billRepository.findByYearMonth(year, month);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Optional<Bill> getBillById(String id) {
        return billRepository.findById(id);
    }
}
