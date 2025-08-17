package com.agrilink.rwanda.web.rest;

import static com.agrilink.rwanda.domain.DeliveryAsserts.*;
import static com.agrilink.rwanda.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.agrilink.rwanda.IntegrationTest;
import com.agrilink.rwanda.domain.Delivery;
import com.agrilink.rwanda.domain.enumeration.OrderStatus;
import com.agrilink.rwanda.repository.DeliveryRepository;
import com.agrilink.rwanda.repository.UserRepository;
import com.agrilink.rwanda.repository.search.DeliverySearchRepository;
import com.agrilink.rwanda.service.DeliveryService;
import com.agrilink.rwanda.service.dto.DeliveryDTO;
import com.agrilink.rwanda.service.mapper.DeliveryMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link DeliveryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DeliveryResourceIT {

    private static final Instant DEFAULT_PICKUP_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PICKUP_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DELIVERY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DELIVERY_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.PENDING;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.CONFIRMED;

    private static final String ENTITY_API_URL = "/api/deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/deliveries/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DeliveryRepository deliveryRepositoryMock;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Mock
    private DeliveryService deliveryServiceMock;

    @Autowired
    private DeliverySearchRepository deliverySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliveryMockMvc;

    private Delivery delivery;

    private Delivery insertedDelivery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createEntity() {
        return new Delivery().pickupDate(DEFAULT_PICKUP_DATE).deliveryDate(DEFAULT_DELIVERY_DATE).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createUpdatedEntity() {
        return new Delivery().pickupDate(UPDATED_PICKUP_DATE).deliveryDate(UPDATED_DELIVERY_DATE).status(UPDATED_STATUS);
    }

    @BeforeEach
    void initTest() {
        delivery = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDelivery != null) {
            deliveryRepository.delete(insertedDelivery);
            deliverySearchRepository.delete(insertedDelivery);
            insertedDelivery = null;
        }
    }

    @Test
    @Transactional
    void createDelivery() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);
        var returnedDeliveryDTO = om.readValue(
            restDeliveryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DeliveryDTO.class
        );

        // Validate the Delivery in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDelivery = deliveryMapper.toEntity(returnedDeliveryDTO);
        assertDeliveryUpdatableFieldsEquals(returnedDelivery, getPersistedDelivery(returnedDelivery));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDelivery = returnedDelivery;
    }

    @Test
    @Transactional
    void createDeliveryWithExistingId() throws Exception {
        // Create the Delivery with an existing ID
        delivery.setId(1L);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDeliveries() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(delivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].pickupDate").value(hasItem(DEFAULT_PICKUP_DATE.toString())))
            .andExpect(jsonPath("$.[*].deliveryDate").value(hasItem(DEFAULT_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliveriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(deliveryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeliveryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(deliveryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliveriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(deliveryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeliveryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(deliveryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDelivery() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        // Get the delivery
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, delivery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(delivery.getId().intValue()))
            .andExpect(jsonPath("$.pickupDate").value(DEFAULT_PICKUP_DATE.toString()))
            .andExpect(jsonPath("$.deliveryDate").value(DEFAULT_DELIVERY_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDelivery() throws Exception {
        // Get the delivery
        restDeliveryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDelivery() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        deliverySearchRepository.save(delivery);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());

        // Update the delivery
        Delivery updatedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDelivery are not directly saved in db
        em.detach(updatedDelivery);
        updatedDelivery.pickupDate(UPDATED_PICKUP_DATE).deliveryDate(UPDATED_DELIVERY_DATE).status(UPDATED_STATUS);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(updatedDelivery);

        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeliveryToMatchAllProperties(updatedDelivery);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Delivery> deliverySearchList = Streamable.of(deliverySearchRepository.findAll()).toList();
                Delivery testDeliverySearch = deliverySearchList.get(searchDatabaseSizeAfter - 1);

                assertDeliveryAllPropertiesEquals(testDeliverySearch, updatedDelivery);
            });
    }

    @Test
    @Transactional
    void putNonExistingDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setId(delivery.getId());

        partialUpdatedDelivery.pickupDate(UPDATED_PICKUP_DATE).deliveryDate(UPDATED_DELIVERY_DATE).status(UPDATED_STATUS);

        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDelivery, delivery), getPersistedDelivery(delivery));
    }

    @Test
    @Transactional
    void fullUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setId(delivery.getId());

        partialUpdatedDelivery.pickupDate(UPDATED_PICKUP_DATE).deliveryDate(UPDATED_DELIVERY_DATE).status(UPDATED_STATUS);

        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryUpdatableFieldsEquals(partialUpdatedDelivery, getPersistedDelivery(partialUpdatedDelivery));
    }

    @Test
    @Transactional
    void patchNonExistingDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(deliveryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDelivery() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);
        deliveryRepository.save(delivery);
        deliverySearchRepository.save(delivery);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the delivery
        restDeliveryMockMvc
            .perform(delete(ENTITY_API_URL_ID, delivery.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(deliverySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDelivery() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);
        deliverySearchRepository.save(delivery);

        // Search the delivery
        restDeliveryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + delivery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(delivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].pickupDate").value(hasItem(DEFAULT_PICKUP_DATE.toString())))
            .andExpect(jsonPath("$.[*].deliveryDate").value(hasItem(DEFAULT_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    protected long getRepositoryCount() {
        return deliveryRepository.count();
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

    protected Delivery getPersistedDelivery(Delivery delivery) {
        return deliveryRepository.findById(delivery.getId()).orElseThrow();
    }

    protected void assertPersistedDeliveryToMatchAllProperties(Delivery expectedDelivery) {
        assertDeliveryAllPropertiesEquals(expectedDelivery, getPersistedDelivery(expectedDelivery));
    }

    protected void assertPersistedDeliveryToMatchUpdatableProperties(Delivery expectedDelivery) {
        assertDeliveryAllUpdatablePropertiesEquals(expectedDelivery, getPersistedDelivery(expectedDelivery));
    }
}
