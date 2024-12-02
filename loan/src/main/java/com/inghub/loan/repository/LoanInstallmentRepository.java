package com.inghub.loan.repository;

import com.inghub.loan.entity.Customer;
import com.inghub.loan.entity.Loan;
import com.inghub.loan.entity.LoanInstallment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    Optional<List<LoanInstallment>> findFirst3ByLoanAndIsPaidOrderByDueDate(Loan loan, Boolean isPaid);
    Optional<List<LoanInstallment>> findAllByLoanAndIsPaid(Loan loan, Boolean isPaid);
    Optional<List<LoanInstallment>> findAllByLoan(Loan loan, Pageable pageable);
}
