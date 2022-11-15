package com.tms.training.controller;

import com.tms.training.entity.*;
import com.tms.training.event.EventProducer;
import com.tms.training.service.DiscussPostService;
import com.tms.training.service.TrainingCourseService;
import com.tms.training.service.UserCourseService;
import com.tms.training.service.UserService;
import com.tms.training.util.CommnityUtil;
import com.tms.training.util.CommunityConstant;
import com.tms.training.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class TeacherController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private TrainingCourseService trainingCourseService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @GetMapping("/MyCreate")
    public String getMyCreate(Model model, Page page) throws ParseException {
        User user = hostHolder.getUser();
        if (user==null){
            return "homepage";
        }
        int rows = trainingCourseService.findTrainingCourseRowsWithStatus(user.getId(),COURSE_BEGIN);
        page.setRows(rows);
        page.setPath("/teacher/MyCreate");
        trainingCourseService.updateAllStatus(user.getId());
        List<TrainingCourse> trainingCourses = trainingCourseService.selectTrainingCourseWithStatus(user.getId(), COURSE_BEGIN, page.getOffset(), page.getLimit());

        model.addAttribute("trainingCourse",trainingCourses);
        return "site/my-create";
    }
    @GetMapping("/AllCreate")
    public String getALLCreate(Model model, Page page){
        User user = hostHolder.getUser();
        int rows = trainingCourseService.findTrainingCourseRows(user.getId());
        page.setRows(rows);
        page.setPath("/teacher/MyCreate");
        List<TrainingCourse> trainingCourse = trainingCourseService.findTrainingCourse(user.getId(), page.getOffset(), page.getLimit());
        model.addAttribute("trainingCourse",trainingCourse);
        return "site/all-create";
    }

    @GetMapping("/courseDetail/{courseId}")
    public String teacherCourse(@PathVariable("courseId") int courseId, Model model,Page page){
        TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(courseId);
        model.addAttribute("course",trainingCourse);
        User user = userService.findUserById(trainingCourse.getUserId());
        model.addAttribute("user",user);

        long count = trainingCourseService.fireCount(courseId);
        model.addAttribute("fire",count);
        int rows = trainingCourse.getPeopleCount();
        page.setRows(rows);
        page.setPath("/teacher/courseDetail/"+courseId);
        int discussCount = discussPostService.findDiscussPostRows(courseId);
        model.addAttribute("discussCount",discussCount);
        List<UserCourse> userCourses = userCourseService.findUserCourseByCourseId(courseId,page.getOffset(), page.getLimit());
        List<Object> list = new ArrayList<>();
        for (UserCourse a:userCourses) {
            HashMap<String,Object> hashMap = new HashMap<>();
            User b = userService.findUserById(a.getUserId());
            hashMap.put("user",b);
            hashMap.put("userCourse",a);
            list.add(hashMap);
        }
        model.addAttribute("list",list);

        return "site/teacher-course";
    }

    @GetMapping("/discussPost/{courseId}")
    public String getTrainingCourse(@PathVariable("courseId")int courseId, Model model, Page page){
        TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(courseId);
        model.addAttribute("course",trainingCourse);
        User user = userService.findUserById(trainingCourse.getUserId());
        model.addAttribute("user",user);

        long count = trainingCourseService.fireCount(courseId);

        model.addAttribute("fire",count);
        int rows = discussPostService.findDiscussPostRows(courseId);
        model.addAttribute("discussCount",rows);
        page.setRows(rows);
        page.setPath("/teacher/discussPost/"+courseId);

        List<DiscussPost> list = discussPostService.findDiscussPosts(courseId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPost = new ArrayList<>();
        if (null != list){
            for (DiscussPost post:list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User postUser = userService.findUserById(post.getUserId());
                map.put("user",postUser);
                discussPost.add(map);
            }
        }
        model.addAttribute("discussPost",discussPost);

        return "site/teacher-discuss";
    }

    @PostMapping("/evaluate")
    @ResponseBody
    public String addEvaluate(String courseId,String userId, String score,String evaluate){
        User user = hostHolder.getUser();
        if (user == null){
            return CommnityUtil.getJSONString(403,"你还没有登入！");
        }
        if (StringUtils.isBlank(score)){
            return CommnityUtil.getJSONString(403,"分数不能为空！");
        }
        if (!StringUtils.isNumeric(score)){
            return CommnityUtil.getJSONString(403,"请输入0-100的整数");
        }

        userCourseService.updateScore(Integer.parseInt(userId),Integer.parseInt(courseId), Integer.parseInt(score),evaluate);

        TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(Integer.parseInt(courseId));
        Event event = new Event()
                .setTopic(TOPIC_SCORE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(Integer.parseInt(courseId))
                .setEntityUserId(Integer.parseInt(userId))
                .setData("title", trainingCourse.getTitle());

        eventProducer.fireEvent(event);

        return CommnityUtil.getJSONString(0,"评分成功");
    }

}
