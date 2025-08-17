package com.agrilink.rwanda.web.rest;

import com.agrilink.rwanda.repository.DeliveryRepository;
import com.agrilink.rwanda.service.DeliveryService;
import com.agrilink.rwanda.service.dto.DeliveryDTO;
import com.agrilink.rwanda.web.rest.errors.BadRequestAlertException;
import com.agrilink.rwanda.web.rest.errors.ElasticsearchExceptionMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.agrilink.rwanda.domain.Delivery}.
 */
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryResource.class);

    private static final String ENTITY_NAME = "delivery";

    @Value("${jhipster.clientApp.name:agriLinkRwanda}")
    private String applicationName;

    private final DeliveryService deliveryService;

    private final DeliveryRepository deliveryRepository;

    public DeliveryResource(DeliveryService deliveryService, DeliveryRepository deliveryRepository) {
        this.deliveryService = deliveryService;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * {@code POST  /deliveries} : Create a new delivery.
     *
     * @param deliveryDTO the deliveryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new deliveryDTO, or with status {@code 400 (Bad Request)} if the delivery has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DeliveryDTO> createDelivery(@RequestBody DeliveryDTO deliveryDTO) throws URISyntaxException {
        LOG.debug("REST request to save Delivery : {}", deliveryDTO);
        if (deliveryDTO.getId() != null) {
            throw new BadRequestAlertException("A new delivery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        deliveryDTO = deliveryService.save(deliveryDTO);
        return ResponseEntity.created(new URI("/api/deliveries/" + deliveryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, deliveryDTO.getId().toString()))
            .body(deliveryDTO);
    }

    /**
     * {@code PUT  /deliveries/:id} : Updates an existing delivery.
     *
     * @param id the id of the deliveryDTO to save.
     * @param deliveryDTO the deliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the deliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryDTO> updateDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DeliveryDTO deliveryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Delivery : {}, {}", id, deliveryDTO);
        if (deliveryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deliveryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deliveryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        deliveryDTO = deliveryService.update(deliveryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deliveryDTO.getId().toString()))
            .body(deliveryDTO);
    }

    /**
     * {@code PATCH  /deliveries/:id} : Partial updates given fields of an existing delivery, field will ignore if it is null
     *
     * @param id the id of the deliveryDTO to save.
     * @param deliveryDTO the deliveryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated deliveryDTO,
     * or with status {@code 400 (Bad Request)} if the deliveryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the deliveryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the deliveryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DeliveryDTO> partialUpdateDelivery(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DeliveryDTO deliveryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Delivery partially : {}, {}", id, deliveryDTO);
        if (deliveryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, deliveryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!deliveryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DeliveryDTO> result = deliveryService.partialUpdate(deliveryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, deliveryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /deliveries} : get all the deliveries.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of deliveries in body.
     */
    @GetMapping("")
    public List<DeliveryDTO> getAllDeliveries(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Deliveries");
        return deliveryService.findAll();
    }

    /**
     * {@code GET  /deliveries/:id} : get the "id" delivery.
     *
     * @param id the id of the deliveryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the deliveryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDTO> getDelivery(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Delivery : {}", id);
        Optional<DeliveryDTO> deliveryDTO = deliveryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(deliveryDTO);
    }

    /**
     * {@code DELETE  /deliveries/:id} : delete the "id" delivery.
     *
     * @param id the id of the deliveryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Delivery : {}", id);
        deliveryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /deliveries/_search?query=:query} : search for the delivery corresponding
     * to the query.
     *
     * @param query the query of the delivery search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<DeliveryDTO> searchDeliveries(@RequestParam("query") String query) {
        LOG.debug("REST request to search Deliveries for query {}", query);
        try {
            return deliveryService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
