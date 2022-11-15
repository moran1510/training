package com.tms.training.service;

import com.tms.training.dao.TrainingCourseMapper;
import com.tms.training.entity.TrainingCourse;
import com.tms.training.util.CommunityConstant;
import com.tms.training.util.RedisKeyUtil;
import com.tms.training.util.RedisUtil;
import com.tms.training.util.SensitiveFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TrainingCourseService implements CommunityConstant {

    @Autowired
    private TrainingCourseMapper trainingCourseMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;


    public List<TrainingCourse> findTrainingCourse(int userId, int offset, int limit){
        return trainingCourseMapper.selectTrainingCourse(userId,offset,limit);
    }

    public int findTrainingCourseRows(@Param("userId") int userId) {
        return trainingCourseMapper.selectTrainingCourseRows(userId);
    }

    public int findTrainingCourseRowsWithStatus(@Param("userId") int userId,int status) {
        return trainingCourseMapper.findTrainingCourseRowsWithStatus(userId,status);
    }



    public int addDiscussPost(TrainingCourse post){
        if (post ==null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return trainingCourseMapper.insertTrainingCourse(post);
    }

    public TrainingCourse findTrainingCourseById(int id){
        return trainingCourseMapper.selectTrainingCourseById(id);
    }

    public  int updatePeopleCount(int id, int PeopleCount){
        return  trainingCourseMapper.updatePeopleCount(id,PeopleCount);
    }

    public  int updateStatus(int id, int status){
        return  trainingCourseMapper.updateStatus(id,status);
    }


    public void fire(int courseId,int userId){
        String fireKey = RedisKeyUtil.getFireKey(courseId);
        boolean isON = redisTemplate.opsForSet().isMember(fireKey,userId);
        if (!isON){
            redisTemplate.opsForSet().add(fireKey,userId);
        }
    }

    public long fireCount(int courseId){
        String fireKey = RedisKeyUtil.getFireKey(courseId);
        return redisTemplate.opsForSet().size(fireKey);
    }

    public void updateAllStatus(int userId) throws ParseException {
        List<TrainingCourse> trainingCourse = findTrainingCourse(userId, 0, -2);
        Date date = new Date();
        for (TrainingCourse course: trainingCourse) {
            Date startTime = new SimpleDateFormat("yyyy-MM-dd").parse(course.getStartTime());
            Date endTime = new SimpleDateFormat("yyyy-MM-dd").parse(course.getEndTime());
            if (date.compareTo(startTime)<0&&course.getStatus()!=COURSE_STATUS){
                updateStatus(course.getId(),0);
            }else if (date.compareTo(startTime)>=0&&date.compareTo(endTime)<=0){
                if (course.getStatus()!=COURSE_BEGIN){
                    course.setStatus(1);
                    updateStatus(course.getId(),COURSE_BEGIN);
                }
            }else if(date.compareTo(endTime)>0&&course.getStatus()!=COURSE_END){
                updateStatus(course.getId(),COURSE_END);
            }
        }

    }

    public List<TrainingCourse> selectTrainingCourseWithStatus(int userId, int status,int offset, int limit){
        return trainingCourseMapper.selectTrainingCourseWithStatus(userId,status,offset,limit);
    }


    public List<TrainingCourse> searchTrainingCourse(int userId, String search, int offset, int limit) {
        search="%"+search+"%";
        return trainingCourseMapper.searchTrainingCourse(userId, search,offset,limit);
    }
}
