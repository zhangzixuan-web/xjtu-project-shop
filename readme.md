```mermaid
graph TD
    %% 功能3: 购物车与下单流程
    subgraph "购物车与下单流程"
        Goods_UI["商品详情<br>file: front/src/views/front/goods.vue"]
        Cart_UI["购物车<br>file: front/src/views/front/cart.vue"]
        Order_UI["订单<br>file: front/src/views/front/order.vue"]
        FE_Utils["工具类<br>file: front/src/utils/request.js"]
        
        Cart_API["购物车控制器<br>file: src/main/java/com/example/controller/CartController.java"]
        Order_API["订单控制器<br>file: src/main/java/com/example/controller/OrderController.java"]
        
        Cart_Service["购物车服务<br>file: src/main/java/com/example/service/CartService.java"]
        Order_Service["订单服务<br>file: src/main/java/com/example/service/OrderService.java"]
        
        Cart_DB["数据库<br>购物车表(cart)"]
        Order_DB["数据库<br>订单表(t_order)"]
        Goods_DB["数据库<br>商品表(goods)"]
        
        Goods_UI -->|"1. 调用: 添加到购物车"| FE_Utils
        FE_Utils -->|"2. API: POST /cart/add"| Cart_API
        Cart_API -->|"3. 调用: add(cart)"| Cart_Service
        Cart_Service -->|"4. 插入: 购物车数据"| Cart_DB
        Cart_DB -->|"5. 返回: 操作结果"| Cart_Service
        Cart_Service -->|"6. 返回: 成功状态"| Cart_API
        Cart_API -->|"7. 返回: 添加结果"| FE_Utils
        FE_Utils -->|"8. 提示: 添加成功"| Goods_UI
        
        Goods_UI -->|"9. 导航: 查看购物车"| Cart_UI
        Cart_UI -->|"10. 调用: 获取购物车列表"| FE_Utils
        FE_Utils -->|"11. API: GET /cart/list"| Cart_API
        Cart_API -->|"12. 调用: list(userId)"| Cart_Service
        Cart_Service -->|"13. 查询: 获取购物车数据"| Cart_DB
        Cart_DB -->|"14. 返回: 购物车列表"| Cart_Service
        Cart_Service -->|"15. 返回: 购物车数据"| Cart_API
        Cart_API -->|"16. 返回: 购物车列表"| FE_Utils
        FE_Utils -->|"17. 渲染: 购物车列表"| Cart_UI
        
        Cart_UI -->|"18. 调用: 创建订单"| FE_Utils
        FE_Utils -->|"19. API: POST /order/create"| Order_API
        Order_API -->|"20. 调用: create(order)"| Order_Service
        Order_Service -->|"21. 插入: 订单数据"| Order_DB
        Order_Service -->|"22. 更新: 减少库存"| Goods_DB
        Order_Service -->|"23. 清空: 购物车"| Cart_DB
        Order_DB -->|"24. 返回: 操作结果"| Order_Service
        Order_Service -->|"25. 返回: 订单ID"| Order_API
        Order_API -->|"26. 返回: 订单创建结果"| FE_Utils
        FE_Utils -->|"27. 导航: 查看订单"| Order_UI
        
        style Goods_UI fill:#90EE90
        style Cart_UI fill:#90EE90
        style Order_UI fill:#90EE90
        style FE_Utils fill:#90EE90
        
        style Cart_API fill:#6495ED
        style Order_API fill:#6495ED
        style Cart_Service fill:#6495ED
        style Order_Service fill:#6495ED
        
        style Cart_DB fill:#FFD700
        style Order_DB fill:#FFD700
        style Goods_DB fill:#FFD700
    end
    