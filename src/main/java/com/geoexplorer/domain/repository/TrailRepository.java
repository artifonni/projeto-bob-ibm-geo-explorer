package com.geoexplorer.domain.repository;

import com.geoexplorer.domain.model.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrailRepository extends JpaRepository<Trail, Long> {

    /**
     * Busca trilha por tecnologia (case-insensitive) sem carregar coleções.
     * Uso recomendado quando apenas os dados da trilha são necessários
     * (ex.: emissão de certificado, busca de desafios).
     */
    Optional<Trail> findByTechnologyIgnoreCase(String technology);

    /**
     * Busca trilha por tecnologia (case-insensitive) carregando apenas os
     * módulos em uma única query via JOIN FETCH, prevenindo N+1 queries.
     * <p>
     * Atenção: NÃO adicionar {@code challenges} ao fetch — o Hibernate não
     * permite fetch simultâneo de duas coleções {@code List} (bag) na mesma
     * query ({@code MultipleBagFetchException}).
     */
    @Query("SELECT t FROM Trail t LEFT JOIN FETCH t.modules "
            + "WHERE LOWER(t.technology) = LOWER(:technology)")
    Optional<Trail> findByTechnologyIgnoreCaseWithModules(@Param("technology") String technology);
}
