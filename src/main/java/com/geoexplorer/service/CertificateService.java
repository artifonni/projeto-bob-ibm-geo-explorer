package com.geoexplorer.service;

import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.InvalidInputException;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CertificateService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Largura do interior da caixa do certificado (sem as bordas). */
    private static final int BOX_WIDTH = 62;
    private static final String HORIZONTAL = "═".repeat(BOX_WIDTH);

    private final TrailRepository trailRepository;

    public CertificateService(TrailRepository trailRepository) {
        this.trailRepository = trailRepository;
    }

    /**
     * Gera um certificado fictício formatado para uma trilha concluída.
     *
     * @param technology nome da tecnologia (ex.: "javascript")
     * @param userName   nome do usuário que concluiu a trilha
     * @return string multi-linha representando o certificado, sempre com todas
     *         as linhas da mesma largura (caixa alinhada)
     * @throws InvalidInputException    se a tecnologia ou o usuário não forem informados
     * @throws ResourceNotFoundException se a tecnologia não existir no banco
     */
    @Transactional(readOnly = true)
    public String generateCertificate(String technology, String userName) {
        if (isBlank(technology)) {
            throw new InvalidInputException("Tecnologia não informada.");
        }
        if (isBlank(userName)) {
            throw new InvalidInputException("Nome do usuário não informado.");
        }

        Trail trail = trailRepository.findByTechnologyIgnoreCase(technology)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trilha não encontrada para a tecnologia: " + technology));

        StringBuilder sb = new StringBuilder();

        sb.append("╔").append(HORIZONTAL).append("╗").append('\n');
        sb.append(centered("GEO-EXPLORER — CERTIFICADO")).append('\n');
        sb.append("╠").append(HORIZONTAL).append("╣").append('\n');
        sb.append(emptyLine()).append('\n');
        sb.append(boxLine("  Certificamos que")).append('\n');
        sb.append(emptyLine()).append('\n');
        for (String line : wrap(userName, BOX_WIDTH - 4)) {
            sb.append(boxLine("    " + line)).append('\n');
        }
        sb.append(emptyLine()).append('\n');
        sb.append(boxLine("  concluiu com êxito a trilha de estudos:")).append('\n');
        sb.append(emptyLine()).append('\n');
        String techLevel = capitalize(trail.getTechnology()) + " — Nível: " + trail.getLevel();
        for (String line : wrap(techLevel, BOX_WIDTH - 4)) {
            sb.append(boxLine("    " + line)).append('\n');
        }
        sb.append(emptyLine()).append('\n');
        List<String> descriptionLines = wrap(trail.getDescription(), BOX_WIDTH - 12);
        sb.append(boxLine("  Descrição: " + descriptionLines.get(0))).append('\n');
        for (int i = 1; i < descriptionLines.size(); i++) {
            sb.append(boxLine("    " + descriptionLines.get(i))).append('\n');
        }
        sb.append(emptyLine()).append('\n');
        sb.append(boxLine("  Data de emissão: " + LocalDate.now().format(DATE_FORMAT))).append('\n');
        sb.append(emptyLine()).append('\n');
        sb.append("╚").append(HORIZONTAL).append("╝");

        return sb.toString();
    }

    // -------------------------------------------------------------------------

    /**
     * Monta uma linha da caixa com o conteúdo preenchido à direita até
     * ocupar exatamente {@link #BOX_WIDTH} caracteres.
     */
    private String boxLine(String content) {
        String text = content == null ? "" : content;
        if (text.length() > BOX_WIDTH) {
            text = text.substring(0, BOX_WIDTH);
        }
        return "║" + text + " ".repeat(BOX_WIDTH - text.length()) + "║";
    }

    private String emptyLine() {
        return boxLine("");
    }

    private String centered(String text) {
        int spaces = BOX_WIDTH - text.length();
        int left = spaces / 2;
        int right = spaces - left;
        return "║" + " ".repeat(left) + text + " ".repeat(right) + "║";
    }

    /**
     * Quebra um texto em linhas de no máximo {@code maxWidth} caracteres,
     * respeitando limites entre palavras e quebrando palavras maiores que a
     * largura. Uma string vazia resulta em uma única linha vazia.
     */
    private List<String> wrap(String text, int maxWidth) {
        if (text == null || text.isBlank()) {
            return List.of("");
        }
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.trim().split("\\s+")) {
            if (word.length() > maxWidth) {
                if (!line.isEmpty()) {
                    lines.add(line.toString());
                    line.setLength(0);
                }
                for (int i = 0; i < word.length(); i += maxWidth) {
                    lines.add(word.substring(i, Math.min(i + maxWidth, word.length())));
                }
            } else if (line.isEmpty()) {
                line.append(word);
            } else if (line.length() + 1 + word.length() <= maxWidth) {
                line.append(' ').append(word);
            } else {
                lines.add(line.toString());
                line.setLength(0);
                line.append(word);
            }
        }
        if (!line.isEmpty()) {
            lines.add(line.toString());
        }
        return lines;
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
