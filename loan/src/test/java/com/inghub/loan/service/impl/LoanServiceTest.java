package com.inghub.loan.service.impl;

import com.inghub.loan.dto.LoanDto;
import com.inghub.loan.dto.LoanInfoDto;
import com.inghub.loan.dto.LoanPaymentDto;
import com.inghub.loan.dto.LoanPaymentInfoDto;
import com.inghub.loan.entity.Customer;
import com.inghub.loan.entity.Loan;
import com.inghub.loan.entity.LoanInstallment;
import com.inghub.loan.exception.ResourceNotFoundException;
import com.inghub.loan.mapper.LoanMapper;
import com.inghub.loan.repository.CustomerRepository;
import com.inghub.loan.repository.LoanInstallmentRepository;
import com.inghub.loan.repository.LoanRepository;
import com.inghub.loan.service.ICustomerService;
import com.inghub.loan.validator.LoanValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Captor
    ArgumentCaptor<Loan> loanCaptor;
    @Captor
    ArgumentCaptor<LoanInstallment> loanInstallmentCaptor;
    @Captor
    ArgumentCaptor<List<LoanInstallment>> loanInstallmentListCaptor;
    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor;
    @Captor
    ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private LoanValidator loanValidator;
    @Mock
    private LoanMapper loanMapper;
    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ICustomerService customerService;

    @InjectMocks
    private LoanService loanService;

    private static LoanDto getLoanDto() {
        LoanDto loanDtoInput = new LoanDto();
        loanDtoInput.setCustomerId(1L);
        loanDtoInput.setLoanAmount(new BigDecimal(100));
        loanDtoInput.setRate(new BigDecimal("0.2"));
        loanDtoInput.setNumberOfInstallment(10);
        return loanDtoInput;
    }

    private static Loan getLoan(LoanDto loanDtoInput) {
        Loan loan = new Loan();
        loan.setLoanAmount(loanDtoInput.getLoanAmount());
        loan.setNumberOfInstallment(loanDtoInput.getNumberOfInstallment());
        return loan;
    }

    private static Loan getCreatedLoan(Customer customer, LoanDto loanDtoInput) {
        Loan createdLoan = new Loan();
        createdLoan.setId(1L);
        createdLoan.setCustomer(customer);
        createdLoan.setNumberOfInstallment(loanDtoInput.getNumberOfInstallment());
        createdLoan.setLoanAmount(loanDtoInput.getLoanAmount());
        createdLoan.setIsPaid(false);
        return createdLoan;
    }

    private static List<LoanInstallment> getLoanInstallmentList() {
        List<LoanInstallment> loanInstallmentList = new ArrayList<>();
        LoanInstallment loanInstallment = new LoanInstallment();
        loanInstallment.setAmount(BigDecimal.TEN);
        loanInstallmentList.add(loanInstallment);
        loanInstallmentList.add(loanInstallment);
        return loanInstallmentList;
    }

    private static List<Loan> getLoanList() {
        List<Loan> loanList = new ArrayList<>();
        Loan loan = new Loan();
        loanList.add(loan);
        loanList.add(loan);
        return loanList;
    }

    private static List<LoanInfoDto> getLoanInfoDtoList() {
        List<LoanInfoDto> loanInfoDtoList = new ArrayList<>();
        LoanInfoDto loanInfoDto = new LoanInfoDto();
        loanInfoDtoList.add(loanInfoDto);
        loanInfoDtoList.add(loanInfoDto);
        return loanInfoDtoList;
    }

    @Test
    void when_createLoanWithNonNExistingCustomerId_then_throwException() {
        LoanDto loanDtoInput = getLoanDto();
        Loan loan = getLoan(loanDtoInput);

        when(loanMapper.dto2Model(loanDtoInput)).thenReturn(loan);
        when(customerService.getCustomer(1L)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> loanService.createLoan(loanDtoInput));
    }

    @Test
    void when_createLoan_then_saveLoanAndSaveInstallmentsAndUpdateCustomer() {
        LoanDto loanDtoInput = getLoanDto();
        Loan loan = getLoan(loanDtoInput);

        Customer customer = new Customer();
        customer.setId(loanDtoInput.getCustomerId());
        customer.setUsedCreditLimit(BigDecimal.ZERO);

        Loan createdLoan = getCreatedLoan(customer, loanDtoInput);

        when(loanMapper.dto2Model(loanDtoInput)).thenReturn(loan);
        when(customerService.getCustomer(1L)).thenReturn(customer);
        when(loanRepository.save(loan)).thenReturn(createdLoan);

        Long savedId = loanService.createLoan(loanDtoInput);

        Mockito.verify(loanRepository, times(1)).save(loanCaptor.capture());
        Mockito.verify(loanInstallmentRepository, times(loanDtoInput.getNumberOfInstallment()))
                .save(loanInstallmentCaptor.capture());
        Mockito.verify(customerRepository, times(1)).save(customerArgumentCaptor.capture());

        // verify saved loan
        assertEquals(loanDtoInput.getLoanAmount(), loanCaptor.getValue().getLoanAmount());
        assertEquals(loanDtoInput.getNumberOfInstallment(), loanCaptor.getValue().getNumberOfInstallment());
        assertEquals(customer, loanCaptor.getValue().getCustomer());
        assertEquals(savedId, createdLoan.getId());

        // verify customer limit update
        assertEquals(loanDtoInput.getLoanAmount(), customerArgumentCaptor.getValue().getUsedCreditLimit());

        // verify created installments
        loanInstallmentCaptor.getAllValues().forEach(loanInstallment -> {
            assertEquals(loanInstallment.getAmount(),
                    loanDtoInput.getLoanAmount().multiply(BigDecimal.ONE.add(loanDtoInput.getRate()))
                            .divide(BigDecimal.valueOf(loanDtoInput.getNumberOfInstallment()), 2,
                                    RoundingMode.DOWN));
            assertEquals(loanInstallment.getLoan(), createdLoan);
            assertNull(loanInstallment.getPaidAmount());
            assertEquals(loanInstallment.getIsPaid(), false);
        });
    }

    @Test
    void when_payLoanWithInvalidLoanId_then_throwException() {
        LoanPaymentDto loanPaymentDto = new LoanPaymentDto();
        loanPaymentDto.setLoanId(1L);

        when(loanRepository.findById(loanPaymentDto.getLoanId())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> loanService.payLoan(loanPaymentDto));
    }

    @Test
    void when_payLoanThatWasAlreadyPaid_then_return() {
        LoanPaymentDto loanPaymentDto = new LoanPaymentDto();
        loanPaymentDto.setLoanId(1L);
        Loan loan = new Loan();
        loan.setIsPaid(true);

        when(loanRepository.findById(loanPaymentDto.getLoanId())).thenReturn(Optional.of(loan));

        LoanPaymentInfoDto loanPaymentInfoDto = loanService.payLoan(loanPaymentDto);

        assertTrue(loanPaymentInfoDto.getIsLoanPaid());
        assertEquals(BigDecimal.ZERO, loanPaymentInfoDto.getPaidAmount());
        assertEquals(0, loanPaymentInfoDto.getNumberOfInstallmentsRemaining());
        assertEquals(0, loanPaymentInfoDto.getNumberOfInstallmentsPaid());
    }

    @Test
    void when_payLoan_then_return() {
        LoanPaymentDto loanPaymentDto = new LoanPaymentDto();
        loanPaymentDto.setLoanId(1L);
        loanPaymentDto.setAmount(new BigDecimal(20));
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setIsPaid(false);
        loan.setLoanAmount(new BigDecimal(20));
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUsedCreditLimit(BigDecimal.ZERO);

        List<LoanInstallment> loanInstallmentList = getLoanInstallmentList();

        when(loanRepository.findById(loanPaymentDto.getLoanId())).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findFirst3ByLoanAndIsPaidOrderByDueDate(loan, false)).thenReturn(
                Optional.of(loanInstallmentList));
        when(loanInstallmentRepository.findAllByLoanAndIsPaid(loan, false)).thenReturn(
                Optional.of(Collections.emptyList()));
        when(loanInstallmentRepository.findAllByLoanAndIsPaid(loan, true)).thenReturn(
                Optional.of(loanInstallmentList));
        when(customerService.getCustomer(1L)).thenReturn(customer);

        LoanPaymentInfoDto loanPaymentInfoDto = loanService.payLoan(loanPaymentDto);

        Mockito.verify(loanInstallmentRepository, times(1)).saveAll(loanInstallmentListCaptor.capture());

        assertEquals(2, loanInstallmentListCaptor.getValue().size());

        loanInstallmentListCaptor.getValue().forEach(loanInstallment -> {
            assertTrue(loanInstallment.getIsPaid());
            assertEquals(loanInstallmentList.get(0).getAmount(), loanInstallment.getAmount());
        });
    }

    @Test
    void when_getLoanList_then_returnLoanList() {
        int pageIndex = 10;
        int size = 5;

        Long customerId = 1L;
        Customer customer = new Customer();
        List<Loan> loanList = getLoanList();
        List<LoanInfoDto> loanInfoDtoList = getLoanInfoDtoList();

        when(customerService.getCustomer(1L)).thenReturn(customer);
        when(loanRepository.findAllByCustomer(customer, PageRequest.of(pageIndex, size))).thenReturn(
                Optional.of(loanList));
        when(loanMapper.loanList2LoanInfoDtoList(loanList)).thenReturn(loanInfoDtoList);

        loanService.getLoanList(customerId, pageIndex, size);

        Mockito.verify(loanRepository, times(1))
                .findAllByCustomer(customerArgumentCaptor.capture(), pageableArgumentCaptor.capture());

        assertEquals(size, pageableArgumentCaptor.getValue().getPageSize());
        assertEquals(pageIndex, pageableArgumentCaptor.getValue().getPageNumber());
        assertEquals(customer, customerArgumentCaptor.getValue());
    }

    @Test
    void when_getInstallmentList_then_returnInstallmentList() {
        int pageIndex = 10;
        int size = 5;
        Long loanId = 1L;
        Loan loan = new Loan();
        Customer customer = new Customer();
        customer.setId(2L);
        loan.setCustomer(customer);
        List<LoanInstallment> loanInstallmentList = getLoanInstallmentList();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findAllByLoan(loan, PageRequest.of(pageIndex, size))).thenReturn(
                Optional.of(loanInstallmentList));

        loanService.getInstallmentList(loanId, pageIndex, size);

        Mockito.verify(loanInstallmentRepository, times(1))
                .findAllByLoan(loanCaptor.capture(), pageableArgumentCaptor.capture());

        assertEquals(size, pageableArgumentCaptor.getValue().getPageSize());
        assertEquals(pageIndex, pageableArgumentCaptor.getValue().getPageNumber());
        assertEquals(loan, loanCaptor.getValue());
    }
}