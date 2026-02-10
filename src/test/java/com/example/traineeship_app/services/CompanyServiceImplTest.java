package com.example.traineeship_app.services;

import com.example.traineeship_app.Dao.CompanyDao;
import com.example.traineeship_app.Dao.EvaluationDao;
import com.example.traineeship_app.Dao.TraineeshipPositionDao;
import com.example.traineeship_app.Dto.CompanyDto;
import com.example.traineeship_app.Dto.TraineeshipPositionDto;
import com.example.traineeship_app.domainmodel.Company;
import com.example.traineeship_app.domainmodel.Evaluation;
import com.example.traineeship_app.domainmodel.TraineeshipPosition;
import com.example.traineeship_app.domainmodel.EvaluationType;
import com.example.traineeship_app.services.Impl.CompanyServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CompanyServiceImplTest {

    @Mock
    private CompanyDao companyDao;

    @Mock
    private TraineeshipPositionDao traineeshipPositionDao;

    @Mock
    private EvaluationDao evaluationDao;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHasProfile_WhenExists() {
        String username = "testUser";
        when(companyDao.findByUsername(username)).thenReturn(Optional.of(new Company()));
        boolean result = companyService.hasProfile(username);
        assertTrue(result);
    }

    @Test
    public void testHasProfile_WhenNotExists() {
        String username = "testUser";
        when(companyDao.findByUsername(username)).thenReturn(Optional.empty());
        boolean result = companyService.hasProfile(username);
        assertFalse(result);
    }

    @Test
    public void testCreateProfile_SavesCompany() {
        CompanyDto dto = new CompanyDto("testUser", "Company A", "Athens");
        companyService.createProfile(dto, "testUser");

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        verify(companyDao).save(captor.capture());

        Company saved = captor.getValue();
        assertEquals("testUser", saved.getUsername());
        assertEquals("Company A", saved.getCompanyName());
    }

    @Test
    public void testAddTraineeshipPosition_Success() {
        String username = "company1";
        Company company = new Company();
        company.setUsername(username);

        when(companyDao.findByUsername(username)).thenReturn(Optional.of(company));

        TraineeshipPositionDto dto = new TraineeshipPositionDto(
                "Java Intern",
                "Learn Java",
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                "Java, Spring",
                "Backend",
                false
        );

        companyService.addTraineeshipPosition(username, dto);

        verify(traineeshipPositionDao).save(any(TraineeshipPosition.class));
    }

    @Test
    public void testSaveOrUpdateEvaluation_CreatesNewEvaluation() {
        Integer positionId = 1;
        when(evaluationDao.findByTraineeshipPosition_Id(positionId)).thenReturn(Optional.empty());
        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(new TraineeshipPosition()));

        companyService.saveOrUpdateCompanyEvaluation(positionId, 5, 4, 3);

        ArgumentCaptor<Evaluation> captor = ArgumentCaptor.forClass(Evaluation.class);
        verify(evaluationDao).save(captor.capture());

        Evaluation evaluation = captor.getValue();
        assertEquals(5, evaluation.getMotivation());
        assertEquals(4, evaluation.getEfficiency());
        assertEquals(3, evaluation.getEffectiveness());
        assertEquals(EvaluationType.COMPANY_EVALUATION, evaluation.getEvaluationType());
    }

    @Test
    public void testGetAvailablePositions_ReturnsList() {
        String username = "companyUser";
        List<TraineeshipPosition> mockPositions = List.of(new TraineeshipPosition(), new TraineeshipPosition());

        when(traineeshipPositionDao.findByCompany_Username(username)).thenReturn(mockPositions);

        List<TraineeshipPosition> result = companyService.getAvailablePositions(username);

        assertEquals(2, result.size());
        verify(traineeshipPositionDao).findByCompany_Username(username);
    }


    @Test
    public void testGetCompanyByUsername_ReturnsCompany() {
        String username = "user";
        Company mockCompany = new Company();
        mockCompany.setUsername(username);

        when(companyDao.findByUsername(username)).thenReturn(Optional.of(mockCompany));

        Company result = companyService.getCompanyByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    public void testDeleteTraineeship_CallsDao() {
        Integer positionId = 5;

        companyService.deleteTraineeship(positionId);

        verify(traineeshipPositionDao).deleteById(positionId);
    }

    @Test
    public void testUpdateCompanyProfile_UpdatesAndSaves() {
        Company existingCompany = new Company();
        existingCompany.setCompanyName("Old Name");

        CompanyDto dto = new CompanyDto("user", "New Name", "New Location");

        when(companyDao.findByUsername("user")).thenReturn(Optional.of(existingCompany));

        companyService.updateCompanyProfile(dto);

        assertEquals("New Name", existingCompany.getCompanyName());
        assertEquals("New Location", existingCompany.getCompanyLocation());
        verify(companyDao).save(existingCompany);
    }

    @Test
    void testGetEvaluationData_ReturnsExistingCompanyEvaluation() {

        Integer positionId = 1;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);

        Evaluation companyEval = new Evaluation();
        companyEval.setEvaluationType(EvaluationType.COMPANY_EVALUATION);

        position.setEvaluations(List.of(companyEval));

        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(position));

        Map<String, Object> result = companyService.getEvaluationData(positionId);

        assertEquals(position, result.get("position"));
        assertEquals(companyEval, result.get("evaluation"));
    }

    @Test
    void testGetEvaluationData_NoExistingEvaluation_ReturnsNewEvaluation() {
        Integer positionId = 2;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);
        position.setEvaluations(new ArrayList<>());

        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(position));

        Map<String, Object> result = companyService.getEvaluationData(positionId);

        assertEquals(position, result.get("position"));
        assertNotNull(result.get("evaluation"));
        assertTrue(result.get("evaluation") instanceof Evaluation);
    }

    @Test
    void testGetEvaluationData_PositionNotFound_Throws() {
        when(traineeshipPositionDao.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            companyService.getEvaluationData(999);
        });
    }

    @Test
    void testSaveOrUpdateCompanyEvaluation_UpdatesExistingEvaluation() {
        Integer positionId = 3;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);

        Evaluation eval = new Evaluation();
        eval.setEvaluationType(EvaluationType.COMPANY_EVALUATION);
        position.setEvaluations(List.of(eval));

        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(position));

        companyService.saveOrUpdateCompanyEvaluation(positionId, 5, 4, 3);

        assertEquals(5, eval.getMotivation());
        assertEquals(4, eval.getEfficiency());
        assertEquals(3, eval.getEffectiveness());

        verify(evaluationDao).save(eval);
    }

    @Test
    void testSaveOrUpdateCompanyEvaluation_CreatesEvaluationIfNotExists() {
        Integer positionId = 4;
        TraineeshipPosition position = new TraineeshipPosition();
        position.setId(positionId);
        position.setEvaluations(new ArrayList<>());

        when(traineeshipPositionDao.findById(positionId)).thenReturn(Optional.of(position));

        companyService.saveOrUpdateCompanyEvaluation(positionId, 1, 2, 3);

        Evaluation eval = position.getEvaluations().get(0);

        assertEquals(EvaluationType.COMPANY_EVALUATION, eval.getEvaluationType());
        assertEquals(1, eval.getMotivation());
        assertEquals(2, eval.getEfficiency());
        assertEquals(3, eval.getEffectiveness());
        assertEquals(position, eval.getTraineeshipPosition());

        verify(evaluationDao).save(eval);
    }

    @Test
    void testSaveOrUpdateCompanyEvaluation_PositionNotFound_Throws() {
        when(traineeshipPositionDao.findById(123)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            companyService.saveOrUpdateCompanyEvaluation(123, 1, 1, 1);
        });
    }
}
