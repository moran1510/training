package com.tms.training.dao;

import com.tms.training.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserCourseMapper {
    List<UserCourse> findUserCourseByUserId(int userId,int offset,int limit);

    List<UserCourse> findUserCourseByCourseId(int courseId,int offset,int limit);

    List<UserCourse> findUserCourse(int userId,int courseId);

    int DoingCourseCount(int userId);

    int AllCourseCount(int userId);

    int insertUserCourse(int userId, int courseId, Date joinTime);

    int updateScore(int userId,int courseId,int score,String evaluate);

    int deleteCourse(int userId,int courseId);
}
