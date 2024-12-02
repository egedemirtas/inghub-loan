package com.inghub.loan.repository;

import com.inghub.loan.entity.Customer;
import com.inghub.loan.entity.Loan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<List<Loan>> findAllByCustomer(Customer customer, Pageable pageable);
}
