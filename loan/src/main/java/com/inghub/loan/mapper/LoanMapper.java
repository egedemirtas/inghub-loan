package com.inghub.loan.mapper;

import com.inghub.loan.dto.InstallmentInfoDto;
import com.inghub.loan.dto.LoanDto;
import com.inghub.loan.dto.LoanInfoDto;
import com.inghub.loan.dto.LoanPaymentDto;
import com.inghub.loan.entity.Loan;
import com.inghub.loan.entity.LoanInstallment;
import com.inghub.loan.request.CreateLoanRequest;
import com.inghub.loan.request.PayLoanRequest;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface LoanMapper {

    LoanDto request2Dto(CreateLoanRequest createLoanRequest);

    LoanPaymentDto request2Dto(PayLoanRequest createLoanRequest);

    Loan dto2Model(LoanDto loanDto);

    List<LoanInfoDto> loanList2LoanInfoDtoList(List<Loan> loanList);

    List<InstallmentInfoDto> loanInstallmentList2InstallmentInfoDtoList(List<LoanInstallment> loanList);
}