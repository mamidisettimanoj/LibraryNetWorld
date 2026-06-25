package com.librarynet.controller;

import com.librarynet.dto.*;
import com.librarynet.service.PublicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publications")
public class PublicationController {
    private final PublicationService service;

    public PublicationController(PublicationService service) { this.service = service; }

    @GetMapping
    public List<PublicationResponse> list(@RequestParam(required = false) Integer startYear,
                                          @RequestParam(required = false) Integer endYear) {
        return service.list(startYear, endYear);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublicationResponse create(@Valid @RequestBody PublicationRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public PublicationResponse update(@PathVariable Long id, @Valid @RequestBody PublicationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiMessage delete(@PathVariable Long id) {
        service.delete(id);
        return new ApiMessage("Publication deleted successfully");
    }
}
