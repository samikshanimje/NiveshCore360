package com.niveshcore360.service;

import com.niveshcore360.entity.Asset;
import com.niveshcore360.entity.MarketData;
import java.util.List;

/**
 * Service interface for Live Market feeds and historical charts.
 */
public interface MarketService {
    List<Asset> getAllAssets();
    Asset getAssetBySymbol(String symbol);
    List<MarketData> getHistoricalData(String symbol);
    void simulateMarketTick();
    void seedMarketAssets();
}
