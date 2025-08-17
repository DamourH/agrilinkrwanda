package com.agrilink.rwanda.repository;

import com.agrilink.rwanda.domain.PurchaseOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PurchaseOrder entity.
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    @Query("select purchaseOrder from PurchaseOrder purchaseOrder where purchaseOrder.buyer.login = ?#{authentication.name}")
    List<PurchaseOrder> findByBuyerIsCurrentUser();

    default Optional<PurchaseOrder> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PurchaseOrder> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PurchaseOrder> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select purchaseOrder from PurchaseOrder purchaseOrder left join fetch purchaseOrder.buyer",
        countQuery = "select count(purchaseOrder) from PurchaseOrder purchaseOrder"
    )
    Page<PurchaseOrder> findAllWithToOneRelationships(Pageable pageable);

    @Query("select purchaseOrder from PurchaseOrder purchaseOrder left join fetch purchaseOrder.buyer")
    List<PurchaseOrder> findAllWithToOneRelationships();

    @Query("select purchaseOrder from PurchaseOrder purchaseOrder left join fetch purchaseOrder.buyer where purchaseOrder.id =:id")
    Optional<PurchaseOrder> findOneWithToOneRelationships(@Param("id") Long id);
}
