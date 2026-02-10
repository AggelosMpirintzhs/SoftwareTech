package com.example.traineeship_app.services;

import com.example.traineeship_app.Dao.EvaluationDao;
import com.example.traineeship_app.Dao.ProfessorDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.Dto.ProfessorDto;
import com.example.traineeship_app.domainmodel.*;
import com.example.traineeship_app.services.Impl.ProfessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProfessorServiceImplTest {

    @Mock
    private ProfessorDao professorDao;

    @Mock
    private TraineeshipPositionDao traineeshipPositionDao;

    @Mock
    private EvaluationDao evaluationDao;

    @InjectMocks
    private ProfessorServiceImpl professorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHasProfile_WhenExists() {
        when(professorDao.findByUsername("prof")).thenReturn(Optional.of(new Professor()));
        assertTrue(professorService.hasProfile("prof"));
    }

    @Test
    public void testHasProfile_WhenNotExists() {
        when(professorDao.findByUsername("unknown")).thenReturn(Optional.empty());
        assertFalse(professorService.hasProfile("unknown"));
    }

    @Test
    public void testCreateProfile_SavesProfessor() {
        ProfessorDto dto = new ProfessorDto("user", "Dr. Smith", Collections.singletonList("AI"));
        professorService.createProfile(dto, "user");
        verify(professorDao).save(any(Professor.class));
    }

    @Test
    public void testGetProfessorByUsername_ReturnsProfessor() {
        Professor mock = new Professor();
        mock.setUsername("user");

        when(professorDao.findByUsername("user")).thenReturn(Optional.of(mock));

        Professor prof = professorService.getProfessorByUsername("user");

        assertEquals("user", prof.getUsername());
    }

    @Test
    public void testUpdateProfessorProfile_UpdatesAndSaves() {
        Professor existing = new Professor();
        existing.setProfessorName("Old");

        ProfessorDto dto = new ProfessorDto("user", "New", Collections.singletonList("ML"));

        when(professorDao.findByUsername("user")).thenReturn(Optional.of(existing));

        professorService.updateProfessorProfile(dto);

        assertEquals("New", existing.getProfessorName());
        assertEquals(List.of("ML"), existing.getInterests());
        verify(professorDao).save(existing);
    }

    @Test
    void testGetEvaluationData_ReturnsExistingEvaluation() {
        Integer positionId = 1;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);

        Evaluation existingEvaluation = new Evaluation();
        existingEvaluation.setEvaluationType(EvaluationType.PROFESSOR_EVALUATION);

        position.setEvaluations(List.of(existingEvaluation));

        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(position));

        Map<String, Object> result = professorService.getEvaluationData(positionId);

        assertNotNull(result);
        assertEquals(position, result.get("position"));
        assertEquals(existingEvaluation, result.get("evaluation"));
    }

    @Test
    void testGetEvaluationData_WhenPositionNotFound_Throws() {
        when(traineeshipPositionDao.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> professorService.getEvaluationData(999));
    }

    @Test
    void testGetEvaluationData_CreatesEvaluationIfNotExists() {
        Integer positionId = 2;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);
        position.setEvaluations(new ArrayList<>());

        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(position));

        Map<String, Object> result = professorService.getEvaluationData(positionId);

        assertNotNull(result.get("evaluation"));
        Evaluation eval = (Evaluation) result.get("evaluation");

        assertEquals(EvaluationType.PROFESSOR_EVALUATION, eval.getEvaluationType());
        assertEquals(position, eval.getTraineeshipPosition());
        assertTrue(position.getEvaluations().contains(eval));
    }

    @Test
    void testSaveOrUpdateProfessorEvaluation_UpdatesExistingEvaluation() {
        Integer positionId = 3;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);

        Evaluation eval = new Evaluation();
        eval.setEvaluationType(EvaluationType.PROFESSOR_EVALUATION);
        position.setEvaluations(List.of(eval));

        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(position));

        professorService.saveOrUpdateProfessorEvaluation(
                positionId, 5, 4, 3, 2, 1
        );

        assertEquals(5, eval.getMotivation());
        assertEquals(4, eval.getEfficiency());
        assertEquals(3, eval.getEffectiveness());
        assertEquals(2, eval.getFacilities());
        assertEquals(1, eval.getGuidance());

        verify(evaluationDao).save(eval);
    }

    @Test
    void testSaveOrUpdateProfessorEvaluation_CreatesEvaluationIfNotExists() {
        Integer positionId = 4;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);
        position.setEvaluations(new ArrayList<>());

        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(position));

        professorService.saveOrUpdateProfessorEvaluation(positionId, 1, 2, 3, 4, 5);

        Evaluation eval = position.getEvaluations().get(0);
        assertEquals(1, eval.getMotivation());
        assertEquals(2, eval.getEfficiency());
        assertEquals(3, eval.getEffectiveness());
        assertEquals(4, eval.getFacilities());
        assertEquals(5, eval.getGuidance());

        verify(evaluationDao).save(eval);
    }

    @Test
    void testSaveOrUpdateProfessorEvaluation_WhenPositionNotFound_Throws() {
        when(traineeshipPositionDao.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                professorService.saveOrUpdateProfessorEvaluation(999, 1, 2, 3, 4, 5));
    }
}
