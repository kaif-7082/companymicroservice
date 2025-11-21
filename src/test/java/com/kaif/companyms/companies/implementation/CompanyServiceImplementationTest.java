package com.kaif.companyms.companies.implementation;

import com.kaif.companyms.companies.Company;
import com.kaif.companyms.companies.CompanyRepository;
import com.kaif.companyms.companies.dto.companyRequestDto;
import com.kaif.companyms.companies.dto.companyResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplementationTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImplementation companyService;

    // --- TEST 1: Get All Companies ---
    @Test
    void testGetAllCompanies_Success() {
        // 1. Arrange
        Company c1 = new Company();
        c1.setId(1L);
        c1.setName("Google");

        Company c2 = new Company();
        c2.setId(2L);
        c2.setName("Microsoft");

        when(companyRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        // 2. Act
        List<companyResponseDto> result = companyService.getAllCompanies();

        // 3. Assert
        assertEquals(2, result.size());
        assertEquals("Google", result.get(0).getName());
        verify(companyRepository, times(1)).findAll();
    }

    // --- TEST 2: Create Company (Success) ---
    @Test
    void testCreateCompany_Success() {
        // 1. Arrange
        companyRequestDto request = new companyRequestDto();
        request.setName("Amazon");
        request.setDescription("E-commerce");
        request.setCeo("Andy Jassy");
        request.setFoundedYear(1994);

        // Stub: Name does NOT exist
        when(companyRepository.existsByName("Amazon")).thenReturn(false);

        // 2. Act
        companyService.createCompany(request);

        // 3. Assert
        verify(companyRepository, times(1)).existsByName("Amazon");
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    // --- TEST 3: Create Company (Duplicate Name Failure) ---
    @Test
    void testCreateCompany_DuplicateName() {
        // 1. Arrange
        companyRequestDto request = new companyRequestDto();
        request.setName("Google");

        // Stub: Name ALREADY exists
        when(companyRepository.existsByName("Google")).thenReturn(true);

        // 2. Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            companyService.createCompany(request);
        });

        assertEquals("Company with name 'Google' already exists!", exception.getMessage());
        // Verify we NEVER saved
        verify(companyRepository, never()).save(any(Company.class));
    }

    // --- TEST 4: Get Company By ID (Success) ---
    @Test
    void testGetCompanyById_Success() {
        // 1. Arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Tesla");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        // 2. Act
        Company result = companyService.getCompanyById(1L);

        // 3. Assert
        assertNotNull(result);
        assertEquals("Tesla", result.getName());
    }

    // --- TEST 5: Update Company (Success) ---
    @Test
    void testUpdateCompany_Success() {
        // 1. Arrange
        Long id = 1L;
        companyRequestDto request = new companyRequestDto();
        request.setName("Meta");
        request.setDescription("Social Media");

        Company existingCompany = new Company();
        existingCompany.setId(id);
        existingCompany.setName("Facebook"); // Old Name

        when(companyRepository.findById(id)).thenReturn(Optional.of(existingCompany));

        // 2. Act
        boolean updated = companyService.updateCompany(request, id);

        // 3. Assert
        assertTrue(updated);
        assertEquals("Meta", existingCompany.getName()); // Check if entity was updated
        verify(companyRepository, times(1)).save(existingCompany);
    }

    // --- TEST 6: Delete Company (Success) ---
    @Test
    void testDeleteCompany_Success() {
        // 1. Arrange
        Long id = 1L;
        when(companyRepository.existsById(id)).thenReturn(true);

        // 2. Act
        boolean deleted = companyService.deleteCompanyById(id);

        // 3. Assert
        assertTrue(deleted);
        verify(companyRepository, times(1)).deleteById(id);
    }

    // --- TEST 7: Delete Company (Not Found) ---
    @Test
    void testDeleteCompany_NotFound() {
        // 1. Arrange
        Long id = 99L;
        when(companyRepository.existsById(id)).thenReturn(false);

        // 2. Act
        boolean deleted = companyService.deleteCompanyById(id);

        // 3. Assert
        assertFalse(deleted);
        verify(companyRepository, never()).deleteById(anyLong());
    }
}