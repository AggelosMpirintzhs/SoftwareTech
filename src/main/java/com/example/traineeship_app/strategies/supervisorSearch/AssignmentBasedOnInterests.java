package com.example.traineeship_app.strategies.supervisorSearch;

import com.example.traineeship_app.Dao.ProfessorDao;
import com.example.traineeship_app.Dto.ProfessorMatchDto;
import com.example.traineeship_app.domainmodel.Professor;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AssignmentBasedOnInterests implements SupervisorAssignmentStrategy {

    private final ProfessorDao professorDao;
    private static final double MATCH_THRESHOLD = 0.4;

    public AssignmentBasedOnInterests(ProfessorDao professorDao) {
        this.professorDao = professorDao;
    }

    @Override
    public List<ProfessorMatchDto> findSuitableProfessors(TraineeshipPosition position) {
        List<Professor> allProfessors = professorDao.findAll();

        List<String> topicList = Arrays.stream(position.getTopics().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        Set<String> positionTopics = new HashSet<>(topicList);
        int totalTopics = positionTopics.size();

        List<ProfessorMatchDto> result = new ArrayList<>();

        for (Professor professor : allProfessors) {
            Set<String> profInterests = new HashSet<>(professor.getInterests());

            Set<String> intersection = new HashSet<>(positionTopics);
            intersection.retainAll(profInterests);

            int matched = intersection.size();
            double similarity = totalTopics == 0 ? 0 : (double) matched / totalTopics;

            if (similarity >= MATCH_THRESHOLD) {
                int load = professor.getSupervisedPositions().size();
                result.add(new ProfessorMatchDto(professor, matched, totalTopics, load));
            }
        }

        return result;
    }
}
