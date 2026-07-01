package com.niveshcore360.repository;

import com.niveshcore360.entity.User;
import com.niveshcore360.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Watchlist operations.
 */
@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUser(User user);
    List<Watchlist> findByUserId(Long userId);
    Optional<Watchlist> findByUserIdAndName(Long userId, String name);
}
