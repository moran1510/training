package com.tms.training.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserCourse {
    private int id;
    private int UserId;
    private Date joinTime;
    private int CourseId;
    private int score;
    private String evaluate;

}
