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

    /**
     * Calculates Compound Annual Growth Rate (CAGR).
     * Formula: CAGR = (End Value / Begin Value)^(1 / Years) - 1
     */
    public static double calculateCAGR(BigDecimal currentValue, BigDecimal initialValue, double years) {
        if (initialValue.compareTo(BigDecimal.ZERO) <= 0 || currentValue.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return 0.0;
        }
        double end = currentValue.doubleValue();
        double start = initialValue.doubleValue();
        return (Math.pow(end / start, 1.0 / years) - 1.0) * 100.0;
    }

    /**
     * Newton-Raphson implementation to solve for XIRR (Extended Internal Rate of Return).
     * Compares date intervals dynamically and returns the rate as a percentage.
     */
    public static double calculateXIRR(java.util.List<Double> payments, java.util.List<java.time.LocalDate> dates) {
        if (payments.size() != dates.size() || payments.size() < 2) {
            return 0.0;
        }

        double r = 0.1; // Initial guess: 10%
        double precision = 1e-6;
        int maxIterations = 100;

        for (int k = 0; k < maxIterations; k++) {
            double f = 0.0;
            double fPrime = 0.0;

            for (int i = 0; i < payments.size(); i++) {
                double days = java.time.temporal.ChronoUnit.DAYS.between(dates.get(0), dates.get(i));
                double exp = days / 365.0;
                
                // Safe check to prevent Math.pow exceptions with negative base rates
                double base = 1.0 + r;
                if (base <= 0) {
                    base = 0.0001;
                }
                
                double term = Math.pow(base, exp);
                f += payments.get(i) / term;
                fPrime -= (payments.get(i) * exp) / (term * base);
            }

            if (Math.abs(fPrime) < 1e-12) {
                break;
            }

            double nextR = r - f / fPrime;
            if (Math.abs(nextR - r) < precision) {
                return nextR * 100.0;
            }
            r = nextR;
        }
        return r * 100.0;
    }

    /**
     * Calculates the standard deviation (volatility) of returns.
     */
    public static double calculateVolatility(java.util.List<Double> returns) {
        if (returns == null || returns.size() < 2) {
            return 0.0;
        }
        double mean = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = returns.stream()
            .mapToDouble(val -> Math.pow(val - mean, 2))
            .sum() / (returns.size() - 1);
        return Math.sqrt(variance);
    }

    /**
     * Calculates the Sharpe Ratio.
     * Sharpe = (Average return - Risk-free rate) / Volatility
     */
    public static double calculateSharpeRatio(java.util.List<Double> returns, double riskFreeRate) {
        if (returns == null || returns.size() < 2) {
            return 0.0;
        }
        double avgReturn = returns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double vol = calculateVolatility(returns);
        if (vol == 0.0) {
            return 0.0;
        }
        return (avgReturn - riskFreeRate) / vol;
    }

    /**
     * Calculates Beta: covariance of asset and market returns divided by variance of market.
     */
    public static double calculateBeta(java.util.List<Double> assetReturns, java.util.List<Double> marketReturns) {
        if (assetReturns == null || marketReturns == null || assetReturns.size() != marketReturns.size() || assetReturns.size() < 2) {
            return 1.0; // Default beta is 1.0 (matches market volatility)
        }
        double meanAsset = assetReturns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double meanMarket = marketReturns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double covariance = 0.0;
        double varianceMarket = 0.0;
        for (int i = 0; i < assetReturns.size(); i++) {
            double diffAsset = assetReturns.get(i) - meanAsset;
            double diffMarket = marketReturns.get(i) - meanMarket;
            covariance += diffAsset * diffMarket;
            varianceMarket += Math.pow(diffMarket, 2);
        }
        
        if (varianceMarket == 0.0) {
            return 1.0;
        }
        return (covariance / (assetReturns.size() - 1)) / (varianceMarket / (marketReturns.size() - 1));
    }

    /**
     * Calculates Alpha: excess return of asset above capital asset pricing model expectations.
     * Alpha = Asset Return - [Risk-Free Rate + Beta * (Market Return - Risk-Free Rate)]
     */
    public static double calculateAlpha(double assetReturn, double riskFreeRate, double marketReturn, double beta) {
        return assetReturn - (riskFreeRate + beta * (marketReturn - riskFreeRate));
    }

    /**
     * Calculates Lumpsum growth value.
     */
    public static BigDecimal calculateLumpsum(BigDecimal principal, BigDecimal annualRate, int years) {
        if (principal.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }
        double p = principal.doubleValue();
        double r = annualRate.doubleValue() / 100.0;
        double fv = p * Math.pow(1.0 + r, years);
        return BigDecimal.valueOf(fv).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Simple Interest.
     */
    public static BigDecimal calculateSimpleInterest(BigDecimal principal, BigDecimal annualRate, int years) {
        if (principal.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = annualRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal interest = principal.multiply(rate).multiply(BigDecimal.valueOf(years));
        return interest.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Present Value (PV).
     */
    public static BigDecimal calculatePresentValue(BigDecimal futureValue, BigDecimal annualRate, int years) {
        if (futureValue.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }
        double fv = futureValue.doubleValue();
        double r = annualRate.doubleValue() / 100.0;
        double pv = fv / Math.pow(1.0 + r, years);
        return BigDecimal.valueOf(pv).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Fixed Deposit (FD) return with standard quarterly compounding.
     */
    public static BigDecimal calculateFD(BigDecimal principal, BigDecimal annualRate, int years) {
        // Quarterly compounding is typical for FDs: 4 times a year
        return calculateCompoundInterest(principal, annualRate, years, 4);
    }

    /**
     * Calculates Recurring Deposit (RD) total maturity value.
     */
    public static BigDecimal calculateRD(BigDecimal monthlyDeposit, BigDecimal annualRate, int years) {
        if (monthlyDeposit.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }
        double p = monthlyDeposit.doubleValue();
        double r = annualRate.doubleValue() / 100.0 / 4.0; // quarterly compounding basis
        double n = years * 4.0; // quarters
        double totalMonths = years * 12.0;

        double sum = 0;
        for (int i = 1; i <= totalMonths; i++) {
            // Compound each installment for the remaining months
            double monthsLeft = totalMonths - i + 1;
            double maturityOfInstallment = p * Math.pow(1.0 + (r * 4.0 / 12.0), monthsLeft);
            sum += maturityOfInstallment;
        }
        return BigDecimal.valueOf(sum).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Public Provident Fund (PPF) maturity value.
     * Compounded annually, deposits assumed at the start of the year.
     */
    public static BigDecimal calculatePPF(BigDecimal annualDeposit, BigDecimal annualRate, int years) {
        if (annualDeposit.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }
        double p = annualDeposit.doubleValue();
        double r = annualRate.doubleValue() / 100.0;
        
        double fv = p * ((Math.pow(1.0 + r, years) - 1.0) / r) * (1.0 + r);
        return BigDecimal.valueOf(fv).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates National Pension System (NPS) maturity corpus.
     */
    public static BigDecimal calculateNPS(BigDecimal monthlyContribution, BigDecimal expectedReturn, int yearsToMaturity) {
        // NPS grows like a SIP until retirement age
        return calculateSIP(monthlyContribution, expectedReturn, yearsToMaturity);
    }

    /**
     * Adjusts a cash flow or expense for inflation.
     */
    public static BigDecimal calculateInflationAdjusted(BigDecimal amount, BigDecimal inflationRate, int years) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return amount;
        }
        double a = amount.doubleValue();
        double inf = inflationRate.doubleValue() / 100.0;
        double futureVal = a * Math.pow(1.0 + inf, years);
        return BigDecimal.valueOf(futureVal).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates loan eligibility based on monthly income and FOIR (Fixed Obligations to Income Ratio).
     * Typically FOIR is 50% of income. Max EMI = Income * 50% - Current EMIs.
     */
    public static BigDecimal calculateLoanEligibility(BigDecimal monthlyIncome, BigDecimal currentEmis, BigDecimal annualRate, int years) {
        if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal maxAllowedEmi = monthlyIncome.multiply(BigDecimal.valueOf(0.50)).subtract(currentEmis);
        if (maxAllowedEmi.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        double emi = maxAllowedEmi.doubleValue();
        double r = annualRate.doubleValue() / 100.0 / 12.0;
        double n = years * 12.0;

        // Eligibility Principal = Emi / [ r * (1+r)^n / ((1+r)^n - 1) ]
        double eligibility = emi / (r * Math.pow(1.0 + r, n) / (Math.pow(1.0 + r, n) - 1.0));
        return BigDecimal.valueOf(eligibility).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Basic Income Tax estimation (New regime 2024 slab approximations).
     */
    public static BigDecimal calculateIncomeTax(BigDecimal annualIncome) {
        if (annualIncome.compareTo(BigDecimal.valueOf(700000)) <= 0) {
            return BigDecimal.ZERO; // Rebate limit in India New Regime
        }
        double income = annualIncome.doubleValue();
        double tax = 0;

        // Slab limits
        if (income > 1500000) {
            tax += (income - 1500000) * 0.30;
            income = 1500000;
        }
        if (income > 1200000) {
            tax += (income - 1200000) * 0.20;
            income = 1200000;
        }
        if (income > 900000) {
            tax += (income - 900000) * 0.15;
            income = 900000;
        }
        if (income > 600000) {
            tax += (income - 600000) * 0.10;
            income = 600000;
        }
        if (income > 300000) {
            tax += (income - 300000) * 0.05;
        }

        return BigDecimal.valueOf(tax).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates Capital Gains Tax.
     * STCG is typically 15%, LTCG is typically 10% (exceeding 1 Lakh limits).
     */
    public static BigDecimal calculateCapitalGainsTax(BigDecimal gains, boolean isLongTerm) {
        if (gains.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (isLongTerm) {
            // Apply 10% tax rate on LTCG
            return gains.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
        } else {
            // Apply 15% tax rate on STCG
            return gains.multiply(BigDecimal.valueOf(0.15)).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
