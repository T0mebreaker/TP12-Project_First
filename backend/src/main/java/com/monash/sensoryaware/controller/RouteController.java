package com.monash.sensoryaware.controller;

import static com.monash.sensoryaware.dto.ApiDtos.*;

import com.monash.sensoryaware.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) { this.routeService = routeService; }

    @PostMapping
    public ResponseEntity<RouteResponse> generate(@Valid @RequestBody RouteRequest request) {
        return ResponseEntity.ok(routeService.generate(request));
    }
}
