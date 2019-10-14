import React from "react";
import {Route, Switch} from "react-router";
import {Home} from "./pages/Home";
import {Trial} from "./pages/Trial";

export const RouteMap = () => (
    <div>
        <Switch>
            <Route path="/" exact component={Home} />
            <Route path="/trial/:name" exact component={Trial} />
        </Switch>
    </div>
);
