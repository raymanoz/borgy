import React from 'react';
import './App.css';
import {Route, Router} from "react-router";
import {Home} from "./pages/Home";
import {History} from 'history';

interface AppProps {
    history: History;
}

const App = ({history}:AppProps) => {
    return (
        <div className="App">
            <header className="App-header">
                <Router history={history}>
                    <Route path={"/"} exact component={Home}/>>
                    <Route path={"/home"} exact component={Home}/>>
                </Router>
            </header>
        </div>
    );
};

export default App;
