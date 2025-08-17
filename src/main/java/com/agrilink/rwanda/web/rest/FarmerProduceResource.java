package com.agrilink.rwanda.web.rest;

import com.agrilink.rwanda.repository.FarmerProduceRepository;
import com.agrilink.rwanda.service.FarmerProduceService;
import com.agrilink.rwanda.service.dto.FarmerProduceDTO;
import com.agrilink.rwanda.web.rest.errors.BadRequestAlertException;
import com.agrilink.rwanda.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.agrilink.rwanda.domain.FarmerProduce}.
 */
@RestController
@RequestMapping("/api/farmer-produces")
public class FarmerProduceResource {

    private static final Logger LOG = LoggerFactory.getLogger(FarmerProduceResource.class);

    private static final String ENTITY_NAME = "farmerProduce";

    @Value("${jhipster.clientApp.name:agriLinkRwanda}")
    private String applicationName;

    private final FarmerProduceService farmerProduceService;

    private final FarmerProduceRepository farmerProduceRepository;

    public FarmerProduceResource(FarmerProduceService farmerProduceService, FarmerProduceRepository farmerProduceRepository) {
        this.farmerProduceService = farmerProduceService;
        this.farmerProduceRepository = farmerProduceRepository;
    }

    /**
     * {@code POST  /farmer-produces} : Create a new farmerProduce.
     *
     * @param farmerProduceDTO the farmerProduceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new farmerProduceDTO, or with status {@code 400 (Bad Request)} if the farmerProduce has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FarmerProduceDTO> createFarmerProduce(@Valid @RequestBody FarmerProduceDTO farmerProduceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FarmerProduce : {}", farmerProduceDTO);
        if (farmerProduceDTO.getId() != null) {
            throw new BadRequestAlertException("A new farmerProduce cannot already have an ID", ENTITY_NAME, "idexists");
        }
        farmerProduceDTO = farmerProduceService.save(farmerProduceDTO);
        return ResponseEntity.created(new URI("/api/farmer-produces/" + farmerProduceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, farmerProduceDTO.getId().toString()))
            .body(farmerProduceDTO);
    }

    /**
     * {@code PUT  /farmer-produces/:id} : Updates an existing farmerProduce.
     *
     * @param id the id of the farmerProduceDTO to save.
     * @param farmerProduceDTO the farmerProduceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated farmerProduceDTO,
     * or with status {@code 400 (Bad Request)} if the farmerProduceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the farmerProduceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FarmerProduceDTO> updateFarmerProduce(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FarmerProduceDTO farmerProduceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FarmerProduce : {}, {}", id, farmerProduceDTO);
        if (farmerProduceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, farmerProduceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!farmerProduceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        farmerProduceDTO = farmerProduceService.update(farmerProduceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, farmerProduceDTO.getId().toString()))
            .body(farmerProduceDTO);
    }

    /**
     * {@code PATCH  /farmer-produces/:id} : Partial updates given fields of an existing farmerProduce, field will ignore if it is null
     *
     * @param id the id of the farmerProduceDTO to save.
     * @param farmerProduceDTO the farmerProduceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated farmerProduceDTO,
     * or with status {@code 400 (Bad Request)} if the farmerProduceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the farmerProduceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the farmerProduceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FarmerProduceDTO> partialUpdateFarmerProduce(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FarmerProduceDTO farmerProduceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FarmerProduce partially : {}, {}", id, farmerProduceDTO);
        if (farmerProduceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, farmerProduceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!farmerProduceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FarmerProduceDTO> result = farmerProduceService.partialUpdate(farmerProduceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, farmerProduceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /farmer-produces} : get all the farmerProduces.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of farmerProduces in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FarmerProduceDTO>> getAllFarmerProduces(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of FarmerProduces");
        Page<FarmerProduceDTO> page;
        if (eagerload) {
            page = farmerProduceService.findAllWithEagerRelationships(pageable);
        } else {
            page = farmerProduceService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /farmer-produces/:id} : get the "id" farmerProduce.
     *
     * @param id the id of the farmerProduceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the farmerProduceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FarmerProduceDTO> getFarmerProduce(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FarmerProduce : {}", id);
        Optional<FarmerProduceDTO> farmerProduceDTO = farmerProduceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(farmerProduceDTO);
    }

    /**
     * {@code DELETE  /farmer-produces/:id} : delete the "id" farmerProduce.
     *
     * @param id the id of the farmerProduceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarmerProduce(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FarmerProduce : {}", id);
        farmerProduceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /farmer-produces/_search?query=:query} : search for the farmerProduce corresponding
     * to the query.
     *
     * @param query the query of the farmerProduce search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<FarmerProduceDTO>> searchFarmerProduces(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of FarmerProduces for query {}", query);
        try {
            Page<FarmerProduceDTO> page = farmerProduceService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
