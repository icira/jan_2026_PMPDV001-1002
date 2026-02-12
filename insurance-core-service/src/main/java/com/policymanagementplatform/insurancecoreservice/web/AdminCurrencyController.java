package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.domain.Currency;
import com.policymanagementplatform.insurancecoreservice.service.CurrencyService;
import com.policymanagementplatform.insurancecoreservice.web.dto.CreateCurrencyRequest;
import com.policymanagementplatform.insurancecoreservice.web.dto.UpdateCurrencyRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
LANES ADDED:
- AdminCurrencyController implements currency management endpoints.
WHY:
- Part 2: admins manage currencies and can activate/deactivate (with constraints).
*/
@RestController
@RequestMapping("/api/admin/currencies")
public class AdminCurrencyController {

    private final CurrencyService currencyService;

    public AdminCurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public List<Currency> list() {
        return currencyService.list();
    }

    @PostMapping
    public Currency create(@Valid @RequestBody CreateCurrencyRequest req) {
        return currencyService.create(req.code(), req.name(), req.exchangeRateToBase(), req.active());
    }

    @PutMapping("/{id}")
    public Currency update(@PathVariable Long id, @Valid @RequestBody UpdateCurrencyRequest req) {
        return currencyService.update(id, req.name(), req.exchangeRateToBase(), req.active());
    }
}
