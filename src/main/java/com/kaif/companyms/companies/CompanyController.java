package com.kaif.companyms.companies;

import com.kaif.companyms.companies.dto.companyRequestDto;
import com.kaif.companyms.companies.dto.companyResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.kaif.companyms.companies.CompanyConstants.*;

@Slf4j
@RestController
@RequestMapping("/companies")
@Tag(name = "Company Controller", description = "Endpoints for managing companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(summary = "Get All Companies", description = "Fetches a list of all registered companies.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<companyResponseDto>> getAllCompanies() {
        return new ResponseEntity<>(companyService.getAllCompanies(), HttpStatus.OK);
    }

    @Operation(summary = "Update Company", description = "Updates details of an existing company. Name must be unique.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(ID_PATH)
    public ResponseEntity<String> updateCompany(@PathVariable Long id, @Valid @RequestBody companyRequestDto companyDto) {
        boolean updated = companyService.updateCompany(companyDto, id);
        if (updated) return new ResponseEntity<>("Company updated", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Upload Logo", description = "Uploads a logo image file for a company.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(LOGO_PATH)
    public ResponseEntity<String> uploadLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            companyService.storeLogo(id, file);
            return ResponseEntity.ok("Logo uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Download Logo", description = "Downloads the logo image of a company.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(LOGO_PATH)
    public ResponseEntity<byte[]> downloadLogo(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        if (company != null && company.getLogoData() != null) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(company.getLogoType())).body(company.getLogoData());
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Create Company", description = "Registers a new company. Name must be unique.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> createCompany(@Valid @RequestBody companyRequestDto companyDto) {
        companyService.createCompany(companyDto);
        return new ResponseEntity<>("Company created", HttpStatus.CREATED);
    }

    @Operation(summary = "Delete Company", description = "Deletes a company by its ID.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(ID_PATH)
    public ResponseEntity<String> deleteCompanyById(@PathVariable Long id) {
        boolean deleted = companyService.deleteCompanyById(id);
        if (deleted) return new ResponseEntity<>("Company deleted", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get Company (Entity)", description = "Fetches raw Company entity. Used internally.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(ID_PATH)
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        if (company != null) return new ResponseEntity<>(company, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get Company (DTO)", description = "Fetches Company details as a DTO. Recommended for clients.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(DTO_ID_PATH)
    public ResponseEntity<companyResponseDto> getCompanyDtoById(@PathVariable Long id) {
        companyResponseDto companyDto = companyService.getCompanyDtoById(id);
        if (companyDto != null) return new ResponseEntity<>(companyDto, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get Sorted Companies", description = "Fetches companies sorted by a specific field.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(SORTED_PATH)
    public ResponseEntity<List<Company>> findSortedCompanies(@PathVariable String field) {
        return ResponseEntity.ok(companyService.findCompaniesWithSorting(field));
    }

    @Operation(summary = "Find Company by Name", description = "Fetches a company using its exact name.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(NAME_PATH)
    public ResponseEntity<companyResponseDto> findCompanyByName(@PathVariable String name) {
        companyResponseDto companyDto = companyService.findCompanyByName(name);
        if (companyDto != null) return new ResponseEntity<>(companyDto, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Search Companies", description = "Searches companies by name, description, or CEO.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(SEARCH_PATH)
    public ResponseEntity<List<companyResponseDto>> searchCompanies(@RequestParam String query) {
        return ResponseEntity.ok(companyService.searchCompanies(query));
    }

    @Operation(summary = "Filter by Year", description = "Fetches companies founded in a specific year.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(FILTER_YEAR_PATH)
    public ResponseEntity<List<companyResponseDto>> getCompaniesByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(companyService.findCompaniesByFoundedYear(year));
    }

    @Operation(summary = "Get Paginated Companies", description = "Fetches companies in pages.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(PAGINATION_PATH)
    public ResponseEntity<Page<Company>> getJobsWithPagination(@PathVariable int page, @PathVariable int pageSize) {
        return ResponseEntity.ok(companyService.findCompanyWithPagination(page, pageSize));
    }
}