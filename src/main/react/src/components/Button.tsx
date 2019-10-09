import * as React from 'react';
import './Button.css';
import {server} from "../utils/server";

export interface Props {
    intensity: number;
    trial: string;
    selected: boolean
}

function handleClick(trialName: string, intensity: number) {
    fetch(server.trial(trialName), {method: "PATCH", body: `{ "intensity": ${intensity} }`})
}

const Button = ({ intensity, trial, selected }: Props) => {
    return (
        <div className="col">
            <div className={`borg-button btn btn-primary ${selected ? 'borg-button-selected' : ''}`}>{intensity}</div>
        </div>
    );
};

export default Button;
