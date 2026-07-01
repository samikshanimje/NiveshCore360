package com.niveshcore360.repository;

import com.niveshcore360.entity.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for MarketData queries.
 */
@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {
    List<MarketData> findByAssetIdOrderByTimestampAsc(Long assetId);
}
