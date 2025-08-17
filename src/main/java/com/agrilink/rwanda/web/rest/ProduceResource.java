package com.agrilink.rwanda.web.rest;

import com.agrilink.rwanda.repository.ProduceRepository;
import com.agrilink.rwanda.service.ProduceService;
import com.agrilink.rwanda.service.dto.ProduceDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.agrilink.rwanda.domain.Produce}.
 */
@RestController
@RequestMapping("/api/produces")
public class ProduceResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProduceResource.class);

    private static final String ENTITY_NAME = "produce";

    @Value("${jhipster.clientApp.name:agriLinkRwanda}")
    private String applicationName;

    private final ProduceService produceService;

    private final ProduceRepository produceRepository;

    public ProduceResource(ProduceService produceService, ProduceRepository produceRepository) {
        this.produceService = produceService;
        this.produceRepository = produceRepository;
    }

    /**
     * {@code POST  /produces} : Create a new produce.
     *
     * @param produceDTO the produceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new produceDTO, or with status {@code 400 (Bad Request)} if the produce has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProduceDTO> createProduce(@Valid @RequestBody ProduceDTO produceDTO) throws URISyntaxException {
        LOG.debug("REST request to save Produce : {}", produceDTO);
        if (produceDTO.getId() != null) {
            throw new BadRequestAlertException("A new produce cannot already have an ID", ENTITY_NAME, "idexists");
        }
        produceDTO = produceService.save(produceDTO);
        return ResponseEntity.created(new URI("/api/produces/" + produceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, produceDTO.getId().toString()))
            .body(produceDTO);
    }

    /**
     * {@code PUT  /produces/:id} : Updates an existing produce.
     *
     * @param id the id of the produceDTO to save.
     * @param produceDTO the produceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produceDTO,
     * or with status {@code 400 (Bad Request)} if the produceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the produceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProduceDTO> updateProduce(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProduceDTO produceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Produce : {}, {}", id, produceDTO);
        if (produceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!produceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        produceDTO = produceService.update(produceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, produceDTO.getId().toString()))
            .body(produceDTO);
    }

    /**
     * {@code PATCH  /produces/:id} : Partial updates given fields of an existing produce, field will ignore if it is null
     *
     * @param id the id of the produceDTO to save.
     * @param produceDTO the produceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produceDTO,
     * or with status {@code 400 (Bad Request)} if the produceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the produceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the produceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProduceDTO> partialUpdateProduce(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProduceDTO produceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Produce partially : {}, {}", id, produceDTO);
        if (produceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!produceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProduceDTO> result = produceService.partialUpdate(produceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, produceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /produces} : get all the produces.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of produces in body.
     */
    @GetMapping("")
    public List<ProduceDTO> getAllProduces() {
        LOG.debug("REST request to get all Produces");
        return produceService.findAll();
    }

    /**
     * {@code GET  /produces/:id} : get the "id" produce.
     *
     * @param id the id of the produceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the produceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProduceDTO> getProduce(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Produce : {}", id);
        Optional<ProduceDTO> produceDTO = produceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(produceDTO);
    }

    /**
     * {@code DELETE  /produces/:id} : delete the "id" produce.
     *
     * @param id the id of the produceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduce(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Produce : {}", id);
        produceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /produces/_search?query=:query} : search for the produce corresponding
     * to the query.
     *
     * @param query the query of the produce search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<ProduceDTO> searchProduces(@RequestParam("query") String query) {
        LOG.debug("REST request to search Produces for query {}", query);
        try {
            return produceService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
