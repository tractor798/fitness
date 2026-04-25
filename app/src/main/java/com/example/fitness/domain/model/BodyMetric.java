package com.example.fitness.domain.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BodyMetric implements Serializable {
    private String date;
    private long timestamp;
    private double weight; // 体重（kg）
    private double bodyFat; // 体脂率（%）
    private double muscleMass; // 肌肉质量（kg）
    private double boneMass; // 骨量（kg）
    private double water; // 水分（%）
    private double visceralFat; // 内脏脂肪
    private double bmi; // BMI值
    private String notes; // 备注

    public BodyMetric() {
        this.timestamp = System.currentTimeMillis();
        // 使用时间戳作为唯一标识，避免同一天多条记录被覆盖
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timestamp));
    }

    public BodyMetric(double weight) {
        this();
        this.weight = weight;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        // 保持与构造函数一致的格式
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timestamp));
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        calculateBMI();
    }

    public double getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(double bodyFat) {
        this.bodyFat = bodyFat;
    }

    public double getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(double muscleMass) {
        this.muscleMass = muscleMass;
    }

    public double getBoneMass() {
        return boneMass;
    }

    public void setBoneMass(double boneMass) {
        this.boneMass = boneMass;
    }

    public double getWater() {
        return water;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public double getVisceralFat() {
        return visceralFat;
    }

    public void setVisceralFat(double visceralFat) {
        this.visceralFat = visceralFat;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // 计算BMI（需要身高信息，这里假设身高通过其他方式获取）
    public void calculateBMI(double heightInM) {
        if (heightInM > 0) {
            this.bmi = weight / (heightInM * heightInM);
        }
    }

    // 快速计算BMI的重载方法（不需要身高，仅用于保存已有的BMI）
    private void calculateBMI() {
        // 如果BMI已设置则不修改
    }

    public String getFormattedDate() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(this.date);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return this.date;
        }
    }

    public String getWeightDisplay() {
        return String.format("%.1f kg", weight);
    }

    public String getBodyFatDisplay() {
        return String.format("%.1f%%", bodyFat);
    }

    public String getBmiDisplay() {
        return String.format("%.1f", bmi);
    }

    public String getMuscleMassDisplay() {
        return String.format("%.1f kg", muscleMass);
    }
}
