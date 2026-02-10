package com.example.traineeship_app.services.Impl;

import org.springframework.transaction.annotation.Transactional;
import com.example.traineeship_app.Dao.ProfessorDao;
import com.example.traineeship_app.Dao.StudentDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.Dto.PositionWithScoreDto;
import com.example.traineeship_app.Dto.ProfessorMatchDto;
import com.example.traineeship_app.domainmodel.*;
import com.example.traineeship_app.services.CommitteeService;
import com.example.traineeship_app.strategies.positionSearch.PositionsSearchFactory;
import com.example.traineeship_app.strategies.positionSearch.PositionsSearchStrategy;
import com.example.traineeship_app.strategies.supervisorSearch.SupervisorAssignmentFactory;
import com.example.traineeship_app.strategies.supervisorSearch.SupervisorAssignmentStrategy;
import com.example.traineeship_app.util.MatchingUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommitteeServiceImpl implements CommitteeService {

    private final StudentDao studentDao;
    private final TraineeshipPositionDao positionDao;
    private final ProfessorDao professorDao;
    private final PositionsSearchFactory searchFactory;
    private final SupervisorAssignmentFactory supervisorAssignmentFactory;

    public CommitteeServiceImpl(StudentDao studentDao, TraineeshipPositionDao positionsDao, ProfessorDao professorDao, PositionsSearchFactory searchFactory, SupervisorAssignmentFactory supervisorAssignmentFactory) {
        this.studentDao = studentDao;
        this.positionDao = positionsDao;
        this.professorDao = professorDao;
        this.searchFactory = searchFactory;
        this.supervisorAssignmentFactory = supervisorAssignmentFactory;
    }

    @Override
    public List<Student> retrieveTraineeshipApplicants() {
        List<Student> allApplicants = studentDao.findByLookingForTraineeshipTrue();
        List<Student> eligibleStudents = new ArrayList<>();

        for (Student student : allApplicants) {
            TraineeshipPosition position = student.getAssignedTraineeship();
            if (position == null || !position.isAssigned()) {
                eligibleStudents.add(student);
            }
        }

        return eligibleStudents;
    }

    @Override
    public List<PositionWithScoreDto> getScoredPositionsForApplicant(Integer studentId, String strategy) {
        Student student = studentDao.findById(Long.valueOf(studentId))
                .orElseThrow(() -> new RuntimeException("Student not found"));

        PositionsSearchStrategy searchStrategy = searchFactory.create(strategy);
        List<TraineeshipPosition> positions = searchStrategy.search(student.getUsername());

        List<PositionWithScoreDto> result = new ArrayList<>();

        for (TraineeshipPosition pos : positions) {
            int skillsMatched = MatchingUtils.countMatchedSkills(student, pos);
            int interestsMatched = MatchingUtils.countMatchedInterests(student, pos);
            boolean locationMatch = MatchingUtils.isLocationMatch(student, pos);
            int score = MatchingUtils.calculateTotalScore(student, pos);
            int totalRequiredSkills = pos.getSkills().split(",").length;
            int totalRequiredInterests = pos.getTopics().split(",").length;
            PositionWithScoreDto dto = new PositionWithScoreDto(
                    pos,
                    score,
                    skillsMatched,
                    interestsMatched,
                    locationMatch,
                    totalRequiredSkills,
                    totalRequiredInterests
            );

            result.add(dto);
        }
        result.sort(Comparator.comparingInt(PositionWithScoreDto::getTotalScore).reversed());

        return result;
    }

    @Override
    public void assignPositionToStudent(Integer studentId, Integer positionId) {
        Student student = studentDao.findById(Long.valueOf(studentId))
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TraineeshipPosition position = positionDao.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        student.setAssignedTraineeship(position);
        position.setStudent(student);
        position.setAssigned(true);

        studentDao.save(student);
        positionDao.save(position);
    }

    @Override
    public List<TraineeshipPosition> getAssignedInProgressPositions() {
        List<TraineeshipPosition> all = positionDao.findAll();
        List<TraineeshipPosition> result = new ArrayList<>();

        for (TraineeshipPosition pos : all) {
            if (pos.getStudent() != null && pos.getStudent().getAvgGrade()==0) {
                result.add(pos);
            }
        }

        return result;
    }

    @Override
    public List<Boolean> getCompletableFlags(List<TraineeshipPosition> positions) {
        List<Boolean> list = new ArrayList<>();

        for (TraineeshipPosition p : positions) {
            list.add(isCompletable(p));
        }

        return list;
    }

    private boolean isCompletable(TraineeshipPosition pos) {
        if (pos.getEvaluations() == null) return false;

        boolean hasCompanyEval = false;
        boolean hasProfessorEval = false;

        for (Evaluation eval : pos.getEvaluations()) {
            if (eval.getEvaluationType() == EvaluationType.COMPANY_EVALUATION) {
                hasCompanyEval = eval.getMotivation() > 0
                        && eval.getEfficiency() > 0
                        && eval.getEffectiveness() > 0;
            }

            if (eval.getEvaluationType() == EvaluationType.PROFESSOR_EVALUATION) {
                hasProfessorEval = eval.getMotivation() > 0
                        && eval.getEfficiency() > 0
                        && eval.getEffectiveness() > 0
                        && eval.getFacilities() > 0
                        && eval.getGuidance() > 0;
            }
        }

        return hasCompanyEval && hasProfessorEval;
    }
    @Override
    public Map<Integer, Boolean> getCompanyEvalFlags(List<TraineeshipPosition> positions) {
        Map<Integer, Boolean> map = new HashMap<>();

        for (TraineeshipPosition position : positions) {
            boolean hasEval = false;

            if (position.getEvaluations() != null) {
                for (Evaluation eval : position.getEvaluations()) {
                    if (eval.getEvaluationType() == EvaluationType.COMPANY_EVALUATION) {
                        hasEval = true;
                        break;
                    }
                }
            }

            map.put(position.getId(), hasEval); // ✅ Integer key
        }

        return map;
    }
    @Override
    public Map<Integer, Boolean> getProfessorEvalFlags(List<TraineeshipPosition> positions) {
        Map<Integer, Boolean> map = new HashMap<>();

        for (TraineeshipPosition position : positions) {
            boolean hasEval = false;

            if (position.getEvaluations() != null) {
                for (Evaluation eval : position.getEvaluations()) {
                    if (eval.getEvaluationType() == EvaluationType.PROFESSOR_EVALUATION) {
                        hasEval = true;
                        break;
                    }
                }
            }

            map.put(position.getId(), hasEval); // ✅ Integer key
        }

        return map;
    }

    @Override
    public List<ProfessorMatchDto> findSuitableProfessors(int positionId, String strategyName) {
        TraineeshipPosition position = positionDao.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid position ID"));

        SupervisorAssignmentStrategy strategy = supervisorAssignmentFactory.getStrategy(strategyName);

        return strategy.findSuitableProfessors(position);
    }

    @Override
    public boolean assignProfessorToPosition(int positionId, int professorId) {
        Optional<TraineeshipPosition> positionOpt = positionDao.findById(positionId);
        Optional<Professor> professorOpt = professorDao.findById((long) professorId);

        if (positionOpt.isEmpty() || professorOpt.isEmpty()) return false;

        TraineeshipPosition position = positionOpt.get();
        Professor professor = professorOpt.get();

        position.setSupervisor(professor);
        professor.getSupervisedPositions().add(position);

        positionDao.save(position);
        professorDao.save(professor);

        return true;
    }

    @Override
    @Transactional
    public void completeTraineeship(Integer positionId) {
        TraineeshipPosition position = positionDao.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        Evaluation companyEval = getEvaluationOfType(position, EvaluationType.COMPANY_EVALUATION);
        Evaluation professorEval = getEvaluationOfType(position, EvaluationType.PROFESSOR_EVALUATION);

        if (companyEval == null || professorEval == null) {
            throw new IllegalStateException("Both evaluations are required to complete the traineeship.");
        }

        double companyAvg = average(companyEval.getMotivation(), companyEval.getEfficiency(), companyEval.getEffectiveness());
        double professorAvg = average(professorEval.getMotivation(), professorEval.getEfficiency(), professorEval.getEffectiveness());

        double overallAverage = (companyAvg + professorAvg) / 2.0;
        boolean passed = passedBasedOnAverage(companyEval, professorEval);

        position.setPassFailGrade(passed);
        position.getStudent().setAvgGrade(overallAverage);

        positionDao.save(position);
        studentDao.save(position.getStudent());
    }

    private Evaluation getEvaluationOfType(TraineeshipPosition position, EvaluationType type) {
        return position.getEvaluations().stream()
                .filter(e -> e.getEvaluationType() == type)
                .findFirst()
                .orElse(null);
    }

    private double average(int... values) {
        return Arrays.stream(values).average().orElse(0);
    }

    private boolean passedBasedOnAverage(Evaluation company, Evaluation professor) {
        List<Integer> grades = Arrays.asList(
                company.getMotivation(),
                company.getEfficiency(),
                company.getEffectiveness(),
                professor.getMotivation(),
                professor.getEfficiency(),
                professor.getEffectiveness(),
                professor.getFacilities(),
                professor.getGuidance()
        );

        double average = grades.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        return average >= 2.5;
    }


}