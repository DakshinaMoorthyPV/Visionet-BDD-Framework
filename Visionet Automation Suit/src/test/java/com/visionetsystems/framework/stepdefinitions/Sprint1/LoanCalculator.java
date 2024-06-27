package com.visionetsystems.framework.stepdefinitions.Sprint1;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoanCalculator {
    
     public static void main(String[] args) {
        BigDecimal desiredDisbursementAmount = new BigDecimal("1100000");
        BigDecimal processingFeePercentage = new BigDecimal("0.01");
        BigDecimal gstPercentage = new BigDecimal("0.18");
        int daysCounted = 30;
        BigDecimal annualInterestRate = new BigDecimal("0.1120");

        // Calculate loan amount needed for desired disbursement
        BigDecimal loanAmountNeeded = desiredDisbursementAmount.divide(
                BigDecimal.ONE.subtract(processingFeePercentage.multiply(BigDecimal.ONE.add(gstPercentage))),
                RoundingMode.HALF_UP
        );

        // Calculate total processing fee
        BigDecimal totalProcessingFee = loanAmountNeeded.multiply(processingFeePercentage.multiply(BigDecimal.ONE.add(gstPercentage)));

        // Calculate the disbursement amount to verify
        BigDecimal disbursementAmount = loanAmountNeeded.subtract(totalProcessingFee);

        // Calculate the monthly interest rate
        BigDecimal interestRateMonthly = annualInterestRate.divide(new BigDecimal("12"), RoundingMode.HALF_UP);

        // Calculate INBT amount
        BigDecimal inbtAmount = new BigDecimal(daysCounted)
                .multiply(loanAmountNeeded)
                .multiply(interestRateMonthly)
                .multiply(new BigDecimal("12"))
                .divide(new BigDecimal("365"), RoundingMode.HALF_UP);

        // Print the results
        System.out.println("Loan Amount Needed: " + loanAmountNeeded.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Processing Fee: " + totalProcessingFee.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Disbursement Amount: " + disbursementAmount.setScale(2, RoundingMode.HALF_UP));
        System.out.println("INBT Amount: " + inbtAmount.setScale(2, RoundingMode.HALF_UP));
    }
}
