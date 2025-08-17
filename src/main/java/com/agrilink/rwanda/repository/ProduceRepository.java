package com.agrilink.rwanda.repository;

import com.agrilink.rwanda.domain.Produce;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Produce entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProduceRepository extends JpaRepository<Produce, Long> {}
