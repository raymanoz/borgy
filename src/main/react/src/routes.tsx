import React from "react";
import {Route, Switch} from "react-router";
import {AggregateTrial} from "./pages/AggregateTrial";
import Home from "./pages/Home";

export const RouteMap = () => (
    <div>
        <Switch>
            <Route path="/" exact component={Home} />
            <Route path="/trial/:name" exact component={AggregateTrial} />
        </Switch>
    </div>
);
