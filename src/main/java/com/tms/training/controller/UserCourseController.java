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
import java.util.*;

@Controller
public class UserCourseController implements CommunityConstant {
    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private TrainingCourseService trainingCourseService;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/joinCourse", method = RequestMethod.POST)
    @ResponseBody
    public String joinCourse(int courseId) {
        User user = hostHolder.getUser();

        userCourseService.insertUserCourse(user.getId(),courseId,new Date());
        TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(courseId);
        trainingCourseService.updatePeopleCount(courseId,trainingCourse.getPeopleCount()+1);


        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_JOIN)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(courseId)
                .setEntityUserId(trainingCourse.getUserId())
                .setData("title", trainingCourse.getTitle());

        eventProducer.fireEvent(event);

        return CommnityUtil.getJSONString(0, "报名成功!");
    }

    @RequestMapping(path = "/cancelCourse", method = RequestMethod.POST)
    @ResponseBody
    public String cancelCourse(int courseId) {
        User user = hostHolder.getUser();

        userCourseService.deleteCourse(user.getId(),courseId);
        TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(courseId);
        trainingCourseService.updatePeopleCount(courseId,trainingCourse.getPeopleCount()-1);

        return CommnityUtil.getJSONString(0, "已取消课程!");
    }

    @GetMapping("/course/myJoinCourse")
    public String myJoinCourse(Model model, Page page) throws ParseException {
        User user = hostHolder.getUser();
        page.setRows(userCourseService.DoingCourseCount(user.getId()));
        page.setPath("/course/myJoinCourse");
        List<UserCourse> userCourse = userCourseService.findUserCourseByUserId(user.getId(), page.getOffset(), page.getLimit());
        List<TrainingCourse> list = new ArrayList<>();
        for (UserCourse course:userCourse) {
            trainingCourseService.updateAllStatus(trainingCourseService.findTrainingCourseById(course.getCourseId()).getUserId());
            TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(course.getCourseId());
            if (trainingCourse.getStatus()==1){
                list.add(trainingCourse);
            }
        }
        model.addAttribute("list",list);
        return "site/my-joinCourse";
    }

    @GetMapping("/course/myAllCourse")
    public String myAllCourse(Model model, Page page) throws ParseException {
        User user = hostHolder.getUser();
        page.setRows(userCourseService.AllCourseCount(user.getId()));
        page.setPath("/course/myallCourse");
        List<UserCourse> userCourse = userCourseService.findUserCourseByUserId(user.getId(), page.getOffset(), page.getLimit());
        List<Object> list = new ArrayList<>();
        for (UserCourse course:userCourse) {
            trainingCourseService.updateAllStatus(trainingCourseService.findTrainingCourseById(course.getCourseId()).getUserId());
            TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(course.getCourseId());
                HashMap<String,Object> map = new HashMap<>();
                map.put("trainingCourse",trainingCourse);
                map.put("score",course.getScore());
                map.put("evaluate",course.getEvaluate());
                map.put("userId",course.getUserId());
                list.add(map);
        }
        model.addAttribute("list",list);

        return "site/my-allCourse";
    }

    @GetMapping("/course/student/{courseId}")
    public String myCourse(@PathVariable("courseId") String courseId,Model model){
        TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(Integer.parseInt(courseId));
        model.addAttribute("course",trainingCourse);
        User user = userService.findUserById(trainingCourse.getUserId());
        model.addAttribute("user",user);

        User host = hostHolder.getUser();
        boolean hasJoin;
        if (host==null){
            hasJoin = false;
            model.addAttribute("hasJoin",hasJoin);
        }else {
            trainingCourseService.fire(Integer.parseInt(courseId),host.getId());
            if(userCourseService.findUserCourse(host.getId(), Integer.parseInt(courseId)).size()!=0){
                model.addAttribute("hasJoin",true);
            }else {
                model.addAttribute("hasJoin",false);
            }
        }
        long count = trainingCourseService.fireCount(Integer.parseInt(courseId));
        model.addAttribute("fire",count);
        List<UserCourse> userCourse = userCourseService.findUserCourse(host.getId(), Integer.parseInt(courseId));
        int score = userCourse.get(0).getScore();
        model.addAttribute("score",score);
        model.addAttribute("evaluate",userCourse.get(0).getEvaluate());

        return "site/my-course";


    }

    //课程评价
    @PostMapping("/course/courseDiscuss")
    @ResponseBody
    public String addCourseDiscuss(String courseId,String discuss){
        if (!StringUtils.isNumeric(courseId)){
            return CommnityUtil.getJSONString(403,"课程不存在");
        }
        User user = hostHolder.getUser();
        DiscussPost post = new DiscussPost();
        post.setContent(discuss);
        post.setUserId(user.getId());
        post.setCreateTime(new Date());
        post.setCourseId(Integer.parseInt(courseId));
        discussPostService.addDiscussPost(post);
        return CommnityUtil.getJSONString(0,"评价成功");
    }
}
