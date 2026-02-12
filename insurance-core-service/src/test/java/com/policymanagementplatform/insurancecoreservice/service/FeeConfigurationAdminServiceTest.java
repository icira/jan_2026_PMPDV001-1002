package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.FeeConfiguration;
import com.policymanagementplatform.insurancecoreservice.domain.FeeConfigurationType;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.FeeConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeeConfigurationAdminServiceTest {

    /*
    LANES ADDED:
    - Unit tests for FeeConfigurationAdminService.
    WHY:
    - Covers create/get/list/update paths, and not-found behavior.
    */

    private FeeConfigurationRepository feeRepository;
    private FeeConfigurationAdminService service;

    @BeforeEach
    void setUp() {
        feeRepository = mock(FeeConfigurationRepository.class);
        service = new FeeConfigurationAdminService(feeRepository);
    }

    @Test
    void createShouldSaveFeeConfiguration() {
        when(feeRepository.save(any(FeeConfiguration.class))).thenAnswer(inv -> inv.getArgument(0));

        FeeConfiguration created = service.create(
                "Admin",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                LocalDate.of(2026, 1, 1),
                null,
                true
        );

        assertNotNull(created);
        assertEquals("Admin", created.getName());
        assertEquals(FeeConfigurationType.ADMIN_FEE, created.getType());

        verify(feeRepository).save(any(FeeConfiguration.class));
        verifyNoMoreInteractions(feeRepository);
    }

    @Test
    void getShouldThrowNotFoundWhenMissing() {
        when(feeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.get(1L));

        verify(feeRepository).findById(1L);
        verifyNoMoreInteractions(feeRepository);
    }

    @Test
    void getShouldReturnWhenExists() {
        FeeConfiguration cfg = mock(FeeConfiguration.class);
        when(feeRepository.findById(1L)).thenReturn(Optional.of(cfg));

        FeeConfiguration result = service.get(1L);

        assertSame(cfg, result);
        verify(feeRepository).findById(1L);
        verifyNoMoreInteractions(feeRepository);
    }

    @Test
    void updateShouldThrowNotFoundWhenMissing() {
        when(feeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                service.update(1L, 3.5, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 12, 31), false)
        );

        verify(feeRepository).findById(1L);
        verifyNoMoreInteractions(feeRepository);
    }

    @Test
    void updateShouldMutateExistingEntityAndReturnIt() {
        FeeConfiguration cfg = spy(new FeeConfiguration(
                "Admin",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                LocalDate.of(2026, 1, 1),
                null,
                true
        ));
        when(feeRepository.findById(1L)).thenReturn(Optional.of(cfg));

        FeeConfiguration updated = service.update(
                1L,
                3.5,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 12, 31),
                false
        );

        assertSame(cfg, updated);
        assertEquals(3.5, updated.getPercentage(), 0.0001);
        assertFalse(updated.isActive());

        verify(feeRepository).findById(1L);
        verify(cfg).update(3.5, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 12, 31), false);
        verifyNoMoreInteractions(feeRepository);
    }

    @Test
    void listShouldDelegateToRepository() {
        FeeConfiguration f1 = mock(FeeConfiguration.class);
        FeeConfiguration f2 = mock(FeeConfiguration.class);
        List<FeeConfiguration> expected = List.of(f1, f2);

        when(feeRepository.findAll()).thenReturn(expected);

        List<FeeConfiguration> result = service.list();

        assertSame(expected, result);
        verify(feeRepository).findAll();
        verifyNoMoreInteractions(feeRepository);
    }

}
