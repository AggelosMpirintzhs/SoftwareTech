package com.example.traineeship_app.strategies.supervisorSearch;

import com.example.traineeship_app.Dao.ProfessorDao;
import com.example.traineeship_app.Dto.ProfessorMatchDto;
import com.example.traineeship_app.domainmodel.Professor;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AssignmentBasedOnLoad implements SupervisorAssignmentStrategy {

    private final ProfessorDao professorDao;

    public AssignmentBasedOnLoad(ProfessorDao professorDao) {
        this.professorDao = professorDao;
    }

    @Override
    public List<ProfessorMatchDto> findSuitableProfessors(TraineeshipPosition position) {
        List<Professor> allProfessors = professorDao.findAll();

        int minLoad = Integer.MAX_VALUE;
        List<Professor> candidates = new ArrayList<>();

        for (Professor professor : allProfessors) {
            int load = professor.getSupervisedPositions().size();

            if (load < minLoad) {
                candidates.clear();
                candidates.add(professor);
                minLoad = load;
            } else if (load == minLoad) {
                candidates.add(professor);
            }
        }


        List<String> positionTopics = Arrays.stream(position.getTopics().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        Set<String> positionTopicSet = new HashSet<>(positionTopics);
        int totalTopics = positionTopicSet.size();

        List<ProfessorMatchDto> result = new ArrayList<>();

        for (Professor professor : candidates) {
            Set<String> profInterests = new HashSet<>(professor.getInterests());
            Set<String> intersection = new HashSet<>(positionTopicSet);
            intersection.retainAll(profInterests);

            int matched = intersection.size();
            int load = professor.getSupervisedPositions().size();

            result.add(new ProfessorMatchDto(professor, matched, totalTopics, load));
        }

        return result;
    }
}
