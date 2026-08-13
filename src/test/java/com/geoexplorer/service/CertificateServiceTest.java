package com.geoexplorer.service;

import com.geoexplorer.domain.model.Level;
import com.geoexplorer.domain.model.Trail;
import com.geoexplorer.domain.repository.TrailRepository;
import com.geoexplorer.exception.InvalidInputException;
import com.geoexplorer.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private TrailRepository trailRepository;

    @InjectMocks
    private CertificateService certificateService;

    @Test
    void generateCertificate_deveConterNomeDoUsuario() {
        Trail trail = new Trail("javascript", "Trilha de JavaScript", Level.BEGINNER);

        when(trailRepository.findByTechnologyIgnoreCase("javascript"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("javascript", "Ana Lima");

        assertThat(cert).contains("Ana Lima");
    }

    @Test
    void generateCertificate_deveConterNomeDaTecnologia() {
        Trail trail = new Trail("javascript", "Trilha de JavaScript", Level.BEGINNER);

        when(trailRepository.findByTechnologyIgnoreCase("javascript"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("javascript", "Ana Lima");

        assertThat(cert).containsIgnoringCase("javascript");
    }

    @Test
    void generateCertificate_deveConterCabecalhoCertificado() {
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);

        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", "Carlos Silva");

        assertThat(cert).contains("GEO-EXPLORER");
        assertThat(cert).contains("CERTIFICADO");
    }

    @Test
    void generateCertificate_deveCapitalizarTecnologiaENivel() {
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);

        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", "Carlos Silva");

        assertThat(cert).contains("Java — Nível: BEGINNER");
    }

    @Test
    void generateCertificate_deveLancarException_quandoTecnologiaNaoExiste() {
        when(trailRepository.findByTechnologyIgnoreCase("golang"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificateService.generateCertificate("golang", "Maria"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("golang");
    }

    @Test
    void generateCertificate_deveLancarInvalidInputException_quandoTecnologiaNaoInformada() {
        assertThatThrownBy(() -> certificateService.generateCertificate(null, "Maria"))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Tecnologia");
        assertThatThrownBy(() -> certificateService.generateCertificate("   ", "Maria"))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void generateCertificate_deveLancarInvalidInputException_quandoUsuarioNaoInformado() {
        assertThatThrownBy(() -> certificateService.generateCertificate("java", null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("usuário");
        assertThatThrownBy(() -> certificateService.generateCertificate("java", "   "))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void generateCertificate_todasAsLinhasDevemTerAMesmaLargura() {
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);
        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", "Ana Lima");

        List<String> lines = cert.lines().toList();
        int expectedWidth = lines.get(0).length();
        assertThat(lines).hasSizeGreaterThan(3);
        assertThat(lines).allSatisfy(line ->
                assertThat(line).hasSize(expectedWidth));
    }

    @Test
    void generateCertificate_devePossuirBordasNaPrimeiraEUltimaLinha() {
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);
        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", "Ana Lima");

        List<String> lines = cert.lines().toList();
        assertThat(lines.get(0)).startsWith("╔").endsWith("╗");
        assertThat(lines.get(lines.size() - 1)).startsWith("╚").endsWith("╝");
    }

    @Test
    void generateCertificate_deveConterDataNoFormatoBrasileiro() {
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);
        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", "Ana Lima");

        assertThat(cert).matches("(?s).*Data de emissão: \\d{2}/\\d{2}/\\d{4}.*");
    }

    @Test
    void generateCertificate_deveQuebrarDescricaoLongaSemQuebrarCaixa() {
        String longDescription = "Trilha extremamente completa com um conteúdo muito extenso que "
                + "excede a largura da caixa do certificado e precisa ser quebrado em várias "
                + "linhas sem quebrar o alinhamento das bordas laterais.";
        Trail trail = new Trail("java", longDescription, Level.ADVANCED);
        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", "Ana Lima");

        List<String> lines = cert.lines().toList();
        int expectedWidth = lines.get(0).length();
        assertThat(cert).contains("Descrição:");
        assertThat(lines).allSatisfy(line ->
                assertThat(line).hasSize(expectedWidth));
    }

    @Test
    void generateCertificate_deveQuebrarNomeLongoSemQuebrarCaixa() {
        String longName = "Maria Clara de Souza e Oliveira Vasconcelos Almeida Ribeiro dos Santos";
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);
        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", longName);

        List<String> lines = cert.lines().toList();
        int expectedWidth = lines.get(0).length();
        assertThat(cert).contains("Maria Clara");
        assertThat(lines).allSatisfy(line ->
                assertThat(line).hasSize(expectedWidth));
    }

    @Test
    void generateCertificate_deveQuebrarPalavraUnicaMuitoLongaNoNome() {
        String singleWordName = "SupercalifragilisticexpialidociousSupercalifragilisticexpialidocious";
        Trail trail = new Trail("java", "Trilha de Java", Level.BEGINNER);
        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", singleWordName);

        List<String> lines = cert.lines().toList();
        int expectedWidth = lines.get(0).length();
        assertThat(lines).allSatisfy(line ->
                assertThat(line).hasSize(expectedWidth));
    }

    @Test
    void generateCertificate_deveQuebrarPalavraUnicaMuitoLongaNaDescricao() {
        String longWord = "Anticonstitucionalissimamente".repeat(3);
        Trail trail = new Trail("java", longWord, Level.ADVANCED);
        when(trailRepository.findByTechnologyIgnoreCase("java"))
                .thenReturn(Optional.of(trail));

        String cert = certificateService.generateCertificate("java", "Ana Lima");

        List<String> lines = cert.lines().toList();
        int expectedWidth = lines.get(0).length();
        assertThat(cert).contains("Descrição:");
        assertThat(lines).allSatisfy(line ->
                assertThat(line).hasSize(expectedWidth));
    }
}
