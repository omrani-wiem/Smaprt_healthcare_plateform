package com.healthcare.orchestratorservice.controller;

import com.healthcare.orchestratorservice.model.Intervention;
import com.healthcare.orchestratorservice.repository.InterventionRepository;
import com.healthcare.orchestratorservice.service.InterventionWorkflow;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interventions")
@RequiredArgsConstructor
public class InterventionController {

    private final InterventionRepository repository;
    private final InterventionWorkflow workflow;

    @GetMapping
    public List<Intervention> list() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    public Intervention get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Intervention introuvable"));
    }

    /** Fiche d'intervention enrichie : intervention + dossier patient consolidé via GraphQL. */
    @GetMapping("/{id}/sheet")
    public Map<String, Object> sheet(@PathVariable Long id) {
        return workflow.buildSheet(get(id));
    }
}