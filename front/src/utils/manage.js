/**
 * 该模块用于处理后台管理界面的菜单数据
 */

// 导出一个空数组 p，用于存储处理后的菜单项
export let p = []

/**
 * 设置和处理菜单数据
 * @param {Array} [permissions] - 从外部传入的权限列表。如果未提供，则从 sessionStorage 中获取。
 */
export function setMenu(permissions) {
    // 如果没有传入权限列表，或者列表为空，则尝试从 sessionStorage 中获取当前用户的权限
    if (!permissions || !permissions.length) {
        let user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
        permissions = user.permission
    }
    
    // 遍历权限列表，为每个菜单项的 path 属性添加 "/manage" 前缀，以匹配路由配置
    permissions.forEach(v => { v.path = "/manage" + v.path })
    
    // 将处理后的权限列表赋值给导出的变量 p，供其他组件（如导航菜单）使用
    p = permissions
}
