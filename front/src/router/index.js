import Vue from 'vue'
import VueRouter from 'vue-router'

// Vue a global use of VueRouter
Vue.use(VueRouter)

// Define static routing, which is accessible to all users
let routes = [
    {
        // Redirect the root path to the login page
        path: '/',
        redirect: '/login',
    },
    {
        // Front-end display page layout routing
        path: '/front',
        name: 'Front',
        component: () => import("../layout/front"),
        // Child routing for front-end pages
        children: [
            {
                path: 'home',
                name: 'FrontHome',
                component: () => import("../views/front/home")
            },
            {
                path: 'search',
                name: 'Search',
                component: () => import("../views/front/search")
            },
            {
                path: 'goods',
                name: 'Goods',
                component: () => import("../views/front/goods")
            },
            {
                path: 'cart',
                name: 'Cart',
                component: () => import("../views/front/cart")
            },
            {
                path: 'preOrder',
                name: 'PreOrder',
                component: () => import("../views/front/preOrder")
            },
            {
                path: 'order',
                name: 'FrontOrder',
                component: () => import("../views/front/order")
            },
            {
                path: 'collect',
                name: 'Collect',
                component: () => import("../views/front/collect")
            },
            {
                path: 'person',
                name: 'Person',
                component: () => import("../views/front/person")
            },
            {
                path: 'address',
                name: 'Address',
                component: () => import("../views/front/address")
            },
        ]
    },
    {
        // Login page routing
        path: '/login',
        name: 'Login',
        component: () => import("../views/login")
    },
    {
        // Registration page routing
        path: '/register',
        name: 'Register',
        component: () => import("../views/register")
    },
]


// Create a factory function for the router instance to facilitate later reset operations
const createRouter = () => new VueRouter({
    // Use history mode to remove the # in the URL
    mode: 'history',
    // Configure routing rules
    routes: routes
})

// Create a router instance
const router = createRouter()

// Get user information from sessionStorage at initialization
let user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {}
// Reset the route based on the user's permissions. This handles the case where the user is already logged in when refreshing the page
resetRouter(user.permission)

/**
 * Reset routing method.
 * Used to clear dynamically loaded routes when switching users or logging out.
 * @param {Array} permissions - User's permission list.
 */
export function resetRouter(permissions) {
    // Re-add dynamic routes only if the user has permissions
    if (permissions) {
        const newRouter = createRouter()
        // Replace the matcher of the old router instance with the new one, which is equivalent to resetting the route
        router.matcher = newRouter.matcher
        // Check if the management route has been added to avoid repeated additions
        if (!router.getRoutes().find(v => v.name === 'Manage')) {
            // Add dynamically generated management routes
            router.addRoute(getPermissions(permissions))
        }
    }
}

/**
 * Generate management-side routes based on user permissions.
 * @param {Array} permissions - User's permission list.
 * @returns {Object} - Route object for the management side.
 */
function getPermissions(permissions) {
    // Get current user information again
    let user = sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {};
    // Define the basic structure of the management-side route, which contains a personal information page that everyone has
    let manage = {
        path: '/manage',
        name: 'Manage',
        component: () => import("../layout/manage"),
        children: [
            { path: 'person', name: '个人信息', component: () => import("../views/manage/person") }
        ]
    };

    // If the user role includes 3, it is determined to be a merchant, and merchant-specific routes are added
    if (user.role && user.role.includes(3)) { // Is Merchant
        manage.children.push(
            { path: "merchantHome", name: "商家主页", component: () => import("../views/manage/MerchantHome.vue") },
            { path: "merchantGoods", name: "商品管理", component: () => import("../views/manage/merchantGoods.vue") },
            { path: "merchantOrder", name: "订单管理", component: () => import("../views/manage/MerchantOrder.vue") },
            { path: "merchantComment", name: "评论管理", component: () => import("../views/manage/MerchantComment.vue") }
        );
    } else if (permissions) { // If it is an administrator or other role, generate routes based on the permission list
        permissions.forEach(item => {
            manage.children.push({
                path: item.path.substring(1), // The back-end returns a path with a /, which needs to be removed
                name: item.name,
                component: () => import("../views/manage" + item.path)
            });
        });
    }
    // Return the constructed management-side route
    return manage;
}


// Global navigation guard (commented out, can be enabled if needed)
// vueRouter.beforeEach((to, from, next) => {
//     // Determine if the route to be accessed requires authentication
//     if (to.path.includes('manage')) {
//         // Check if the user is logged in
//         if (!user.id) {
//             // If not logged in, redirect to the login page
//             next({
//                 path: '/login'
//             })
//         } else {
//             // If logged in, allow normal navigation
//             next()
//         }
//     } else {
//         // No authentication required, allow normal navigation
//         next()
//     }
// })

// Export the configured router instance
export default router
