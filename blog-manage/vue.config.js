module.exports = {
    // baseUrl: './',
    assetsDir: 'static',
    publicPath:'/admin',
    // publichPath:'/bolg',
    productionSourceMap: false,
    devServer: {
        proxy: {
            '/api':{
                target:'http://47.96.114.93:8090',
                changeOrigin:true,
                pathRewrite:{
                    '/api':''
                }
            }
        }
    }
}