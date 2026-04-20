package com.example.fitness.data.repository;

import com.example.fitness.WorkoutSession;
import java.util.List;

/**
 * 训练会话数据仓库接口
 */
public interface WorkoutRepository {
    
    /**
     * 保存训练会话
     * @param session 训练会话
     */
    void saveSession(WorkoutSession session);
    
    /**
     * 获取所有训练历史
     * @return 训练会话列表，按时间倒序
     */
    List<WorkoutSession> getAllSessions();
    
    /**
     * 获取最近的N条训练记录
     * @param limit 数量限制
     * @return 训练会话列表
     */
    List<WorkoutSession> getRecentSessions(int limit);
    
    /**
     * 清空所有训练记录
     */
    void clearAll();
}
