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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * Popula o banco H2 em memória com as trilhas fictícias do arquivo trails-seed.json.
 * Sem @Profile — necessário nos dois modos (cli e mcp).
 *
 * <p>{@code @Order(HIGHEST_PRECEDENCE)} é crítico no profile cli: o runner do
 * Spring Shell (DefaultShellApplicationRunner) tem {@code @Order(0)} e bloqueia a
 * thread principal no REPL. Se o seed rodar depois dele, as tabelas ficam vazias.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final TrailRepository trailRepository;
    private final ObjectMapper objectMapper;

    public DataInitializer(TrailRepository trailRepository, ObjectMapper objectMapper) {
        this.trailRepository = trailRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        try {
            if (trailRepository.count() > 0) {
                log.info("Banco já populado — seed ignorado.");
                return;
            }

            ClassPathResource resource = new ClassPathResource("data/trails-seed.json");
            try (InputStream is = resource.getInputStream()) {
                List<TrailSeed> seedData =
                        objectMapper.readValue(is, new TypeReference<>() {});

                List<Trail> trails = seedData.stream().map(this::toTrail).toList();
                trailRepository.saveAll(trails);

                for (Trail trail : trails) {
                    log.info("Trilha '{}' carregada com {} módulo(s) e {} desafio(s).",
                            trail.getTechnology(),
                            trail.getModules().size(),
                            trail.getChallenges().size());
                }
                log.info("Seed concluído — {} trilha(s) carregada(s).", trailRepository.count());
            }
        } catch (Exception e) {
            log.error("Falha ao popular o seed de trilhas a partir de trails-seed.json.", e);
            throw new RuntimeException("Falha ao carregar trails-seed.json", e);
        }
    }

    private Trail toTrail(TrailSeed seed) {
        Trail trail = new Trail(seed.technology(), seed.description(), seed.level());

        if (seed.modules() != null) {
            for (ModuleSeed module : seed.modules()) {
                trail.getModules().add(new Module(module.title(), module.content(), module.order(), trail));
            }
        }
        if (seed.challenges() != null) {
            for (ChallengeSeed challenge : seed.challenges()) {
                trail.getChallenges().add(new Challenge(challenge.title(), challenge.description(), challenge.level(), trail));
            }
        }
        return trail;
    }

    // ===== DTOs temporários de desserialização (trails-seed.json) =====

    public record TrailSeed(String technology, String description, Level level,
                            List<ModuleSeed> modules, List<ChallengeSeed> challenges) {}

    public record ModuleSeed(String title, String content, int order) {}

    public record ChallengeSeed(String title, String description, Level level) {}
}
