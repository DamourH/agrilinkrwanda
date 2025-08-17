package com.agrilink.rwanda.web.rest;

import static com.agrilink.rwanda.domain.FarmerProduceAsserts.*;
import static com.agrilink.rwanda.web.rest.TestUtil.createUpdateProxyForBean;
import static com.agrilink.rwanda.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agrilink.rwanda.IntegrationTest;
import com.agrilink.rwanda.domain.FarmerProduce;
import com.agrilink.rwanda.domain.enumeration.QualityGrade;
import com.agrilink.rwanda.domain.enumeration.Unit;
import com.agrilink.rwanda.repository.FarmerProduceRepository;
import com.agrilink.rwanda.repository.UserRepository;
import com.agrilink.rwanda.repository.search.FarmerProduceSearchRepository;
import com.agrilink.rwanda.service.FarmerProduceService;
import com.agrilink.rwanda.service.dto.FarmerProduceDTO;
import com.agrilink.rwanda.service.mapper.FarmerProduceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FarmerProduceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FarmerProduceResourceIT {

    private static final Double DEFAULT_QUANTITY = 1D;
    private static final Double UPDATED_QUANTITY = 2D;

    private static final Unit DEFAULT_UNIT = Unit.KG;
    private static final Unit UPDATED_UNIT = Unit.GRAM;

    private static final BigDecimal DEFAULT_PRICE_PER_UNIT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_PER_UNIT = new BigDecimal(2);

    private static final Instant DEFAULT_AVAILABLE_FROM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_FROM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_AVAILABLE_UNTIL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_AVAILABLE_UNTIL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final QualityGrade DEFAULT_GRADE = QualityGrade.A_GRADE;
    private static final QualityGrade UPDATED_GRADE = QualityGrade.B_GRADE;

    private static final String ENTITY_API_URL = "/api/farmer-produces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/farmer-produces/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FarmerProduceRepository farmerProduceRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private FarmerProduceRepository farmerProduceRepositoryMock;

    @Autowired
    private FarmerProduceMapper farmerProduceMapper;

    @Mock
    private FarmerProduceService farmerProduceServiceMock;

    @Autowired
    private FarmerProduceSearchRepository farmerProduceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFarmerProduceMockMvc;

    private FarmerProduce farmerProduce;

    private FarmerProduce insertedFarmerProduce;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FarmerProduce createEntity() {
        return new FarmerProduce()
            .quantity(DEFAULT_QUANTITY)
            .unit(DEFAULT_UNIT)
            .pricePerUnit(DEFAULT_PRICE_PER_UNIT)
            .availableFrom(DEFAULT_AVAILABLE_FROM)
            .availableUntil(DEFAULT_AVAILABLE_UNTIL)
            .grade(DEFAULT_GRADE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FarmerProduce createUpdatedEntity() {
        return new FarmerProduce()
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .availableFrom(UPDATED_AVAILABLE_FROM)
            .availableUntil(UPDATED_AVAILABLE_UNTIL)
            .grade(UPDATED_GRADE);
    }

    @BeforeEach
    void initTest() {
        farmerProduce = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFarmerProduce != null) {
            farmerProduceRepository.delete(insertedFarmerProduce);
            farmerProduceSearchRepository.delete(insertedFarmerProduce);
            insertedFarmerProduce = null;
        }
    }

    @Test
    @Transactional
    void createFarmerProduce() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        // Create the FarmerProduce
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);
        var returnedFarmerProduceDTO = om.readValue(
            restFarmerProduceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(farmerProduceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FarmerProduceDTO.class
        );

        // Validate the FarmerProduce in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFarmerProduce = farmerProduceMapper.toEntity(returnedFarmerProduceDTO);
        assertFarmerProduceUpdatableFieldsEquals(returnedFarmerProduce, getPersistedFarmerProduce(returnedFarmerProduce));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedFarmerProduce = returnedFarmerProduce;
    }

    @Test
    @Transactional
    void createFarmerProduceWithExistingId() throws Exception {
        // Create the FarmerProduce with an existing ID
        farmerProduce.setId(1L);
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFarmerProduceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(farmerProduceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FarmerProduce in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        // set the field null
        farmerProduce.setQuantity(null);

        // Create the FarmerProduce, which fails.
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        restFarmerProduceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(farmerProduceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUnitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        // set the field null
        farmerProduce.setUnit(null);

        // Create the FarmerProduce, which fails.
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        restFarmerProduceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(farmerProduceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPricePerUnitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        // set the field null
        farmerProduce.setPricePerUnit(null);

        // Create the FarmerProduce, which fails.
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        restFarmerProduceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(farmerProduceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAvailableFromIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        // set the field null
        farmerProduce.setAvailableFrom(null);

        // Create the FarmerProduce, which fails.
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        restFarmerProduceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(farmerProduceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllFarmerProduces() throws Exception {
        // Initialize the database
        insertedFarmerProduce = farmerProduceRepository.saveAndFlush(farmerProduce);

        // Get all the farmerProduceList
        restFarmerProduceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(farmerProduce.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].pricePerUnit").value(hasItem(sameNumber(DEFAULT_PRICE_PER_UNIT))))
            .andExpect(jsonPath("$.[*].availableFrom").value(hasItem(DEFAULT_AVAILABLE_FROM.toString())))
            .andExpect(jsonPath("$.[*].availableUntil").value(hasItem(DEFAULT_AVAILABLE_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFarmerProducesWithEagerRelationshipsIsEnabled() throws Exception {
        when(farmerProduceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFarmerProduceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(farmerProduceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFarmerProducesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(farmerProduceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFarmerProduceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(farmerProduceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFarmerProduce() throws Exception {
        // Initialize the database
        insertedFarmerProduce = farmerProduceRepository.saveAndFlush(farmerProduce);

        // Get the farmerProduce
        restFarmerProduceMockMvc
            .perform(get(ENTITY_API_URL_ID, farmerProduce.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(farmerProduce.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.toString()))
            .andExpect(jsonPath("$.pricePerUnit").value(sameNumber(DEFAULT_PRICE_PER_UNIT)))
            .andExpect(jsonPath("$.availableFrom").value(DEFAULT_AVAILABLE_FROM.toString()))
            .andExpect(jsonPath("$.availableUntil").value(DEFAULT_AVAILABLE_UNTIL.toString()))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFarmerProduce() throws Exception {
        // Get the farmerProduce
        restFarmerProduceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFarmerProduce() throws Exception {
        // Initialize the database
        insertedFarmerProduce = farmerProduceRepository.saveAndFlush(farmerProduce);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        farmerProduceSearchRepository.save(farmerProduce);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());

        // Update the farmerProduce
        FarmerProduce updatedFarmerProduce = farmerProduceRepository.findById(farmerProduce.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFarmerProduce are not directly saved in db
        em.detach(updatedFarmerProduce);
        updatedFarmerProduce
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .availableFrom(UPDATED_AVAILABLE_FROM)
            .availableUntil(UPDATED_AVAILABLE_UNTIL)
            .grade(UPDATED_GRADE);
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(updatedFarmerProduce);

        restFarmerProduceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, farmerProduceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(farmerProduceDTO))
            )
            .andExpect(status().isOk());

        // Validate the FarmerProduce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFarmerProduceToMatchAllProperties(updatedFarmerProduce);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<FarmerProduce> farmerProduceSearchList = Streamable.of(farmerProduceSearchRepository.findAll()).toList();
                FarmerProduce testFarmerProduceSearch = farmerProduceSearchList.get(searchDatabaseSizeAfter - 1);

                assertFarmerProduceAllPropertiesEquals(testFarmerProduceSearch, updatedFarmerProduce);
            });
    }

    @Test
    @Transactional
    void putNonExistingFarmerProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        farmerProduce.setId(longCount.incrementAndGet());

        // Create the FarmerProduce
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFarmerProduceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, farmerProduceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(farmerProduceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FarmerProduce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFarmerProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        farmerProduce.setId(longCount.incrementAndGet());

        // Create the FarmerProduce
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFarmerProduceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(farmerProduceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FarmerProduce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFarmerProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        farmerProduce.setId(longCount.incrementAndGet());

        // Create the FarmerProduce
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFarmerProduceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(farmerProduceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FarmerProduce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateFarmerProduceWithPatch() throws Exception {
        // Initialize the database
        insertedFarmerProduce = farmerProduceRepository.saveAndFlush(farmerProduce);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the farmerProduce using partial update
        FarmerProduce partialUpdatedFarmerProduce = new FarmerProduce();
        partialUpdatedFarmerProduce.setId(farmerProduce.getId());

        partialUpdatedFarmerProduce
            .quantity(UPDATED_QUANTITY)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .availableFrom(UPDATED_AVAILABLE_FROM)
            .availableUntil(UPDATED_AVAILABLE_UNTIL);

        restFarmerProduceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFarmerProduce.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFarmerProduce))
            )
            .andExpect(status().isOk());

        // Validate the FarmerProduce in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFarmerProduceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFarmerProduce, farmerProduce),
            getPersistedFarmerProduce(farmerProduce)
        );
    }

    @Test
    @Transactional
    void fullUpdateFarmerProduceWithPatch() throws Exception {
        // Initialize the database
        insertedFarmerProduce = farmerProduceRepository.saveAndFlush(farmerProduce);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the farmerProduce using partial update
        FarmerProduce partialUpdatedFarmerProduce = new FarmerProduce();
        partialUpdatedFarmerProduce.setId(farmerProduce.getId());

        partialUpdatedFarmerProduce
            .quantity(UPDATED_QUANTITY)
            .unit(UPDATED_UNIT)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .availableFrom(UPDATED_AVAILABLE_FROM)
            .availableUntil(UPDATED_AVAILABLE_UNTIL)
            .grade(UPDATED_GRADE);

        restFarmerProduceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFarmerProduce.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFarmerProduce))
            )
            .andExpect(status().isOk());

        // Validate the FarmerProduce in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFarmerProduceUpdatableFieldsEquals(partialUpdatedFarmerProduce, getPersistedFarmerProduce(partialUpdatedFarmerProduce));
    }

    @Test
    @Transactional
    void patchNonExistingFarmerProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        farmerProduce.setId(longCount.incrementAndGet());

        // Create the FarmerProduce
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFarmerProduceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, farmerProduceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(farmerProduceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FarmerProduce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFarmerProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        farmerProduce.setId(longCount.incrementAndGet());

        // Create the FarmerProduce
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFarmerProduceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(farmerProduceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FarmerProduce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFarmerProduce() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        farmerProduce.setId(longCount.incrementAndGet());

        // Create the FarmerProduce
        FarmerProduceDTO farmerProduceDTO = farmerProduceMapper.toDto(farmerProduce);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFarmerProduceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(farmerProduceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FarmerProduce in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteFarmerProduce() throws Exception {
        // Initialize the database
        insertedFarmerProduce = farmerProduceRepository.saveAndFlush(farmerProduce);
        farmerProduceRepository.save(farmerProduce);
        farmerProduceSearchRepository.save(farmerProduce);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the farmerProduce
        restFarmerProduceMockMvc
            .perform(delete(ENTITY_API_URL_ID, farmerProduce.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(farmerProduceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchFarmerProduce() throws Exception {
        // Initialize the database
        insertedFarmerProduce = farmerProduceRepository.saveAndFlush(farmerProduce);
        farmerProduceSearchRepository.save(farmerProduce);

        // Search the farmerProduce
        restFarmerProduceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + farmerProduce.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(farmerProduce.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].pricePerUnit").value(hasItem(sameNumber(DEFAULT_PRICE_PER_UNIT))))
            .andExpect(jsonPath("$.[*].availableFrom").value(hasItem(DEFAULT_AVAILABLE_FROM.toString())))
            .andExpect(jsonPath("$.[*].availableUntil").value(hasItem(DEFAULT_AVAILABLE_UNTIL.toString())))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE.toString())));
    }

    protected long getRepositoryCount() {
        return farmerProduceRepository.count();
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

    protected FarmerProduce getPersistedFarmerProduce(FarmerProduce farmerProduce) {
        return farmerProduceRepository.findById(farmerProduce.getId()).orElseThrow();
    }

    protected void assertPersistedFarmerProduceToMatchAllProperties(FarmerProduce expectedFarmerProduce) {
        assertFarmerProduceAllPropertiesEquals(expectedFarmerProduce, getPersistedFarmerProduce(expectedFarmerProduce));
    }

    protected void assertPersistedFarmerProduceToMatchUpdatableProperties(FarmerProduce expectedFarmerProduce) {
        assertFarmerProduceAllUpdatablePropertiesEquals(expectedFarmerProduce, getPersistedFarmerProduce(expectedFarmerProduce));
    }
}
