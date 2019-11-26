import * as React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {App} from "./App";
import "./lotte-css/stylesheets/lotte-css.scss";
import configureStore from "./store";

const store = configureStore();

const Root = () => (
    <Provider store={store}>
        {App()}
    </Provider>
);

ReactDOM.render(<Root/>, document.getElementById("root"));
