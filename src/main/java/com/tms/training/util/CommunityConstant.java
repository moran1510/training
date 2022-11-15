package com.tms.training.util;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS=0;
    int ACTIVATION_REPEAT=1;
    int ACTIVATION_FAIL=2;

    int DEFAULT_EXPIRED_SECONDS = 3600*12;
    int REMEMBER_EXOIRED_SECONDS = 3600*24*100;
    int ENTITY_TYPE_POST = 1;
    int ENTITY_TYPE_COMMENT = 2;
    int ENTITY_TYPE_USER = 3;
    int COURSE_STATUS = 0;
    int COURSE_BEGIN = 1;
    int COURSE_END = 2;
    int COURSE_LOSER = 3;

    /**
     * 主题: 打分
     */
    String TOPIC_SCORE = "score";

    /**
     * 主题: 加入课程
     */
    String TOPIC_JOIN = "join";



    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;

}
