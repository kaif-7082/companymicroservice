package com.kaif.companyms.companies.messaging;

import com.kaif.companyms.companies.Company;
import com.kaif.companyms.companies.CompanyRepository;
import com.kaif.companyms.companies.clients.ReviewClient;
import com.kaif.companyms.companies.dto.ReviewMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReviewMessageConsumer {
    private final CompanyRepository companyRepository;
    private final ReviewClient reviewClient;

    public ReviewMessageConsumer(CompanyRepository companyRepository, ReviewClient reviewClient) {
        this.companyRepository = companyRepository;
        this.reviewClient = reviewClient;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "company-group")
    public void consumeMessage(ReviewMessage message) {
        System.out.println("Consumed Message: " + message);

        // 1. Fetch new average from ReviewMS
        Double newRating = reviewClient.getAverageRating(message.getCompanyId()).get("averageRating");

        // 2. Update Company DB
        Company company = companyRepository.findById(message.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setRating(newRating);
        companyRepository.save(company);

        System.out.println("Updated Company " + message.getCompanyId() + " with new rating: " + newRating);
    }
}