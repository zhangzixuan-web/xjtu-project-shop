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