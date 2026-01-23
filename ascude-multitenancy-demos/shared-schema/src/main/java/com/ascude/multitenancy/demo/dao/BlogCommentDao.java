package com.ascude.multitenancy.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ascude.multitenancy.demo.entity.BlogComment;

import java.util.List;
import java.util.Map;


public interface BlogCommentDao extends BaseMapper<BlogComment> {

    /**
     * 查询文章评论 手动分页
     */
    List<BlogComment> selectArticleComments(Map<String, Object> map);

    Integer selectArticleCommentsCount(Map<String, Object> map);

    List<BlogComment> selectArticleCommentsByPlus(Map<String, Object> map, IPage<BlogComment> page);

    List<BlogComment> getCommentByReplyId(Long replyId);

}
