package com.tms.training.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
public class DiscussPost {
    private int id;
    private int userId;
    private int courseId;
    private String content;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
