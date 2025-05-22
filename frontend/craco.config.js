const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  webpack: {
    configure: (webpackConfig, { env, paths }) => {
      // Entry point for main page
      webpackConfig.entry = {
        main: paths.appIndexJs,
        history: './src/history.js', // <-- your history page entry
      };

      // Output settings
      webpackConfig.output.filename = 'static/js/[name].js';

      // Remove the default HtmlWebpackPlugin instance
      webpackConfig.plugins = webpackConfig.plugins.filter(
        plugin => !(plugin instanceof HtmlWebpackPlugin)
      );

      // Add HtmlWebpackPlugin for index.html
      webpackConfig.plugins.push(
        new HtmlWebpackPlugin({
          inject: true,
          template: paths.appHtml,
          chunks: ['main'],
          filename: 'index.html'
        })
      );

      // Add HtmlWebpackPlugin for history.html
      webpackConfig.plugins.push(
        new HtmlWebpackPlugin({
          inject: true,
          template: 'public/history.html',
          chunks: ['history'],
          filename: 'history.html'
        })
      );

      return webpackConfig;
    },
  },
};
