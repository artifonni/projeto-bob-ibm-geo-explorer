package com.geoexplorer;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

/**
 * Valida que o ponto de entrada da aplicação sobe o contexto Spring e que o
 * método {@code main} delega corretamente para o {@link SpringApplication}.
 */
@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@ActiveProfiles("cli")
class GeoExplorerApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void main_deveDelegarParaSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            GeoExplorerApplication.main(new String[]{"--spring.shell.interactive.enabled=false"});

            mocked.verify(() -> SpringApplication.run(
                    GeoExplorerApplication.class,
                    new String[]{"--spring.shell.interactive.enabled=false"}));
        }
    }
}
