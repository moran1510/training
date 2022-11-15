package com.tms.training.service;

import com.tms.training.dao.UserCourseMapper;
import com.tms.training.entity.UserCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserCourseService {
    @Autowired
    private UserCourseMapper userCourseMapper;

    public List<UserCourse> findUserCourseByUserId(int userId,int offset,int limit) {
        return userCourseMapper.findUserCourseByUserId(userId,offset,limit);
    }

    public List<UserCourse> findUserCourseByCourseId(int courseId,int offset,int limit) {
        return userCourseMapper.findUserCourseByCourseId(courseId,offset,limit);
    }

    public List<UserCourse> findUserCourse(int userId, int courseId) {
        return userCourseMapper.findUserCourse(userId,courseId);
    }

    public int insertUserCourse(int userId, int courseId, Date joinTime) {
        return userCourseMapper.insertUserCourse(userId,courseId,joinTime);
    }

    public int updateScore(int userId, int courseId, int score,String evaluate) {
        return userCourseMapper.updateScore(userId,courseId,score,evaluate);
    }

    public int deleteCourse(int userId, int courseId){
        return userCourseMapper.deleteCourse(userId, courseId);
    }

    public int DoingCourseCount(int userId) {
        return userCourseMapper.DoingCourseCount(userId);
    }

    public int AllCourseCount(int userId) {
        return  userCourseMapper.AllCourseCount(userId);
    }

}
