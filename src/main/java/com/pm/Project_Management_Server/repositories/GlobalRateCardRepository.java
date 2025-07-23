package com.pm.Project_Management_Server.repositories;



import com.pm.Project_Management_Server.entity.GlobalRateCard;
import com.pm.Project_Management_Server.entity.ResourceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GlobalRateCardRepository extends JpaRepository<GlobalRateCard, Long> {

    Optional<GlobalRateCard> findByLevel(ResourceLevel level);

    boolean existsByLevel(ResourceLevel level);

    Optional<GlobalRateCard> findFirstByLevelAndStartDateLessThanEqualAndEndDateGreaterThanEqual(ResourceLevel level, LocalDate start, LocalDate end);
}
