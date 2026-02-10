package com.example.traineeship_app.strategies.supervisorSearch;

import org.springframework.stereotype.Component;

@Component
public class SupervisorAssignmentFactory {

    private final AssignmentBasedOnInterests assignmentBasedOnInterests;
    private final AssignmentBasedOnLoad assignmentBasedOnLoad;

    public SupervisorAssignmentFactory(AssignmentBasedOnInterests assignmentBasedOnInterests,
                                       AssignmentBasedOnLoad assignmentBasedOnLoad) {
        this.assignmentBasedOnInterests = assignmentBasedOnInterests;
        this.assignmentBasedOnLoad = assignmentBasedOnLoad;
    }

    public SupervisorAssignmentStrategy getStrategy(String strategy) {
        return switch (strategy.toLowerCase()) {
            case "interests" -> assignmentBasedOnInterests;
            case "load" -> assignmentBasedOnLoad;
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategy);
        };
    }
}
