package com.example.traineeship_app.strategies.positionSearch;

import org.springframework.stereotype.Component;

@Component
public class PositionsSearchFactory {

    private final SearchBasedOnInterests searchBasedOnInterests;
    private final SearchBasedOnLocation searchBasedOnLocation;
    private final CompositeSearch compositeSearch;

    public PositionsSearchFactory(SearchBasedOnInterests searchBasedOnInterests,
                                  SearchBasedOnLocation searchBasedOnLocation,
                                  CompositeSearch compositeSearch) {
        this.searchBasedOnInterests = searchBasedOnInterests;
        this.searchBasedOnLocation = searchBasedOnLocation;
        this.compositeSearch = compositeSearch;
    }

    public PositionsSearchStrategy create(String strategy) {
        return switch (strategy.toLowerCase()) {
            case "interests" -> searchBasedOnInterests;
            case "location" -> searchBasedOnLocation;
            case "both" -> compositeSearch;
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
    }
}
