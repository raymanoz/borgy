import * as React from 'react';
import './Button.css';

export interface Props {
    intensity: Number;
    label: String;
}


function handleClick(intensity: Number) {
    fetch(`http://localhost:9000/click`, {method: "POST", body: `{ "intensity": ${intensity} }`})
}


function Button({ intensity, label }: Props) {
    return (
      <button className={"borg-button"} onClick={() => handleClick(intensity)}>
          {label}
      </button>
    );
}

export default Button;
