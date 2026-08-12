package com.geoexplorer.domain.repository;

import com.geoexplorer.domain.model.Module;
import com.geoexplorer.domain.model.Trail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByTrailOrderByModuleOrderAsc(Trail trail);
}
