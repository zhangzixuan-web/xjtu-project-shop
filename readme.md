```markdown
front/
├── public/
│   └── (静态资源，如 index.html, favicon.ico)
├── src/
│   ├── assets/
│   │   └── (图片、视频等媒体文件)
│   ├── components/
│   │   ├── common/
│   │   │   └── Identify.vue
│   │   └── LogisticsMap.vue
│   ├── layout/
│   │   ├── front.vue
│   │   └── manage.vue
│   ├── router/
│   │   └── index.js
│   ├── store/
│   │   └── index.js
│   ├── style/
│   │   ├── base.css
│   │   └── bakery-theme.css
│   ├── utils/
│   │   ├── manage.js
│   │   └── request.js
│   ├── views/
│   │   ├── front/
│   │   │   ├── home.vue
│   │   │   ├── goods.vue
│   │   │   ├── search.vue
│   │   │   ├── cart.vue
│   │   │   ├── order.vue
│   │   │   ├── person.vue
│   │   │   └── ... (其他面向用户的视图)
│   │   ├── manage/
│   │   │   ├── home.vue
│   │   │   ├── user.vue
│   │   │   ├── goods.vue
│   │   │   ├── order.vue
│   │   │   ├── MerchantHome.vue
│   │   │   ├── merchantGoods.vue
│   │   │   └── ... (其他管理后台视图)
│   │   ├── login.vue
│   │   └── register.vue
│   ├── App.vue
│   └── main.js
├── babel.config.js
├── package.json
├── package-lock.json
└── vue.config.js

src
└── main
    ├── java
    │   └── com
    │       └── example
    │           ├── common
    │           │   ├── handler
    │           │   │   └── ListHandler.java
    │           │   ├── AuthInterceptor.java
    │           │   ├── CorsConfig.java
    │           │   ├── MybatisPlusConfig.java
    │           │   ├── Result.java
    │           │   └── WebMvcConfig.java
    │           ├── controller
    │           │   ├── UserController.java
    │           │   ├── GoodsController.java
    │           │   ├── OrderController.java
    │           │   ├── RoleController.java
    │           │   ├── MerchantController.java
    │           │   └── ... (其他14个Controller)
    │           ├── dto
    │           │   ├── FileVO.java
    │           │   └── PreOrderQo.java
    │           ├── entity
    │           │   ├── User.java
    │           │   ├── Goods.java
    │           │   ├── Order.java
    │           │   ├── Role.java
    │           │   └── ... (其他11个实体类)
    │           ├── exception
    │           │   ├── CustomException.java
    │           │   └── GlobalExceptionHandler.java
    │           ├── mapper
    │           │   ├── UserMapper.java
    │           │   ├── GoodsMapper.java
    │           │   ├── OrderMapper.java
    │           │   └── ... (其他12个Mapper接口)
    │           ├── service
    │           │   ├── UserService.java
    │           │   ├── GoodsService.java
    │           │   ├── OrderService.java
    │           │   ├── LogisticsSimulatorService.java
    │           │   └── ... (其他12个Service)
    │           ├── utils
    │           │   └── generator
    │           │       └── (代码生成器相关)
    │           └── Application.java
    └── resources
        ├── mapper
        │   ├── User.xml
        │   ├── Goods.xml
        │   ├── Order.xml
        │   └── ... (其他10个MyBatis XML文件)
        ├── static
        │   └── file
        │       └── (用户上传的图片等文件)
        └── application.yml
```