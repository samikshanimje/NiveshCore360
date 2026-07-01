package com.niveshcore360.repository;

import com.niveshcore360.entity.Asset;
import com.niveshcore360.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Unified Asset operations.
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);
    List<Asset> findByAssetType(AssetType assetType);
}
