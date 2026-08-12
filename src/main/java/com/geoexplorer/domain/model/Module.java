package com.geoexplorer.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "module_order", nullable = false)
    private Integer moduleOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trail_id", nullable = false)
    private Trail trail;

    public Module() {}

    public Module(String title, String content, Integer moduleOrder, Trail trail) {
        this.title = title;
        this.content = content;
        this.moduleOrder = moduleOrder;
        this.trail = trail;
    }

    // ===== Getters e Setters =====

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getModuleOrder() { return moduleOrder; }
    public void setModuleOrder(Integer moduleOrder) { this.moduleOrder = moduleOrder; }

    public Trail getTrail() { return trail; }
    public void setTrail(Trail trail) { this.trail = trail; }
}
