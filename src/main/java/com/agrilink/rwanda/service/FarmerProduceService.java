package com.agrilink.rwanda.service;

import com.agrilink.rwanda.service.dto.FarmerProduceDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.agrilink.rwanda.domain.FarmerProduce}.
 */
public interface FarmerProduceService {
    /**
     * Save a farmerProduce.
     *
     * @param farmerProduceDTO the entity to save.
     * @return the persisted entity.
     */
    FarmerProduceDTO save(FarmerProduceDTO farmerProduceDTO);

    /**
     * Updates a farmerProduce.
     *
     * @param farmerProduceDTO the entity to update.
     * @return the persisted entity.
     */
    FarmerProduceDTO update(FarmerProduceDTO farmerProduceDTO);

    /**
     * Partially updates a farmerProduce.
     *
     * @param farmerProduceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FarmerProduceDTO> partialUpdate(FarmerProduceDTO farmerProduceDTO);

    /**
     * Get all the farmerProduces.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FarmerProduceDTO> findAll(Pageable pageable);

    /**
     * Get all the farmerProduces with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FarmerProduceDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" farmerProduce.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FarmerProduceDTO> findOne(Long id);

    /**
     * Delete the "id" farmerProduce.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the farmerProduce corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FarmerProduceDTO> search(String query, Pageable pageable);
}
