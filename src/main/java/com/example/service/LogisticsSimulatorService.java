package com.example.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 物流信息模拟服务
 * <p>
 * 该服务用于生成模拟的物流跟踪数据，以供前端展示。
 * 它不依赖于任何真实的物流查询接口。
 * </p>
 */
@Service
public class LogisticsSimulatorService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 地理位置内部类，包含名称和经纬度
     */
    @Data
    @AllArgsConstructor
    private static class GeoLocation {
        private String name;
        private double longitude;
        private double latitude;
    }

    // 预设一些带经纬度的城市作为物流节点
    private static final GeoLocation[] GEO_LOCATIONS = {
            new GeoLocation("北京中转中心", 116.4074, 39.9042),
            new GeoLocation("西安分拨中心", 108.9402, 34.3416),
            new GeoLocation("成都集散点", 104.0668, 30.5728),
            new GeoLocation("深圳宝安区网点", 113.8831, 22.5833)
    };

    /**
     * 物流跟踪步骤内部类
     */
    @Data
    @AllArgsConstructor
    private static class TrackingStep {
        private String time;
        private String context;
        private double longitude;
        private double latitude;
    }

    /**
     * 生成模拟的物流跟踪数据
     * @param companyName 物流公司名称 (当前实现中未使用)
     * @return JSON格式的物流跟踪步骤字符串
     */
    public String generateFakeTrackingData(String companyName) {
        List<TrackingStep> steps = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 定义物流路径上的几个关键节点
        GeoLocation startPoint = GEO_LOCATIONS[0];
        GeoLocation midPoint1 = GEO_LOCATIONS[1];
        GeoLocation midPoint2 = GEO_LOCATIONS[2];
        GeoLocation endPoint = GEO_LOCATIONS[3];

        // 逆向生成物流步骤，从签收开始往前推
        steps.add(new TrackingStep(now.format(formatter), "【" + endPoint.getName() + "】您的快件已被签收。", endPoint.getLongitude(), endPoint.getLatitude()));
        steps.add(new TrackingStep(now.minusHours(3).format(formatter), "【" + endPoint.getName() + "】派件员正在为您派送。", endPoint.getLongitude(), endPoint.getLatitude()));
        steps.add(new TrackingStep(now.minusHours(15).format(formatter), "快件已到达【" + midPoint2.getName() + "】。", midPoint2.getLongitude(), midPoint2.getLatitude()));
        steps.add(new TrackingStep(now.minusDays(1).format(formatter), "快件离开【" + midPoint1.getName() + "】，下一站【" + midPoint2.getName() + "】。", midPoint1.getLongitude(), midPoint1.getLatitude()));
        steps.add(new TrackingStep(now.minusDays(2).format(formatter), "【" + startPoint.getName() + "】的收件员已揽收快件。", startPoint.getLongitude(), startPoint.getLatitude()));

        // 返回给前端时，通常希望按时间顺序（从旧到新），所以反转列表
        Collections.reverse(steps);

        try {
            // 将步骤列表序列化为JSON字符串
            return new ObjectMapper().writeValueAsString(steps);
        } catch (Exception e) {
            // 序列化失败时返回空JSON数组
            return "[]";
        }
    }
}
