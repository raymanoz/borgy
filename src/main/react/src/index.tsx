import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import {BrowserRouter} from "react-router-dom";
import {RouteMap} from './routes';


ReactDOM.render(
    <BrowserRouter>
        <RouteMap/>
    </BrowserRouter>,
    document.getElementById('root')
);
