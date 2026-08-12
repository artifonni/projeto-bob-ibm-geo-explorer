package com.geoexplorer.domain.repository;

import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TrailRepository extends JpaRepository<Trail, Long> {

    Optional<Trail> findByTechnologyIgnoreCase(String technology);

    List<Trail> findByLevel(Level level);

    boolean existsByTechnologyIgnoreCase(String technology);
}
