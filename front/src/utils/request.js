import axios from 'axios'
import router from '../router'
import Vue from 'vue'

// 定义后端 API 的基础 URL
const BASE_URL = "http://localhost:9999/"

// 创建一个 axios 实例，用于向后端发送请求
const API = axios.create({
    // 设置基础 URL，之后所有请求的 URL 都会自动拼接在这个地址后面
    baseURL: BASE_URL,
    // 设置请求超时时间为 60 秒
    timeout: 60000
});

// === 请求拦截器 ===
// 在请求发送到服务器之前，对请求进行预处理
API.interceptors.request.use(config => {
    // 统一设置请求头
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    
    // 从 sessionStorage 中获取用户信息
    let user = sessionStorage.getItem("user") || '{}';
    user = JSON.parse(user);
    
    // 如果用户已登录，将用户的 token 添加到请求头中，用于身份验证
    config.headers['token'] = user.token;
    
    // 返回处理后的配置
    return config
}, error => {
    // 如果请求配置出错，则返回一个被拒绝的 Promise
    return Promise.reject(error)
});

// === 响应拦截器 ===
// 在接收到服务器响应之后，对响应数据进行预处理
API.interceptors.response.use(
    response => {
        // 获取响应体中的数据
        let res = response.data;
        
        // 如果响应类型是 'blob'，说明是文件下载，直接返回响应体
        if (response.config.responseType === 'blob') {
            return res
        }
        
        // 兼容处理后端可能返回的 JSON 字符串
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }

        // 如果后端返回的业务码是 '401'，表示 token 失效或未授权
        if (res.code === '401') {
            // 清除 sessionStorage 中的用户数据
            sessionStorage.removeItem("user");
            // 跳转到登录页面
            router.push({path: '/login'});
            // 返回响应数据，让调用方可以继续处理
            return res;
        }
        
        // 对于其他正常情况，直接返回响应数据
        return res;
    },
    error => {
        // 如果发生网络错误或服务器错误，在控制台打印错误信息
        console.log('err' + error) // for debug
        // 返回一个被拒绝的 Promise，让调用方可以捕获并处理错误
        return Promise.reject(error)
    }
)

// 导出配置好的 axios 实例
export default API
