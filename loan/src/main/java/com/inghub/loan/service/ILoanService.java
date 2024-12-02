package com.inghub.loan.service;

import com.inghub.loan.dto.*;

import java.util.List;

public interface ILoanService {

    Long createLoan(LoanDto loanDto);

    LoanPaymentInfoDto payLoan(LoanPaymentDto loanPaymentDto);

    List<LoanInfoDto> getLoanList(Long customerId, Integer pageIndex, Integer size);

    List<InstallmentInfoDto> getInstallmentList(Long loanId, Integer pageIndex, Integer size);
}
