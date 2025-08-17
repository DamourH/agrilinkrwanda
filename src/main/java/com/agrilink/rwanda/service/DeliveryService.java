package com.agrilink.rwanda.service;

import com.agrilink.rwanda.service.dto.DeliveryDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.agrilink.rwanda.domain.Delivery}.
 */
public interface DeliveryService {
    /**
     * Save a delivery.
     *
     * @param deliveryDTO the entity to save.
     * @return the persisted entity.
     */
    DeliveryDTO save(DeliveryDTO deliveryDTO);

    /**
     * Updates a delivery.
     *
     * @param deliveryDTO the entity to update.
     * @return the persisted entity.
     */
    DeliveryDTO update(DeliveryDTO deliveryDTO);

    /**
     * Partially updates a delivery.
     *
     * @param deliveryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DeliveryDTO> partialUpdate(DeliveryDTO deliveryDTO);

    /**
     * Get all the deliveries.
     *
     * @return the list of entities.
     */
    List<DeliveryDTO> findAll();

    /**
     * Get all the deliveries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DeliveryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" delivery.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DeliveryDTO> findOne(Long id);

    /**
     * Delete the "id" delivery.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the delivery corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<DeliveryDTO> search(String query);
}
