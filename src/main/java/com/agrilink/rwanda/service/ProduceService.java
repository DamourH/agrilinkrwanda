package com.agrilink.rwanda.service;

import com.agrilink.rwanda.service.dto.ProduceDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.agrilink.rwanda.domain.Produce}.
 */
public interface ProduceService {
    /**
     * Save a produce.
     *
     * @param produceDTO the entity to save.
     * @return the persisted entity.
     */
    ProduceDTO save(ProduceDTO produceDTO);

    /**
     * Updates a produce.
     *
     * @param produceDTO the entity to update.
     * @return the persisted entity.
     */
    ProduceDTO update(ProduceDTO produceDTO);

    /**
     * Partially updates a produce.
     *
     * @param produceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProduceDTO> partialUpdate(ProduceDTO produceDTO);

    /**
     * Get all the produces.
     *
     * @return the list of entities.
     */
    List<ProduceDTO> findAll();

    /**
     * Get the "id" produce.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProduceDTO> findOne(Long id);

    /**
     * Delete the "id" produce.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the produce corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<ProduceDTO> search(String query);
}
