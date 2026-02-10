package com.example.traineeship_app.services;

import com.example.traineeship_app.Dao.ProfessorDao;
import com.example.traineeship_app.Dao.StudentDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.Dto.PositionWithScoreDto;
import com.example.traineeship_app.domainmodel.*;
import com.example.traineeship_app.services.Impl.CommitteeServiceImpl;
import com.example.traineeship_app.strategies.positionSearch.PositionsSearchFactory;
import com.example.traineeship_app.strategies.positionSearch.PositionsSearchStrategy;
import com.example.traineeship_app.strategies.supervisorSearch.SupervisorAssignmentFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CommitteeServiceImplTest {

    private CommitteeServiceImpl committeeService;
    private StudentDao studentDao;
    private TraineeshipPositionDao positionDao;
    private ProfessorDao professorDao;
    private PositionsSearchFactory searchFactory;
    private SupervisorAssignmentFactory supervisorAssignmentFactory;

    @BeforeEach
    void setUp() {
        studentDao = mock(StudentDao.class);
        positionDao = mock(TraineeshipPositionDao.class);
        professorDao = mock(ProfessorDao.class);
        searchFactory = mock(PositionsSearchFactory.class);
        supervisorAssignmentFactory = mock(SupervisorAssignmentFactory.class);

        committeeService = new CommitteeServiceImpl(studentDao, positionDao, professorDao, searchFactory, supervisorAssignmentFactory);
    }

    @Test
    void testRetrieveTraineeshipApplicants_filtersAssigned() {
        Student unassigned = new Student();
        unassigned.setAssignedTraineeship(null);

        Student assigned = new Student();
        TraineeshipPosition pos = new TraineeshipPosition();
        pos.setAssigned(true);
        assigned.setAssignedTraineeship(pos);

        when(studentDao.findByLookingForTraineeshipTrue()).thenReturn(List.of(unassigned, assigned));

        List<Student> result = committeeService.retrieveTraineeshipApplicants();
        assertEquals(1, result.size());
        assertTrue(result.contains(unassigned));
    }

    @Test
    void testAssignPositionToStudent_setsRelations() {
        Student student = new Student();
        student.setUsername("john");
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(1);

        when(studentDao.findById(1L)).thenReturn(Optional.of(student));
        when(positionDao.findById(1)).thenReturn(Optional.of(position));

        committeeService.assignPositionToStudent(1, 1);

        assertEquals(position, student.getAssignedTraineeship());
        assertEquals(student, position.getStudent());
        assertTrue(position.isAssigned());

        verify(studentDao).save(student);
        verify(positionDao).save(position);
    }

    @Test
    void testCompleteTraineeship_setsGrades() {
        Student student = new Student();
        student.setUsername("student1");
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(1);
        position.setStudent(student);

        Evaluation companyEval = new Evaluation(
                null, EvaluationType.COMPANY_EVALUATION, 4, 3, 5, 0, 0, position
        );

        Evaluation profEval = new Evaluation(
                null, EvaluationType.PROFESSOR_EVALUATION, 4, 3, 5, 4, 4, position
        );

        position.setEvaluations(List.of(companyEval, profEval));

        when(positionDao.findById(1)).thenReturn(Optional.of(position));

        committeeService.completeTraineeship(1);

        assertTrue(position.isPassFailGrade());
        assertTrue(student.getAvgGrade() > 0);
        verify(positionDao).save(position);
        verify(studentDao).save(student);
    }

    @Test
    void testGetCompletableFlags_logic() {
        TraineeshipPosition complete = new TraineeshipPosition();
        complete.setId(1);

        Evaluation companyEval = new Evaluation(
                null, EvaluationType.COMPANY_EVALUATION, 3, 3, 3, 0, 0, complete
        );

        Evaluation professorEval = new Evaluation(
                null, EvaluationType.PROFESSOR_EVALUATION, 3, 3, 3, 3, 3, complete
        );

        complete.setEvaluations(List.of(companyEval, professorEval));

        TraineeshipPosition incomplete = new TraineeshipPosition();
        incomplete.setId(2);

        Evaluation incompleteEval = new Evaluation(
                null, EvaluationType.COMPANY_EVALUATION, 3, 3, 3, 0, 0, incomplete
        );

        incomplete.setEvaluations(List.of(incompleteEval)); // Missing professor evaluation

        List<Boolean> flags = committeeService.getCompletableFlags(List.of(complete, incomplete));

        assertEquals(List.of(true, false), flags);
    }

    @Test
    void retrieveTraineeshipApplicants_filtersAssigned() {
        Student assigned = new Student();
        TraineeshipPosition assignedPos = new TraineeshipPosition();
        assignedPos.setAssigned(true);
        assigned.setAssignedTraineeship(assignedPos);

        Student unassigned = new Student();
        unassigned.setAssignedTraineeship(null);

        when(studentDao.findByLookingForTraineeshipTrue()).thenReturn(List.of(assigned, unassigned));

        List<Student> result = committeeService.retrieveTraineeshipApplicants();
        assertEquals(1, result.size());
        assertTrue(result.contains(unassigned));
    }

    @Test
    void assignPositionToStudent_assignsCorrectly() {
        Student student = new Student();
        student.setAssignedTraineeship(null);

        TraineeshipPosition position = new TraineeshipPosition();
        position.setAssigned(false);

        when(studentDao.findById(1L)).thenReturn(Optional.of(student));
        when(positionDao.findById(2)).thenReturn(Optional.of(position));

        committeeService.assignPositionToStudent(1, 2);

        assertEquals(position, student.getAssignedTraineeship());
        assertEquals(student, position.getStudent());
        assertTrue(position.isAssigned());
        verify(studentDao).save(student);
        verify(positionDao).save(position);
    }

    @Test
    void getCompanyEvalFlags_returnsCorrectMap() {
        Evaluation eval = new Evaluation();
        eval.setEvaluationType(EvaluationType.COMPANY_EVALUATION);

        TraineeshipPosition p1 = new TraineeshipPosition();
        p1.setId(1);
        p1.setEvaluations(List.of(eval));

        TraineeshipPosition p2 = new TraineeshipPosition();
        p2.setId(2);
        p2.setEvaluations(null);

        Map<Integer, Boolean> result = committeeService.getCompanyEvalFlags(List.of(p1, p2));
        assertTrue(result.get(1));
        assertFalse(result.get(2));
    }

    @Test
    void getProfessorEvalFlags_returnsCorrectMap() {
        Evaluation eval = new Evaluation();
        eval.setEvaluationType(EvaluationType.PROFESSOR_EVALUATION);

        TraineeshipPosition p1 = new TraineeshipPosition();
        p1.setId(1);
        p1.setEvaluations(List.of(eval));

        TraineeshipPosition p2 = new TraineeshipPosition();
        p2.setId(2);
        p2.setEvaluations(null);

        Map<Integer, Boolean> result = committeeService.getProfessorEvalFlags(List.of(p1, p2));
        assertTrue(result.get(1));
        assertFalse(result.get(2));
    }

    @Test
    void assignProfessorToPosition_successfulAssignment() {
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(1);
        Professor professor = new Professor();
        professor.setSupervisedPositions(new ArrayList<>());

        when(positionDao.findById(1)).thenReturn(Optional.of(position));
        when(professorDao.findById(2L)).thenReturn(Optional.of(professor));

        boolean result = committeeService.assignProfessorToPosition(1, 2);

        assertTrue(result);
        assertEquals(professor, position.getSupervisor());
        assertTrue(professor.getSupervisedPositions().contains(position));
        verify(positionDao).save(position);
        verify(professorDao).save(professor);
    }

//    @Test
//    void getScoredPositionsForApplicant_returnsSortedList() {
//        Student student = new Student();
//        student.setUsername("john123");
//        student.setInterests(List.of("AI"));
//        student.setSkills(List.of("Java"));
//        when(studentDao.findById(1L)).thenReturn(Optional.of(student));
//
//        Company company1 = new Company();
//        company1.setCompanyLocation("Athens");
//
//        Company company2 = new Company();
//        company2.setCompanyLocation("Thessaloniki");
//
//        TraineeshipPosition pos1 = new TraineeshipPosition();
//        pos1.setId(1);
//        pos1.setSkills("Java");
//        pos1.setTopics("AI");
//        pos1.setCompany(company1);
//
//        TraineeshipPosition pos2 = new TraineeshipPosition();
//        pos2.setId(2);
//        pos2.setSkills("Python");
//        pos2.setTopics("ML");
//        pos2.setCompany(company2);
//
//        PositionsSearchStrategy strategy = mock(PositionsSearchStrategy.class);
//        when(searchFactory.create("default")).thenReturn(strategy);
//        when(strategy.search("john123")).thenReturn(List.of(pos1, pos2));
//
//        List<PositionWithScoreDto> result = committeeService.getScoredPositionsForApplicant(1, "default");
//
//        assertEquals(2, result.size());
//        assertTrue(result.get(0).getTotalScore() >= result.get(1).getTotalScore());
//    }


}
