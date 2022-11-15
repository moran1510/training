package com.tms.training;



import com.alibaba.fastjson.JSONObject;
import com.tms.training.dao.DiscussPostMapper;
import com.tms.training.dao.TrainingCourseMapper;
import com.tms.training.dao.UserMapper;
import com.tms.training.entity.DiscussPost;
import com.tms.training.entity.Message;
import com.tms.training.entity.TrainingCourse;
import com.tms.training.entity.User;
import com.tms.training.service.MessageService;
import com.tms.training.service.TrainingCourseService;
import com.tms.training.service.UserCourseService;
import com.tms.training.util.CommnityUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootTest
class TrainingApplicationTests {
    @Autowired
    private TrainingCourseMapper trainingCourseMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test(){
        System.out.println(trainingCourseMapper.selectTrainingCourseRows(0));
        System.out.println(userMapper.selectByName("s"));
    }

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private TrainingCourseService trainingCourseService;
    @Test
    void getName(){
       // userCourseService.findUserCourseByCourseId(1);
        userCourseService.updateScore(176,302,100,"123");
//        trainingCourseMapper.selectTrainingCourseRows(100);
//        TrainingCourse course = new TrainingCourse();
//        course.setTitle("  123");
//        course.setUserId(0);
//        course.setCreateTime(new Date());
//        course.setContent("123");
//        trainingCourseService.addDiscussPost(course);
    }


    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private MessageService messageService;
    @Test
    void testMain() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("2021年2月2号");
        System.out.println(simpleDateFormat);
//        Message message = new Message();
//        message.setFromId(1);
//        message.setToId(1);
//        message.setConversationId("String.valueOf(1)");
//        message.setCreateTime(new Date());
//
//        Map<String, Object> content = new HashMap<>();
//        content.put("userId",1);
//        content.put("entityId", 1);
//
//
//        message.setContent(JSONObject.toJSONString(content));
//        messageService.addMessage(message);
//        System.out.println(discussPostMapper.selectDiscussPosts(300, 5, 5));
//        DiscussPost a = new DiscussPost();
//        a.setContent("想摆烂！！！！！");
//        a.setStatus(0);
//        a.setUserId(1);
//        a.setCourseId(300);
//        a.setCreateTime(new Date());
//        System.out.println(discussPostMapper.selectDiscussPostRows(300));
//        discussPostMapper.insertDiscussPost(a);
//        System.out.println(discussPostMapper.selectDiscussPostRows(300));
//        System.out.println(discussPostMapper.selectDiscussPostById(300, 1));
//        discussPostMapper.updateStatus(300,1,1);


    }

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() {
        kafkaProducer.sendMessage("join", "你好");
        kafkaProducer.sendMessage("test", "在吗");

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

@Component
class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }

}

@Component
class KafkaConsumer {

    @KafkaListener(topics = {"join"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }





}
