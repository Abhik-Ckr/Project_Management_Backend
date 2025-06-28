package com.pm.Project_Management_Server.Services;

import com.pm.Project_Management_Server.Repositories.ContractRepository;
import com.pm.Project_Management_Server.Repositories.ProjectRepository;
import com.pm.Project_Management_Server.Repositories.ResourceRequiredRepository;
import com.pm.Project_Management_Server.dto.ContractDTO;
import com.pm.Project_Management_Server.dto.ContractResponseDTO;
import com.pm.Project_Management_Server.entity.Contract;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.entity.ResourceRequired;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService{
    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;
    private final ResourceRequiredRepository resourceRequiredRepository;

    @Override
    public ContractResponseDTO createContract(ContractDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + dto.getProjectId()));
        
        ResourceRequired resourceRequired = resourceRequiredRepository.findById(dto.getResourceReqId())
                .orElseThrow(() -> new RuntimeException("ResourceRequired not found with id: " + dto.getResourceReqId()));
        
        if (!resourceRequired.getProject().getId().equals(dto.getProjectId())) {
            throw new IllegalArgumentException("ResourceRequired does not belong to the given project.");
        }
        
        Contract contract = new Contract();
        contract.setProject(project);
        contract.setResourceRequired(resourceRequired);
        contract.setDuration(dto.getDuration().toString());
        contract.setAmountQuoted(dto.getAmountQuoted().doubleValue());
        
        Contract saved = contractRepository.save(contract);
        return toResponseDTO(saved);
    }

    @Override
    public List<ContractResponseDTO> getContractsByProject(Long projectId) {
        Contract contract = contractRepository.findByProject_Id(projectId);
        if (contract == null) {
            throw new RuntimeException("Contract not found for projectId: " + projectId);
        }
        return List.of(toResponseDTO(contract));
    }

    @Override
    public ContractResponseDTO updateContractAmount(Long contractId, Double newAmount) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + contractId));
        contract.setAmountQuoted(newAmount);
        Contract saved = contractRepository.save(contract);
        return toResponseDTO(saved);
    }

    private ContractResponseDTO toResponseDTO(Contract contract) {
        return new ContractResponseDTO(
                contract.getId(),
                contract.getProject().getProjectName(),
                contract.getDuration(),
                contract.getAmountQuoted(),
                contract.getResourceRequired().getResourceLevel().name()
        );
    }
}
