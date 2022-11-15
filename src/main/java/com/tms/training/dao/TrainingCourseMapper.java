package com.tms.training.dao;

import com.tms.training.entity.TrainingCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TrainingCourseMapper {
    List<TrainingCourse> selectTrainingCourse(int userId, int offset, int limit);

    List<TrainingCourse> selectTrainingCourseWithStatus(int userId, int status,int offset, int limit);

    int selectTrainingCourseRows(@Param("userId") int userId);

    int findTrainingCourseRowsWithStatus(@Param("userId") int userId,int status);

    int insertTrainingCourse(TrainingCourse trainingCourse);

    TrainingCourse selectTrainingCourseById(int id);

    int updatePeopleCount(int id, int peopleCount);

    int updateStatus(int id, int status);

    List<TrainingCourse> searchTrainingCourse(int userId, String search, int offset, int limit);
}
