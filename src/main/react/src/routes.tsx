import React from "react";
import {Route, Switch} from "react-router";
import {Home} from "./pages/Home";

export const RouteMap = () => (
    <div>
        <Switch>
            <Route path="/" exact component={Home} />
            <Route path="/home" exact component={Home} />
        </Switch>
    </div>
);