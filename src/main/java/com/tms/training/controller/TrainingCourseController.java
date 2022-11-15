package com.tms.training.controller;

import com.tms.training.entity.DiscussPost;
import com.tms.training.entity.Page;
import com.tms.training.entity.TrainingCourse;
import com.tms.training.entity.User;
import com.tms.training.service.DiscussPostService;
import com.tms.training.service.TrainingCourseService;
import com.tms.training.service.UserCourseService;
import com.tms.training.service.UserService;
import com.tms.training.util.CommnityUtil;
import com.tms.training.util.CommunityConstant;
import com.tms.training.util.HostHolder;
import com.tms.training.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/course")
public class TrainingCourseController implements CommunityConstant {

    @Autowired
    private TrainingCourseService trainingCourseService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @PostMapping("/add")
    @ResponseBody
    public String addTrainingCourse(String title,String content,String startTime,String endTime){
        User user = hostHolder.getUser();
        if (user == null){
            return CommnityUtil.getJSONString(403,"你还没有登入！");
        }
        if (StringUtils.isBlank(title)){
            return CommnityUtil.getJSONString(403,"标题不能为空！");
        }
        if (StringUtils.isBlank(content)){
            return CommnityUtil.getJSONString(403,"内容不能为空！");
        }
        if (StringUtils.isBlank(startTime)){
            return CommnityUtil.getJSONString(403,"开始时间不能为空！");
        }
        if (StringUtils.isBlank(endTime)){
            return CommnityUtil.getJSONString(403,"结束不能为空！");
        }

        LocalDate startDate = LocalDate.parse(startTime);
        LocalDate endDate = LocalDate.parse(endTime);
        if (startDate.compareTo(endDate)>0){
            return  CommnityUtil.getJSONString(406,"结束日期不能早于开始日期！");
        }

        TrainingCourse trainingCourse = new TrainingCourse();
        trainingCourse.setUserId(user.getId());
        trainingCourse.setTitle(title);
        trainingCourse.setContent(content);
        trainingCourse.setCreateTime(new Date());
        trainingCourse.setPeopleCount(0);
        trainingCourse.setStartTime(startTime.replace("-",""));
        trainingCourse.setEndTime(endTime.replace("-",""));
        trainingCourseService.addDiscussPost(trainingCourse);

        return  CommnityUtil.getJSONString(0,"发布成功！");
    }

    @GetMapping("/courseDetail/{courseId}")
    public String getTrainingCourse(@PathVariable("courseId")int courseId, Model model, Page page){
        TrainingCourse trainingCourse = trainingCourseService.findTrainingCourseById(courseId);
        model.addAttribute("course",trainingCourse);
        User user = userService.findUserById(trainingCourse.getUserId());
        model.addAttribute("user",user);

        User host = hostHolder.getUser();
        boolean hasJoin;
        if (host==null){
            hasJoin = false;
        }else {
            trainingCourseService.fire(courseId,host.getId());
            if(userCourseService.findUserCourse(host.getId(),courseId).size()!=0){
                model.addAttribute("hasJoin",true);
            }else {
                model.addAttribute("hasJoin",false);
            }
        }
        long count = trainingCourseService.fireCount(courseId);
        model.addAttribute("fire",count);
        int rows = discussPostService.findDiscussPostRows(courseId);
        page.setRows(rows);
        page.setPath("/course/courseDetail/"+courseId);

        model.addAttribute("rows",rows);

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
        System.out.println("    1234     ");
        return "site/course-detail";
    }



}
