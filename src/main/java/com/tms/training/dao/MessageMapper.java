package com.tms.training.dao;

import com.tms.training.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface MessageMapper {
    int insertMessage(Message message);
    // 查询某个主题下最新的通知
    Message selectLatestNotice(int userId, String topic);

    // 查询某个主题所包含的通知数量
    int selectNoticeCount(int userId, String topic);

    // 查询未读的通知的数量
    int selectNoticeUnreadCount(int userId, String topic);

    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

    int updateStatus(List<Integer> ids, int status);

    int selectLetterUnreadCount(int userId, String conversationId);
}
