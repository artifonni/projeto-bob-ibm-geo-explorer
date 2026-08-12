package com.geoexplorer.service;

import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.ChallengeRepository;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class ChallengeService {

    private final TrailRepository trailRepository;
    private final ChallengeRepository challengeRepository;
    private final Random random;

    public ChallengeService(TrailRepository trailRepository,
                            ChallengeRepository challengeRepository) {
        this.trailRepository = trailRepository;
        this.challengeRepository = challengeRepository;
        this.random = new Random();
    }

    /**
     * Retorna um desafio aleatório para a tecnologia e nível informados.
     *
     * @param technology nome da tecnologia (ex.: "python")
     * @param level      nível do desafio: BEGINNER, INTERMEDIATE ou ADVANCED
     * @return um {@link Challenge} selecionado aleatoriamente
     * @throws ResourceNotFoundException se a tecnologia não existir ou não houver
     *                                   desafios para o nível solicitado
     */
    @Transactional(readOnly = true)
    public Challenge getChallenge(String technology, String level) {
        Level parsedLevel = parseLevel(level);

        Trail trail = trailRepository.findByTechnologyIgnoreCase(technology)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trilha não encontrada para a tecnologia: " + technology));

        List<Challenge> candidates = challengeRepository.findByTrailAndLevel(trail, parsedLevel);

        if (candidates.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Nenhum desafio encontrado para a tecnologia '" + technology
                    + "' no nível '" + level + "'.");
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    // -------------------------------------------------------------------------

    private Level parseLevel(String level) {
        try {
            return Level.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException(
                    "Nível inválido: '" + level + "'. Use BEGINNER, INTERMEDIATE ou ADVANCED.");
        }
    }
}
