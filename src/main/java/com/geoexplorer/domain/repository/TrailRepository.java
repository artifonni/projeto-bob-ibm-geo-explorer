package com.geoexplorer.domain.repository;

import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrailRepository extends JpaRepository<Trail, Long> {

    /**
     * Busca trilha por tecnologia (case-insensitive) carregando módulos e
     * desafios em uma única query via JOIN FETCH, prevenindo N+1 queries.
     */
    @EntityGraph(attributePaths = {"modules", "challenges"})
    Optional<Trail> findByTechnologyIgnoreCase(String technology);

    List<Trail> findByLevel(Level level);

    boolean existsByTechnologyIgnoreCase(String technology);
}
