//package com.pm.Project_Management_Server.test;
//
//import com.pm.Project_Management_Server.dto.GlobalRateCardDTO;
//import com.pm.Project_Management_Server.dto.ProjectRateCardDTO;
//import com.pm.Project_Management_Server.entity.*;
//import com.pm.Project_Management_Server.exceptions.ProjectNotFoundException;
//import com.pm.Project_Management_Server.repositories.GlobalRateCardRepository;
//import com.pm.Project_Management_Server.repositories.ProjectRateCardRepository;
//import com.pm.Project_Management_Server.repositories.ProjectRepository;
//import com.pm.Project_Management_Server.services.RateCardServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class RateCardServiceTest {
//
//    @Mock private GlobalRateCardRepository globalRateCardRepo;
//    @Mock private ProjectRateCardRepository projectRateCardRepo;
//    @Mock private ProjectRepository projectRepo;
//
//    @InjectMocks private RateCardServiceImpl service;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testAddRateCard_Success() {
//        Project project = Project.builder().id(1L).build();
//        ProjectRateCardDTO dto = new ProjectRateCardDTO(null, 1L, ResourceLevel.SR, 1000.0, true, null);
//
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(projectRateCardRepo.save(any())).thenAnswer(inv -> {
//            ProjectRateCard pr = inv.getArgument(0);
//            pr.setId(10L);
//            return pr;
//        });
//
//        ProjectRateCardDTO result = service.addRateCard(dto);
//        assertNotNull(result.getId());
//        assertEquals(1L, result.getProjectId());
//        assertEquals(1000.0, result.getRate());
//    }
//
//    @Test
//    void testGetAllGlobalRates() {
//        GlobalRateCard g1 = new GlobalRateCard(1L, ResourceLevel.JR, 500.0);
//        GlobalRateCard g2 = new GlobalRateCard(2L, ResourceLevel.SR, 900.0);
//        when(globalRateCardRepo.findAll()).thenReturn(List.of(g1, g2));
//
//        List<GlobalRateCardDTO> result = service.getAllGlobalRates();
//        assertEquals(2, result.size());
//    }
//
//    @Test
//    void testGetProjectRates_WithCustomRates() {
//        ProjectRateCard prc = new ProjectRateCard();
//        prc.setId(1L);
//        prc.setLevel(ResourceLevel.ADVANCE);
//        prc.setRate(2000.0);
//        prc.setActive(true);
//        Project project = Project.builder().id(3L).build();
//        prc.setProject(project);
//
//        when(projectRateCardRepo.findByProjectId(3L)).thenReturn(List.of(prc));
//
//        List<ProjectRateCardDTO> result = service.getProjectRates(3L);
//        assertEquals(1, result.size());
//        assertEquals(2000.0, result.get(0).getRate());
//    }
//
//    @Test
//    void testGetProjectRates_FallbackToGlobal() {
//        when(projectRateCardRepo.findByProjectId(5L)).thenReturn(List.of());
//
//        GlobalRateCard g1 = new GlobalRateCard(10L, ResourceLevel.SR, 888.0);
//        when(globalRateCardRepo.findAll()).thenReturn(List.of(g1));
//
//        List<ProjectRateCardDTO> result = service.getProjectRates(5L);
//        assertEquals(1, result.size());
//        assertEquals(888.0, result.get(0).getRate());
//        assertNull(result.get(0).getProjectId());
//    }
//
//    @Test
//    void testOverrideRate_Success() {
//        Project project = Project.builder().id(1L).build();
//        ProjectRateCard existing = new ProjectRateCard();
//        existing.setId(1L);
//        existing.setLevel(ResourceLevel.JR);
//        existing.setRate(800.0);
//        existing.setProject(project);
//        existing.setActive(true);
//
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(projectRateCardRepo.findByProjectIdAndLevel(1L, ResourceLevel.JR)).thenReturn(Optional.of(existing));
//        when(projectRateCardRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        ProjectRateCardDTO result = service.overrideRate(1L, ResourceLevel.JR, 1200.0);
//        assertTrue(result.getActive());
//        assertEquals(1200.0, result.getRate());
//    }
//
////    @Test
////    void testOverrideRate_InvalidLevel() {
////        assertThrows(IllegalArgumentException.class, () -> service.overrideRate(1L, "INVALID", 999.0));
////    }
//    @Test
//    void testOverrideRate_InvalidLevel() {
//
//        Project dummyProject = Project.builder().id(1L).build();
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(dummyProject));
//
//        assertThrows(IllegalArgumentException.class, () ->
//                service.overrideRate(1L, "INVALID", 999.0)
//        );
//    }
//
//
//    @Test
//    void testInitializeProjectRatesFromGlobal_Success() {
//        Project project = Project.builder().id(2L).build();
//        GlobalRateCard g1 = new GlobalRateCard(1L, ResourceLevel.INTERMEDIATE, 666.0);
//
//        when(projectRepo.findById(2L)).thenReturn(Optional.of(project));
//        when(globalRateCardRepo.findAll()).thenReturn(List.of(g1));
//
//        service.initializeProjectRatesFromGlobal(2L);
//        verify(projectRateCardRepo, times(1)).save(any());
//    }
//
//    // ------------------ Exception Tests ------------------
//
//    @Test
//    void testAddRateCard_ProjectNotFound() {
//        ProjectRateCardDTO dto = new ProjectRateCardDTO();
//        dto.setProjectId(999L);
//
//        when(projectRepo.findById(999L)).thenReturn(Optional.empty());
//        assertThrows(ProjectNotFoundException.class, () -> service.addRateCard(dto));
//    }
//
//    @Test
//    void testOverrideRate_ProjectNotFound() {
//        when(projectRepo.findById(999L)).thenReturn(Optional.empty());
//        assertThrows(ProjectNotFoundException.class, () -> service.overrideRate(999L, ResourceLevel.JR, 800.0));
//    }
//
//    @Test
//    void testInitializeProjectRatesFromGlobal_ProjectNotFound() {
//        when(projectRepo.findById(404L)).thenReturn(Optional.empty());
//        assertThrows(ProjectNotFoundException.class, () -> service.initializeProjectRatesFromGlobal(404L));
//    }
//}
