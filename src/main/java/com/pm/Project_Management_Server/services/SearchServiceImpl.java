package com.pm.Project_Management_Server.services;

import com.pm.Project_Management_Server.dto.ClientDTO;
import com.pm.Project_Management_Server.dto.ProjectDTO;
import com.pm.Project_Management_Server.dto.SearchResultDTO;
import com.pm.Project_Management_Server.entity.Client;
import com.pm.Project_Management_Server.entity.Project;
import com.pm.Project_Management_Server.repositories.ClientRepository;
import com.pm.Project_Management_Server.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    @Override
    public SearchResultDTO globalSearch(String keyword) {
        List<ProjectDTO> projects;
        projects = projectRepository.findByProjectNameContainingIgnoreCase(keyword)
                .stream().map(this::mapProjectToDTO).collect(Collectors.toList());

        List<ClientDTO> clients = clientRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(this::mapClientToDTO).collect(Collectors.toList());

        return new SearchResultDTO(projects, clients);
    }

    private ProjectDTO mapProjectToDTO(Project p) {
        return ProjectDTO.builder()
                .id(p.getId())
                .projectName(p.getProjectName())
                .build();
    }

    private ClientDTO mapClientToDTO(Client c) {
        return ClientDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}

