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
      <button className={"borg-button"} onClick={() => handleClick(trial, intensity)}>
          {label}
      </button>
    );
}

export default Button;
