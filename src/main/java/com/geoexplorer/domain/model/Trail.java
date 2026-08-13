package com.geoexplorer.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trails")
public class Trail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String technology;

    @NotBlank
    @Column(nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @OneToMany(mappedBy = "trail", cascade = CascadeType.ALL, orphanRemoval = true,
               fetch = FetchType.LAZY)
    private List<Module> modules = new ArrayList<>();

    @OneToMany(mappedBy = "trail", cascade = CascadeType.ALL, orphanRemoval = true,
               fetch = FetchType.LAZY)
    private List<Challenge> challenges = new ArrayList<>();

    public Trail() {}

    public Trail(String technology, String description, Level level) {
        this.technology = technology;
        this.description = description;
        this.level = level;
    }

    // ===== Getters e Setters =====

    public Long getId() { return id; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

    public List<Module> getModules() { return modules; }
    public void setModules(List<Module> modules) { this.modules = modules; }

    public List<Challenge> getChallenges() { return challenges; }
    public void setChallenges(List<Challenge> challenges) { this.challenges = challenges; }
}
