package com.inghub.loan.service.impl;

import com.inghub.loan.dto.*;
import com.inghub.loan.entity.Customer;
import com.inghub.loan.entity.Loan;
import com.inghub.loan.entity.LoanInstallment;
import com.inghub.loan.exception.ResourceNotFoundException;
import com.inghub.loan.mapper.LoanMapper;
import com.inghub.loan.repository.CustomerRepository;
import com.inghub.loan.repository.LoanInstallmentRepository;
import com.inghub.loan.repository.LoanRepository;
import com.inghub.loan.service.ICustomerService;
import com.inghub.loan.service.ILoanService;
import com.inghub.loan.validator.LoanValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class LoanService implements ILoanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanService.class);

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LoanValidator loanValidator;
    @Autowired
    private LoanMapper loanMapper;
    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ICustomerService customerService;

    @Transactional
    @Override
    public Long createLoan(LoanDto loanDto) {
        loanValidator.validateCustomerId(loanDto.getCustomerId());
        loanValidator.validateCustomerLimit(loanDto.getCustomerId(), loanDto.getLoanAmount());

        BigDecimal totalLoanAmount = loanDto.getLoanAmount().multiply(BigDecimal.ONE.add(loanDto.getRate()));

        BigDecimal monthlyInstallment =
                totalLoanAmount.divide(BigDecimal.valueOf(loanDto.getNumberOfInstallment()), 2,
                        RoundingMode.DOWN);

        BigDecimal totalPayBack =
                monthlyInstallment.multiply(BigDecimal.valueOf(loanDto.getNumberOfInstallment()));
        // total loan may not be divisible without decimals,
        // in that case last installment must cover lost decimals
        // otherwise lastInstallment = monthlyInstallment
        BigDecimal lastInstallment = totalLoanAmount.subtract(totalPayBack).add(monthlyInstallment);

        Loan loan = loanMapper.dto2Model(loanDto);
        loan.setIsPaid(false);
        Customer customer = customerService.getCustomer(loanDto.getCustomerId());
        loan.setCustomer(customer);
        Loan createdLoan = loanRepository.save(loan);

        createInstallments(monthlyInstallment, lastInstallment, createdLoan);
        updateCustomerLimit(customer, loanDto.getLoanAmount());

        return createdLoan.getId();
    }

    @Transactional
    @Override
    public LoanPaymentInfoDto payLoan(LoanPaymentDto loanPaymentDto) {
        loanValidator.validateCustomerId(loanPaymentDto.getCustomerId());
        Loan loan = getLoanById(loanPaymentDto.getLoanId());

        // return if loan paid already
        if (loan.getIsPaid()) {
            return new LoanPaymentInfoDto(0, 0, BigDecimal.ZERO, true);
        }

        BigDecimal paidAmount = payInstallments(loan, loanPaymentDto.getAmount());
        List<LoanInstallment> unpaidInstallmentList = getLoanInstallmentsByIsPaid(loan, false);
        List<LoanInstallment> paidInstallmentList = getLoanInstallmentsByIsPaid(loan, true);

        // if all installments paid update loan and customer
        if (unpaidInstallmentList.isEmpty()) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
            Customer customer = customerService.getCustomer(loanPaymentDto.getLoanId());
            customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(loan.getLoanAmount()));
        }

        return new LoanPaymentInfoDto(paidInstallmentList.size(), unpaidInstallmentList.size(), paidAmount,
                unpaidInstallmentList.isEmpty());
    }

    @Override
    public List<LoanInfoDto> getLoanList(Long customerId, Integer pageIndex, Integer size) {
        loanValidator.validateCustomerId(customerId);
        Customer customer = customerService.getCustomer(customerId);
        Pageable page = PageRequest.of(pageIndex, size);
        List<Loan> loanList =
                loanRepository.findAllByCustomer(customer, page).orElse(Collections.emptyList());
        return loanMapper.loanList2LoanInfoDtoList(loanList);
    }

    @Override
    public List<InstallmentInfoDto> getInstallmentList(Long loanId, Integer pageIndex, Integer size) {
        Loan loan = getLoanById(loanId);
        loanValidator.validateCustomerId(loan.getCustomer().getId());
        Pageable page = PageRequest.of(pageIndex, size);
        List<LoanInstallment> loanList =
                loanInstallmentRepository.findAllByLoan(loan, page).orElse(Collections.emptyList());
        return loanMapper.loanInstallmentList2InstallmentInfoDtoList(loanList);
    }

    private Loan getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("loan", loanId.toString(), messageSource));
    }

    private List<LoanInstallment> getLoanInstallmentsByIsPaid(Loan loan, Boolean isPaid) {
        return loanInstallmentRepository.findAllByLoanAndIsPaid(loan, isPaid).orElseThrow(
                () -> new ResourceNotFoundException("Loan installments", loan.getId().toString(),
                        messageSource));
    }

    private BigDecimal payInstallments(Loan loan, BigDecimal paymentAmount) {
        List<LoanInstallment> loanInstallmentList =
                loanInstallmentRepository.findFirst3ByLoanAndIsPaidOrderByDueDate(loan, false).orElseThrow(
                        () -> new ResourceNotFoundException("Loan installments", loan.getId().toString(),
                                messageSource));

        BigDecimal paidAmount = BigDecimal.ZERO;
        for (LoanInstallment loanInstallment : loanInstallmentList) {
            paymentAmount = paymentAmount.subtract(loanInstallment.getAmount());
            if (BigDecimal.ZERO.compareTo(paymentAmount) <= 0) {
                loanInstallment.setIsPaid(true);
                loanInstallment.setPaymentDate(LocalDateTime.now());
                paidAmount = paidAmount.add(loanInstallment.getAmount());
            } else {
                break;
            }
        }
        loanInstallmentRepository.saveAll(loanInstallmentList);
        return paidAmount;
    }

    private void updateCustomerLimit(Customer customer, BigDecimal loanAmount) {
        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(loanAmount));
        customerRepository.save(customer);
    }

    private void createInstallments(BigDecimal monthlyInstallment, BigDecimal lastInstallment, Loan loan) {
        for (int installmentCount = 1;
             installmentCount <= loan.getNumberOfInstallment(); installmentCount++) {
            LoanInstallment loanInstallment = new LoanInstallment();
            loanInstallment.setAmount(
                    installmentCount != loan.getNumberOfInstallment() ? monthlyInstallment : lastInstallment);
            loanInstallment.setLoan(loan);
            LocalDateTime dueDate = calculateDueDayByInstallmentCount(installmentCount);
            loanInstallment.setDueDate(dueDate);
            loanInstallment.setIsPaid(false);
            loanInstallmentRepository.save(loanInstallment);
        }
    }

    private LocalDateTime calculateDueDayByInstallmentCount(Integer installmentCount) {
        LocalDateTime nextMonthDate = LocalDateTime.now().plusMonths(installmentCount);
        return nextMonthDate.withDayOfMonth(1);
    }


}
