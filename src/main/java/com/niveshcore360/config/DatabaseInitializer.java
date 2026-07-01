package com.niveshcore360.config;

import com.niveshcore360.dto.UserDTO;
import com.niveshcore360.entity.MutualFund;
import com.niveshcore360.entity.Role;
import com.niveshcore360.entity.Stock;
import com.niveshcore360.repository.MutualFundRepository;
import com.niveshcore360.repository.StockRepository;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Component executing on application startup to seed core reference data and test accounts.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final MutualFundRepository mutualFundRepository;
    private final AuthService authService;

    @Autowired
    public DatabaseInitializer(UserRepository userRepository,
                               StockRepository stockRepository,
                               MutualFundRepository mutualFundRepository,
                               AuthService authService) {
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.mutualFundRepository = mutualFundRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Reference Stocks if empty
        if (stockRepository.count() == 0) {
            stockRepository.save(Stock.builder().ticker("TCS").companyName("Tata Consultancy Services").currentPrice(new BigDecimal("3850.00")).lastUpdated(LocalDateTime.now()).build());
            stockRepository.save(Stock.builder().ticker("RELIANCE").companyName("Reliance Industries Ltd.").currentPrice(new BigDecimal("2950.00")).lastUpdated(LocalDateTime.now()).build());
            stockRepository.save(Stock.builder().ticker("INFY").companyName("Infosys Technologies").currentPrice(new BigDecimal("1420.00")).lastUpdated(LocalDateTime.now()).build());
            stockRepository.save(Stock.builder().ticker("HDFCBANK").companyName("HDFC Bank Ltd.").currentPrice(new BigDecimal("1680.00")).lastUpdated(LocalDateTime.now()).build());
            stockRepository.save(Stock.builder().ticker("TATAMOTORS").companyName("Tata Motors Ltd.").currentPrice(new BigDecimal("980.00")).lastUpdated(LocalDateTime.now()).build());
        }

        // 2. Seed Reference Mutual Funds if empty
        if (mutualFundRepository.count() == 0) {
            mutualFundRepository.save(MutualFund.builder().fundName("SBI Bluechip Fund - Direct Growth").nav(new BigDecimal("85.5000")).riskRating("Medium").lastUpdated(LocalDateTime.now()).build());
            mutualFundRepository.save(MutualFund.builder().fundName("HDFC Mid-Cap Opportunities Fund").nav(new BigDecimal("142.2000")).riskRating("High").lastUpdated(LocalDateTime.now()).build());
            mutualFundRepository.save(MutualFund.builder().fundName("Parag Parikh Flexi Cap Fund").nav(new BigDecimal("72.8000")).riskRating("Low").lastUpdated(LocalDateTime.now()).build());
            mutualFundRepository.save(MutualFund.builder().fundName("Axis Small Cap Fund").nav(new BigDecimal("98.1500")).riskRating("High").lastUpdated(LocalDateTime.now()).build());
        }

        // 3. Seed Users if empty
        if (userRepository.count() == 0) {
            // Seed Admin
            authService.register(UserDTO.builder()
                    .username("admin")
                    .password("admin123")
                    .email("admin@niveshcore360.com")
                    .fullName("System Administrator")
                    .role(Role.ADMIN)
                    .build());

            // Seed Standard Client
            authService.register(UserDTO.builder()
                    .username("user")
                    .password("user123")
                    .email("user@niveshcore360.com")
                    .fullName("Samiksha Nimje")
                    .role(Role.USER)
                    .build());
        }
    }
}
