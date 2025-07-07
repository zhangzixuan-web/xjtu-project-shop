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

@Service
public class LogisticsSimulatorService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    @Data
    @AllArgsConstructor
    private static class TrackingStep {
        private String time;
        private String context;
        private double longitude;
        private double latitude;
    }

    public String generateFakeTrackingData(String companyName) {
        List<TrackingStep> steps = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        GeoLocation startPoint = GEO_LOCATIONS[0];
        GeoLocation midPoint1 = GEO_LOCATIONS[1];
        GeoLocation midPoint2 = GEO_LOCATIONS[2];
        GeoLocation endPoint = GEO_LOCATIONS[3];

        steps.add(new TrackingStep(now.format(formatter), "【" + endPoint.getName() + "】您的快件已被签收。", endPoint.getLongitude(), endPoint.getLatitude()));
        steps.add(new TrackingStep(now.minusHours(3).format(formatter), "【" + endPoint.getName() + "】派件员正在为您派送。", endPoint.getLongitude(), endPoint.getLatitude()));
        steps.add(new TrackingStep(now.minusHours(15).format(formatter), "快件已到达【" + midPoint2.getName() + "】。", midPoint2.getLongitude(), midPoint2.getLatitude()));
        steps.add(new TrackingStep(now.minusDays(1).format(formatter), "快件离开【" + midPoint1.getName() + "】，下一站【" + midPoint2.getName() + "】。", midPoint1.getLongitude(), midPoint1.getLatitude()));
        steps.add(new TrackingStep(now.minusDays(2).format(formatter), "【" + startPoint.getName() + "】的收件员已揽收快件。", startPoint.getLongitude(), startPoint.getLatitude()));

        // 返回给前端时，通常希望按时间顺序（从旧到新），所以反转一下
        Collections.reverse(steps);

        try {
            return new ObjectMapper().writeValueAsString(steps);
        } catch (Exception e) {
            return "[]";
        }
    }
}
