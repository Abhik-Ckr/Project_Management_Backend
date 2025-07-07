package com.pm.Project_Management_Server.repositories;

import com.pm.Project_Management_Server.entity.Users;
import com.pm.Project_Management_Server.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserName(String userName);

    Optional<Users> findByEmail(String email);

    List<Users> findByUserType(UserType userType);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}
