package com.geoexplorer.domain.repository;

import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByTrailAndLevel(Trail trail, Level level);

    List<Challenge> findByTrail(Trail trail);
}
