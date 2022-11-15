package com.tms.training.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TrainingCourse {

    private int id;

    private int userId;
    // 互联网校招
    private String title;

    private String content;

    private int type;

    private int status;

    private Date createTime;

    private String startTime;

    private String endTime;

    private int peopleCount;

    private double score;
}
