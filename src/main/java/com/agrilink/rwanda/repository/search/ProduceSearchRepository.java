package com.agrilink.rwanda.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.agrilink.rwanda.domain.Produce;
import com.agrilink.rwanda.repository.ProduceRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Produce} entity.
 */
public interface ProduceSearchRepository extends ElasticsearchRepository<Produce, Long>, ProduceSearchRepositoryInternal {}

interface ProduceSearchRepositoryInternal {
    Stream<Produce> search(String query);

    Stream<Produce> search(Query query);

    @Async
    void index(Produce entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ProduceSearchRepositoryInternalImpl implements ProduceSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProduceRepository repository;

    ProduceSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ProduceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Produce> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Produce> search(Query query) {
        return elasticsearchTemplate.search(query, Produce.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Produce entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Produce.class);
    }
}
