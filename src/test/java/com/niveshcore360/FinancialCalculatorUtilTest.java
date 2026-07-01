package com.niveshcore360;

import com.niveshcore360.util.FinancialCalculatorUtil;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests validating financial calculator equations.
 */
public class FinancialCalculatorUtilTest {

    @Test
    public void testCalculateSIP() {
        BigDecimal monthlyInvestment = new BigDecimal("5000.00");
        BigDecimal rate = new BigDecimal("12.00");
        int years = 10;

        BigDecimal fv = FinancialCalculatorUtil.calculateSIP(monthlyInvestment, rate, years);
        
        assertNotNull(fv);
        // Expecting a value around 1,161,695.38 (approximate standard calculation)
        assertTrue(fv.compareTo(new BigDecimal("1000000")) > 0);
    }

    @Test
    public void testCalculateEMI() {
        BigDecimal principal = new BigDecimal("1000000.00");
        BigDecimal rate = new BigDecimal("8.50");
        int years = 15;

        BigDecimal emi = FinancialCalculatorUtil.calculateEMI(principal, rate, years);

        assertNotNull(emi);
        // Expecting a value around 9,847.40
        assertTrue(emi.compareTo(new BigDecimal("9000")) > 0);
        assertTrue(emi.compareTo(new BigDecimal("11000")) < 0);
    }

    @Test
    public void testCalculateCompoundInterest() {
        BigDecimal principal = new BigDecimal("50000.00");
        BigDecimal rate = new BigDecimal("7.50");
        int years = 5;

        BigDecimal maturity = FinancialCalculatorUtil.calculateCompoundInterest(principal, rate, years, 1);

        assertNotNull(maturity);
        // Expecting around 71,781.47
        assertTrue(maturity.compareTo(new BigDecimal("70000")) > 0);
    }
}
