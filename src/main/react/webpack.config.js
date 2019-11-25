const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');
const path = require('path');

module.exports = (env, argv) => {
    const app_server = argv.mode === "development" ? "'http://localhost:9000'" : "''";

    console.log("app_server", app_server);

    return {
        resolve: {
            extensions: ['.tsx', '.ts', '.js'],
        },
        entry: './src/index.tsx',
        devtool: 'source-map',
        module: {
            rules: [
                {
                    test: /\.tsx?$/,
                    use: ['awesome-typescript-loader','tslint-loader'],
                    exclude: /node_modules/,
                },
                {
                    test: /\.css$/i,
                    use: ['style-loader', 'css-loader'],
                },
                {
                    test: /\.s[ac]ss$/i,
                    use: ['style-loader', 'css-loader', 'sass-loader'],
                },
                {
                    test: /\.(eot|gif|ico|jpe?g|png|svg|ttf|woff2?)$/i,
                    use: 'file-loader'
                },
            ],
        },
        output: {
            filename: 'borgy.js',
            publicPath: "/",
            path: path.resolve(__dirname, 'build'),
        },
        plugins: [
            new HtmlWebpackPlugin({
                template: './public/index.html',
                filename: 'index.html',
                inject: 'body'
            }),
            new webpack.DefinePlugin({
                'process.env.REACT_APP_SERVER': app_server
            })
        ],
        devServer: {
            historyApiFallback: true
        }
    }
};