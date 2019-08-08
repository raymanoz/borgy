import * as React from 'react';
import './Button.css';
import {server} from "../utils/server";

export interface Props {
    intensity: Number;
    label: string;
    trial: string;
}


function handleClick(trialName: string, intensity: Number) {
    fetch(server.trial(trialName), {method: "PATCH", body: `{ "intensity": ${intensity} }`})
}


function Button({ intensity, label, trial }: Props) {
    return (
        <div>
            <button className={"borg-button btn btn-primary"} onClick={() => handleClick(trial, intensity)}>
                {intensity}
            </button>
            <div className={"borg-button-label"}>{label}</div>
        </div>
    );
}

export default Button;
