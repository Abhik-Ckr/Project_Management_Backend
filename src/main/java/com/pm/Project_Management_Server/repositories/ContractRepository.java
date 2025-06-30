package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Contract findByProject_Id(Long projectId);
}
