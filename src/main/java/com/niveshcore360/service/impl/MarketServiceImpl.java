package com.niveshcore360.service.impl;

import com.niveshcore360.entity.Asset;
import com.niveshcore360.entity.AssetType;
import com.niveshcore360.entity.MarketData;
import com.niveshcore360.repository.AssetRepository;
import com.niveshcore360.repository.MarketDataRepository;
import com.niveshcore360.service.MarketService;
import com.niveshcore360.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation managing live simulator market data feeds.
 */
@Service
@Slf4j
public class MarketServiceImpl implements MarketService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private MarketDataRepository marketDataRepository;

    private final SecureRandom random = new SecureRandom();

    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    public Asset getAssetBySymbol(String symbol) {
        return assetRepository.findBySymbol(symbol)
            .orElseThrow(() -> new ResourceNotFoundException("Asset not found with symbol: " + symbol));
    }

    @Override
    public List<MarketData> getHistoricalData(String symbol) {
        Asset asset = getAssetBySymbol(symbol);
        List<MarketData> data = marketDataRepository.findByAssetIdOrderByTimestampAsc(asset.getId());
        
        // If historical data is missing, dynamically generate some on demand
        if (data.isEmpty()) {
            generateHistoricalMockData(asset);
            data = marketDataRepository.findByAssetIdOrderByTimestampAsc(asset.getId());
        }
        return data;
    }

    @Override
    @Transactional
    public void simulateMarketTick() {
        List<Asset> assets = assetRepository.findAll();
        for (Asset asset : assets) {
            double changePercent = -1.5 + (random.nextDouble() * 3.2); // Random Walk: -1.5% to +1.7%
            double multiplier = 1.0 + (changePercent / 100.0);
            
            BigDecimal currentPrice = asset.getCurrentPrice();
            BigDecimal nextPrice = currentPrice.multiply(BigDecimal.valueOf(multiplier))
                .setScale(4, RoundingMode.HALF_UP);
            
            if (nextPrice.compareTo(BigDecimal.ZERO) <= 0) {
                nextPrice = BigDecimal.valueOf(0.01);
            }
            
            asset.setCurrentPrice(nextPrice);
            assetRepository.save(asset);

            // Record this tick into historical logs
            MarketData tick = MarketData.builder()
                .asset(asset)
                .openPrice(currentPrice)
                .closePrice(nextPrice)
                .highPrice(currentPrice.max(nextPrice).multiply(BigDecimal.valueOf(1.002)))
                .lowPrice(currentPrice.min(nextPrice).multiply(BigDecimal.valueOf(0.998)))
                .volume(BigDecimal.valueOf(1000 + random.nextInt(9000)))
                .timestamp(LocalDateTime.now())
                .build();
            marketDataRepository.save(tick);
        }
        log.debug("Simulated market tick update complete.");
    }

    @Override
    @Transactional
    public void seedMarketAssets() {
        if (assetRepository.count() > 0) {
            return;
        }

        log.info("Seeding market database assets (Stocks, Funds, Gold, Crypto, ETFs)...");
        List<Asset> assets = new ArrayList<>();

        // 1. Stocks
        assets.add(Asset.builder().symbol("TCS").name("Tata Consultancy Services").assetType(AssetType.STOCK).currentPrice(BigDecimal.valueOf(3850.00)).riskRating("Medium").build());
        assets.add(Asset.builder().symbol("INFY").name("Infosys Limited").assetType(AssetType.STOCK).currentPrice(BigDecimal.valueOf(1420.00)).riskRating("Medium").build());
        assets.add(Asset.builder().symbol("RELIANCE").name("Reliance Industries").assetType(AssetType.STOCK).currentPrice(BigDecimal.valueOf(2910.00)).riskRating("Medium").build());
        assets.add(Asset.builder().symbol("HDFCBANK").name("HDFC Bank Limited").assetType(AssetType.STOCK).currentPrice(BigDecimal.valueOf(1510.00)).riskRating("Low").build());

        // 2. Mutual Funds
        assets.add(Asset.builder().symbol("SBI_BLUECHIP").name("SBI Bluechip Fund - Direct").assetType(AssetType.MUTUAL_FUND).currentPrice(BigDecimal.valueOf(82.50)).riskRating("Low").build());
        assets.add(Asset.builder().symbol("HDFC_INDEX").name("HDFC Nifty 50 Index Fund").assetType(AssetType.MUTUAL_FUND).currentPrice(BigDecimal.valueOf(34.20)).riskRating("Low").build());
        assets.add(Asset.builder().symbol("NIPPON_SMALL").name("Nippon India Small Cap Fund").assetType(AssetType.MUTUAL_FUND).currentPrice(BigDecimal.valueOf(145.80)).riskRating("High").build());

        // 3. Gold
        assets.add(Asset.builder().symbol("GOLD").name("Sovereign Physical Gold (1g)").assetType(AssetType.GOLD).currentPrice(BigDecimal.valueOf(7250.00)).riskRating("Low").build());

        // 4. Cryptocurrencies
        assets.add(Asset.builder().symbol("BTC").name("Bitcoin").assetType(AssetType.CRYPTO).currentPrice(BigDecimal.valueOf(5520000.00)).riskRating("High").build());
        assets.add(Asset.builder().symbol("ETH").name("Ethereum").assetType(AssetType.CRYPTO).currentPrice(BigDecimal.valueOf(312000.00)).riskRating("High").build());

        // 5. ETFs
        assets.add(Asset.builder().symbol("SPY").name("SPDR S&P 500 ETF Trust").assetType(AssetType.ETF).currentPrice(BigDecimal.valueOf(43500.00)).riskRating("Medium").build());
        assets.add(Asset.builder().symbol("QQQ").name("Invesco QQQ Trust (Nasdaq 100)").assetType(AssetType.ETF).currentPrice(BigDecimal.valueOf(36500.00)).riskRating("Medium").build());

        for (Asset asset : assets) {
            Asset saved = assetRepository.save(asset);
            generateHistoricalMockData(saved);
        }
        log.info("Asset database pre-seeding finished.");
    }

    private void generateHistoricalMockData(Asset asset) {
        LocalDateTime time = LocalDateTime.now().minusDays(30);
        BigDecimal price = asset.getCurrentPrice();

        for (int i = 0; i < 30; i++) {
            double changePercent = -1.8 + (random.nextDouble() * 3.6);
            BigDecimal open = price;
            BigDecimal close = price.multiply(BigDecimal.valueOf(1.0 + (changePercent / 100.0)))
                .setScale(4, RoundingMode.HALF_UP);
            
            if (close.compareTo(BigDecimal.ZERO) <= 0) {
                close = BigDecimal.valueOf(0.01);
            }

            BigDecimal high = open.max(close).multiply(BigDecimal.valueOf(1.0 + (random.nextDouble() * 0.01)));
            BigDecimal low = open.min(close).multiply(BigDecimal.valueOf(1.0 - (random.nextDouble() * 0.01)));

            MarketData data = MarketData.builder()
                .asset(asset)
                .openPrice(open)
                .closePrice(close)
                .highPrice(high)
                .lowPrice(low)
                .volume(BigDecimal.valueOf(5000 + random.nextInt(15000)))
                .timestamp(time)
                .build();
            marketDataRepository.save(data);

            price = close;
            time = time.plusDays(1);
        }
    }
}
