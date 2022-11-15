package com.tms.training.service;

import com.tms.training.dao.DiscussPostMapper;
import com.tms.training.entity.DiscussPost;
import com.tms.training.util.SensitiveFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public  List<DiscussPost> findDiscussPosts(int courseId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(courseId,offset,limit);
    }

    public int findDiscussPostRows(@Param("courseId") int courseId) {
        return discussPostMapper.selectDiscussPostRows(courseId);
    }

    public int addDiscussPost(DiscussPost post){
        if (post ==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public List<DiscussPost> findDiscussPostById(int courseId,int userId){
        return discussPostMapper.selectDiscussPostById(courseId,userId);
    }

    public  int updateStatus(int courseId,int userId,int status){
        return  discussPostMapper.updateStatus(courseId,userId,status);
    }

}
