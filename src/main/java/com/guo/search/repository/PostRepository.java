package com.guo.search.repository;

import com.guo.search.model.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends ElasticsearchRepository<PostDocument,Long> {

    // 符合jap命名规范的接口
}
