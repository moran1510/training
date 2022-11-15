package com.tms.training.dao;

import com.tms.training.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int courseId, int offset, int limit);

    int selectDiscussPostRows(@Param("courseId") int courseId);

    int insertDiscussPost(DiscussPost discussPost);

    List<DiscussPost> selectDiscussPostById(int courseId,int userId);

    int updateStatus(int courseId,int userId,int status);

}
