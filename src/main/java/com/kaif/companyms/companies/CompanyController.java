package com.kaif.companyms.companies;

import com.kaif.companyms.companies.dto.companyRequestDto;
import com.kaif.companyms.companies.dto.companyResponseDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

// --- ADD THIS IMPORT ---
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/companies")
public class CompanyController {
    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<companyResponseDto>> getAllCompanies() {
        log.info("GET /companies - Request to get all companies");
        return new ResponseEntity<>(companyService.getAllCompanies(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCompany(@PathVariable Long id,
                                                @Valid @RequestBody companyRequestDto companyDto) {
        log.info("PUT /companies/{} - Request to update company", id);
        boolean updated = companyService.updateCompany(companyDto, id);
        if (updated) {
            return new ResponseEntity<>("Company updated", HttpStatus.OK);
        }
        log.warn("PUT /companies/{} - Company not found", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/logo")
    public ResponseEntity<String> uploadLogo(@PathVariable Long id,
                                             @RequestParam("file") MultipartFile file) {
        try {
            companyService.storeLogo(id, file);
            return ResponseEntity.ok("Logo uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> downloadLogo(@PathVariable Long id) {
        log.info("GET /companies/{}/logo - Request to download logo", id);
        Company company = companyService.getCompanyById(id); // We just re-use the existing method

        if (company != null && company.getLogoData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(company.getLogoType()))
                    .body(company.getLogoData());
        } else {
            log.warn("GET /companies/{}/logo - Logo or company not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> createCompany(@Valid @RequestBody companyRequestDto companyDto) {
        log.info("POST /companies - Request to create new company: {}", companyDto.getName());
        companyService.createCompany(companyDto);
        log.info("POST /companies - Company created successfully: {}", companyDto.getName());
        return new ResponseEntity<>("Company created", HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompanyById(@PathVariable Long id) {
        log.info("DELETE /companies/{} - Request to delete company", id);

        // We should also delete associated Jobs and Reviews.
        // This would require an event/message to jobms and reviewms.
        // For now, we just delete the company as per monolith logic.

        boolean deleted = companyService.deleteCompanyById(id);
        if (deleted) {
            return new ResponseEntity<>("Company deleted", HttpStatus.OK);
        }
        log.warn("DELETE /companies/{} - Company not found", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // This endpoint is crucial for jobms to validate/fetch company data
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        log.info("GET /companies/{} - (Internal) Request to get company entity", id);
        Company company = companyService.getCompanyById(id);
        if (company != null) {
            return new ResponseEntity<>(company, HttpStatus.OK);
        }
        log.warn("GET /companies/{} - Company entity not found", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // This endpoint is for external clients wanting the DTO
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/dto/{id}")
    public ResponseEntity<companyResponseDto> getCompanyDtoById(@PathVariable Long id) {
        log.info("GET /companies/dto/{} - Request to get company dto", id);
        companyResponseDto companyDto = companyService.getCompanyDtoById(id);
        if (companyDto != null) {
            return new ResponseEntity<>(companyDto, HttpStatus.OK);
        }
        log.warn("GET /companies/dto/{} - Company not found", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // --- Endpoints from Monolith ---

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/sorted/{field}")
    public ResponseEntity<List<Company>> findSortedCompanies(@PathVariable String field) {
        log.info("GET /companies/sorted/{} - Request to get sorted companies", field);
        List<Company> sortedCompanies = companyService.findCompaniesWithSorting(field);
        return ResponseEntity.ok(sortedCompanies);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/name/{name}")
    public ResponseEntity<companyResponseDto> findCompanyByName(@PathVariable String name) {
        log.info("GET /companies/name/{} - Request to get company by name", name);
        companyResponseDto companyDto = companyService.findCompanyByName(name);
        if (companyDto != null) {
            return new ResponseEntity<>(companyDto, HttpStatus.OK);
        }
        log.warn("GET /companies/name/{} - Company not found", name);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<companyResponseDto>> searchCompanies(@RequestParam String query) {
        log.info("GET /companies/search - Request to search companies with query: {}", query);
        List<companyResponseDto> companies = companyService.searchCompanies(query);
        return ResponseEntity.ok(companies);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/filterByYear/{year}")
    public ResponseEntity<List<companyResponseDto>> getCompaniesByYear(@PathVariable Integer year) {
        log.info("GET /companies/filterByYear/{} - Request to filter companies by year", year);
        List<companyResponseDto> companies = companyService.findCompaniesByFoundedYear(year);
        return ResponseEntity.ok(companies);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/pagination/{page}/{pageSize}")
    public ResponseEntity<Page<Company>> getJobsWithPagination(@PathVariable int page, @PathVariable int pageSize) {
        log.info("GET /companies/pagination/{}/{} - Request to get paginated companies", page, pageSize);
        Page<Company> companies = companyService.findCompanyWithPagination(page, pageSize);
        return ResponseEntity.ok(companies);
    }
}