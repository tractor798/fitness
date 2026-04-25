package com.example.fitness.domain.repository;

import com.example.fitness.domain.model.BodyMetric;
import java.util.List;

/**
 * 身体指标数据仓库接口
 */
public interface BodyMetricRepository {
    
    /**
     * 保存身体指标
     * @param metric 身体指标数据
     */
    void saveMetric(BodyMetric metric);
    
    /**
     * 获取所有身体指标
     * @return 指标列表，按时间倒序
     */
    List<BodyMetric> getAllMetrics();
    
    /**
     * 获取最新的身体指标
     * @return 最新指标，不存在返回null
     */
    BodyMetric getLatestMetric();
    
    /**
     * 根据日期获取指标
     * @param date 日期字符串
     * @return 指标对象
     */
    BodyMetric getMetricByDate(String date);
    
    /**
     * 删除指定日期的指标
     * @param date 日期字符串
     */
    void deleteMetric(String date);
    
    /**
     * 获取最近N天的指标
     * @param days 天数
     * @return 指标列表
     */
    List<BodyMetric> getMetricsForDays(int days);
    
    /**
     * 清空所有指标
     */
    void clearAll();
}
