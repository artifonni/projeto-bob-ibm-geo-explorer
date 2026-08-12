package com.geoexplorer.service;

import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CertificateService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TrailRepository trailRepository;

    public CertificateService(TrailRepository trailRepository) {
        this.trailRepository = trailRepository;
    }

    /**
     * Gera um certificado fictício formatado para uma trilha concluída.
     *
     * @param technology nome da tecnologia (ex.: "javascript")
     * @param userName   nome do usuário que concluiu a trilha
     * @return string multi-linha representando o certificado
     * @throws ResourceNotFoundException se a tecnologia não existir no banco
     */
    @Transactional(readOnly = true)
    public String generateCertificate(String technology, String userName) {
        Trail trail = trailRepository.findByTechnologyIgnoreCase(technology)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trilha não encontrada para a tecnologia: " + technology));

        String issueDate = LocalDate.now().format(DATE_FORMAT);
        String techDisplay = capitalize(trail.getTechnology());

        return """
                ╔══════════════════════════════════════════════════════════╗
                ║               GEO-EXPLORER — CERTIFICADO                ║
                ╠══════════════════════════════════════════════════════════╣
                ║                                                          ║
                ║  Certificamos que                                        ║
                ║                                                          ║
                ║    %s
                ║                                                          ║
                ║  concluiu com êxito a trilha de estudos:                ║
                ║                                                          ║
                ║    %s — Nível: %s
                ║                                                          ║
                ║  Descrição: %s
                ║                                                          ║
                ║  Data de emissão: %s                              ║
                ║                                                          ║
                ╚══════════════════════════════════════════════════════════╝
                """.formatted(
                padRight(userName, 56),
                padRight(techDisplay, 20),
                trail.getLevel(),
                padRight(trail.getDescription(), 50),
                issueDate
        );
    }

    // -------------------------------------------------------------------------

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
    }

    private String padRight(String text, int length) {
        if (text == null) text = "";
        if (text.length() >= length) return text.substring(0, length);
        return text + " ".repeat(length - text.length());
    }
}
