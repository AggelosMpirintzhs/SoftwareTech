package com.example.traineeship_app.strategies.positionSearch;

import com.example.traineeship_app.Dao.StudentDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.domainmodel.Student;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.util.MatchingUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchBasedOnLocation implements PositionsSearchStrategy {

    private final TraineeshipPositionDao positionDao;
    private final StudentDao studentDao;

    public SearchBasedOnLocation(TraineeshipPositionDao positionDao, StudentDao studentDao) {
        this.positionDao = positionDao;
        this.studentDao = studentDao;
    }

    @Override
    public List<TraineeshipPosition> search(String applicantUsername) {
        Student student = studentDao.findByUsername(applicantUsername)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String preferredLocation = student.getPreferredLocation();
        List<TraineeshipPosition> allPositions = positionDao.findAll();
        List<TraineeshipPosition> matchingPositions = new ArrayList<>();

        for (TraineeshipPosition position : allPositions) {
            if (!position.isAssigned()
                    && MatchingUtils.studentCoversAllRequiredSkills(student, position)
                    && position.getCompany().getCompanyLocation().equalsIgnoreCase(preferredLocation)) {
                matchingPositions.add(position);
            }
        }

        return matchingPositions;
    }
}
