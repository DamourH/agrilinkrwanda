package com.agrilink.rwanda.repository;

import com.agrilink.rwanda.domain.FarmerProduce;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FarmerProduce entity.
 */
@Repository
public interface FarmerProduceRepository extends JpaRepository<FarmerProduce, Long> {
    @Query("select farmerProduce from FarmerProduce farmerProduce where farmerProduce.farmer.login = ?#{authentication.name}")
    List<FarmerProduce> findByFarmerIsCurrentUser();

    default Optional<FarmerProduce> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FarmerProduce> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FarmerProduce> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select farmerProduce from FarmerProduce farmerProduce left join fetch farmerProduce.farmer",
        countQuery = "select count(farmerProduce) from FarmerProduce farmerProduce"
    )
    Page<FarmerProduce> findAllWithToOneRelationships(Pageable pageable);

    @Query("select farmerProduce from FarmerProduce farmerProduce left join fetch farmerProduce.farmer")
    List<FarmerProduce> findAllWithToOneRelationships();

    @Query("select farmerProduce from FarmerProduce farmerProduce left join fetch farmerProduce.farmer where farmerProduce.id =:id")
    Optional<FarmerProduce> findOneWithToOneRelationships(@Param("id") Long id);
}
