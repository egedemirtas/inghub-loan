package com.inghub.loan.controller;

import com.inghub.loan.dto.ErrorResponseDto;
import com.inghub.loan.dto.InstallmentInfoDto;
import com.inghub.loan.dto.LoanInfoDto;
import com.inghub.loan.dto.LoanPaymentInfoDto;
import com.inghub.loan.mapper.LoanMapper;
import com.inghub.loan.request.CreateLoanRequest;
import com.inghub.loan.request.PayLoanRequest;
import com.inghub.loan.response.CreateLoanResponse;
import com.inghub.loan.service.ILoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Loan API", description = "Create & pay a loan; view loans & installments")
@RestController
@RequestMapping(path = "/loan", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class LoanController {

    @Autowired
    private ILoanService loanService;
    @Autowired
    private LoanMapper loanMapper;

    @Operation(summary = "Create loan", description = "Creates loan for a customer")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "HTTP status CREATED", content = @Content(
            schema = @Schema(implementation = CreateLoanResponse.class))),
            @ApiResponse(responseCode = "400", description = "HTTP bad request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "HTTP internal server error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "HTTP not authorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),})
    @PostMapping
    public ResponseEntity<CreateLoanResponse> createLoan(
            @Valid @RequestBody CreateLoanRequest createLoanRequest) {
        Long loanId = loanService.createLoan(loanMapper.request2Dto(createLoanRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateLoanResponse(loanId));
    }

    @Operation(summary = "Pay installments", description = "Pays for installments for given loan")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "HTTP status OK", content = @Content(
            schema = @Schema(implementation = LoanPaymentInfoDto.class))),
            @ApiResponse(responseCode = "400", description = "HTTP bad request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "HTTP internal server error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "HTTP not authorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),})
    @PostMapping("/installment")
    public ResponseEntity<LoanPaymentInfoDto> payInstallment(
            @Valid @RequestBody PayLoanRequest payLoanRequest) {
        LoanPaymentInfoDto loanInfo = loanService.payLoan(loanMapper.request2Dto(payLoanRequest));
        return ResponseEntity.status(HttpStatus.OK).body(loanInfo);
    }

    @Operation(summary = "Get loans", description = "Get loans of a given customer with pagination")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "HTTP status OK",
                                content = @Content(schema = @Schema(implementation = LoanInfoDto.class))),
            @ApiResponse(responseCode = "400", description = "HTTP bad request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "HTTP internal server error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "HTTP not authorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),})
    @GetMapping
    public ResponseEntity<List<LoanInfoDto>> getLoans(
            @RequestParam @NotNull(message = "{loan.request.field.error.empty}") Long customerId,
            @RequestParam @NotNull(message = "{loan.request.field.error.empty}") Integer pageIndex,
            @RequestParam @NotNull(message = "{loan.request.field.error.empty}") Integer size) {
        List<LoanInfoDto> response = loanService.getLoanList(customerId, pageIndex, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Get installment", description = "Get installments of a loan with pagination")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "HTTP status OK", content = @Content(
            schema = @Schema(implementation = InstallmentInfoDto.class))),
            @ApiResponse(responseCode = "400", description = "HTTP bad request",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "HTTP internal server error",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "HTTP not authorized",
                         content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),})
    @GetMapping("/installment")
    public ResponseEntity<List<InstallmentInfoDto>> getInstallmentList(
            @RequestParam @NotNull(message = "{loan.request.field.error.empty}") Long loanId,
            @RequestParam @NotNull(message = "{loan.request.field.error.empty}") Integer pageIndex,
            @RequestParam @NotNull(message = "{loan.request.field.error.empty}") Integer size) {
        List<InstallmentInfoDto> response = loanService.getInstallmentList(loanId, pageIndex, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
