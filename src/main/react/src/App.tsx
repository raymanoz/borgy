import React from 'react';
import './App.css';
import {Trial} from "./pages/Trial";

const App: React.FC = () => {
    return (
        <div className="App">
            <header className="App-header">
                <Trial />
            </header>
        </div>
    );
};

export default App;
