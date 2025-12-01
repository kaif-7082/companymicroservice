package com.kaif.companyms.companies;

import com.kaif.companyms.companies.dto.companyRequestDto;
import com.kaif.companyms.companies.dto.companyResponseDto;
import org.springframework.data.domain.Page;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

// Updated to match monolith features and use DTOs
public interface CompanyService {

    List<companyResponseDto> getAllCompanies();

    boolean updateCompany(companyRequestDto company, Long id);

    void createCompany(companyRequestDto company);

    boolean deleteCompanyById(Long id);

    Company getCompanyById(Long id); // Keep returning entity for internal use (e.g., jobms call)

    companyResponseDto getCompanyDtoById(Long id); // New: Return DTO for controller


    List<Company> findCompaniesWithSorting(String field);

    companyResponseDto findCompanyByName(String name);

    List<companyResponseDto> searchCompanies(String query);

    List<companyResponseDto> findCompaniesByFoundedYear(Integer year);

    Page<Company> findCompanyWithPagination(int page, int pageSize);

    void storeLogo(Long id, MultipartFile file) throws IOException;
}