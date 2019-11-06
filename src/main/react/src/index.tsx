import "bootstrap/dist/css/bootstrap.min.css";
import * as React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {App} from "./App";
import "./favicon.ico";
import "./index.css";
import "./lotte-css/stylesheets/lotte.scss";
import configureStore from "./store";

const store = configureStore();

const Root = () => (
    <Provider store={store}>
        {App()}
    </Provider>
);

ReactDOM.render(<Root/>, document.getElementById("root"));
