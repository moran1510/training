package com.tms.training.controller;

import com.tms.training.entity.Page;
import com.tms.training.entity.TrainingCourse;
import com.tms.training.entity.User;
import com.tms.training.service.MessageService;
import com.tms.training.service.TrainingCourseService;
import com.tms.training.service.UserService;
import com.tms.training.util.CommunityConstant;
import com.tms.training.util.HostHolder;
import com.tms.training.util.RedisKeyUtil;
import com.tms.training.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    public static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private TrainingCourseService trainingCourseService;

    @GetMapping("/homepage")
    public String getHomePage(Model model, Page page,String search){
        page.setRows(trainingCourseService.findTrainingCourseRows(0));
        page.setPath("/homepage");
        User host = hostHolder.getUser();
        if (host!=null){
            int count = messageService.findNoticeUnreadCount(host.getId(), TOPIC_JOIN) + messageService.findNoticeUnreadCount(host.getId(), TOPIC_SCORE);
            model.addAttribute("noticeCount",count);
        }
        List<TrainingCourse> list = new ArrayList<>();
        if (StringUtils.isBlank(search)){
            list = trainingCourseService.findTrainingCourse(0, page.getOffset(), page.getLimit());

        }else if (StringUtils.isNumeric(search)){
            TrainingCourse course = trainingCourseService.findTrainingCourseById(Integer.parseInt(search));
            model.addAttribute("search",search);
            if (course!=null){
                list .add(course) ;
            }

        }
        else {
            list = trainingCourseService.searchTrainingCourse(0, search,page.getOffset(), page.getLimit());
            model.addAttribute("search",search);
        }
        List<Map<String,Object>> trainingCourse = new ArrayList<>();
        if (null != list){
            for (TrainingCourse course:list){
                Map<String,Object> map = new HashMap<>();
                map.put("course",course);
                trainingCourse.add(map);
                User user = userService.findUserById(course.getUserId());
                map.put("user",user);
                String fireKey = RedisKeyUtil.getFireKey(course.getId());
                long fireCount = redisTemplate.opsForSet().size(fireKey);
                map.put("fireCount",fireCount);
            }
        }
        model.addAttribute("trainingCourse",trainingCourse);
        return "homepage";
    }


    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        fileName = uploadPath + "/" + fileName;
        String substring = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+substring);
        try (
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(fileName);
        ){
            byte[] bytes = new byte[1024];
            int b = 0;
            while ((b=inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败！"+e.getMessage());
        }
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }





//    @GetMapping("/abc")
//    public String abc(){
//        return "abc";
//    }
//    @PostMapping("/testtime")
//    public String testTime(String time){
//        System.out.println(time);
//        return "abc";
//    }


}
