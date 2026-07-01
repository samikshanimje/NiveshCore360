package com.niveshcore360;

import com.niveshcore360.dto.PortfolioSummaryDTO;
import com.niveshcore360.entity.*;
import com.niveshcore360.repository.InvestmentRepository;
import com.niveshcore360.repository.PortfolioRepository;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.service.impl.PortfolioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests validating portfolio aggregate valuations and return math.
 */
public class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InvestmentRepository investmentRepository;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPortfolioSummaryCalculation() {
        Portfolio portfolio = Portfolio.builder()
                .id(10L)
                .name("Growth Portfolio")
                .build();

        Asset asset = Asset.builder()
                .id(1L)
                .symbol("INFY")
                .name("Infosys")
                .currentPrice(new BigDecimal("1500.00")) // current
                .assetType(AssetType.STOCK)
                .build();

        // 10 units bought at 1000.00 each -> Investment: 10000.00, Current value: 15000.00
        Investment inv = Investment.builder()
                .id(1L)
                .portfolio(portfolio)
                .assetType(AssetType.STOCK)
                .asset(asset)
                .quantity(new BigDecimal("10.0000"))
                .purchasePrice(new BigDecimal("1000.00"))
                .purchaseDate(LocalDate.now())
                .build();

        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));
        when(investmentRepository.findByPortfolioId(10L)).thenReturn(Arrays.asList(inv));

        PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(10L);

        assertNotNull(summary);
        assertEquals(new BigDecimal("10000.00"), summary.getTotalInvestment());
        assertEquals(new BigDecimal("15000.00"), summary.getTotalCurrentValue());
        assertEquals(new BigDecimal("5000.00"), summary.getTotalProfitLoss());
        assertEquals(new BigDecimal("50.00"), summary.getTotalProfitLossPercentage());
        assertEquals(new BigDecimal("100.00"), summary.getStockWeight());
        assertEquals(new BigDecimal("0.00"), summary.getMutualFundWeight());
    }
}
