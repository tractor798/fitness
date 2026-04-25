package com.example.fitness.domain.usecase;

import com.example.fitness.domain.repository.BodyMetricRepository;
import com.example.fitness.domain.model.BodyMetric;
import com.example.fitness.config.AppConfig;

/**
 * 保存身体指标记录的UseCase
 */
public class SaveBodyMetricUseCase {
    
    private final BodyMetricRepository repository;
    
    public SaveBodyMetricUseCase(BodyMetricRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 执行用例，保存新的身体指标记录
     * @param weight 体重 (kg)
     * @param bodyFat 体脂率 (%)
     * @param muscleMass 肌肉量 (kg)
     * @param bmi BMI值
     * @param notes 备注
     * @param userHeight 用户身高 (cm)，用于自动计算BMI（如果bmi为空）
     * @return 是否保存成功
     */
    public boolean execute(double weight, Double bodyFat, Double muscleMass, Double bmi, String notes, double userHeight) {
        try {
            BodyMetric metric = new BodyMetric();
            metric.setWeight(weight);
            
            if (bodyFat != null) {
                metric.setBodyFat(bodyFat);
            }
            
            if (muscleMass != null) {
                metric.setMuscleMass(muscleMass);
            }
            
            if (bmi != null && bmi > 0) {
                metric.setBmi(bmi);
            } else {
                // 自动计算BMI
                double heightInMeters = (userHeight > 0 ? userHeight : AppConfig.DEFAULT_HEIGHT_FOR_BMI) / 100.0;
                if (heightInMeters > 0) {
                    metric.calculateBMI(heightInMeters * 100); // calculateBMI内部会转换单位
                }
            }
            
            if (notes != null) {
                metric.setNotes(notes);
            }
            
            repository.saveMetric(metric);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}