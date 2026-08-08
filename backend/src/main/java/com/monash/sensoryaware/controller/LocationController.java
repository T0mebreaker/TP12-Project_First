package com.monash.sensoryaware.controller;

import static com.monash.sensoryaware.dto.ApiDtos.*;

import com.monash.sensoryaware.service.HistoryService;
import com.monash.sensoryaware.service.LocationService;
import com.monash.sensoryaware.service.NearbyPlaceService;
import com.monash.sensoryaware.service.RouteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    private final RouteService routeService;
    private final LocationService locationService;
    private final HistoryService historyService;
    private final NearbyPlaceService nearbyPlaceService;

    public LocationController(RouteService routeService, LocationService locationService, HistoryService historyService, NearbyPlaceService nearbyPlaceService) {
        this.routeService = routeService;
        this.locationService = locationService;
        this.historyService = historyService;
        this.nearbyPlaceService = nearbyPlaceService;
    }

    @GetMapping("/supported")
    public List<LocationReference> supported() { return routeService.supportedLocations(); }

    @GetMapping("/{id}")
    public LocationDetail detail(@PathVariable String id) { return locationService.detail(id); }

    @GetMapping("/{id}/history")
    public HistoricalTrendResult history(@PathVariable String id) { return historyService.trend(id); }

    @GetMapping("/{id}/nearby-places")
    public NearbyPlacesResult nearby(@PathVariable String id) { return nearbyPlaceService.nearby(id); }
}
