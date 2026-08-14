package com.geoexplorer.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utilitário de build: lê o {@code target/site/jacoco/jacoco.csv} gerado pelo
 * JaCoCo ao final da fase {@code test} e imprime um resumo da cobertura por
 * área (pacote) + total. Não faz parte do código de produção.
 */
public final class CoverageSummaryPrinter {

    private static final String CSV = "target/site/jacoco/jacoco.csv";

    private CoverageSummaryPrinter() {
    }

    public static void main(String[] args) throws IOException {
        Path csv = Path.of(CSV);
        if (!Files.exists(csv)) {
            System.out.println("[coverage-summary] " + CSV
                    + " não encontrado — rode os testes para gerar a cobertura.");
            return;
        }

        Map<String, long[]> byPackage = new LinkedHashMap<>();
        for (String line : Files.readAllLines(csv)) {
            if (line.isBlank() || line.startsWith("GROUP,")) {
                continue;
            }
            String[] c = line.split(",", -1);
            if (c.length < 8) {
                continue;
            }
            long missed = Long.parseLong(c[3]);
            long covered = Long.parseLong(c[4]);
            byPackage.computeIfAbsent(c[1], k -> new long[2])[0] += missed;
            byPackage.computeIfAbsent(c[1], k -> new long[2])[1] += covered;
        }

        long totalMissed = 0;
        long totalCovered = 0;

        System.out.println();
        System.out.println("┌────────────────────────────────────┬──────────────┐");
        System.out.println("│ Área                               │ Cobertura    │");
        System.out.println("├────────────────────────────────────┼──────────────┤");
        for (Map.Entry<String, long[]> e : byPackage.entrySet()) {
            long missed = e.getValue()[0];
            long covered = e.getValue()[1];
            totalMissed += missed;
            totalCovered += covered;
            System.out.printf("│ %-34s │ %9.2f%%  │%n", e.getKey(), percent(covered, missed));
        }
        System.out.println("├────────────────────────────────────┼──────────────┤");
        System.out.printf("│ %-34s │ %9.2f%%  │%n", "TOTAL", percent(totalCovered, totalMissed));
        System.out.println("└────────────────────────────────────┴──────────────┘");
        System.out.println();
    }

    private static double percent(long covered, long missed) {
        return covered * 100.0 / (covered + missed);
    }
}
