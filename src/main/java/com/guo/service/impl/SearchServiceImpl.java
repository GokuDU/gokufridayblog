package com.guo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.Post;
import com.guo.search.model.PostDocument;
import com.guo.search.mq.entity.PostMqIndexMessage;
import com.guo.search.repository.PostRepository;
import com.guo.service.PostService;
import com.guo.service.SearchService;
import com.guo.vo.PostVO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PostService postService;

    // 搜索
    @Override
    public IPage search(Page page, String keyword) {
        // 1. MyBatisPlus的page --》 jpa的page
        // 因为 jpa 的分页是从0开始的  所以 mybatisplus 的当前页需要 -1
        Long current = page.getCurrent() - 1;
        Long size = page.getSize();
        Pageable pageable= PageRequest.of(current.intValue(), size.intValue());

        // 2. 搜索 es 得到page
        // 我们是查询多个字段（标题，作者，分类）  multiMatchQuery 多个匹配查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery(keyword, "title", "authorName", "categoryName");
        // Page<T> search(QueryBuilder query, Pageable pageable)
        // 这样就拿到 jpa 的 pageData
        org.springframework.data.domain.Page<PostDocument> documents = postRepository.search(multiMatchQueryBuilder, pageable);

        // 3. jpa的pageData --》 MyBatisPlus的pageData
        // Page(long current, long size, long total)
        IPage searchPageData = new Page(page.getCurrent(), page.getSize(), documents.getTotalElements());
        searchPageData.setRecords(documents.getContent());

        return searchPageData;
    }

    // Es索引初始化
    @Override
    public int initEsData(List<PostVO> records) {
        if (records == null && records.isEmpty())
            return 0;

        // 批量存储 实体 PostVO ---》 实体 PostDocument
        List<PostDocument> documents = new ArrayList<>();
        for (PostVO record : records) {
            // 映射转换
            PostDocument postDocument = modelMapper.map(record, PostDocument.class);
            documents.add(postDocument);
        }
        postRepository.saveAll(documents);

        return documents.size();
    }

    // 创建或修改文章时 更新索引
    @Override
    public void createOrUpdateIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();

        PostVO postVO = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", postId));

        PostDocument postDocument = modelMapper.map(postVO, PostDocument.class);

        postRepository.save(postDocument);

        log.info("es 索引更新成功 --> "+ postDocument.toString());
    }

    // 删除文章时 删除索引
    @Override
    public void removeIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();

        postRepository.deleteById(postId);

        log.info("es 索引删除成功 --> "+ message.toString());
    }
}
