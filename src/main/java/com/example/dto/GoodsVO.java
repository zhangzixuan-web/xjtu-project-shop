package com.example.dto;



import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime; // 注意：如果你的 Goods 实体用的是 java.util.Date，这里也用 Date
import java.util.List;

@Data // 使用 Lombok 自动生成 getter/setter/toString 等方法
public class GoodsVO {

    // --- 从你的 Goods 实体类中复制所有需要返回给前端的字段 ---
    private Long id;
    private String name;
    private String description;
    private String no;
    private BigDecimal price;
    private BigDecimal realPrice;
    private Double discount;
    private Integer store;
    private Integer praise;
    private Integer sales;
    private Long categoryId;
    private String categoryName;
    private String createTime; // 你的 JSON 里是 String，这里保持一致
    private Boolean recommend;
    private Long merchantId;

    // --- 这是最关键的修改 ---
    // 将 imgs 字段的类型从 String 改为 List<String>
    private List<String> imgs;
}
