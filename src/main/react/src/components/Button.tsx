import * as React from 'react';
import './Button.css';

export interface Props {
    intensity: Number;
    label: String;
    trial: String;
}


function handleClick(trial: String, intensity: Number) {
    fetch(`http://localhost:9000/api/trials/${trial}`, {method: "PATCH", body: `{ "intensity": ${intensity} }`})
}


function Button({ intensity, label, trial }: Props) {
    return (
      <button className={"borg-button"} onClick={() => handleClick(trial, intensity)}>
          {label}
      </button>
    );
}

export default Button;
