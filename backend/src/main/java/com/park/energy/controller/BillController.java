package com.park.energy.controller;

import com.park.energy.model.Adjustment;
import com.park.energy.model.Bill;
import com.park.energy.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public List<Bill> getAllBills() {
        return billService.getAllBills();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable String id) {
        Optional<Bill> bill = billService.getBillById(id);
        return bill.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/generate/{enterpriseId}/{year}/{month}")
    public ResponseEntity<Bill> generateBill(
            @PathVariable String enterpriseId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        try {
            Bill bill = billService.generateMonthlyBill(enterpriseId, year, month);
            return ResponseEntity.ok(bill);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Bill> confirmBill(
            @PathVariable String id,
            @RequestParam(required = false) String confirmedBy) {
        try {
            Bill confirmed = billService.confirmBill(id, confirmedBy != null ? confirmedBy : "system");
            return ResponseEntity.ok(confirmed);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/adjustment")
    public ResponseEntity<Adjustment> createAdjustment(
            @PathVariable String id,
            @RequestParam Adjustment.AdjustmentType type,
            @RequestParam BigDecimal amount,
            @RequestParam String reason,
            @RequestParam(required = false) String createdBy) {
        try {
            Adjustment adjustment = billService.createAdjustment(
                    id, type, amount, reason, createdBy != null ? createdBy : "system");
            return ResponseEntity.ok(adjustment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Bill> payBill(
            @PathVariable String id,
            @RequestParam BigDecimal amount) {
        try {
            Bill paid = billService.payBill(id, amount);
            return ResponseEntity.ok(paid);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/enterprise/{enterpriseId}")
    public List<Bill> getBillsByEnterprise(@PathVariable String enterpriseId) {
        return billService.getBillsByEnterprise(enterpriseId);
    }

    @GetMapping("/month/{year}/{month}")
    public List<Bill> getBillsByMonth(@PathVariable Integer year, @PathVariable Integer month) {
        return billService.getBillsByYearMonth(year, month);
    }
}
