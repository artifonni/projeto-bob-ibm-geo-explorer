package com.geoexplorer.service;

import com.geoexplorer.domain.dto.ChallengeDTO;
import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.ChallengeRepository;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class ChallengeService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final TrailRepository trailRepository;
    private final ChallengeRepository challengeRepository;

    public ChallengeService(TrailRepository trailRepository,
                            ChallengeRepository challengeRepository) {
        this.trailRepository = trailRepository;
        this.challengeRepository = challengeRepository;
    }

    /**
     * Retorna um desafio aleatório para a tecnologia e nível informados.
     *
     * @param technology nome da tecnologia (ex.: "python")
     * @param level      nível do desafio: BEGINNER, INTERMEDIATE ou ADVANCED
     * @return {@link ChallengeDTO} selecionado aleatoriamente via SecureRandom
     * @throws ResourceNotFoundException se a tecnologia não existir ou não houver
     *                                   desafios para o nível solicitado
     */
    @Transactional(readOnly = true)
    public ChallengeDTO getChallenge(String technology, String level) {
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

        Challenge ch = candidates.get(SECURE_RANDOM.nextInt(candidates.size()));
        return new ChallengeDTO(ch.getTitle(), ch.getDescription(), ch.getLevel());
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
