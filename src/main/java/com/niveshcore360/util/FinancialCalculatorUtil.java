package com.niveshcore360.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility helper class for financial calculators (SIP, EMI, Compound Interest, Retirement).
 */
public class FinancialCalculatorUtil {

    /**
     * Calculates the future value of a Systematic Investment Plan (SIP).
     * Formula: M = P * [((1 + i)^n - 1) / i] * (1 + i)
     */
    public static BigDecimal calculateSIP(BigDecimal monthlyInvestment, BigDecimal annualRate, int years) {
        if (monthlyInvestment.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }
        double p = monthlyInvestment.doubleValue();
        double r = annualRate.doubleValue() / 100.0 / 12.0;
        double n = years * 12.0;
        
        double futureValue = p * ((Math.pow(1.0 + r, n) - 1.0) / r) * (1.0 + r);
        return BigDecimal.valueOf(futureValue).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the Equated Monthly Installment (EMI) for a loan.
     * Formula: E = P * r * (1 + r)^n / ((1 + r)^n - 1)
     */
    public static BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, int years) {
        if (principal.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }
        double p = principal.doubleValue();
        double r = annualRate.doubleValue() / 100.0 / 12.0;
        double n = years * 12.0;

        double emi = p * r * Math.pow(1.0 + r, n) / (Math.pow(1.0 + r, n) - 1.0);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Compound Interest.
     * Formula: A = P * (1 + r/n)^(n*t)
     */
    public static BigDecimal calculateCompoundInterest(BigDecimal principal, BigDecimal annualRate, int years, int compoundingsPerYear) {
        if (principal.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0 || compoundingsPerYear <= 0) {
            return BigDecimal.ZERO;
        }
        double p = principal.doubleValue();
        double r = annualRate.doubleValue() / 100.0;
        double t = years;
        double k = compoundingsPerYear;

        double amount = p * Math.pow(1.0 + (r / k), t * k);
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the retirement corpus and monthly savings target.
     * Corpus = Inflation-adjusted expenses * 12 * [(1 - (1 + r_real)^-N) / r_real]
     * where r_real = ((1 + postRetReturn) / (1 + inflation)) - 1
     */
    public static RetirementResult calculateRetirement(
            int currentAge, int retirementAge, BigDecimal currentMonthlyExpenses,
            BigDecimal inflationRate, BigDecimal postRetReturn, int lifeExpectancy) {
        
        int yearsToRetire = retirementAge - currentAge;
        int retirementYears = lifeExpectancy - retirementAge;

        if (yearsToRetire <= 0 || retirementYears <= 0) {
            return new RetirementResult(BigDecimal.ZERO, BigDecimal.ZERO);
        }

        // 1. Inflation adjusted expenses at retirement
        double inflation = inflationRate.doubleValue() / 100.0;
        double postReturn = postRetReturn.doubleValue() / 100.0;
        double monthlyExp = currentMonthlyExpenses.doubleValue();

        double monthlyExpAtRetirement = monthlyExp * Math.pow(1.0 + inflation, yearsToRetire);

        // 2. Real rate of return post retirement
        double realReturn = ((1.0 + postReturn) / (1.0 + inflation)) - 1.0;
        if (realReturn == 0) realReturn = 0.001; // Avoid divide by zero

        // 3. Corpus needed at retirement
        double corpusNeeded = monthlyExpAtRetirement * 12.0 * 
                ((1.0 - Math.pow(1.0 + realReturn, -retirementYears)) / realReturn);

        // 4. Monthly savings needed pre-retirement (assuming 10% expected return on savings)
        double preReturn = 0.10 / 12.0; // Assume 10% annual return
        double totalMonths = yearsToRetire * 12.0;
        double monthlySavingsNeeded = corpusNeeded / (((Math.pow(1.0 + preReturn, totalMonths) - 1.0) / preReturn) * (1.0 + preReturn));

        return new RetirementResult(
                BigDecimal.valueOf(corpusNeeded).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(monthlySavingsNeeded).setScale(2, RoundingMode.HALF_UP)
        );
    }

    /**
     * Data carrier object representing the calculated retirement profile.
     */
    public static class RetirementResult {
        public final BigDecimal corpusNeeded;
        public final BigDecimal monthlySavingsNeeded;

        public RetirementResult(BigDecimal corpusNeeded, BigDecimal monthlySavingsNeeded) {
            this.corpusNeeded = corpusNeeded;
            this.monthlySavingsNeeded = monthlySavingsNeeded;
        }
    }
}
