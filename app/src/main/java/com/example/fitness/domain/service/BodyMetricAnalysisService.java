package com.example.fitness.domain.service;

import com.example.fitness.BodyMetric;
import com.example.fitness.data.repository.BodyMetricRepository;
import java.util.List;

/**
 * 身体指标分析服务
 * 负责计算身体指标相关的统计数据
 */
public class BodyMetricAnalysisService {
    
    private final BodyMetricRepository repository;
    
    public BodyMetricAnalysisService(BodyMetricRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 获取体重变化（最新 - 最旧）
     */
    public double getWeightChange() {
        List<BodyMetric> metrics = repository.getAllMetrics();
        if (metrics.size() >= 2) {
            BodyMetric latest = metrics.get(0);
            BodyMetric oldest = metrics.get(metrics.size() - 1);
            return latest.getWeight() - oldest.getWeight();
        } else if (!metrics.isEmpty()) {
            return 0;
        }
        return 0;
    }
    
    /**
     * 获取指定天数内的体重变化
     */
    public double getWeightChangeInDays(int days) {
        List<BodyMetric> metrics = repository.getMetricsForDays(days);
        if (!metrics.isEmpty()) {
            BodyMetric latest = metrics.get(0);
            BodyMetric oldest = metrics.get(metrics.size() - 1);
            return latest.getWeight() - oldest.getWeight();
        }
        return 0;
    }
    
    /**
     * 获取指定天数内的平均体重
     */
    public double getAverageWeight(int days) {
        List<BodyMetric> metrics = repository.getMetricsForDays(days);
        if (metrics.isEmpty()) {
            return 0;
        }
        
        double sum = 0;
        for (BodyMetric metric : metrics) {
            sum += metric.getWeight();
        }
        return sum / metrics.size();
    }
    
    /**
     * 获取BMI变化趋势
     * @param days 天数
     * @return BMI变化值
     */
    public double getBmiChangeInDays(int days) {
        List<BodyMetric> metrics = repository.getMetricsForDays(days);
        if (metrics.size() >= 2) {
            BodyMetric latest = metrics.get(0);
            BodyMetric oldest = metrics.get(metrics.size() - 1);
            return latest.getBmi() - oldest.getBmi();
        }
        return 0;
    }
}
