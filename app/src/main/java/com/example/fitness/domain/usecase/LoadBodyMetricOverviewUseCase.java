package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.BodyMetricRepository;
import com.example.fitness.domain.service.BodyMetricAnalysisService;
import com.example.fitness.domain.model.BodyMetric;

/**
 * 加载身体指标概览数据的UseCase
 */
public class LoadBodyMetricOverviewUseCase {
    
    private final BodyMetricRepository repository;
    private final BodyMetricAnalysisService analysisService;
    
    public LoadBodyMetricOverviewUseCase(BodyMetricRepository repository, BodyMetricAnalysisService analysisService) {
        this.repository = repository;
        this.analysisService = analysisService;
    }
    
    /**
     * 执行用例，获取最新的身体指标及变化趋势
     */
    public OverviewResult execute(int trendDays) {
        BodyMetric latest = repository.getLatestMetric();
        
        if (latest == null) {
            return new OverviewResult(null, 0.0);
        }
        
        double weightChange = analysisService.getWeightChangeInDays(trendDays);
        return new OverviewResult(latest, weightChange);
    }
    
    /**
     * 概览结果
     */
    public static class OverviewResult {
        public final BodyMetric latestMetric;
        public final double weightChange;
        
        public OverviewResult(BodyMetric latestMetric, double weightChange) {
            this.latestMetric = latestMetric;
            this.weightChange = weightChange;
        }
    }
}