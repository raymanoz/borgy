import * as React from 'react';
import './Button.css';
import {server} from "../utils/server";

export interface Props {
    intensity: Number;
    trial: string;
}


function handleClick(trialName: string, intensity: Number) {
    fetch(server.trial(trialName), {method: "PATCH", body: `{ "intensity": ${intensity} }`})
}


function Button({ intensity, trial }: Props) {
    return (
            <div className="col">
                <button className={"borg-button btn btn-primary"} onClick={() => handleClick(trial, intensity)}>{intensity}</button>
            </div>
    );
}

export default Button;
