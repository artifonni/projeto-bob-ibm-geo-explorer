package com.geoexplorer.controller;

import com.geoexplorer.domain.dto.ChallengeDTO;
import com.geoexplorer.domain.dto.TrailDTO;
import com.geoexplorer.exception.GeoExplorerException;
import com.geoexplorer.exception.ResourceNotFoundException;
import com.geoexplorer.service.CertificateService;
import com.geoexplorer.service.ChallengeService;
import com.geoexplorer.service.TrailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeoExplorerController {

    private final TrailService trailService;
    private final ChallengeService challengeService;
    private final CertificateService certificateService;

    public GeoExplorerController(TrailService trailService,
                                 ChallengeService challengeService,
                                 CertificateService certificateService) {
        this.trailService       = trailService;
        this.challengeService   = challengeService;
        this.certificateService = certificateService;
    }

    /** GET /trail?technology=java */
    @GetMapping("/trail")
    public ResponseEntity<TrailDTO> getTrail(@RequestParam String technology) {
        try {
            return ResponseEntity.ok(trailService.getTrail(technology));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (GeoExplorerException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** GET /challenge?technology=java&level=BEGINNER */
    @GetMapping("/challenge")
    public ResponseEntity<ChallengeDTO> getChallenge(@RequestParam String technology,
                                                     @RequestParam String level) {
        try {
            return ResponseEntity.ok(challengeService.getChallenge(technology, level));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (GeoExplorerException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** GET /certificate?technology=java&user=Ana+Lima */
    @GetMapping("/certificate")
    public ResponseEntity<String> getCertificate(@RequestParam String technology,
                                                 @RequestParam String user) {
        try {
            return ResponseEntity.ok(certificateService.generateCertificate(technology, user));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (GeoExplorerException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
