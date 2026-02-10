package com.example.traineeship_app.strategies.positionSearch;

import com.example.traineeship_app.Dao.StudentDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.util.MatchingUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CompositeSearch implements PositionsSearchStrategy {

    private final TraineeshipPositionDao positionDao;
    private final StudentDao studentDao;

    private static final double JACCARD_THRESHOLD = 0.3;

    public CompositeSearch(TraineeshipPositionDao positionDao, StudentDao studentDao) {
        this.positionDao = positionDao;
        this.studentDao = studentDao;
    }

    @Override
    public List<TraineeshipPosition> search(String applicantUsername) {
        Student student = studentDao.findByUsername(applicantUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String preferredLocation = student.getPreferredLocation();
        List<String> interestsRaw = student.getInterests();
        Set<String> studentInterests = interestsRaw == null ? Set.of()
                : interestsRaw.stream().map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toSet());

        List<TraineeshipPosition> allPositions = positionDao.findAll();
        List<TraineeshipPosition> results = new ArrayList<>();

        for (TraineeshipPosition position : allPositions) {
            if (position.isAssigned()) continue;
            if (!MatchingUtils.studentCoversAllRequiredSkills(student, position)) continue;

            if (position.getCompany() == null || !preferredLocation.equalsIgnoreCase(position.getCompany().getCompanyLocation())) {
                continue;
            }

            String topicsRaw = position.getTopics();
            if (topicsRaw == null || topicsRaw.isBlank()) continue;

            Set<String> positionTopics = Arrays.stream(topicsRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toSet());

            double similarity = calculateJaccard(studentInterests, positionTopics);
            if (similarity > JACCARD_THRESHOLD) {
                results.add(position);
            }
        }

        return results;
    }

    private double calculateJaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;

        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);

        Set<String> union = new HashSet<>(a);
        union.addAll(b);

        return (double) intersection.size() / union.size();
    }
}
