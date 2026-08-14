package com.geoexplorer.service;

import com.geoexplorer.domain.dto.ChallengeDTO;
import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.ChallengeRepository;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.InvalidInputException;
import com.geoexplorer.exception.InvalidLevelException;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ChallengeService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /** Próximo índice por (tecnologia:nível) para rotacionar sem repetir. */
    private final ConcurrentMap<String, AtomicInteger> nextIndexByKey = new ConcurrentHashMap<>();

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
     * @return {@link ChallengeDTO} selecionado de forma rotativa (sem repetir
     *         o desafio em chamadas consecutivas)
     * @throws InvalidInputException    se a tecnologia não for informada
     * @throws InvalidLevelException    se o nível não for informado ou for inválido
     * @throws ResourceNotFoundException se a tecnologia não existir ou não houver
     *                                   desafios para o nível solicitado
     */
    @Transactional(readOnly = true)
    public ChallengeDTO getChallenge(String technology, String level) {
        if (isBlank(technology)) {
            throw new InvalidInputException("Tecnologia não informada.");
        }

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

        Challenge ch = pickChallenge(technology, parsedLevel, candidates);
        return new ChallengeDTO(ch.getTitle(), ch.getDescription(), ch.getLevel());
    }

    /**
     * Seleciona o desafio de forma rotativa (round-robin) por tecnologia e nível,
     * garantindo que chamadas consecutivas retornem desafios diferentes.
     * O ponto de partida é aleatório para não ser previsível entre reinícios.
     */
    private Challenge pickChallenge(String technology, Level level, List<Challenge> candidates) {
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        String key = technology.trim().toLowerCase(Locale.ROOT) + ":" + level.name();
        AtomicInteger nextIndex = nextIndexByKey.computeIfAbsent(
                key, k -> new AtomicInteger(SECURE_RANDOM.nextInt(candidates.size())));

        return candidates.get(nextIndex.getAndIncrement() % candidates.size());
    }

    // -------------------------------------------------------------------------

    private Level parseLevel(String level) {
        if (isBlank(level)) {
            throw new InvalidLevelException(
                    "Nível não informado. Use BEGINNER, INTERMEDIATE ou ADVANCED.");
        }
        try {
            return Level.valueOf(level.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new InvalidLevelException(
                    "Nível inválido: '" + level + "'. Use BEGINNER, INTERMEDIATE ou ADVANCED.");
        }
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
