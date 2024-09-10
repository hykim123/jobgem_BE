package com.sist.jobgem.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sist.jobgem.dto.PostCountApplyDto;
import com.sist.jobgem.entity.Post;
import com.sist.jobgem.entity.QApplyment;
import com.sist.jobgem.entity.QPost;

import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostCountApplyDto> findByFilterWithApplyCount(Map<String, Object> map) {
        QPost post = QPost.post;
        QApplyment applyment = QApplyment.applyment;
        BooleanBuilder builder = new BooleanBuilder();
        if (map.get("coIdx") != null) {
            builder.and(post.coIdx.eq(Integer.parseInt(map.get("coIdx").toString())));
        }
        if (map.get("deadline") != null) {
            builder.and(post.poDeadline.eq(LocalDate.now()));
        }
        if (map.get("state") != null) {
            System.err.println("poState: " + map.get("state"));
            builder.and(post.poState.eq(Integer.parseInt(map.get("state").toString())));
        }
        if (map.get("searchType") != null) {
            if (map.get("searchType").toString().equals("title")) {
                builder.and(post.poTitle.contains(map.get("searchWord").toString()));
            } else if (map.get("searchType").toString().equals("content")) {
                builder.and(post.poContent.contains(map.get("searchWord").toString()));
            }
        }
        return queryFactory
                .select(Projections.constructor(PostCountApplyDto.class,
                        post.id, post.coIdx, post.poTitle, post.poContent, post.poDate, post.poDeadline,
                        post.poImgurl, post.poSal, post.poWorkhour, post.poSubType, post.poAddr,
                        post.poEmail, post.poFax, post.poState,
                        applyment.count().intValue().as("applyCount")))
                .from(post)
                .leftJoin(applyment).on(post.id.eq(applyment.poIdx))

                .where(builder)
                .groupBy(post.id, post.coIdx, post.poTitle, post.poContent, post.poDate, post.poDeadline,
                        post.poImgurl, post.poSal, post.poWorkhour, post.poSubType, post.poAddr,
                        post.poEmail, post.poFax, post.poState)
                .fetch();
    }
}
