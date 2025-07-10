'use strict'
// vue.config.js 配置说明
//官方vue.config.js 参考文档 https://cli.vuejs.org/zh/config/#css-loaderoptions
module.exports = {
  /**
   * The base URL your application will be deployed at.
   * By default, Vue CLI assumes your app will be deployed at the root of a domain.
   */
  publicPath: process.env.NODE_ENV === "production" ? "/" : "/",

  /**
   * The directory where the production build files will be generated in when running `npm run build`.
   * The default is 'dist'.
   */
  outputDir: 'dist',

  /**
   * Configuration options for the webpack-dev-server.
   */
  devServer: {
    // Host for the server. '0.0.0.0' makes it accessible from any network interface.
    host: '0.0.0.0',
    // Port for the development server.
    port: 8001,
    // Automatically open the browser after the server has been started.
    open: true,
    /**
     * Proxy settings for the development server to avoid CORS issues when making API requests.
     * Any request starting with '/api' will be forwarded to the backend server.
     */
    proxy: {
      "/api": {
        // The target backend server URL.
        target: 'http://localhost:9999',
        // If you want to proxy websockets, set this to true.
        secure: false,
        // Needed for virtual-hosted sites, changes the 'Host' header of the request to the target URL.
        changeOrigin: true,
        // Rewrites the request path. Here, it removes the '/api' prefix.
        // e.g., a request to '/api/user' becomes '/user' when forwarded to the backend.
        pathRewrite: {
          '^/api': ''
        },
      }
    },
    // Disables the host check. This is not recommended as it can be a security risk,
    // but it can be useful for certain development setups.
    disableHostCheck: true
  },
}
