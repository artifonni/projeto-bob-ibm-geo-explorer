package com.geoexplorer.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoexplorer.domain.model.Challenge;
import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Module;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.TrailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Popula o banco H2 em memória com as trilhas fictícias do arquivo trails-seed.json.
 * Sem @Profile — necessário nos dois modos (cli e mcp).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final TrailRepository trailRepository;
    private final ObjectMapper objectMapper;

    public DataInitializer(TrailRepository trailRepository, ObjectMapper objectMapper) {
        this.trailRepository = trailRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (trailRepository.count() > 0) {
            log.info("Banco já populado — seed ignorado.");
            return;
        }

        ClassPathResource resource = new ClassPathResource("data/trails-seed.json");
        try (InputStream is = resource.getInputStream()) {
            List<Map<String, Object>> seedData =
                    objectMapper.readValue(is, new TypeReference<>() {});

            for (Map<String, Object> entry : seedData) {
                Trail trail = new Trail(
                        (String) entry.get("technology"),
                        (String) entry.get("description"),
                        Level.valueOf((String) entry.get("level"))
                );
                trailRepository.save(trail);

                // Módulos
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> modules =
                        (List<Map<String, Object>>) entry.get("modules");
                if (modules != null) {
                    for (Map<String, Object> mod : modules) {
                        Module module = new Module(
                                (String) mod.get("title"),
                                (String) mod.get("content"),
                                (Integer) mod.get("order"),
                                trail
                        );
                        trail.getModules().add(module);
                    }
                }

                // Desafios
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> challenges =
                        (List<Map<String, Object>>) entry.get("challenges");
                if (challenges != null) {
                    for (Map<String, Object> ch : challenges) {
                        Challenge challenge = new Challenge(
                                (String) ch.get("title"),
                                (String) ch.get("description"),
                                Level.valueOf((String) ch.get("level")),
                                trail
                        );
                        trail.getChallenges().add(challenge);
                    }
                }

                trailRepository.save(trail);
                log.info("Trilha '{}' carregada com {} módulo(s) e {} desafio(s).",
                        trail.getTechnology(),
                        trail.getModules().size(),
                        trail.getChallenges().size());
            }

            log.info("Seed concluído — {} trilha(s) carregada(s).", trailRepository.count());
        }
    }
}
