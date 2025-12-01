package com.kaif.companyms.companies.implementation;

import com.kaif.companyms.companies.Company;
import com.kaif.companyms.companies.CompanyRepository;
import com.kaif.companyms.companies.CompanyService;
import com.kaif.companyms.companies.dto.companyRequestDto;
import com.kaif.companyms.companies.dto.companyResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile; // --- ADD THIS ---
import java.io.IOException; // --- ADD THIS ---


@Slf4j
@Service
public class CompanyServiceImplementation implements CompanyService {

    @Autowired
    RestTemplate restTemplate;

    private CompanyRepository companyRepository;

    public CompanyServiceImplementation(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<companyResponseDto> getAllCompanies() {
        log.info("Executing getAllCompanies");
        List<Company> companies = companyRepository.findAll();
        List<companyResponseDto> responseDtos = companies.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        log.info("Found {} companies", responseDtos.size());
        return responseDtos;
    }

    @Override
    public boolean updateCompany(companyRequestDto companyDto, Long id) {
        log.info("Attempting to update company with id: {}", id);
        Optional<Company> companyOptional = companyRepository.findById(id);

        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();


            // Check if the name is being changed AND if the new name already exists
            if (!company.getName().equals(companyDto.getName()) &&
                    companyRepository.existsByName(companyDto.getName())) {

                throw new IllegalArgumentException("Company with name '" + companyDto.getName() + "' already exists!");
            }


            mapToEntity(companyDto, company);
            companyRepository.save(company);
            log.info("Successfully updated company with id: {}", id);
            return true;
        }

        log.warn("Failed to update. Company not found with id: {}", id);
        return false;
    }

    @Override
    public void createCompany(companyRequestDto companyDto) {

        if (companyRepository.existsByName(companyDto.getName())) {
            throw new IllegalArgumentException("Company with name '" + companyDto.getName() + "' already exists!");
        }
        log.info("Creating new company: {}", companyDto.getName());
        Company company = new Company();
        mapToEntity(companyDto, company); // Use helper
        companyRepository.save(company);
        log.info("Successfully created company with id: {}", company.getId());
    }

    @Override
    public boolean deleteCompanyById(Long id) {
        log.info("Attempting to delete company with id: {}", id);
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            log.info("Successfully deleted company with id: {}", id);
            return true;
        }
        log.warn("Failed to delete. Company not found with id: {}", id);
        return false;
    }

    @Override
    public Company getCompanyById(Long id) {
        log.info("Finding company entity by id: {}", id);
        // This method is used by jobms, so it returns the raw entity
        return companyRepository.findById(id).orElse(null);
    }

    @Override
    public companyResponseDto getCompanyDtoById(Long id) {
        log.info("Finding company dto by id: {}", id);
        Company company = companyRepository.findById(id).orElse(null);
        if (company == null) {
            log.warn("Company not found with id: {}", id);
            return null;
        }
        return mapToDto(company);
    }

    @Override
    public List<Company> findCompaniesWithSorting(String field) {
        log.info("Finding companies with sorting on field: {}", field);
        return companyRepository.findAll(Sort.by(Sort.Direction.DESC, field));
    }

    @Override
    public companyResponseDto findCompanyByName(String name) {
        log.info("Finding company by name: {}", name);
        Company company = companyRepository.findByName(name);
        return (company != null) ? mapToDto(company) : null;
    }

    @Override
    public List<companyResponseDto> searchCompanies(String query) {
        log.info("Searching for companies with query: {}", query);
        List<Company> companies = companyRepository.searchCompanies(query);
        return companies.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<companyResponseDto> findCompaniesByFoundedYear(Integer year) {
        log.info("Finding companies by founded year: {}", year);
        List<Company> companies = companyRepository.findByFoundedYear(year);
        return companies.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Page<Company> findCompanyWithPagination(int page, int pageSize) {
        log.info("Finding companies with pagination - page: {}, pageSize: {}", page, pageSize);
        return companyRepository.findAll(PageRequest.of(page, pageSize));
    }

    @Override
    public void storeLogo(Long id, MultipartFile file) throws IOException {
        log.info("Attempting to store logo for company id: {}", id);
        Optional<Company> companyOptional = companyRepository.findById(id);
        if (companyOptional.isPresent() && !file.isEmpty()) {
            Company company = companyOptional.get();
            company.setLogoData(file.getBytes());
            company.setLogoType(file.getContentType());
            companyRepository.save(company);
            log.info("Logo stored successfully for company id: {}", id);
        } else {
            log.warn("Failed to store logo. Company not found or file is empty.");
            if (companyOptional.isEmpty()) {
                throw new RuntimeException("Company not found with id: " + id);
            }
        }
    }


    private companyResponseDto mapToDto(Company company) {
        companyResponseDto dto = new companyResponseDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setCeo(company.getCeo());
        dto.setFoundedYear(company.getFoundedYear());
        return dto;
    }

    private void mapToEntity(companyRequestDto dto, Company company) {
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setCeo(dto.getCeo());
        company.setFoundedYear(dto.getFoundedYear());
    }
}