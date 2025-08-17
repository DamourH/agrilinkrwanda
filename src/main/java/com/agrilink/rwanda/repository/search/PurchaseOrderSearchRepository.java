package com.agrilink.rwanda.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.agrilink.rwanda.domain.PurchaseOrder;
import com.agrilink.rwanda.repository.PurchaseOrderRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link PurchaseOrder} entity.
 */
public interface PurchaseOrderSearchRepository
    extends ElasticsearchRepository<PurchaseOrder, Long>, PurchaseOrderSearchRepositoryInternal {}

interface PurchaseOrderSearchRepositoryInternal {
    Page<PurchaseOrder> search(String query, Pageable pageable);

    Page<PurchaseOrder> search(Query query);

    @Async
    void index(PurchaseOrder entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PurchaseOrderSearchRepositoryInternalImpl implements PurchaseOrderSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PurchaseOrderRepository repository;

    PurchaseOrderSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PurchaseOrderRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PurchaseOrder> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PurchaseOrder> search(Query query) {
        SearchHits<PurchaseOrder> searchHits = elasticsearchTemplate.search(query, PurchaseOrder.class);
        List<PurchaseOrder> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PurchaseOrder entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), PurchaseOrder.class);
    }
}
