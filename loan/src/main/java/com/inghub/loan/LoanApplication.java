package com.inghub.loan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(info = @Info(title = "Bank Loan Application",
                                description = "Made for ING HUB challenge\n. " +
                                        "Provides creation of loan, payment of loan and also view of " +
                                        "installments/loans", version = "v1",
                                contact = @Contact(name = "Ege D", email = "egedemirtas@outlook.com",
                                                   url = "https://github.com/egedemirtas"),
                                license = @License(name = "Apache 2.0", url = "www.mylicenseinfo.com")))
@EnableJpaAuditing(auditorAwareRef = "AuditorAwareImpl")
@SpringBootApplication
public class LoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanApplication.class, args);
    }

}
