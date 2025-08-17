package com.agrilink.rwanda.web.rest;

import static com.agrilink.rwanda.domain.ProduceAsserts.*;
import static com.agrilink.rwanda.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agrilink.rwanda.IntegrationTest;
import com.agrilink.rwanda.domain.Produce;
import com.agrilink.rwanda.repository.ProduceRepository;
import com.agrilink.rwanda.repository.search.ProduceSearchRepository;
import com.agrilink.rwanda.service.dto.ProduceDTO;
import com.agrilink.rwanda.service.mapper.ProduceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProduceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProduceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/produces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/produces/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProduceRepository produceRepository;

    @Autowired
    private ProduceMapper produceMapper;

    @Autowired
    private ProduceSearchRepository produceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProduceMockMvc;

    private Produce produce;

    private Produce insertedProduce;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produce createEntity() {
        return new Produce().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produce createUpdatedEntity() {
        return new Produce().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        produce = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProduce != null) {
            produceRepository.delete(insertedProduce);
            produceSearchRepository.delete(insertedProduce);
            insertedProduce = null;
        }
    }

    @Test
    @Transactional
    void createProduce() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        // Create the Produce
        ProduceDTO produceDTO = produceMapper.toDto(produce);
        var returnedProduceDTO = om.readValue(
            restProduceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProduceDTO.class
        );

        // Validate the Produce in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProduce = produceMapper.toEntity(returnedProduceDTO);
        assertProduceUpdatableFieldsEquals(returnedProduce, getPersistedProduce(returnedProduce));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProduce = returnedProduce;
    }

    @Test
    @Transactional
    void createProduceWithExistingId() throws Exception {
        // Create the Produce with an existing ID
        produce.setId(1L);
        ProduceDTO produceDTO = produceMapper.toDto(produce);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProduceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Produce in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        // set the field null
        produce.setName(null);

        // Create the Produce, which fails.
        ProduceDTO produceDTO = produceMapper.toDto(produce);

        restProduceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProduces() throws Exception {
        // Initialize the database
        insertedProduce = produceRepository.saveAndFlush(produce);

        // Get all the produceList
        restProduceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produce.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getProduce() throws Exception {
        // Initialize the database
        insertedProduce = produceRepository.saveAndFlush(produce);

        // Get the produce
        restProduceMockMvc
            .perform(get(ENTITY_API_URL_ID, produce.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(produce.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingProduce() throws Exception {
        // Get the produce
        restProduceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProduce() throws Exception {
        // Initialize the database
        insertedProduce = produceRepository.saveAndFlush(produce);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        produceSearchRepository.save(produce);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());

        // Update the produce
        Produce updatedProduce = produceRepository.findById(produce.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProduce are not directly saved in db
        em.detach(updatedProduce);
        updatedProduce.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        ProduceDTO produceDTO = produceMapper.toDto(updatedProduce);

        restProduceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produceDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Produce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProduceToMatchAllProperties(updatedProduce);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Produce> produceSearchList = Streamable.of(produceSearchRepository.findAll()).toList();
                Produce testProduceSearch = produceSearchList.get(searchDatabaseSizeAfter - 1);

                assertProduceAllPropertiesEquals(testProduceSearch, updatedProduce);
            });
    }

    @Test
    @Transactional
    void putNonExistingProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        produce.setId(longCount.incrementAndGet());

        // Create the Produce
        ProduceDTO produceDTO = produceMapper.toDto(produce);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produceDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        produce.setId(longCount.incrementAndGet());

        // Create the Produce
        ProduceDTO produceDTO = produceMapper.toDto(produce);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(produceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        produce.setId(longCount.incrementAndGet());

        // Create the Produce
        ProduceDTO produceDTO = produceMapper.toDto(produce);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProduceWithPatch() throws Exception {
        // Initialize the database
        insertedProduce = produceRepository.saveAndFlush(produce);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produce using partial update
        Produce partialUpdatedProduce = new Produce();
        partialUpdatedProduce.setId(produce.getId());

        partialUpdatedProduce.description(UPDATED_DESCRIPTION);

        restProduceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduce.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduce))
            )
            .andExpect(status().isOk());

        // Validate the Produce in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProduceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProduce, produce), getPersistedProduce(produce));
    }

    @Test
    @Transactional
    void fullUpdateProduceWithPatch() throws Exception {
        // Initialize the database
        insertedProduce = produceRepository.saveAndFlush(produce);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produce using partial update
        Produce partialUpdatedProduce = new Produce();
        partialUpdatedProduce.setId(produce.getId());

        partialUpdatedProduce.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restProduceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduce.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduce))
            )
            .andExpect(status().isOk());

        // Validate the Produce in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProduceUpdatableFieldsEquals(partialUpdatedProduce, getPersistedProduce(partialUpdatedProduce));
    }

    @Test
    @Transactional
    void patchNonExistingProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        produce.setId(longCount.incrementAndGet());

        // Create the Produce
        ProduceDTO produceDTO = produceMapper.toDto(produce);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, produceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(produceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        produce.setId(longCount.incrementAndGet());

        // Create the Produce
        ProduceDTO produceDTO = produceMapper.toDto(produce);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(produceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        produce.setId(longCount.incrementAndGet());

        // Create the Produce
        ProduceDTO produceDTO = produceMapper.toDto(produce);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(produceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProduce() throws Exception {
        // Initialize the database
        insertedProduce = produceRepository.saveAndFlush(produce);
        produceRepository.save(produce);
        produceSearchRepository.save(produce);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the produce
        restProduceMockMvc
            .perform(delete(ENTITY_API_URL_ID, produce.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(produceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProduce() throws Exception {
        // Initialize the database
        insertedProduce = produceRepository.saveAndFlush(produce);
        produceSearchRepository.save(produce);

        // Search the produce
        restProduceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + produce.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produce.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    protected long getRepositoryCount() {
        return produceRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Produce getPersistedProduce(Produce produce) {
        return produceRepository.findById(produce.getId()).orElseThrow();
    }

    protected void assertPersistedProduceToMatchAllProperties(Produce expectedProduce) {
        assertProduceAllPropertiesEquals(expectedProduce, getPersistedProduce(expectedProduce));
    }

    protected void assertPersistedProduceToMatchUpdatableProperties(Produce expectedProduce) {
        assertProduceAllUpdatablePropertiesEquals(expectedProduce, getPersistedProduce(expectedProduce));
    }
}
